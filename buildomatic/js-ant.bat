echo off

REM  
REM chk to see if bundled ant should be used (..\apache-ant or ..\..\apache-ant) 
REM  

set bundledAntPath=..\apache-ant
if exist "..\apache-ant" goto useBundledAnt
set bundledAntPath=..\..\apache-ant
if exist "..\..\apache-ant" goto useBundledAnt 

REM try to use existing ant

echo INFO: No bundled ant found (..\apache-ant or ..\..\apache-ant). Using existing ant

set ANT_RUN=ant

goto :doneAntSetup

:useBundledAnt

set ANT_HOME=%bundledAntPath%
set PATH=%ANT_HOME%\bin;%PATH%
set ANT_RUN=%ANT_HOME%\bin\ant.bat

:doneAntSetup

REM Collect the command line args

set DROP_FIRST=%0

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto :setArgs
:doneSetArgs

%ANT_RUN% -nouserlib -f build.xml %CMD_LINE_ARGS%
