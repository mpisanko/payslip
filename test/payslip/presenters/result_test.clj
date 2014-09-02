(ns payslip.presenters.result-test
  (:require [midje.sweet :refer :all]
            [clojure.java.io :refer [writer]]
            [clj-time.core :as t]
            [payslip.presenters.result :refer :all]
            [payslip.models.calculated :refer :all]))

(def headers [:name :pay-period :gross-income :income-tax :net-income :super])
(def header-names {:name "name" :pay-period "pay period" :gross-income "gross income"
                   :income-tax "income tax" :net-income "net income" :super "super"})
(def results [(->CalculatedPayslip "David" "Rudd" (t/local-date 2014 03 01) (t/local-date 2014 03 31) 5004 922 4082 450)
              (->CalculatedPayslip "Ryan" "Chen" (t/local-date 2014 03 01) (t/local-date 2014 03 31) 10000 2696 7304 1000)])

(def expected-metadata
  "name,pay period,gross income,income tax,net income,super\n")

(def expected-results
  (str "David Rudd,01 March - 31 March,5004,922,4082,450\n"
       "Ryan Chen,01 March - 31 March,10000,2696,7304,1000\n"))

(def test-file (java.io.File/createTempFile "test" "csv"))

(defn get-csv-writer
  []
  (do
    (.deleteOnExit test-file)
    (writer test-file)))

(defn present-results-using-csv-presenter
  []
  (with-open [writer (get-csv-writer)]
    (do
      (present-result (->CsvResultPresenter headers header-names writer) (first results))
      (present-result (->CsvResultPresenter headers header-names writer) (second results)))))

(facts
  CsvResultPresenter
  (with-state-changes [(before :facts (present-results-using-csv-presenter))]
    (fact "it writes results to file in CSV format"
      (slurp test-file) => expected-results)))

(defn present-metadata-using-csv-presenter
  []
  (with-open [writer (get-csv-writer)]
    (present-metadata (->CsvResultPresenter headers header-names writer))))

(facts
  CsvResultPresenter
  (with-state-changes [(before :facts (present-metadata-using-csv-presenter))]
    (fact "it writes metadata to file in CSV format"
          (slurp test-file) => expected-metadata)))
