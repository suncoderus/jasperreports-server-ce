
REM
REM usage:      run-sed-on-directory  <root-dir-path>
REM
REM example:    ./run-sed-on-directory.bat  my-work-dir
REM

set ARG1=%1
echo ARG1=%ARG1%


REM
REM Add bitrock tags
REM

sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Dashboard.xml > %ARG1%/resources/supermart/Dashboard.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/CustomerDashboard.xml > %ARG1%/resources/supermart/Details/CustomerDashboard.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/CustomerDetail.xml > %ARG1%/resources/supermart/Details/CustomerDetail.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/CustomerState.xml >  %ARG1%/resources/supermart/Details/CustomerState.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/ProductDetail.xml >  %ARG1%/resources/supermart/Details/ProductDetail.xml-OUT

sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/PromotionDetail.xml > %ARG1%/resources/supermart/Details/PromotionDetail.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/Details/TopFives.xml > %ARG1%/resources/supermart/Details/TopFives.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/RevenueAndProfit/RevenueByStoreTypePieChart.xml > %ARG1%/resources/supermart/RevenueAndProfit/RevenueByStoreTypePieChart.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/RevenueAndProfit/SalesGauges.xml > %ARG1%/resources/supermart/RevenueAndProfit/SalesGauges.xml-OUT
sed -f sed-mod-datasource.sed %ARG1%/resources/supermart/SalesByMonth/SalesByMonth.xml > %ARG1%/resources/supermart/SalesByMonth/SalesByMonth.xml-OUT

REM
REM Copy files back to orig names
REM

mv %ARG1%/resources/supermart/Dashboard.xml-OUT %ARG1%/resources/supermart/Dashboard.xml
mv %ARG1%/resources/supermart/Details/CustomerDashboard.xml-OUT %ARG1%/resources/supermart/Details/CustomerDashboard.xml
mv %ARG1%/resources/supermart/Details/CustomerDetail.xml-OUT %ARG1%/resources/supermart/Details/CustomerDetail.xml
mv %ARG1%/resources/supermart/Details/CustomerState.xml-OUT   %ARG1%/resources/supermart/Details/CustomerState.xml
mv %ARG1%/resources/supermart/Details/ProductDetail.xml-OUT   %ARG1%/resources/supermart/Details/ProductDetail.xml

mv %ARG1%/resources/supermart/Details/PromotionDetail.xml-OUT  %ARG1%/resources/supermart/Details/PromotionDetail.xml
mv %ARG1%/resources/supermart/Details/TopFives.xml-OUT  %ARG1%/resources/supermart/Details/TopFives.xml
mv %ARG1%/resources/supermart/RevenueAndProfit/RevenueByStoreTypePieChart.xml-OUT  %ARG1%/resources/supermart/RevenueAndProfit/RevenueByStoreTypePieChart.xml
mv %ARG1%/resources/supermart/RevenueAndProfit/SalesGauges.xml-OUT  %ARG1%/resources/supermart/RevenueAndProfit/SalesGauges.xml
mv %ARG1%/resources/supermart/SalesByMonth/SalesByMonth.xml-OUT  %ARG1%/resources/supermart/SalesByMonth/SalesByMonth.xml





echo Done