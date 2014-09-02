(ns payslip.payslip-processor
  (:require [payslip.calculators.calculators :refer :all]
            [payslip.calculators.pay-period-calculators :refer :all]
            [payslip.calculators.income-tax-calculator :refer :all]
            [payslip.models.calculated :refer :all]))

(defprotocol PayslipProcessor
  "Processes input data in order to calculate full payslips"
  (process-payslip [processor payslip])
  (process-payslips [processor payslips]))

(deftype BasicPayslipProcessor [rounding-calculator per-period-calculator income-tax-calculator pay-period-calculator]
  PayslipProcessor
  (process-payslip [_ payslip]
    (let [{:keys [annual-salary super-rate payment-start-date first-name last-name]} payslip]
      (if-let [errors (:errors payslip)]
        (->CalculatedPayslip first-name last-name (:payment-start-date errors) nil (:annual-salary errors) nil nil (:super-rate errors))
        (let [round (partial calculate rounding-calculator)
              per-period (partial calculate per-period-calculator)
              rounded-per-period (comp round per-period)
              gross (rounded-per-period annual-salary)
              super (round (calculate (->SuperCalculator super-rate) gross))
              tax (rounded-per-period (calculate income-tax-calculator annual-salary))
              net (- gross tax)
              pay-period (calculate pay-period-calculator payment-start-date)
              {start-date :pay-period-start end-date :pay-period-end} pay-period]
          (->CalculatedPayslip first-name last-name start-date end-date gross tax net super)))))

  (process-payslips [processor payslips]
    (map (partial process-payslip processor) payslips)))
