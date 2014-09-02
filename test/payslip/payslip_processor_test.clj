(ns payslip.payslip-processor-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [payslip.payslip-processor :refer :all]
            [payslip.models.input :refer :all]
            [payslip.models.calculated :refer :all]
            [payslip.calculators.calculators :refer :all]
            [payslip.calculators.income-tax-calculator :refer :all]
            [payslip.calculators.pay-period-calculators :refer :all]))

(def input-payslip
  (->InputPayslip "Ryan" "Chen" 120000 10 (t/local-date 2014 06 01)))
(def second-input
  (->InputPayslip "David" "Rudd" 60050 9 (t/local-date 2014 05 03)))

(def input-with-errors
  (merge
    (->InputPayslip "David" "Rudd" 60050 9 (t/local-date 2014 05 03))
    {:errors
      {:annual-salary "annual-salary cannot be negative"
       :super-rate "super-rate must be between 0 and 50%"
       :payment-start-date "invalid payment-start-date"}}))

(def expected-payslip
  (->CalculatedPayslip "Ryan" "Chen" (t/local-date 2014 06 01) (t/local-date 2014 06 30) 10000 2696 7304 1000))

(def second-expected
  (->CalculatedPayslip "David" "Rudd" (t/local-date 2014 05 03) (t/local-date 2014 06 02) 5004 922 4082 450))

(def expected-payslip-with-errors
  (->CalculatedPayslip "David" "Rudd" "invalid payment-start-date" nil "annual-salary cannot be negative" nil nil "super-rate must be between 0 and 50%"))

(def rates [
             {:lower-bound 0 :upper-bound 18200 :base-amount 0 :percentage 0.0}
             {:lower-bound 18201 :upper-bound 37000 :base-amount 0 :percentage 0.19}
             {:lower-bound 37001 :upper-bound 80000 :base-amount 3572 :percentage 0.325}
             {:lower-bound 80001 :upper-bound 180000 :base-amount 17547 :percentage 0.37}
             {:lower-bound 180001 :upper-bound 9999999999999 :base-amount 54547 :percentage 0.45}
           ])

(def processor
  (->BasicPayslipProcessor (->RoundingCalculator) (->PerPeriodCalculator 12) (->IncomeTaxCalculator rates) (->MonthlyPayPeriodCalculator)))

(facts
  BasicPayslipProcessor
  (fact
    process-payslip
    (process-payslip processor input-payslip) => expected-payslip)
  (fact
    "processing records with errors"
    (process-payslip processor input-with-errors) => expected-payslip-with-errors)
  (fact
    process-payslips
    (process-payslips processor [input-payslip second-input]) => [expected-payslip second-expected]))

