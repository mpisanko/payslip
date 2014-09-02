(ns payslip.calculators.pay-period-calculators
  (:require [clj-time.core :as t]
            [payslip.calculators.calculators :refer [Calculator]]))

(deftype MonthlyPayPeriodCalculator []
  Calculator
  (calculate [_ start-date]
    (let [end-date (t/minus (t/plus start-date (t/months 1)) (t/days 1))]
      {:pay-period-start start-date :pay-period-end end-date})))