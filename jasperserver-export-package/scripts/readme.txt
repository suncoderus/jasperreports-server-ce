jasperserver-export-package/scripts/readme.txt
----------------------------------------------

2010-09-30
----------

- The buildomatic folder is now included in the installer
  Buildomatic is capable of carrying out automated command line
  installations. 
  - It can also handle all aspects of importing and exporting
    (the core configuration files and jar dependencies are held
     in the buildomatic folder).

- Because of this we are moving all command line interaction from
  the scripts folder to the buildomatic folder.

- The key files in this directory are the js-import.* and js-export.*
  files. These files are being moved to the buildomatic folder. 



