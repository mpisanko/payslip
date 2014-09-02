(ns payslip.parsers.input-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [clojure.java.io :refer [reader]]
            [payslip.parsers.input :refer :all]
            [payslip.models.input :refer :all]
            [payslip.validators.validators :refer :all]))

(def csv-input
  (str "first-name,last-name,annual-salary,super-rate,payment-start-date\n"
       "David,Rudd,60050,9,01 March\n"
       "Ryan,Chen,120000,10,01 March"))

(def invalid-csv-input
  (str "first-name,last-name,annual-salary,super-rate,payment-start-date\n"
       "David,Rudd,-60050,66,33 Movember\n"
       "Ryan,Chen,120000,10,01 March"))

(def expected-payslips
  [(->InputPayslip "David" "Rudd" 60050 9 (t/local-date 2014 03 01))
   (->InputPayslip "Ryan" "Chen" 120000 10 (t/local-date 2014 03 01))])

(def expected-payslips-with-error
  [(merge (first expected-payslips)
          {:errors
            {:annual-salary "annual-salary cannot be negative"
             :payment-start-date "invalid payment-start-date"
             :super-rate "super-rate must be between 0 and 50%"}
           :payment-start-date nil
           :annual-salary -60050
           :super-rate 66})
   (second expected-payslips)])

(def test-file (java.io.File/createTempFile "test" "csv"))

(def validator
  (->InputValidator))
(defn initialise-csv-input-stream
  [contents]
  (do
    (.deleteOnExit test-file)
    (spit test-file contents)))

(facts
  CsvInputParser
  (with-state-changes [(before :facts (initialise-csv-input-stream csv-input))]
    (fact
      "it parses contents of the csv and produces collection of InputPayslip-s"
      (parse-input (->CsvInputParser (reader test-file) validator)) => expected-payslips))

  (with-state-changes [(before :facts (initialise-csv-input-stream invalid-csv-input))]
    (fact
      "it appends errors to invalid rows"
      (parse-input (->CsvInputParser (reader test-file) validator)) => expected-payslips-with-error)))
