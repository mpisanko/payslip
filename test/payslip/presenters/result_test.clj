(ns payslip.presenters.result-test
  (:require [midje.sweet :refer :all]
            [clojure.java.io :refer [writer]]
            [clj-time.core :as t]
            [payslip.presenters.result :refer :all]
            [payslip.models.calculated :refer :all]))

(def headers [:name :pay-period :gross-income :income-tax :net-income :super])
(def header-names {:name "name" :pay-period "pay period" :gross-income "gross income"
                   :income-tax "income tax" :net-income "net income" :super "super"})
(def result-1 
  (->CalculatedPayslip "David" "Rudd" (t/local-date 2014 03 01) (t/local-date 2014 03 31) 5004 922 4082 450))
(def result-2 
  (->CalculatedPayslip "Ryan" "Chen" (t/local-date 2014 03 01) (t/local-date 2014 03 31) 10000 2696 7304 1000))

(def expected-metadata
  "name,pay period,gross income,income tax,net income,super")

(def expected-result-1
  "David Rudd,01 March - 31 March,5004,922,4082,450")
(def expected-result-2
       "Ryan Chen,01 March - 31 March,10000,2696,7304,1000")

(facts
  CsvResultPresenter
  (facts
    present-result
    (fact 
      (present-result (->CsvResultPresenter headers header-names) result-1) => expected-result-1)
    (fact 
      (present-result (->CsvResultPresenter headers header-names) result-2) => expected-result-2))
  (facts
    present-metadata
    (fact
      (present-metadata (->CsvResultPresenter headers header-names)) => expected-metadata)))
