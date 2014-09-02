Calculate Employee Monthly Payslip
==

Problem: Employee monthly payslip
==

When I input the employee's details: first name, last name, annual salary(positive integer) and super rate(0% - 50% inclusive), payment start date, the program should generate payslip information with name, pay period,  gross income, income tax, net income and super.

The calculation details will be the following:
•       pay period = per calendar month
•       gross income = annual salary / 12 months
•       income tax = based on the tax table provide below
•       net income = gross income - income tax
•       super = gross income x super rate

Notes: All calculation results should be rounded to the whole dollar. If >= 50 cents round up to the next dollar increment, otherwise round down.


The following rates for 2012-13 apply from 1 July 2012.

Taxable income   Tax on this income
0 - $18,200     Nil
$18,201 - $37,000       19c for each $1 over $18,200
$37,001 - $80,000       $3,572 plus 32.5c for each $1 over $37,000
$80,001 - $180,000      $17,547 plus 37c for each $1 over $80,000
$180,001 and over       $54,547 plus 45c for each $1 over $180,000

The tax table is from ATO: http://www.ato.gov.au/content/12333.htm

Example Data
Employee annual salary is 60,050, super rate is 9%, how much will this employee be paid for the month of March ?
•       pay period = Month of March (01 March to 31 March)
•       gross income = 60,050 / 12 = 5,004.16666667 (round down) = 5,004
•       income tax = (3,572 + (60,050 - 37,000) x 0.325) / 12  = 921.9375 (round up) = 922
•       net income = 5,004 - 922 = 4,082
•       super = 5,004 x 9% = 450.36 (round down) = 450

Here is the csv input and output format we provide. (But feel free to use any format you want)

Input (first name, last name, annual salary, super rate (%), payment start date):
David,Rudd,60050,9%,01 March – 31 March
Ryan,Chen,120000,10%,01 March – 31 March

Output (name, pay period, gross income, income tax, net income, super):
David Rudd,01 March – 31 March,5004,922,4082,450
Ryan Chen,01 March – 31 March,10000,2696,7304,1000

As part of your solution:
•       List any assumptions that you have made in order to solve this problem.
•       Provide instruction on how to run the application
•       Provide a test harness to validate your solution.

Assumptions:
===
 - salaries are calculated on a monthly basis - and cover a month irrespective of start day (eg: March 3rd - April 2nd)
 - input CSV file must have header fields with exact values of: first-name,last-name,annual-salary,super-rate,payment-start-date
 - configuration is given via environment variable: PAYSLIP_CFG and consists of data specified in EDN format (as can be seen in resources/config.edn)
 - rows with erroneous values will have information about those errors output
 - maximum income is capped at $9999999999999

Running the application:
===
Install Java 7 or higher.
Install leiningen (download https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein, make it executable and place on your PATH)
Set the PAYSLIP_CFG environment variable to the values required (your changes here would currently only specify input-file-name and report-file-name - to indicate input and output file locations), eg:
export PAYSLIP_CFG='{
                      :tax-rates [
                                   {:lower-bound 0 :upper-bound 18200 :base-amount 0 :percentage 0.0}
                                   {:lower-bound 18201 :upper-bound 37000 :base-amount 0 :percentage 0.19}
                                   {:lower-bound 37001 :upper-bound 80000 :base-amount 3572 :percentage 0.325}
                                   {:lower-bound 80000 :upper-bound 180000 :base-amount 17547 :percentage 0.37}
                                   {:lower-bound 180001 :upper-bound 9999999999999 :base-amount 54547 :percentage 0.45}
                                 ]
                      :number-of-periods-per-year 12
                      :headers [:name :pay-period :gross-income :income-tax :net-income :super]
                      :header-names {:name "name" :pay-period "pay period" :gross-income "gross income"
                                     :income-tax "income tax" :net-income "net income" :super "super"}
                      :input-file-name "input.csv"
                      :report-file-name "payslips.csv"
                    }'
You can run it either:
 - using leiningen (from the root directory containing project.clj): ```lein run```
 - or create an executable by executing (again from the root directory containing project.clj): ```lein uberjar```
   and then running it with java: java -jar target/payslip-0.0.1-standalone.jar

Testing:
===
The application is tested using midje.
To run the full test suite just run (from the root directory containing project.clj): ```lein midje```

