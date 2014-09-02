(ns payslip.calculators.income-tax-calculator
  (:require [payslip.calculators.calculators :refer [Calculator calculate]]))

(defn- income-matches-bracket?
  [income bracket]
  (and
    (<= (:lower-bound bracket) income)
    (<= income (:upper-bound bracket))))

(defn- bracket-for-income
  "Find which tax bracket this taxable income falls into"
  [tax-rates taxable-income]
  (first (filter (partial income-matches-bracket? taxable-income) tax-rates)))

(defn- payable-inside-bracket
  "Calculate how much of the income should be taxed in this bracket"
  [income bracket]
  (+ 1 (- income (:lower-bound bracket))))

(defn- base-plus-percentage
  "Sum up the base for bracket and percentage of what is due in the bracket"
  [base over-lower-bound bracket]
  (+ base  (* over-lower-bound (:percentage bracket))))

(deftype IncomeTaxCalculator [tax-rates]
  Calculator
  (calculate [_ amount]
    (let [bracket (bracket-for-income tax-rates amount)
          base-for-bracket (:base-amount bracket)
          over-lower-bound (payable-inside-bracket amount bracket)]
      (base-plus-percentage base-for-bracket over-lower-bound bracket))))
