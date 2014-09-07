(ns payslip.core
  (:gen-class :main true)
  (:require [clojure.java.io :as io]
            [payslip.payslip-processor :refer :all]
            [payslip.config :as config]
            [payslip.calculators.calculators :refer :all]
            [payslip.calculators.income-tax-calculator :refer :all]
            [payslip.calculators.pay-period-calculators :refer :all]
            [payslip.parsers.input :refer :all]
            [payslip.presenters.result :refer :all]
            [payslip.validators.validators :refer :all]
            [payslip.payslip-processor :refer :all]))

(def processor
  (->BasicPayslipProcessor
    (->RoundingCalculator)
    (->PerPeriodCalculator (config/number-of-periods-per-year))
    (->IncomeTaxCalculator (config/tax-rates))
    (->MonthlyPayPeriodCalculator)))

(def result-presenter 
  (->CsvResultPresenter (config/headers) (config/header-names)))

(def input-parser 
  (->CsvInputParser (->InputValidator)))

(defn- calculate-result
  [payslip]
  (process-payslip processor payslip))

(defn- parse-input-partial
  [metadata]
  (partial parse-input input-parser metadata))

(defn- calculate-result-from-parsed-input
  [metadata]
  (comp calculate-result (parse-input-partial metadata)))

(defn- present
  [payslip]
  (present-result result-presenter payslip))

(defn- write-line
  [writer line]
  (.write writer (str line "\n")))

(defn -main
  [& args]
  (println "Processing: " (config/input-file-name))
  (with-open [reader (io/reader (config/input-file-name))
              writer (io/writer (config/report-file-name))]
    (let [lines (line-seq reader)
          metadata (parse-metadata input-parser (first lines))]
      (do
        (write-line writer (present-metadata result-presenter))
        (doseq [payslip (map (calculate-result-from-parsed-input metadata) (rest lines))]
          (write-line writer (present payslip)))))))
