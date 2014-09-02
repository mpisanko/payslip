(ns payslip.validators.validators-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [payslip.validators.validators :refer :all]
            [payslip.models.input :refer :all]))

(def valid-input
  (->InputPayslip "David" "Rudd" 60050 9 (t/local-date 2014 03 01)))

(def invalid-input
  (->InputPayslip "David" "Rudd" -60050 66 nil))

(def validator (->InputValidator))

(facts
  InputValidator
  (fact
    "returns nil for valid input"
    (errors validator valid-input) => nil)
  (fact
    "returns errors for invalid input"
    (errors validator invalid-input) => {:errors
                                          {:payment-start-date "invalid payment-start-date"
                                           :super-rate "super-rate must be between 0 and 50%"
                                           :annual-salary "annual-salary cannot be negative"}}))



