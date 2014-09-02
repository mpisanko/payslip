(ns payslip.config
  (:require [clojure.edn :as edn]))

(def cfg
  (edn/read-string (System/getenv "PAYSLIP_CFG")))

(defn config
  [path]
  (get-in cfg path))

(defn tax-rates
  []
  (config [:tax-rates]))

(defn number-of-periods-per-year []
  (config [:number-of-periods-per-year]))

(defn headers
  []
  (config [:headers]))

(defn header-names
  []
  (config [:header-names]))

(defn input-file-name
  []
  (config [:input-file-name]))

(defn report-file-name
  []
  (config [:report-file-name]))


