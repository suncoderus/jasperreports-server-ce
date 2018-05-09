@echo off

REM
REM Collect the command line args
REM

set JS_CMD_NAME=%0

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

REM
REM Add config_dir and classpath to exp_classpath
REM

set CONFIG_DIR=config

set EXP_CLASSPATH=%CONFIG_DIR%;%CLASSPATH%


REM
REM Loop through and add all class jars to exp_classpath
REM
REM Jars are in ..\jasperserver\WEB-INF\lib or lib
REM

if exist "lib" goto setJarsLib

set EXP_CLASSPATH=%EXP_CLASSPATH%;..\jasperserver\WEB-INF\lib\*
REM for %%i in ("..\jasperserver\WEB-INF\lib\*.jar") do call ".\cpappend.bat" %%i

goto :setJarsDone

:setJarsLib

set EXP_CLASSPATH=%EXP_CLASSPATH%;lib\*
REM for %%i in ("lib\*.jar") do call ".\cpappend.bat" %%i

:setJarsDone


REM
REM Set the java command
REM
REM If "..\java\bin\java.exe" exists then use it
REM

if exist "..\java\bin\java.exe" goto setLocalJava

goto setStandardJava
:setLocalJava

set JAVA_HOME="..\java"
set PATH=..\java\bin;%PATH%

goto doneJava
:setStandardJava

if "%JAVA_HOME%"=="" goto warnJava

goto doneJava
:warnJava

echo "WARNING: Did not find a JAVA_HOME environment variable setting. Script will continue."

:doneJava

REM
REM Loop through and add all spring config xmls
REM

set JS_EXPORT_IMPORT_CONFIG=
for %%i in ("%CONFIG_DIR%\applicationContext*.xml") do call ".\configappend.bat" %%i


java -classpath "%EXP_CLASSPATH%" com.jaspersoft.jasperserver.export.RemoveDuplicatedDisplayName %JS_CMD_NAME% --configResources %JS_EXPORT_IMPORT_CONFIG% %CMD_LINE_ARGS%
