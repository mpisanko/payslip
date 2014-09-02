(ns payslip.calculators.income-tax-calculator-test
  (:require [midje.sweet :refer :all]
            [payslip.calculators.income-tax-calculator :refer :all]
            [payslip.calculators.calculators :refer [calculate]]))

(def rates [
  {:lower-bound 0 :upper-bound 18200 :base-amount 0 :percentage 0.0}
  {:lower-bound 18201 :upper-bound 37000 :base-amount 0 :percentage 0.19}
  {:lower-bound 37001 :upper-bound 80000 :base-amount 3572 :percentage 0.325}
  {:lower-bound 80001 :upper-bound 180000 :base-amount 17547 :percentage 0.37}
  {:lower-bound 180001 :upper-bound 9999999999999 :base-amount 54547 :percentage 0.45}
])

(def income-tax-calculator (->IncomeTaxCalculator rates))

(facts
  IncomeTaxCalculator
  (tabular "tax free treshold"
    (fact (calculate income-tax-calculator ?amount) => ?tax)
    ?amount   ?tax
    0         0.0
    18000     0.0
    18200     0.0)
  (tabular "18201 - 37000"
    (fact (calculate income-tax-calculator ?amount) => ?tax)
    ?amount   ?tax
    18201     0.19
    30000     2242.0
    37000     3572.0)
  (tabular "37001 - 80000"
    (fact (calculate income-tax-calculator ?amount) => ?tax)
    ?amount   ?tax
    37001     3572.325
    60606     11243.95
    80000     17547.0)
  (tabular "80001 - 180000"
    (fact (calculate income-tax-calculator ?amount) => ?tax)
    ?amount   ?tax
    80001     17547.37
    120000    32347.0
    180000    54547.0)
  (tabular "180001 + "
    (fact (calculate income-tax-calculator ?amount) => ?tax)
    ?amount   ?tax
    180001    54547.45
    200000    63547.0
    18000000  8073547.0))
