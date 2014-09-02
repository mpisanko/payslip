(ns payslip.calculators.calculators-test
  (:require [midje.sweet :refer :all]
            [payslip.calculators.calculators :refer :all]))

(facts
  RoundingCalculator
  (fact "rounds the number up to nearest dollar, above fifty cents (inclusive)"
    (calculate (->RoundingCalculator) 0.5) => 1)
  (fact "rounds the number down to nearest dollar, below fifty cents (exclusive)"
    (calculate (->RoundingCalculator) 0.499999) => 0))

(facts
  PerPeriodCalculator
  (fact "calculates value of one of n periods from total"
    (calculate (->PerPeriodCalculator 12) 120000) => 10000)
  (fact "period amount is not rounded (usage of roughly necessary due to floating point arithmetics)"
    (calculate (->PerPeriodCalculator 12) 96004.8) => (roughly 8000.4 0.001)))

(facts
  SuperCalculator
  (fact "calculates given percentage of amount"
    (calculate (->SuperCalculator 9) 100) => 9.0)
  (fact "calculated amount is not rounded"
    (calculate (->SuperCalculator 66.6) 10) => (roughly 6.66)))
