(ns payslip.validators.validators)

(defprotocol Validator
  (errors [validator record]))

(defn- valid?
  [k v]
  (case k
    :payment-start-date (if (nil? v)
                          {:payment-start-date "invalid payment-start-date"})
    :annual-salary (if (< v 0)
                      {:annual-salary "annual-salary cannot be negative"})
    :super-rate (if (or (< v 0) (< 50 v))
                  {:super-rate "super-rate must be between 0 and 50%"})))

(deftype InputValidator []
  Validator
  (errors [_ record]
    (let [keys-to-validate [:payment-start-date :super-rate :annual-salary]
          fields-to-validate (select-keys record keys-to-validate)
          errors (into {} (for [[k v] fields-to-validate] (valid? k v)))]
      (if-not (empty? errors)
        {:errors errors}))))


