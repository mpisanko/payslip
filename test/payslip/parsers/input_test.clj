(ns payslip.parsers.input-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [clojure.java.io :refer [reader]]
            [payslip.parsers.input :refer :all]
            [payslip.models.input :refer :all]
            [payslip.validators.validators :refer :all]))

(def raw-metadata
  "first-name,last-name,annual-salary,super-rate,payment-start-date")

(def expected-metadata
  [:first-name :last-name :annual-salary :super-rate :payment-start-date])

(def csv-input-1
  "David,Rudd,60050,9,01 March")

(def csv-input-2
  "Ryan,Chen,120000,10,01 March")

(def invalid-csv-input
  "David,Rudd,-60050,66,33 Movember")

(def expected-payslip-1
  (->InputPayslip "David" "Rudd" 60050 9 (t/local-date 2014 03 01)))

(def expected-payslip-2
   (->InputPayslip "Ryan" "Chen" 120000 10 (t/local-date 2014 03 01)))

(def expected-payslip-with-error
  (merge expected-payslip-1
          {:errors
            {:annual-salary "annual-salary cannot be negative"
             :payment-start-date "invalid payment-start-date"
             :super-rate "super-rate must be between 0 and 50%"}
           :payment-start-date nil
           :annual-salary -60050
           :super-rate 66}))

(def parser
  (->CsvInputParser (->InputValidator)))

(facts
  CsvInputParser
  (facts
    "parse-metadata"
    (fact
      "it parses metadata"
      (parse-metadata parser raw-metadata) => expected-metadata))
  (facts 
    "parse-input"
    (fact 
      "it parses correct input 1"
      (parse-input parser expected-metadata csv-input-1) => expected-payslip-1)
    (fact 
      "it parses correct input 2"
      (parse-input parser expected-metadata csv-input-2) => expected-payslip-2)
    (fact
      "it parses incorrect rows into errored rows"
      (parse-input parser expected-metadata invalid-csv-input) => expected-payslip-with-error)))
