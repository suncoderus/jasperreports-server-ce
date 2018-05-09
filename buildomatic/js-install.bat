@ECHO OFF

rem ///
rem /// JasperReports Server Pro installation script.
rem ///
rem /// Usage: js-install.bat {option:(<EMPTY>|minimal|drop-db|regen-config|test)}
rem ///

CALL "bin/do-js-install.bat" pro %*
