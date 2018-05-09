@echo off

rem Schema Workbench launch script

rem base Mondrian JARs need to be included

set CP=%~dp0lib/commons-dbcp.jar
set CP=%CP%;%~dp0lib/commons-io.jar
set CP=%CP%;%~dp0lib/commons-lang.jar
set CP=%CP%;%~dp0lib/commons-collections.jar
set CP=%CP%;%~dp0lib/commons-pool.jar
set CP=%CP%;%~dp0lib/eigenbase-properties.jar
set CP=%CP%;%~dp0lib/eigenbase-resgen.jar
set CP=%CP%;%~dp0lib/eigenbase-xom.jar
set CP=%CP%;%~dp0lib/javacup.jar
set CP=%CP%;%~dp0lib/log4j.jar
set CP=%CP%;%~dp0lib/mondrian.jar
set CP=%CP%;%~dp0lib/jlfgr.jar
set CP=%CP%;%~dp0lib/commons-math.jar
set CP=%CP%;%~dp0lib/commons-vfs.jar
set CP=%CP%;%~dp0lib/commons-logging.jar
set CP=%CP%;%~dp0lib/olap4j.jar
set CP=%CP%;%~dp0lib/asm-commons.jar
set CP=%CP%;%~dp0lib/asm.jar
set CP=%CP%;%~dp0lib/xercesImpl.jar

rem Workbench GUI code and resources

set CP=%CP%;%~dp0lib/workbench.jar

rem Have a .schemaWorkbench directory for local 

for /F "delims=/" %%i in ('echo %USERPROFILE%') do set ROOT=%%~si

if not exist %ROOT%\.schemaWorkbench mkdir %ROOT%\.schemaWorkbench
if not exist %ROOT%\.schemaWorkbench\log4j.xml copy log4j.xml %ROOT%\.schemaWorkbench
if not exist %ROOT%\.schemaWorkbench\mondrian.properties copy mondrian.properties %ROOT%\.schemaWorkbench

rem put mondrian.properties on the classpath for it to be picked up

set CP=%ROOT%/.schemaWorkbench;%CP%

rem or
rem set the log4j.properties system property 
rem "-Dlog4j.properties=path to <.properties or .xml file>"
rem in the java command below to adjust workbench logging

rem add all needed JDBC drivers to the classpath

for /r %~dp0drivers %%i in ("*.jar") do call %~dp0cpappend %%i

rem add all needed plugin jars to the classpath

for %%i in ("%~dp0plugins\*.jar") do call %~dp0cpappend %%i


java -Xms100m -Xmx500m -cp "%CP%" -Dlog4j.configuration=file:///%ROOT%\.schemaWorkbench\log4j.xml mondrian.gui.Workbench

rem End workbench.bat
