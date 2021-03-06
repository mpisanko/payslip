(ns payslip.parsers.input
  (:require [clojure.string :refer [split]]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.local :as l]
            [payslip.models.input :refer :all]
            [payslip.validators.validators :refer :all]))

(defprotocol InputParser
  "Abstracts parsing input from some source"
  (parse-metadata [parser raw-metadata]
    "Parse metadata for specific format")
  (parse-input [parser metadata record]
    "Parse specific input format"))


(defn- split-fields
  [line]
  (split line (re-pattern ",")))

(defn- headers
  [line]
  (map keyword (split-fields line)))

(def keys-with-numeric-entries [:annual-salary :super-rate])

(defn- current-year
  []
  (t/year (l/local-now)))

(defn- extract-map-with-keys-and-transformed-values
  [keys transform-function original]
  (let [subset-with-keys (select-keys original keys)]
    (into {} (for [[k v] subset-with-keys] [k (transform-function v)]))))

(def start-date-format (f/formatter "dd MMMMyyyy"))

(defn- parse-start-date
  [date-string]
  (let [date-with-year (str date-string (current-year))]
    (try
      (f/parse-local-date start-date-format date-with-year)
      (catch Exception e (println e)))))

(def keys-with-date-entries [:payment-start-date])

(defn- create-input-payslip
  [line]
  (let [parsed-numeric-entries (extract-map-with-keys-and-transformed-values keys-with-numeric-entries read-string line)
        parsed-date-entries (extract-map-with-keys-and-transformed-values keys-with-date-entries parse-start-date line)
        parsed-record (merge line parsed-numeric-entries parsed-date-entries)]
    (map->InputPayslip parsed-record)))

(defn- add-header-keys-to-fields
  [headers value-record]
  (zipmap headers value-record))

(defn- parse-body
  [headers validator line]
  (let [value-record (split-fields line)
        keyed-record (add-header-keys-to-fields headers value-record)
        input-slip (create-input-payslip keyed-record)]
    (merge input-slip (errors validator input-slip))))

(deftype CsvInputParser [validator]
  InputParser
  (parse-metadata
    [_ raw-metadata]
    (headers raw-metadata))
  (parse-input
    [_ metadata record]
    (parse-body metadata validator record)))
