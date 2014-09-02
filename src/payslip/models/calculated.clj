(ns payslip.models.calculated)

(defrecord CalculatedPayslip [first-name last-name pay-period-start pay-period-end gross-income income-tax net-income super])
