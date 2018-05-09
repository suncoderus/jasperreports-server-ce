buildomatic/conf_source/db/postgresql/jdbc/readme.txt
-----------------------------------------------------

2016-09-09
----------

Added most current JDBC41 driver from the postgresql website

  postgresql-9.4-1210.jdbc41.jar 

Added most current JDBC42 driver from the postgresql website

  postgresql-9.4-1210.jdbc41.jar

2014-04-18
----------

Added most current JDBC41 driver from the postgresql website

  postgresql-9.3-1101.jdbc41.jar
    - should be backwards compatible with postgresql 8.x
    - runs under Java 1.7 only (not Java 1.6)
    - support for JDBC4 (support is limited see postgresql online documentation)
 
NOTE: This new driver is not the default driver currently configured. 
The default driver specified in the buildomatic configurations is the following:

  postgresql-9.2-1002.jdbc4.jar

To change to the most current driver shown above, edit your:

  buildomatic/default_master.properties

Change:

# maven.jdbc.version=9.2-1002.jdbc4

To:

maven.jdbc.version=9.3-1101.jdbc41


2013-03-29
----------

Remove the older 801 jars. 


2013-02-26
----------

Drivers updated to versions 1002: 

  postgresql-9.2-1002.jdbc3.jar
  postgresql-9.2-1002.jdbc4.jar


2011-07-27
----------

Drivers in this folder:

  postgresql-9.0-801.jdbc3.jar
    - backwards compatible with postgresql 8.x
    - compatible with postgresql 9.0
    - runs under Java 1.5 and Java 1.6

  postgresql-9.0-801.jdbc4.jar
    - compatible with postgresql 9.0
    - runs under Java 1.6
    - support for JDBC4 (support is limited see postgresql online documentation)


  http://jdbc.postgresql.org/download.html
    - download info


