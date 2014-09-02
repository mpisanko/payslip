(ns payslip.calculators.per-period-calculators-test
  (:require [midje.sweet :refer :all]
            [clj-time.core :as t]
            [payslip.calculators.calculators :refer [calculate]]
            [payslip.calculators.pay-period-calculators :refer :all]))

(def monthly-pay-period-calculator
  (->MonthlyPayPeriodCalculator))

(facts
  MonthlyPayPeriodCalculator
  (tabular
    (calculate monthly-pay-period-calculator ?date) => {:pay-period-start ?date :pay-period-end ?end-date}
    ?date                       ?end-date
    (t/local-date 2014 02 01)   (t/local-date 2014 02 28)
    ;  "for leap year it handles February correctly"
    (t/local-date 2012 02 01)   (t/local-date 2012 02 29)
    (t/local-date 2014 05 05)   (t/local-date 2014 06 04)
    (t/local-date 2014 01 01)   (t/local-date 2014 01 31)
    (t/local-date 2014 12 20)   (t/local-date 2015 01 19)
    (t/local-date 2014 12 31)   (t/local-date 2015 01 30)))