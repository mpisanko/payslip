(ns payslip.presenters.result
  (:require [clojure.string :refer [join]]
            [clojure.set :refer [rename-keys]]
            [clj-time.format :as f]))

(defprotocol ResultPresenter
  "Abstracts presenting results - a collection of CalculatedPayslips"
  (present-metadata [presenter]
   "presents metadata in its specific format")
  (present-result [presenter result]
   "presents a result in its specific format"))


(defrecord CalculatedPayslipAdapter [name pay-period gross-income income-tax net-income super])

(defn- lookup-header-values
  [headers values]
  (map #(% values) headers))

(defn- generate-csv-line
  [headers values]
  (join "," (lookup-header-values headers values)))

(def date-format
  (f/formatter "dd MMMM"))

(defn- format-date
  [date]
  (if (nil? date)
    "invalid date"
    (try
      (f/unparse-local-date date-format date)
      (catch Exception e date))))

(defn- present-name
  [record]
  {:name (str (:first-name record) " " (:last-name record))})

(defn- present-pay-period
  [record]
  {:pay-period
    (str
      (format-date (:pay-period-start record))
      " - "
      (format-date (:pay-period-end record)))})

(defn- present-errors
  [record]
  (merge (rename-keys (:errors record) {:payment-start-date :pay-period})))

(defn- adapt-result
  "wraps calculated payslip in structure suitable for CSV presenter"
  [result]
  (map->CalculatedPayslipAdapter
    (merge
      result
      (present-name result)
      (present-pay-period result)
      (present-errors result))))

(defn- format-result-csv
  [headers result]
  (generate-csv-line headers (adapt-result result)))

(defn- header-line
  [headers header-names]
  (generate-csv-line headers header-names))

(defn- generate-csv-contents
  [headers result]
  (format-result-csv headers result))

(deftype CsvResultPresenter [headers header-names]
  ResultPresenter
  (present-metadata [_]
    (header-line headers header-names))
  (present-result [_ result]
    (generate-csv-contents headers result)))