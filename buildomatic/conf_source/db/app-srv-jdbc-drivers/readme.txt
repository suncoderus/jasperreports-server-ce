conf_source/db/app-srv-jdbc-drivers/readme.txt
----------------------------------------------

Date: 2014-04-21
----------------

This folder is meant to hold the JDBC drivers that are intended to be copied
to the lib folder of the application server. 

Note that we do not distribute the mysql JDBC driver because it is licensed 
under the GPL license and we don't re-distribute 3rd party GPL components. 

Additionally, we are not distributing JDBC drivers from commercial database
applications. 
 
The drivers in this folder will automatically be copied to the application server
as part of the buildomatic deploy-webapp-pro/ce target. 

