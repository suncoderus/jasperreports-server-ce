jasperserver-remote-tests/test-backup/readme.txt
------------------------------------------------

2013-10-17
----------

Additional details in bug: 

  http://bugzilla.jaspersoft.com/show_bug.cgi?id=35048

The tests that are in this folder were originally in the following location

  jasperserver-rest/src/main/java

However, these test classes really are test classes. They require the junit
jar in order to compile. But, we want to make the junit jar have a <scope> of
test so that the junit jar is not included into the jasperserver-pro.war file.
This remote-tests folder already has a set of tests that are executed. 

So, in the future the test classes in this folder can be evaluated to see
if they are useful. 

If they are useful they can be incorporated into the logic of this remote-tests
folder. If they are not useful they can be deleted. 

Also this readme.txt file and the test-backup folder can be deleted. 

  



