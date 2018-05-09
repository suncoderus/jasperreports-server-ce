Installing Custom Data Source Examples

These examples may be used in both JasperServer or JasperServer Pro. For further information on developing
custom data sources, refer to the JasperServer User's Guide, or the JasperServer Pro Administration Guide.

The examples are found in <js-install>/samples/customDataSource. Once you have deployed a JasperServer web 
application to your application server, you can use Ant to build and deploy the examples. 
If you used an installer to install JasperServer version 2.1 or later, you will have Ant installed already. Ant may be run with 
the following command:

<js-install>/ant/bin/ant <ant-arguments>(for Linux/Unix)
<js-install>\ant\bin\ant <ant-arguments> (for Windows)

If you installed JasperServer manually with a WAR file, you will need to download Ant from http://ant.apache.org. Ant 
1.6.2 was used for testing, but earlier versions should also work.

The JVM used for installing the examples needs to be a full Java Development Kit, because it needs to compile Java source 
files. Ensure that the JAVA_HOME environment variable points to a JDK installation.
The sample directory includes the following files and directories:
*	build.xml: The Ant build file
*	src: Java source directory
*	webapp: A directory containing other files required by the examples, such as JSPs and Spring configuration files, 
    which are copied directly to the JasperServer web application directory
*	reports: A directory containing example JRXML files that use the sample custom data sources

Take the following steps to install the samples in your JasperServer web application (this can be built from the source code 
or the delivered version of JasperServer):
*	At the command line, change directories to the custom data source sample directory (<js-
    install>/samples/customDataSource)
*	Edit build.xml and set the webAppDir property to the root of the JasperServer web application.
*	Run the Ant command (see above) with no arguments, which will execute the default target, which is named 
    deploy. The deploy target will run the following tasks:
    o	Compile the Java source under the src directory
    o	Deploy the compiled Java class files to the JasperServer web application
    o	Deploy files under the webapp directory to the the JasperServer web application
*	Restart the application server

Custom Bean Data Source

The custom bean data source implementation creates a data source from a collection of Java beans declared in the source 
code. Its Spring bean definition file is located in <js-install>/samples/customDataSource/webapp/WEB-
INF/applicationContext-sampleCDS.xml. It An example of a report using this data source is located in <js-
install>/samples/customDataSource/reports/simpleCDS.jrxml.

Webscraper Custom Data Source

The webscraper custom data source implementation can fetch a web page, decode the HTML, and extract selected data 
which is turned into field values in the data source. Its Spring bean definition file is located in <js-
install>/samples/customDataSource/webapp/WEB-INF/applicationContext-webscraperDS.xml. 

These are the configuration items for the datasource:
*	URL: An HTTP URL which refers to the HTML page containing the desired content
*	DOM path: An XPath expression which locates HTML elements to be turned into rows in the data source
*	Field paths: XPath expressions for each field defined in the JRXML which are used to locate the field value within 
each row selected by the DOM path

The implementation creates a data source by taking the following steps:
*	Uses the URL to issue a GET request for an HTML page.
*	Converts the HTML response into XML using JTidy (http://jtidy.sourceforge.net).
*	Uses the DOM path to select XML elements from the converted response.
*	Create a new data source row for each selected element
*	For each field, use the field path to determine the content for each field
*	The data source has two parameters: the URL of the web page, and the XPath which determines that elements in 
    the HTML page become rows in the data source. The parameters can be specified either by a data source definition 
    in the repository or by a query string in the JRXML. 

The example reports for this data source read a web page from http://www.craigslist.org and extract a list of items for sale. 

The file reports/webscrapertest.jrxml has no query defined. Instead, it relies on an instance of the custom data source 
that has been created in the repository. Typical parameters to use with this data source are:
*	URL: http://sfbay.craigslist.org/sfc/cta/
*	DOM Path: /html/body/div[2]/div/p

The file reports/webscraperQEtest.jrxml contains a queryString element which specifies the URL and the DOM 
path. It should be used without defining a data source instance, because JasperServer will not run the query executer for this 
particular implementation if a data source is defined for the report unit.
