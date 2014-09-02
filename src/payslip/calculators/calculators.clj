(ns payslip.calculators.calculators
  (:require [clojure.math.numeric-tower :as maths]))

(defprotocol Calculator
  "An abstraction of a Calculator"
  (calculate [calculator amount]
  "Calculate the whatever the calculator is for given a base amount"))

(deftype PerPeriodCalculator [number-of-periods]
  Calculator
  (calculate [calculator amount]
    (/ amount number-of-periods)))

(deftype RoundingCalculator []
  Calculator
  (calculate [_ amount]
    (maths/round amount)))

(deftype SuperCalculator [super-rate]
  Calculator
  (calculate [_ amount]
    (* amount super-rate 0.01)))
