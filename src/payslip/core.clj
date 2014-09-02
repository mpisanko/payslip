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

(defn- calculate-result
  [payslip]
  (process-payslip processor payslip))

(defn -main
  [& args]
  (println "Processing: " (config/input-file-name))
  (with-open [reader (io/reader (config/input-file-name))
              writer (io/writer (config/report-file-name))]
    (let [input-parser (->CsvInputParser reader (->InputValidator))
          result-presenter (->CsvResultPresenter (config/headers) (config/header-names) writer)]
      (do
        (present-metadata result-presenter)
        (doseq [payslip (map calculate-result (parse-input input-parser))]
          (present-result result-presenter payslip))))))
