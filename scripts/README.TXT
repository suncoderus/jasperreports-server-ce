scripts/README.TXT
------------------

For the 4.0 release, the scripts folder has been deprecated. 

Over the past few releases, most of the functionality and files
formerly found in the scripts folder have been moved to the 
buildomatic set of folders. 


New Folders in Scripts Folder:
==============================

2012-04-12:

Setup JasperServer as a Linux service: 

scripts/linux/

  jasperserver      (example script)
  readme.txt        (info on setting up service)


Folders Moved to Buildomatic Folder:
====================================

Import-Export
-------------

The scripts:

  js-export.bat, js-export.sh
  
  js-import.bat, js-import.bat
  
Have been moved to the following location:

  <install-dir>/buildomatic
  
These scripts run the same way they have in the past.

Run the following commands to view options:

  js-export --help
  
  js-import --help

The configuration files and jars to support these scripts
are found here:

  <install-dir>/buildomatic/conf_source/ieCe  or  iePro
  
  <install-dir>/buildomatic/conf_source/ieCe/lib  or iePro/lib
  
  
JDBC Drivers
------------

All jdbc driver jars are found in the buildomatic folder structure.

For example:

  <install-dir>/buildomatic/conf_source/db/mysql/jdbc
  
  <install-dir>/buildomatic/conf_source/db/postgresql/jdbc
    
  <install-dir>/buildomatic/conf_source/db/oracle/jdbc

  <install-dir>/buildomatic/conf_source/db/sqlserver/jdbc

  <install-dir>/buildomatic/conf_source/db/vertica/jdbc
  
  
Upgrade Scripts
---------------

  <install-dir>/buildomatic/install_resources/sql/mysql
  
  <install-dir>/buildomatic/install_resources/sql/postgresql
    
  etc...
  
  
Database Schema DDL Scripts
---------------------------

Same as upgrade scripts:

  <install-dir>/buildomatic/install_resources/sql/mysql
  
  <install-dir>/buildomatic/install_resources/sql/postgresql
  
  etc...


