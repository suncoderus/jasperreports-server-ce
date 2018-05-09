@ECHO OFF

:: Windows 2000 and later only
IF NOT "%OS%"=="Windows_NT" GOTO OsErr

:: Remove the following line to use this batch file
:: in Windows NT 4 with the Resource Kit installed
VER | FIND "Windows NT" >NUL && GOTO OsErr

:: Help required?
IF NOT "%~1"=="" GOTO Syntax

:: Enable Command Extensions and use local variables
VERIFY OTHER 2>nul
SETLOCAL ENABLEEXTENSIONS
IF ERRORLEVEL 1 GOTO CmdExtErr

:: Export registry settings to a temporary file
START /W REGEDIT /E "%Temp%.\_TimeTemp.reg" "HKEY_CURRENT_USER\Control Panel\International"
:: Read the time format from the exported data
FOR /F "tokens=1* delims==" %%A IN ('TYPE "%Temp%.\_TimeTemp.reg" ^| FINDSTR /R /I /B /C:"\"iTime\"="') DO SET iTime=%%~B
:: Thanks for Daniel R. Foster for correcting a typo in the next line
FOR /F "tokens=1* delims==" %%A IN ('TYPE "%Temp%.\_TimeTemp.reg" ^| FINDSTR /R /I /B /C:"\"sTime\"="') DO SET sTime=%%~B
DEL "%Temp%.\_TimeTemp.reg"

:: Store current time in default Locale format
FOR /F "tokens=*" %%A IN ('TIME/T') DO SET Now=%%A

:: Format time depending on registry settings
FOR /F "tokens=1,2* delims=%sTime% " %%A IN ('ECHO %Now%') DO (
	SET Hour=%%A
	SET Mins=%%B
)
:: Translate AM/PM to 24 hours format;
:: A correction was required for Windows XP,
:: thanks for Harry Teufel for finding this bug
ECHO.%Mins%| FIND /I "AM" >NUL && SET AmPm=A
ECHO.%Mins%| FIND /I "PM" >NUL && SET AmPm=P
:: AM/PM can be all upper or mixed case, with or without leading space; we'll just erase all possible characters
SET Mins=%Mins: =%
SET Mins=%Mins:A=%
SET Mins=%Mins:a=%
SET Mins=%Mins:M=%
SET Mins=%Mins:m=%
SET Mins=%Mins:P=%
SET Mins=%Mins:p=%
IF 1%Hour% LSS 20 SET Hour=0%Hour%
IF 1%Mins% LSS 20 SET Mins=0%Mins%
IF %Hour% LSS 12 IF /I "%AmPm%"=="P" SET /A Hour=1%Hour%-88
IF %Hour% EQU 12 IF /I "%AmPm%"=="A" SET    Hour=00
SET Now=%Hour%%sTime%%Mins%

:Sort
FOR /F "tokens=1,2* delims=%sTime% " %%A IN ('ECHO %Now%') DO SET SortTime=%%A-%%B
:: Thanks for Holger Stein who mailed me this correction (add leading zero):
IF %SortTime% LSS 1000 IF %SortTime% GEQ 100 SET SortTime=0%SortTime%

:: Purge local variables except SortTime
ENDLOCAL & SET NOW_HH_MM=%SortTime%
GOTO End

:CmdExtErr
ECHO.
ECHO Command extensions need to be enabled for this batch file to run correctly.
ECHO.
ECHO You can run this batch file using the command:
ECHO.
ECHO     CMD /X %~n0
ECHO.
ECHO to enable command extensions, however, the results will not be saved in
ECHO environment variables that way.
GOTO Syntax

:OsErr
ECHO.
ECHO This batch file requires Windows 2000 or a later version!
ECHO.
ECHO To use this batch file with Windows NT 4 you need to install FINDSTR from
ECHO the Resource Kit, and to remove the NT 4 check from this batch file.
ECHO Read the comment lines in this batch file for details.

:Syntax
ECHO.
ECHO Date utility for Windows NT 4 / 2000 / XP
ECHO Saves current time in HH-mm format
ECHO to NOW_HH_MM environment variable.
ECHO.
ECHO Prepared by Vladimir Tsukur on the basis of
ECHO SortTime batch script.
ECHO.
ECHO See below for original disclaimer notes:
ECHO.
ECHO -----------------------------------------------------------
ECHO.
ECHO SortTime.bat,  Version 3.40 for Windows 2000 / XP
ECHO Displays the time of execution in the system's default time format and in
ECHO hhmm format for sorting purposes.
ECHO.
ECHO Usage:  SORTTIME
ECHO.
ECHO Notes:  [1]  The sorted value is stored in the environment variable SORTTIME.
ECHO              The SORTTIME value is independent of "International", "Regional"
ECHO              or "Locale" settings.
ECHO         [2]  This batch file uses native Windows 2000 commands only.
ECHO              To allow the use in Windows NT 4, make sure FINDSTR from the
ECHO              Resource Kit is installed, and remove the NT 4 check from this
ECHO              batch file. Read the comment lines for details.
ECHO         [3]  KiXtart, Perl, JScript, Rexx and VBScript versions are available
ECHO              at http://www.robvanderwoude.com/datetimenonbatch.html
ECHO.
ECHO Written by Rob van der Woude
ECHO http://www.robvanderwoude.com
ECHO ^(with corrections by Daniel R. Foster, Harry Teufel and Holger Stein^)
ECHO.

:End
:: Done
