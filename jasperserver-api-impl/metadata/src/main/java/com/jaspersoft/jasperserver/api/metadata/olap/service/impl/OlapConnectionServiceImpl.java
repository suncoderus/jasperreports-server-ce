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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationDetailImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationResultImpl;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.common.util.StaticCharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.JndiFallbackResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.XMLATestResult;
import com.jaspersoft.jasperserver.api.metadata.olap.service.XMLATestResult.XMLATestCode;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.tags.MondrianOlapModelTag;
import com.tonbeller.jpivot.tags.OlapModelProxy;
import com.tonbeller.jpivot.xmla.XMLA_Model;
import com.tonbeller.jpivot.xmla.XMLA_OlapModelTag;
import com.tonbeller.wcf.controller.RequestContext;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.MondrianException;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.spi.CatalogLocator;
import mondrian.util.Pair;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.OlapConnection;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

/**
 * @author sbirney
 *         $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OlapConnectionServiceImpl implements OlapConnectionService, ReportDataSourceServiceFactory {

    /**
     * property keys used for olap4j connection
     */
    public static final String OLAP4J_DRIVER = "olap4jDriver";
    public static final String OLAP4J_URL_PREFIX = "urlPrefix";
    public static final String XMLA_USER = "user";
    public static final String XMLA_PASSWORD = "password";

    private static final String OLAP_CONNECTION_JNDI_DATA_SOURCE = "DataSource";
    private static final String OLAP_CONNECTION_JDBC = "Jdbc";
    private static final String OLAP_CONNECTION_JDBC_USER = "JdbcUser";
    private static final String OLAP_CONNECTION_JDBC_PASSWORD = "JdbcPassword";

    private static final String DATA_SOURCE_INFO = "DataSourceInfo";

    private static final Log log = LogFactory.getLog(OlapConnectionServiceImpl.class);

    private UserAuthorityService userService;
    private TenantService tenantService;
    private JndiFallbackResolver jndiFallbackResolver;
    private JdbcDriverService jdbcDriverService;
    private ProfileAttributesResolver profileAttributesResolver;

    private CatalogLocator repositoryCatalogLocator = new RepositoryCatalogLocator();

    protected String OLAP4J_CACHE = null;
    protected String OLAP4J_CACHE_NAME = null;
    protected String OLAP4J_CACHE_MODE = null;
    protected String OLAP4J_CACHE_TIMEOUT = null;
    protected String OLAP4J_CACHE_SIZE = null;

    @Autowired
    private SecureExceptionHandler secureExceptionHandler;

    /*
     * (non-Javadoc)
     *
     * @see com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService.createOlapModel()
     *
     * @return a newly constructed and configured OlapModel initialize is not
     * yet called.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public OlapModel createOlapModel(ExecutionContext context, OlapUnit olapUnit) {
        OlapClientConnection clientConn = (OlapClientConnection) dereference(
                context, olapUnit.getOlapClientConnection());
        if (clientConn instanceof XMLAConnection) {
            return createXmlaModel(context, olapUnit);
        }
        String mdx = olapUnit.getMdxQuery();
        MondrianConnection conn = (MondrianConnection) clientConn;
        /*
         * FIXME Need to be able to configure the extensions
		 *
		 * URL url; if (config == null) url = getDefaultConfig(); else url =
		 * pageContext.getServletContext().getResource(config);
		 */
        MondrianModel model = null;
        try {
            model = (MondrianModel) ModelFactory
                    .instance(getDefaultMondrianConfig());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        model.setMdxQuery(mdx);
        model.setConnectProperties(getMondrianConnectProperties(context, conn));
        model.setDynLocale(String.valueOf(LocaleContextHolder.getLocale()));

        // Set the catalog locator
        model.setCatalogLocator(repositoryCatalogLocator);

		/*
         * FIXME use of other values?
		 *
		 * mm.setDynresolver(cfg.getDynResolver());
		 * mm.setDynLocale(cfg.getDynLocale()); if
		 * ("false".equalsIgnoreCase(cfg.getConnectionPooling()))
		 * mm.setConnectionPooling(false);
		 * mm.setExternalDataSource(cfg.getExternalDataSource());
		 */
        return model;
    }

    protected URL getDefaultMondrianConfig() {
        return MondrianOlapModelTag.class
                .getResource("/com/tonbeller/jpivot/mondrian/config.xml");
    }

    protected URL getDefaultXMLAConfig() {
        return XMLA_OlapModelTag.class.getResource("config.xml");
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public OlapModel createXmlaModel(ExecutionContext context, OlapUnit xmlaUnit) {
        String mdx = xmlaUnit.getMdxQuery();
        XMLAConnection xmlaConn = (XMLAConnection) dereference(context,
                xmlaUnit.getOlapClientConnection());

        URL url;
        /*
         * if (config == null) url = getClass().getResource("config.xml"); else
		 * url = pageContext.getServletContext().getResource(config);
		 */
        url = getDefaultXMLAConfig();

        // let Digester create a model from config input
        // the config input stream MUST refer to the XMLA_Model class
        // <model class="com.tonbeller.bii.xmla.XMLA_Model"> is required
        Model model;
        try {
            model = ModelFactory.instance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!(model instanceof XMLA_Model))
            throw new JSException(
                    "jsexception.invalid.class.attribute.for.model.tag",
                    new Object[]{getDefaultXMLAConfig()});

        XMLA_Model xmlaModel = (XMLA_Model) model;

        xmlaModel.setCatalog(xmlaConn.getCatalog());

        // if the xmlaConnection metadata object does not specify
        // username or password, then use the login-in user's credentials
        if (lacksAuthentication(xmlaConn)) {
            User user = getCurrentUserDetails();
            String fullyQualifiedName = user.getUsername();
            if (user.getTenantId() != null) {
                fullyQualifiedName += tenantService.getUserOrgIdDelimiter() + user.getTenantId();
            }
            xmlaModel.setUser(fullyQualifiedName);
            xmlaModel.setPassword(user.getPassword());
        } else {
            xmlaModel.setUser(xmlaConn.getUsername());
            xmlaModel.setPassword(xmlaConn.getPassword());
        }

        xmlaModel.setDataSource(xmlaConn.getDataSource());

        xmlaModel.setMdxQuery(mdx);
        xmlaModel.setID(xmlaConn.getCatalog() + "-" + xmlaUnit.hashCode()); // ???
        xmlaModel.setUri(xmlaConn.getURI());

        log.debug("XMLA USERNAME = " + xmlaModel.getUser());
        log.debug("XMLA PASSWORD = " + xmlaModel.getPassword());

        return xmlaModel;
    }

    protected boolean lacksAuthentication(XMLAConnection xmlaConn) {
        return xmlaConn.getUsername() == null || xmlaConn.getPassword() == null
                || xmlaConn.getUsername().equals("")
                || xmlaConn.getPassword().equals("");
    }

	protected MetadataUserDetails getCurrentUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof MetadataUserDetails) {
			return (MetadataUserDetails) auth.getPrincipal();
		}
		return null;
	}

    /*
     * Because of the way the repository works, this version of validate
     * only works if the OlapUnit has already been saved.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public ValidationResult validate(ExecutionContext context, OlapUnit unit) {
        return validate(context, unit, null, null, null);
    }

	/*
     * if your OlapUnit has not yet been saved as a repository resource,
	 * you must call this version of validate and pass in the schema, connection,
	 * and datasource.
	 */

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public ValidationResult validate(ExecutionContext context, OlapUnit unit,
                                     FileResource schema, OlapClientConnection conn,
                                     ReportDataSource dataSource) {
        ValidationResultImpl result = new ValidationResultImpl();
        validateMDX(context, result, unit, schema, conn, dataSource);
        // TODO: validate the datasource as well
        return result;
    }

    /*
     * Because of the way the repository works, this version of validateMDX
     * only works if the OlapUnit has already been saved.
     */
    protected void validateMDX(ExecutionContext context,
                               ValidationResultImpl result, OlapUnit unit) {
        validateMDX(context, result, unit, null, null, null);
    }

    protected void validateMDX(ExecutionContext context,
                               ValidationResultImpl result, OlapUnit unit, FileResource schema,
                               OlapClientConnection conn, ReportDataSource dataSource) {
        MondrianConnection resource = null;
        if (conn instanceof MondrianConnection)
            resource = (MondrianConnection) conn;

        if (resource == null)
            resource = getConnectionResource(context, unit);

        if (resource == null)
            return;

        try {
            mondrian.olap.Connection monConnection = getTestMondrianConnection(context, resource, dataSource, schema);
            monConnection.parseQuery(unit.getMdxQuery());
        } catch (Exception e) {
            ValidationDetailImpl detail = new ValidationDetailImpl();
            detail.setValidationClass(OlapUnit.class);
            detail.setName(unit.getName());
            detail.setLabel(unit.getLabel());
            detail.setResult(ValidationResult.STATE_ERROR);
            detail.setException(e);
            detail.setMessage(e.getMessage());
            result.addValidationDetail(detail);
            log.warn("Validation Failed for Olap Unit: " + unit.getName(), e);
        }

    }

    /*
     * mondrianConnection
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public MondrianConnection getConnectionResource(ExecutionContext context, OlapUnit unit) {
        Resource clientConn = dereference(context, unit.getOlapClientConnection());
        if (clientConn instanceof MondrianConnection) {
            return (MondrianConnection) clientConn;
        }
        if (clientConn instanceof XMLAConnection) {
            // TODO: have to find the matching MondrianXMLADefinition
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public mondrian.olap.Connection getMondrianConnection(ExecutionContext context, String connResourceName) {
        MondrianConnection conn = (MondrianConnection) getRepository().getResource(context, connResourceName);

        if (conn == null) {
            log.error("missing MondrianConnection resource: "
                    + connResourceName);
            throw new JSException(
                    "jsexception.mondrian.no.connection.for.resource",
                    new Object[]{connResourceName});
        }
        return getMondrianConnection(context, conn);
    }

    private mondrian.olap.Connection getMondrianConnection(ExecutionContext context, MondrianConnection conn) {
        return getMondrianConnection(context, conn, null);
    }

    protected mondrian.olap.Connection getMondrianConnection(ExecutionContext context, MondrianConnection conn, ReportDataSource dataSource) {
        Util.PropertyList connectProps = getMondrianConnectProperties(context, conn, dataSource);

        try {
            return DriverManager.getConnection(connectProps, repositoryCatalogLocator);
        } catch (MondrianException e) {
            String dataSourceString = connectProps.get(OLAP_CONNECTION_JNDI_DATA_SOURCE);

            if (ExceptionUtils.indexOfThrowable(e, NoInitialContextException.class) > 0 && dataSourceString != null) {
                Map<String, String> jdbcProperties = jndiFallbackResolver.getJdbcPropertiesMap(dataSourceString);

                try {
                    // Remove JNDI reference.
                    connectProps.remove(OLAP_CONNECTION_JNDI_DATA_SOURCE);

                    // Add JDBC url, username and password.
                    connectProps.put(OLAP_CONNECTION_JDBC, jdbcProperties.get(JndiFallbackResolver.JDBC_URL));
                    connectProps.put(OLAP_CONNECTION_JDBC_USER, jdbcProperties.get(JndiFallbackResolver.JDBC_USERNAME));
                    connectProps.put(OLAP_CONNECTION_JDBC_PASSWORD, jdbcProperties.get(JndiFallbackResolver.JDBC_PASSWORD));

                    return DriverManager.getConnection(connectProps, repositoryCatalogLocator);
                } catch (Throwable t) {
                    throw new JSException("Error getting connection from jndi fallback properties.", t);
                }
            } else {
                throw e;
            }
        }
    }

    protected mondrian.olap.Connection getTestMondrianConnection(ExecutionContext context, MondrianConnection conn,
                                                                 ReportDataSource dataSource, FileResource mondrianSchema) throws Exception {
        Util.PropertyList connectProps = getMondrianConnectProperties(context, conn, dataSource);
        if (mondrianSchema != null && mondrianSchema.getData() != null) {
            //Remove Catalog Uri since Catalogcontent
            connectProps.remove(RolapConnectionProperties.Catalog.toString());
            connectProps.put(RolapConnectionProperties.CatalogContent.toString(), new String(mondrianSchema.getData(),
                    "UTF-8"));
        }

        return DriverManager.getConnection(connectProps, null);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void initializeAndShow(OlapModelProxy omp, String viewUri,
                                  OlapModel model, OlapUnit unit) throws Exception {
        omp.initializeAndShow(viewUri, model);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public OlapModel initializeOlapModel(ExecutionContext executionContext,
                                         OlapUnit olapUnit, HttpSession sess) {

        RequestContext context = RequestContext.instance();

        OlapModel model = createOlapModel(executionContext, olapUnit);

        if (model == null) {
            throw new JSException("jsexception.no.olap.model.created.for",
                    new Object[]{olapUnit.getURIString()});
        }

        model = (OlapModel) model.getTopDecorator();
        model.setLocale(context.getLocale());
        //Set right locale
        if (sess.getServletContext() != null) {
            sess.getServletContext().setAttribute("locale", LocaleContextHolder.getLocale());
        }
        model.setServletContext(sess.getServletContext());
        model.setID(olapUnit.getURIString());

		/*
         ClickableExtension ext = (ClickableExtension) model.getExtension(ClickableExtension.ID);
		 if (ext == null) {
		 ext = new ClickableExtensionImpl();
		 model.addExtension(ext);
		 }
		 ext.setClickables(clickables);
		 */
        // stackMode
        OlapModelProxy omp = OlapModelProxy.instance(olapUnit.getURIString(),
                sess, false);
        /*	    if (queryName != null)
         omp.initializeAndShow(queryName, model);
		 else
		 */
        try {
            initializeAndShow(omp, olapUnit.getURIString(), model, olapUnit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return omp;
    }

    // this could be a general repository method too...
    @Transactional(propagation = Propagation.REQUIRED)
    public String getFileResourceData(ExecutionContext context,
                                      FileResource file) {
        RepositoryService rep = getRepository();
        StringBuffer fileString = new StringBuffer();
        InputStream data;
        if (file.hasData()) {
            data = file.getDataStream();
        } else {
            log.debug("FILE URI STRING = " + file.getURIString());
            FileResourceData resourceData = rep.getResourceData(context, file
                    .getURIString());
            data = resourceData.getDataStream();
        }
        log.debug("FILE = " + file);

        // use character encoding
        String encoding = getEncodingProvider().getCharacterEncoding();
        String line;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(data,
                    encoding));
            while ((line = in.readLine()) != null) {
                fileString.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileString.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Util.PropertyList getOlapConnectProperties(ExecutionContext context, OlapClientConnection conn) {
        if (conn instanceof MondrianConnection) {
            log.debug("go fetch Mondrian Connection Properties");
            return getMondrianConnectProperties(context, (MondrianConnection) conn, null);

        } else if (conn instanceof XMLAConnection) {
            log.debug("go fetch XMLConnection Properties");
            return getXMLAConnectProperties(context, (XMLAConnection) conn);

        } else {
            throw new IllegalArgumentException("unknown repo connection type " + conn.getClass().getName());
        }
    }

    /**
     * from an xml/a connection, get a prop list suitable for olap4j
     *
     * @param context
     * @param conn
     */
    private Util.PropertyList getXMLAConnectProperties(ExecutionContext context, XMLAConnection conn) {
        //Resolve Profile Attribute for XML/A Connection
        conn = profileAttributesResolver.mergeResource(conn);

        Util.PropertyList connectProps = new Util.PropertyList();
        connectProps.put(XmlaOlap4jDriver.Property.SERVER.toString(), conn.getURI());
        connectProps.put(XmlaOlap4jDriver.Property.CATALOG.toString(), conn.getCatalog());


        /////////////////////////////////////////////////////
        //
        // http://bugzilla.jaspersoft.com/show_bug.cgi?id=22563
        // 2011-04-19  thorick   Be sure to append any TenantID here
        //                       else the returned DataSource names won't match
        //                       what the XMLA Olap4j Connection expects to see.
        //
        String dataSource = conn.getDataSource();
// This code breaks XMLA connectivity to non JRS data sources, so it has to go
//        MetadataUserDetails userDetails = getCurrentUserDetails();
//        String username = null;
//        String tenantId = null;
//        if (userDetails != null) {
//          username = userDetails.getUsername();
//          tenantId = userDetails.getTenantId();
//
//          dataSource = dataSource + "TenantID=" + tenantId + ";";
//          log.debug("Appending TenantId to DataSource name '"+dataSource+"'");
//        }


        connectProps.put(XmlaOlap4jDriver.Property.DATABASE.toString(), dataSource);
        connectProps.put(XMLA_USER, conn.getUsername());
        connectProps.put(XMLA_PASSWORD, conn.getPassword());
        // what olap4j driver & prefix?
        connectProps.put(OLAP4J_DRIVER, "org.olap4j.driver.xmla.XmlaOlap4jDriver");
        connectProps.put(OLAP4J_URL_PREFIX, "jdbc:xmla:");
        return connectProps;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Util.PropertyList getMondrianConnectProperties(
            ExecutionContext context, MondrianConnection conn) {
        return getMondrianConnectProperties(context, conn, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Util.PropertyList getMondrianConnectProperties(
            ExecutionContext context, MondrianConnection conn,
            ReportDataSource dataSource) {

        // assemble a mondrian connection PropertyList
        if (dataSource == null) {
            dataSource = (ReportDataSource) dereference(context, conn.getDataSource());
            if (dataSource == null) {
                throw new JSException("null data source on dereference of mondrian connection " + conn.getURIString() + " for " +
                        (conn.getDataSource().isLocal() ? "local: " + conn.getDataSource().getLocalResource().getURIString()
                                : conn.getDataSource().getReferenceURI()));
            }
        }

        Util.PropertyList connectProps = new Util.PropertyList();

        connectProps.put(RolapConnectionProperties.Provider.toString(), "mondrian");
        connectProps.put(OLAP4J_DRIVER, "mondrian.olap4j.MondrianOlap4jDriver");
        connectProps.put(OLAP4J_URL_PREFIX, "jdbc:mondrian:");
        connectProps.put(RolapConnectionProperties.Locale.toString(), LocaleContextHolder.getLocale().toString());

        /*
         * The URI here has to be an "internal" representation (in multi-tenancy terms)
         * so that the Mondrian schema cache has the right key to flush.
         */
        String transformedUri = repositoryCatalogLocator.locate(transformUri(conn.getSchema().getReferenceURI()));

        transformedUri =
                connectProps.put(RolapConnectionProperties.Catalog.toString(), transformedUri);

        // writing a DynamicSchemaProcessor looks like the way to update schema
        // on the fly
        //  connectProps.put(RolapConnectionProperties.DynamicSchemaProcessor.toString(), "NONE");
        // To cope with changes in the underlying schema
        connectProps.put(RolapConnectionProperties.UseContentChecksum.toString(), useContentChecksum);

        //Resolve data source attributes
        dataSource = profileAttributesResolver.mergeResource(dataSource);

        if (dataSource instanceof JdbcReportDataSource) {
            JdbcReportDataSource jdbcDataSource = (JdbcReportDataSource) dataSource;
            connectProps.put(RolapConnectionProperties.Jdbc.toString(),
                    jdbcDataSource.getConnectionUrl());
            String driverClassName = jdbcDataSource.getDriverClass();
            connectProps.put(RolapConnectionProperties.JdbcDrivers.toString(),
                    driverClassName);

            try {
                // load the driver- may not have been done, mondrian expects it
                log.info("Loading jdbc driver: " + driverClassName);
                jdbcDriverService.register(driverClassName);
            } catch (ClassNotFoundException cnfe) {
                log.error("CANNOT LOAD DRIVER: " + driverClassName);
                throw new RuntimeException(cnfe);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (jdbcDataSource.getUsername() != null && jdbcDataSource.getUsername().trim().length() > 0) {
                connectProps.put(RolapConnectionProperties.JdbcUser.toString(),
                        jdbcDataSource.getUsername());
            }

            if (jdbcDataSource.getPassword() != null && jdbcDataSource.getPassword().trim().length() > 0) {
                connectProps.put(RolapConnectionProperties.JdbcPassword.toString(), jdbcDataSource.getPassword());
            }

        } else {
            // We have a JNDI data source
            JndiJdbcReportDataSource jndiDataSource = (JndiJdbcReportDataSource) dataSource;

            String jndiURI = "";

            if (jndiDataSource.getJndiName() != null && !jndiDataSource.getJndiName().startsWith("java:")) {
                try {
                    Context ctx = new InitialContext();
                    ctx.lookup("java:comp/env/" + jndiDataSource.getJndiName());
                    jndiURI = "java:comp/env/";
                } catch (NamingException e) {
                    //Added as short time solution due of http://bugzilla.jaspersoft.com/show_bug.cgi?id=26570.
                    //The main problem - this code executes in separate tread (non http).
                    //Jboss 7 support team recommend that you use the non-component environment namespace for such situations.
                    try {
                        Context ctx = new InitialContext();
                        ctx.lookup(jndiDataSource.getJndiName());
                        jndiURI = "";

                    } catch (NamingException ex) {

                    }
                }
            }
            jndiURI = jndiURI + jndiDataSource.getJndiName();
            connectProps.put(RolapConnectionProperties.DataSource.toString(), jndiURI);
        }

        if (log.isDebugEnabled()) {
            log.debug("connection properties prepared: " + connectProps);
        }

        // TODO get from web context and metadata
        // + "RoleXX='California manager';";
        return connectProps;
    }

    /**
     * No transformation of URIs in Community Edition
     *
     * @param uri
     * @return
     */
    protected String transformUri(String uri) {
        return uri;
    }

    /* should something like this be part of the repository api? */
    @Transactional(propagation = Propagation.REQUIRED)
    public Resource dereference(ExecutionContext context, ResourceReference ref) {
        if (ref.isLocal())
            return ref.getLocalResource();
        // resources used by olap objects can be accessible with execute-only perms
        context = ExecutionContextImpl.getRuntimeExecutionContext(context);
        return getRepository().getResource(context, ref.getReferenceURI());
    }

    /**
     * saveResource creates path of folders as necessary and put the resource in
     * the bottommost folder does not update if the target already exists. maybe
     * this can be added to the RepositoryService API?
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveResource(ExecutionContext context, String path,
                             Resource resource) {
        RepositoryService rep = getRepository();

        // check if the target already exists
        String targetUri = path
                + (path.endsWith(Folder.SEPARATOR) ? "" : Folder.SEPARATOR)
                + resource.getName();
        if (rep.resourceExists(context, targetUri)) {
            return;
        }

        Folder folder = mkdirs(context, path);
        resource.setParentFolder(folder);
        rep.saveResource(context, resource);
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Folder mkdirs(ExecutionContext context, String path) {
        RepositoryService rep = getRepository();

        // travel down the elements of the path
        String[] splitPath = path.split(Folder.SEPARATOR);
        String folderName = "/"; // start with root
        Folder parentFolder = null; // root's parent is null
        Folder folder = rep.getFolder(context, folderName);
        for (int i = 0; i < splitPath.length; i++) {
            log.debug("Current path element is " + splitPath[i]);
            if ("".equals(splitPath[i])) {
                continue; // ignore extra slashes
            }
            log.debug("Folder name '" + folderName + "' yields folder '" + folder + "'");
            if (!folderName.equals("/")) {
                folderName += "/";
            }
            folderName += splitPath[i];
            parentFolder = folder; // remember parent
            folder = rep.getFolder(context, folderName);
            if (folder == null) {
                folder = new FolderImpl();
                folder.setName(splitPath[i]);
                folder.setLabel(splitPath[i]);
                folder.setDescription(splitPath[i] + " folder");
                folder.setParentFolder(parentFolder);
                rep.saveFolder(context, folder);
            }
        }
        log.debug("Folder name '" + folderName + "' yields folder '" + folder
                + "'");
        return folder;
    }

    // PROPERTIES

    private RepositoryService mRepository;

    private String useContentChecksum;

    public RepositoryService getRepository() {
        return mRepository;
    }

    public void setRepository(RepositoryService repository) {
        mRepository = repository;
    }

    public String getUseContentChecksum() {
        return useContentChecksum;
    }

    public void setUseContentChecksum(String useContentChecksum) {
        this.useContentChecksum = useContentChecksum;
    }

    private StaticCharacterEncodingProvider encodingProvider;

    /**
     * returns character encoding provided by jaspersoft
     *
     * @return
     */
    public StaticCharacterEncodingProvider getEncodingProvider() {
        return encodingProvider;
    }

    /**
     * sets character encoding provided by jaspersoft
     *
     * @param encodingProviderIn
     */
    public void setEncodingProvider(
            StaticCharacterEncodingProvider encodingProviderIn) {
        encodingProvider = encodingProviderIn;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public ReportDataSourceService createService(ReportDataSource dataSource) {
        ReportDataSourceService dsService;
        if (dataSource instanceof MondrianConnection) {
            MondrianConnection mondrianConnection = (MondrianConnection) dataSource;
            Connection connection = getMondrianConnection(null,
                    mondrianConnection);
            dsService = new MondrianConnectionDataSourceService(connection);
        } else if (dataSource instanceof XMLAConnection) {
            XMLAConnection xmlaConnection = (XMLAConnection) dataSource;
            String tenantSeparator = (tenantService != null) ? tenantService.getUserOrgIdDelimiter() : null;
            if (lacksAuthentication(xmlaConnection)) {
                User user = getCurrentUserDetails();
                dsService = new XmlaConnectionDataSourceService(xmlaConnection, tenantSeparator, user);
            } else {
                dsService = new XmlaConnectionDataSourceService(xmlaConnection, tenantSeparator);
            }
        } else {
            throw new JSException("jsexception.invalid.olap.datasource",
                    new Object[]{dataSource.getClass()});
        }
        return dsService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OlapConnection getOlapConnection(ExecutionContext context, String resourceName) {
        OlapClientConnection conn = (OlapClientConnection) getRepository().getResource(context, resourceName);
        return getOlapConnection(context, conn);
    }

    public OlapConnection getOlapConnection(ExecutionContext context, OlapClientConnection conn) {
        Util.PropertyList propList = getOlapConnectProperties(context, conn);

        Properties props = new Properties();
        String driverClass = null;
        String urlPrefix = null;
        for (Pair<String, String> pair : propList) {
            if (pair.getKey().equals(OLAP4J_DRIVER)) {
                driverClass = pair.getValue();
            } else if (pair.getKey().equals(OLAP4J_URL_PREFIX)) {
                urlPrefix = pair.getValue();
            } else {
                props.put(pair.getKey(), pair.getValue());
            }
        }

        //OLAP4J cache configuration
        if (getOLAP4J_CACHE() != null && getOLAP4J_CACHE().length() > 0) {
            props.put("Cache", getOLAP4J_CACHE());
            props.put("Cache.Name", getOLAP4J_CACHE_NAME());
            props.put("Cache.Mode", getOLAP4J_CACHE_MODE());
            props.put("Cache.Timeout", getOLAP4J_CACHE_TIMEOUT());
            props.put("Cache.Size", getOLAP4J_CACHE_SIZE());
        }

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();

            sb.append("OlapConnectionProperties Full Set: ");
            for (Pair<String, String> pair : propList) {
                sb.append("key='" + pair.getKey() + "', val='" + pair.getValue() + "', ");
            }

            sb.append("\nOlapConnectionProperties Driver Set: ");
            for (Object key : props.keySet()) {
                sb.append("key='" + key + "', val='" + props.get(key) + "', ");
            }

            sb.append("\nDriver Class='" + (driverClass == null ? "NULL" : driverClass) + "'");
            sb.append("\n   urlPrefix='" + (urlPrefix == null ? "NULL" : urlPrefix) + "'");

            log.debug(sb.toString());
        }

        // load driver  and Connection
        java.sql.Connection rConnection = null;
        try {
            jdbcDriverService.register(driverClass);
            rConnection = java.sql.DriverManager.getConnection(urlPrefix, props);
        } catch (MondrianException e) {
            String dataSource = (String) props.get(OLAP_CONNECTION_JNDI_DATA_SOURCE);

            if (ExceptionUtils.indexOfThrowable(e, NoInitialContextException.class) > 0 && dataSource != null) {
                Map<String, String> jdbcProperties = jndiFallbackResolver.getJdbcPropertiesMap(dataSource);

                try {
                    // Remove JNDI reference.
                    props.remove(OLAP_CONNECTION_JNDI_DATA_SOURCE);

                    // Add JDBC url, username and password.
                    props.put(OLAP_CONNECTION_JDBC, jdbcProperties.get(JndiFallbackResolver.JDBC_URL));
                    props.put(OLAP_CONNECTION_JDBC_USER, jdbcProperties.get(JndiFallbackResolver.JDBC_USERNAME));
                    props.put(OLAP_CONNECTION_JDBC_PASSWORD, jdbcProperties.get(JndiFallbackResolver.JDBC_PASSWORD));

                    rConnection = java.sql.DriverManager.getConnection(urlPrefix, props);
                } catch (Throwable t) {
                    throw new JSException("Error getting connection from jndi fallback properties.", t);
                }
            } else {
                throw e;
            }
        } catch (Throwable t) {
            throw new JSException("error loading olap4j driver and getting Connection '" + driverClass + "'", t);
        }

        ((OlapConnection) rConnection).setLocale(LocaleContextHolder.getLocale());

        return (OlapConnection) rConnection;
    }

    public XMLATestResult testConnection(ExecutionContext context, XMLAConnection xmlaConnection) {
        try {
            OlapConnectionService service = this;
            OlapConnection connection = service.getOlapConnection(context, xmlaConnection);
            ResultSet rs;

            XMLATestResult testResult;

            try {
                rs = connection.getMetaData().getDatabases();
            } catch (Exception e) {
                Throwable ee = e;
                while (ee.getCause() != null && ee.getCause() != ee) {
                    ee = ee.getCause();
                }

                ErrorDescriptor ed = secureExceptionHandler.handleException(e);

                if (ee.getClass().equals(ConnectException.class)
                        || ee.getClass().equals(SocketException.class)
                        || ee.getClass().equals(FileNotFoundException.class)
                        || ee.getClass().equals(UnknownHostException.class)) {
                    return new XMLATestResult(XMLATestCode.URI_CONNECTION_FAILED, ed);
                } else if (ee.getClass().equals(StringIndexOutOfBoundsException.class)) {
                    return new XMLATestResult(XMLATestCode.BAD_URI, ed);
                } else if (ee.getClass().equals(IOException.class)) {
                    if (ee.getMessage().contains("401")) {
                        return new XMLATestResult(XMLATestCode.BAD_CREDENTIALS, ed);
                    } else {
                        return new XMLATestResult(XMLATestCode.BAD_URI, ed);
                    }
                } else {
                    return new XMLATestResult(XMLATestCode.OTHER, ed);
                }
            }

            try {
                Schema schema = connection.getOlapSchema();
            } catch (Exception e) {
                ErrorDescriptor ed = secureExceptionHandler.handleException(e);
                if (e.getMessage().contains("No datasource could be found")) {
                    LinkedList<String> options = new LinkedList<String>();
                    try {
                        while (rs.next()) {
                            options.add(rs.getString(1));
                        }
                    } catch (Exception ee) {
                        //do nothing
                    }
                    return new XMLATestResult(XMLATestCode.BAD_DATASOURCE, options.toArray(new String[0]), ed);
                } else if (e.getMessage().contains("There is no catalog named")) {
                    LinkedList<String> options = new LinkedList<String>();
                    for (Catalog cat : connection.getOlapCatalogs()) {
                        options.add(cat.getName());
                    }
                    return new XMLATestResult(XMLATestCode.BAD_CATALOG, options.toArray(new String[0]), ed);
                } else {
                    return new XMLATestResult(XMLATestCode.OTHER, ed);
                }
            }
            return new XMLATestResult(XMLATestCode.OK);
        } catch (Exception e) {
            return new XMLATestResult(XMLATestCode.OTHER, secureExceptionHandler.handleException(e));
        }
    }

    //
    // Entry point for unit tests
    //
    public mondrian.olap.Connection getOlap4jMondrianConnection(ExecutionContext context, String resourceName) {
        OlapConnection oConn = getOlapConnection(context, resourceName);
        if (oConn != null) {
            return getOlap4jMondrianConnectionFromOlapConnection(oConn);
        }
        return null;    //  we should have Exceptioned out before reaching here
    }


    protected mondrian.olap.Connection getOlap4jMondrianConnectionFromOlapConnection(OlapConnection oConn) {
        try {
            return (mondrian.olap.Connection) ((java.sql.Wrapper) oConn).unwrap(mondrian.olap.Connection.class);
        } catch (Exception e) {
            throw new JSException("could not obtain mondrian.olap.Connection from '" + oConn + "' " + e.getMessage());
        }
    }


    public UserAuthorityService getUserService() {
        return userService;
    }

    public void setUserService(UserAuthorityService userService) {
        this.userService = userService;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Searches all members of a Class until a member of the given type
     * is found.
     * <p/>
     * Returns a reference to that Field or NULL if none found
     * <p/>
     * WARNING:  this method will return the FIRST found member of the specified
     * type.  Be sure that this is what you want.
     * IF there are multiple members of type 'A', you will get the first one.
     *
     * @param cl
     * @param typeName
     * @return
     */
    public static Field getFieldByTypeName(Class cl,
                                           String typeName) {
        // Check we have valid arguments
        assert (cl != null);
        assert (typeName != null);

        final Field fields[] =
                cl.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (typeName.equals(fields[i].getType().getName())) {

                fields[i].setAccessible(true);
                return fields[i];
            }
        }
        return null;
    }

    public void setJndiFallbackResolver(JndiFallbackResolver jndiFallbackResolver) {
        this.jndiFallbackResolver = jndiFallbackResolver;
    }


    public String getOLAP4J_CACHE() {
        return OLAP4J_CACHE;
    }

    public void setOLAP4J_CACHE(String oLAP4J_CACHE) {
        OLAP4J_CACHE = oLAP4J_CACHE;
    }

    public String getOLAP4J_CACHE_NAME() {
        return OLAP4J_CACHE_NAME;
    }

    public void setOLAP4J_CACHE_NAME(String oLAP4J_CACHE_NAME) {
        OLAP4J_CACHE_NAME = oLAP4J_CACHE_NAME;
    }

    public String getOLAP4J_CACHE_MODE() {
        return OLAP4J_CACHE_MODE;
    }

    public void setOLAP4J_CACHE_MODE(String oLAP4J_CACHE_MODE) {
        OLAP4J_CACHE_MODE = oLAP4J_CACHE_MODE;
    }

    public String getOLAP4J_CACHE_TIMEOUT() {
        return OLAP4J_CACHE_TIMEOUT;
    }

    public void setOLAP4J_CACHE_TIMEOUT(String oLAP4J_CACHE_TIMEOUT) {
        OLAP4J_CACHE_TIMEOUT = oLAP4J_CACHE_TIMEOUT;
    }

    public String getOLAP4J_CACHE_SIZE() {
        return OLAP4J_CACHE_SIZE;
    }

    public void setOLAP4J_CACHE_SIZE(String oLAP4J_CACHE_SIZE) {
        OLAP4J_CACHE_SIZE = oLAP4J_CACHE_SIZE;
    }

    public JdbcDriverService getJdbcDriverService() {
        return jdbcDriverService;
    }

    public void setJdbcDriverService(JdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }

    public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
        this.profileAttributesResolver = profileAttributesResolver;
    }
}
