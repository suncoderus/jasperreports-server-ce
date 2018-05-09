/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
    tweakWar.js
    This is run by the build of jasperserver-war in jasperserver-pro
    
    It reads in one more more jiconfig files that specify various updates
    to configuration files in the JasperServer OS webapp.
    Current files updated:
        WEB-INF/applicationContext.xml
        WEB-INF/applicationContext-security.xml
        WEB-INF/jasperserver-servlet.xml

example file:
<jiconfig>

    <!-- additions to applicationContext.xml -->

    <!-- this adds an entry to the sessionFactory bean that configures Hibernate -->
    <addHibernateConfig hbmfile="/com/jaspersoft/ji/adhoc/AdhocState.hbm.xml"/>

    <!-- this adds an entry to the persistentMappings bean that maps client classes
        (or interfaces) to their corresponding persisted objects -->
    <addPersistentClassMapping key="com.jaspersoft.ji.adhoc.AdhocState"
            value="com.jaspersoft.ji.adhoc.hibernate.RepoAdhocTableState"/>

    <!-- this adds an entry to the persistentMappings bean that maps client classes
        (or interfaces) to their corresponding persisted objects -->
    <addPersistentClassMapping key="com.jaspersoft.ji.adhoc.AdhocTableState"
            value="com.jaspersoft.ji.adhoc.hibernate.RepoAdhocTableState"/>

    <!-- this adds an entry to the mappingResourceFactory bean that maps client interfaces
         to their corresponding client implementations -->
    <addClientClassMapping key="com.jaspersoft.ji.adhoc.AdhocReportUnit"
                        value="com.jaspersoft.ji.adhoc.AdhocReportUnitImpl"/>

    <!-- adding an MVC JSP
         (updates jasperserver-servlet.xml and applicationContext-security.xml)

         Adds a new entry to the handlerMapping bean which maps URIs to their controllers,
         and adds a new entry to the paramResolver bean which maps it to a jsp under WEB-INF/jsp.
         If uri isn't set, it's assumed to be "/" + jsp + ".html"
         * NOTE: uri currently ignored
         If role is set, adds the uri to the filterInvocationInterceptor in
         applicationContext-security.xml with the given role string.
         This lets non-admins see the page. The roles string is a comma-separated
         list of roles. Current roles are ROLE_USER, ROLE_ADMIN (default), and ROLE_ANONYMOUS. -->
    <addJSP
        jsp="olap/viewOlap"
        uri="/olap/viewOlap.html"
        controllerBean ="olapModelController"
        roles="ROLE_USER,ROLE_ANONYMOUS"/>

    <!-- adds a new filter to one of the filter chains defined in applicationContext-security.xml.
         There are multiple filter chains, each for a different URL pattern.
         filterBean is the name of the bean to insert.
         before is the bean it should come before.
         pattern lets us specify which filter chain. -->
    <addFilter
        filterBean="JILicenseFilter"
        before="exceptionTranslationFilter"
        pattern="/**"/>

    <!-- add a message catalog to the messageSource bean. This name should refer to a properties file
         somewhere on the classpath -->
    <addMessageCatalog path="LicenseMessages"/>

    <!-- eval a js expression on the given file to do any kind of tweak that isn't covered here
         The file is read in and turned into the XML variable 'top' which represents
         the top level node. You can include any number of expressions.-->
    <evalOnFile path="WEB-INF/web.xml>
        <expr>top.* += &lt;something/&gt;</expr>
    </evalOnFile>


    <!-- setting of properties
         right now I'm only letting you do appCon.xml, jasperserver.servlet.xml, and appCon-security.xml
         but I suppose I could do any file
    -->
    <setProperty file="servlet" bean="configurationBean" property="paginationPageSize" value="20"/>

    <!-- add listener class to web.xml
    -->
    <addListener class="my.Listener"/>

    <!-- add Spring bean filter to web.xml
    -->
    <addServletSpringFilter beanName="filterBean" urlPattern="/filtered_path/*"/>

</jiconfig>
*/
importPackage(java.io);

// keep comments and whitespace in
XML.ignoreComments = false;
XML.ignoreWhitespace = false;

function writeFile(path, contents) {
    var fw = new FileWriter(path);
    fw.write(contents);
    fw.close();
}

