buildomatic-additional/install_resources/extra-files/readme.txt
---------------------------------------------------------------

License source will be getting moved to a separate svn project.
Until then build the Pro source by including the js-license-<ver>.jar
as a 3rd party dependency. 

In this folder is a special version of the root pom.xml file
that has been modified to specify this dependency. This 
temp-root-pom.xml file is copied on top of existing root pom.xml. 

ji-license-<ver>.jar should be found in the jasperserver-repo 
maven style source path location. 


