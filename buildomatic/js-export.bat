@echo off

REM Collect the command line args

set JS_CMD_NAME=%0

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

REM Set the config folder to use
REM if Pro config exists, then set to Pro config
REM otherwise, assume ce config

if exist "conf_source\iePro" goto setConfPro
set CONFIG_DIR=conf_source\ieCe
goto doneSetConf
:setConfPro
set CONFIG_DIR=conf_source\iePro
:doneSetConf

REM additional config dir to find the js.jdbc.properties
set ADDITIONAL_CONFIG_DIR=build_conf\default

REM Add all jars to the export classpath variable
set EXP_CLASSPATH=%CONFIG_DIR%\lib\*

REM Set the java command
REM
REM If "..\java\bin\java.exe" exists then use it

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

REM Set the java memory options

set JAVA_OPTS=%JAVA_OPTS% -Xms128m -Xmx512m

REM Add config dirs to EXP_CLASSPATH

set EXP_CLASSPATH=%CONFIG_DIR%;%ADDITIONAL_CONFIG_DIR%;%EXP_CLASSPATH%;.

java -classpath "%EXP_CLASSPATH%" %JAVA_OPTS% com.jaspersoft.jasperserver.export.ExportCommand %JS_CMD_NAME% %CMD_LINE_ARGS%