function clobberDir(path) {
    var dir = new File(path);
    var list = dir.listFiles();
    for (i in list) {
        // gotta love JS syntax...
        list[i]["delete"]();
    }
    dir["delete"]();
}

// this returns a list of files with the given extension
function getFilesByExtension(dir, ext) {
    var filter = new FileFilter({ accept: function(f) { return f.name.endsWith("." + ext) }});
    var files = new File(dir).listFiles(filter);
    var paths = [];
    for (var i in files) {
        paths.push(files[i].path);
    }
    return paths;
}

function saveXML(xml, filename) {
    writeFile(filename, xml.toXMLString());
}

// look up bean, then property, in a spring bean definition file
function beanProperty(root, beanName, propName) {
    var prop = root.beansNS::bean.(@id == beanName).beansNS::property.(@name == propName);
    // if not found, create it
    if (prop.length() == 0) {
        // print("root: " + root.toXMLString());
        // print("bean " + beanName + ": " + root.bean.(@id == beanName).toXMLString());
        var newChild = <property name={propName}/>;
        newChild.setNamespace(beansNS);
        root.beansNS::bean.(@id == beanName).appendChild(newChild);
        prop = root.beansNS::bean.(@id == beanName).beansNS::property.(@name == propName);
    }
    return prop;
}

function debug(str) {
    // print(str);
}

function printStackTrace(exp) {    
    if (exp == undefined) {
        try {
            exp.toString();
        } catch (e) {
            exp = e;
        }
    }
    // note that user could have caught some other
    // "exception"- may be even a string or number -
    // and passed the same as argument. Also, check for
    // rhinoException property before using it
    if (exp instanceof Error && 
        exp.rhinoException != undefined) {
        exp.rhinoException.printStackTrace();
    }
}

var beansNS = new Namespace("http://www.springframework.org/schema/beans");
var wsddNS = new Namespace("http://xml.apache.org/axis/wsdd/");
var noNS = new Namespace("");

var webapp = arguments[0];
var configPath = arguments[1];
var savePath; //  = 'oldfiles';

// file locations
var appConFile = webapp + '/WEB-INF/applicationContext.xml';
var servletFile = webapp + '/WEB-INF/jasperserver-servlet.xml';
var webXmlFile = webapp + '/WEB-INF/web.xml';
var wsDeployFile = webapp + '/WEB-INF/server-config.wsdd';
// var securityFile = webapp + '/WEB-INF/applicationContext-security.xml';

// read in files to xml
var appCon = new XML(readFile(appConFile));
var servlet = new XML(readFile(servletFile));
var webXml = new XML(readFile(webXmlFile));
var wsDeploy = new XML(readFile(wsDeployFile));
// var security = new XML(readFile(securityFile));

