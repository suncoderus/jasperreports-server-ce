JI Pseudo Localizer 
===============
There is a utility tool we developed that pseudo-localizes any given bundle file by adding a prefix and suffix to each bundle string. It creates another bundle file as a result. This tool requires two arguments to run: 

   1. The path for localizer-specific property file: This specifies what prefix and suffix to add to the bundle strings, as well as the suffix for the name of the newly created bundle file containing localized strings. 
   2. The path for the bundle file to be localized 

Both files should be in ANSII. 

Localizer Specific Property File 
=========================
This file should define three parameters:

PREFIX : Specifies what string to append to the beginning of any bundle string.
SUFFIX : Specifies what string to append to the end of any bundle string.
BUNDLE_NAME_SUFFIX : Specifies localized filename extension according to standard Java rules. For example, if you pass ja as the value and the bundle file to be localized has a name MyBundle.properties, the pseudo localized bundle strings will be saved in the file named MyBundle_ja.properties . 

This file should be converted to ASCII with native2ascii that comes with JDK: 

     native2ascii -encoding Unicode <filename> <new_filename>

How To Run 
===========
1. Create your localizer specific properties file, similar to localizer.properties in this directory.
2. Run it thru native2ascii as described above and the end result will be similar to loc.properties in this directory, for example:
                  native2ascii -encoding Unicode localizer.properties loc.properties . 
3. Run java -cp . com.jaspersoft.i18n.PseudoLocalizer {localizer_property_file_path} {bundle_file_path_name} , for example:
                  java -cp . com.jaspersoft.i18n.PseudoLocalizer c:\loc.properties c:\MyBundle.properties . 
4. The localized bundle file will be created in the same location of your original bundle file. 

See https://twiki.jaspersoft.com/twiki/bin/view/Main/JiI18NPseudoLocalizer for more info.