try {
    // save before tweaking if there is a savedir
    // we can diff easier because the formatting will be the same
    if (savePath) {
        clobberDir(savePath);
        var saveDir = new File(savePath);
        saveDir.mkdir();
        var a = savePath + "/" + new File(appConFile).name;
        var b = savePath + "/" + new File(servletFile).name;
        // var c = savePath + "/" + new File(securityFile).name;
        saveXML(appCon, a);
        saveXML(servlet, b);
        // saveXML(security, c);
    }

    // file or dir?
    var cfg = new File(configPath);
    if (cfg.directory) {
        configFiles = getFilesByExtension(configPath, 'xml');
    } else {
        configFiles = [ configPath ];
    }

    for (i in configFiles) {
        var configFile = configFiles[i];
        print ("processing config file: " + configFile);
        // read in config to xml
        var config = new XML(readFile(configFile));
        // get list of hbm configs
        var sessionMapperList =
            beanProperty(appCon, 'sessionFactory', 'mappingResources').beansNS::list;

        // get list of hbm configs we want to add and add them
        var hclist = config.addHibernateConfig;
        for (var hc in hclist) {
            var newChild = <value>{hclist[hc].@hbmfile}</value>;
            newChild.setNamespace(beansNS);
            sessionMapperList.appendChild(newChild);
            debug('adding hbm config ' + hclist[hc].@hbmfile);
        }

        // get list of client mappings
        var clientClassMappings =
            beanProperty(appCon, 'mappingResourceFactory', 'implementationClassMappings').beansNS::map;

        // get list of mappings we want to add and add them
        var ccmlist = config.addClientClassMapping;
        for (var ccm in ccmlist) {
            var newChild = <entry key={ccmlist[ccm].@key} value={ccmlist[ccm].@value}/>;
            newChild.setNamespace(beansNS);
            clientClassMappings.appendChild(newChild);
            debug('adding client class mapping: ' + newChild);
        }

        // get list of persistent mappings
        var persistentClassMappings =
            beanProperty(appCon, 'persistentMappings', 'implementationClassMappings').beansNS::map;

        // get list of mappings we want to add and add them
        var pcmlist = config.addPersistentClassMapping;
        for (var pcm in pcmlist) {
            var newChild = <entry key={pcmlist[pcm].@key} value={pcmlist[pcm].@value}/>;
            newChild.setNamespace(beansNS);
            persistentClassMappings.appendChild(newChild);
            debug('adding persistent class mapping: ' + newChild);
        }

        var handlerMappings = beanProperty(servlet, 'urlHandlerMapping', 'properties').beansNS::props;
        var methodMappings = beanProperty(servlet, 'paramResolver', 'mappings').beansNS::props;

        // get JSPs that we want to add
        var newJSPs = config.addJSP;
        for (i in newJSPs) {
            var jsp = newJSPs[i];
            debug('adding new jsp: ' + jsp);
            // construct uri from jsp
            var uri = '/' + jsp.@jsp + '.html';

            var newHandlerMapping = <prop key={ uri }>{ jsp.@controllerBean } </prop>;
            newHandlerMapping.setNamespace(beansNS);
            handlerMappings.appendChild(newHandlerMapping);

            var newMethodMapping = <prop key={ uri }>{ jsp.@jsp } </prop>;
            newMethodMapping.setNamespace(beansNS);
            methodMappings.appendChild(newMethodMapping);

            // add role to filterInvocationInterceptor if present
            /*
            if (jsp.@role) {
                var pageRolesDef = security.
                    beansNS::bean.(@id == 'filterInvocationInterceptor').
                    beansNS::property.(@name == 'objectDefinitionSource').beansNS::value;
                var pageRoles = pageRolesDef.toString().split('\n');
                // add new role before first pattern (contains '=')
                for (i in pageRoles) {
                    if (pageRoles[i].indexOf('=') > -1) {
                        pageRoles.splice(i, 0, uri.toLowerCase() + '=' + jsp.@roles);
                        break;
                    }
                }
                // put roles together again, and put back into XML
                pageRolesDef.* = pageRoles.join('\n');
            }
            */
        }
        // add filter to filter chain in security
        /*
        var newFilters = config.addFilter;
        for (i in newFilters) {
            var newFilter = newFilters[i];
            debug('adding new filter: ' + newFilter);
            var filterDef = beanProperty(
                security, 'filterChainProxy', 'filterInvocationDefinitionSource').beansNS::value;
            var filters = filterDef.toString().split('\n');
            // find filter with matching pattern, and insert new filter before desired location
            for (i in filters) {
                if (filters[i].indexOf(newFilter.@pattern + '=') > -1) {
                    var index = filters[i].indexOf(newFilter.@before);
                    if (index > -1) {
                        filters[i] =
                            filters[i].slice(0, index) +
                            newFilter.@filterBean + ',' +
                            filters[i].slice(index);
                        break;
                    }
                }
            }
            // put filters together again, and put back into XML
            filterDef.* = filters.join('\n');
        }
        */
        
        // add message catalogs to messageSource in appCon
        var newCats = config.addMessageCatalog;
        var msgCatList = beanProperty(appCon, 'messageSource', 'basenames').beansNS::list;
        for (i in newCats) {
            var newCat = newCats[i];
            var newChild = <value>WEB-INF/bundles/{ newCat.@name }</value>;
            newChild.setNamespace(beansNS);
            msgCatList.appendChild(newChild);
        }

        // add internal catalogs to messageSource in appCon
        var newIntCats = config.addInternalCatalog;
        for (i in newIntCats) {
            var newIntCat = newIntCats[i];
            var newChild = <value>WEB-INF/internal/{ newIntCat.@name }</value>;
            newChild.setNamespace(beansNS);
            msgCatList.appendChild(newChild);
        }

        // generic updates of files (powerful but dangerous!)
        for each (var ev in config.evalOnFile) {
            var path = webapp + '/' + ev.@path;
            var top = new XML(readFile(path));
            if (savePath) {
                var saveFile = savePath + "/" + new File(path).name;
                saveXML(top, saveFile);
            }
            for each (var expr in ev.expr) {
                try {
                    eval(expr.toString());
                } catch (e) {
                    print('error evaluating ' + expr + ' in file ' + ev.@path);
            print(e);
                }
            }
            saveXML(top, path);
        }

        // generic updates of properties
        for each (var setProp in config.setProperty) {
            // should map to one of our files (appCon, servlet, security) or else it'll blow up
            // TODO allow child element as value
            // TODO generic adding things to lists, props, maps, etc.
            if (setProp.@file.length() > 0) {
                eval("var file = " + setProp.@file);
                var prop = beanProperty(file, setProp.@bean, setProp.@property);
                prop.@value = setProp.@value;
            } else if (setProp.@path.length() > 0) {
                var path = webapp + '/' + setProp.@path;
                var file = new XML(readFile(path));
                var prop = beanProperty(file, setProp.@bean, setProp.@property);
                prop.@value = setProp.@value;
                saveXML(file, path);
            }
        }
        // web xml stuff, set default namespace correctly
        default xml namespace = webXml.namespace();
        // add listeners to web.xml, after first listener
        for each (var addListener in config.noNS::addListener) {
        	var listenerNode = <listener>
    	            <listener-class>{ addListener.@noNS::className } </listener-class>
        	    </listener>;
        	if (addListener.@noNS::position == 'last') {
	            webXml.insertChildAfter(webXml.listener[webXml.listener.length() - 1], listenerNode);
        	} else {
	            webXml.insertChildBefore(webXml.listener[0], listenerNode);
        	}
        }
        
		// add filters to web.xml, after last filter
		for each (var filter in config.noNS::addServletSpringFilter) {
			var filterNode =
				<filter>
		        	<filter-name>{filter.@noNS::beanName}</filter-name>
					<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    			</filter>;
			var filterMappingNode =
				<filter-mapping>
		        	<filter-name>{filter.@noNS::beanName}</filter-name>
					<url-pattern>{filter.@noNS::urlPattern}</url-pattern>
    			</filter-mapping>;
			webXml.insertChildAfter(webXml.filter[webXml.filter.length() - 1], filterNode);
			webXml.insertChildAfter(webXml.filter[webXml.filter.length() - 1], filterMappingNode);
        }
        
        // set web.xml context param
        // only works if param's already there
        for each (var setContextParam in config.noNS::setContextParam) {
            for each(var p in webXml['context-param']) {
                if (p['param-name'] == setContextParam.@noNS::name) {
                    p['param-value'] = setContextParam.@noNS::value;
                    break;
                }
            }
        }
        for each (var setServletClass in config.noNS::setServletClass) {
            for each(var p in webXml['servlet']) {
                if (p['servlet-name'] == setServletClass.@noNS::name) {
                    p['servlet-class'] = setServletClass.@noNS::className;
                    break;
                }
            }
        }
        for each (var addServlet in config.noNS::addServlet) {
            webXml.appendChild(  <servlet>
                <servlet-name>{addServlet.@noNS::name}</servlet-name>
                <servlet-class>{addServlet.@noNS::className}</servlet-class>
            </servlet>);
            webXml.appendChild(  <servlet-mapping>
                <servlet-name>{addServlet.@noNS::name}</servlet-name>
                <url-pattern>{addServlet.@noNS::urlPattern}</url-pattern>
            </servlet-mapping>);
        }

        //Web Services
        default xml namespace = wsDeploy.namespace();
        for each (var addService in config.noNS::addService) {
            for each (var service in addService.wsddNS::service) {
                wsDeploy.appendChild(service);
            }
        }

        // reset
        default xml namespace = "";
    }

    // write file
    saveXML(appCon, appConFile);
    saveXML(servlet, servletFile);
    saveXML(webXml, webXmlFile);
    saveXML(wsDeploy, wsDeployFile);
    // saveXML(security, securityFile);

} catch (e) {
    printStackTrace(e);
}
