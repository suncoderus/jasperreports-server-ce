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

package com.jaspersoft.jasperserver.war.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AwsDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.TibcoDriverManagerImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.VirtualReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.core.util.JSONUtil;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JdkTimeZonesList;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.ReportDataSourceWrapper;
import com.jaspersoft.jasperserver.war.dto.StringOption;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class DataSourceAction extends FormAction implements ApplicationContextAware {
    private static final String ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS = "resourceIdNotSupportedSymbols";
    private static final String RESOURCE_DATA_SOURCE_CONNECTION_STATE_FAILED = "resource.dataSource.connectionState.failed";
    private static final String RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED = "resource.dataSource.connectionState.passed";

    private RepositoryService repository;
    private JasperServerConstImpl constants = new JasperServerConstImpl();
    private JdkTimeZonesList timezones;
    private ResourceFactory dataSourceMappings;
    private List<String> awsRegions;
    private AwsDataSourceRecovery awsDataSourceRecovery;
    private AwsProperties awsProperties;

    private AwsEc2MetadataClient awsEc2MetadataClient;
    private InstanceProductTypeResolver instanceProductTypeResolver;

    public static final String AJAX_RESPONSE_MODEL = "ajaxResponseModel";

    public static final String FORM_OBJECT_KEY="dataResource";
    public static final String JDBC_DRIVERS_JSON_KEY="jdbcDriversJSON";
    public static final String PASSWORD_SUBSTITUTION_KEY="passwordSubstitution";
    public static final String VALIDATION_PATTERNS="validationPatterns";
    public static final String DYNAMIC_URL_PART_PATTERN="dynamicUrlPartPattern";
	public static final String DATASOURCEURI_PARAM = "resource";
	public static final String PARENT_FOLDER_ATTR = "parentFolder";
	public static final String PARENT_FOLDER_URI = "ParentFolderUri";
	public static final String DATASOURCE_JDBC = "jdbc";
	public static final String DATASOURCE_JNDI = "jndi";
	public static final String DATASOURCE_BEAN = "bean";
    public static final String DATASOURCE_VIRTUAL = "virtual";
    public static final String DATASOURCE_AWS = "aws";
	public static final String TYPE = "type";

    public static final String SUB_DATASOURCES_JSON_KEY = "selectedSubDs";
    public static final String SUB_DATASOURCE_ID_KEY = "dsId";
    public static final String SUB_DATASOURCE_URI_KEY = "dsUri";
    public static final String SUB_DATASOURCE_NAME_KEY = "dsName";
    public static final String DEPENDENT_RESOURCES_JSON_KEY = "dependentResources";

    public static final int MAX_DEPENDENT_RESOURCES = 20;

    protected MessageSource messages;
    private ConfigurationBean configuration;
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    private EngineService engine;
	private String queryLanguageFlowAttribute;
    private String UPLOAD_DRIVER_PREFIX = "file_";
    private String passwordSubstitution = null;

    private ObjectMapper jsonMapper = new ObjectMapper();
    private JdbcDriverService jdbcDriverService;
    private Map<String, Map<String, Object>> jdbcConnectionMap;
    private String dynamicUrlPartPattern;
    private Map<String, String> validationPatternsMap;

    ApplicationContext ctx;

    public DataSourceAction(){
		setFormObjectClass(ReportDataSourceWrapper.class); //custom form backing object class
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW); 		//this is a multi-page wizard!
	}

    /* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        ctx = arg0;
    }

    public Event initAction(RequestContext context) throws Exception {
        ReportDataSourceWrapper formObject = (ReportDataSourceWrapper) getFormObject(context);
        Object parentFlowObject = null;
        if (formObject != null) {
            parentFlowObject = formObject.getParentFlowObject();
        }
        if(formObject.isNewMode()) {
            context.getFlowScope().put(TYPE, context.getExternalContext().getRequestParameterMap().get(TYPE));
        } else {
            context.getFlowScope().put(TYPE, getTypeByFormObjectByType(formObject.getType()));
        }

        String typeFromRequest = context.getExternalContext().getRequestParameterMap().get(TYPE);
        boolean forceNewMode = formObject.isSubflowMode();
        if (formObject.isNewMode() || typeFromRequest != null && !typeFromRequest.equals(getTypeByFormObjectByType(formObject.getType())) )  {
            formObject = (ReportDataSourceWrapper) createFormObject(context);
            if (forceNewMode) {
                formObject.setParentFlowObject(parentFlowObject);
                formObject.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
            }
        }

		ReportDataSource ds = formObject.getReportDataSource();

		Locale displayLocale = LocaleContextHolder.getLocale();
		String selectedTimezone = null;
		if (ds instanceof JdbcReportDataSource)
			selectedTimezone = ((JdbcReportDataSource)ds).getTimezone();
		if (ds instanceof JndiJdbcReportDataSource)
			selectedTimezone = ((JndiJdbcReportDataSource)ds).getTimezone();

		List timezoneList = timezones.getTimeZones(displayLocale);
		timezoneList = new ArrayList(timezoneList);
		if (selectedTimezone != null && selectedTimezone.length()> 0) {
			TimeZone zone = TimeZone.getTimeZone(selectedTimezone);
			StringOption option = new StringOption(selectedTimezone, zone.getDisplayName(displayLocale));
			if (!timezoneList.contains(option))
				timezoneList.add(0, option);
		}
		context.getFlowScope().put("timezones", timezoneList);
		context.getFlowScope().put("selectedTimezone", selectedTimezone);
        getFormObjectAccessor(context).setCurrentFormObject(formObject, ScopeType.FLOW);
        context.getFlowScope().put(FORM_OBJECT_KEY, formObject);
        context.getFlowScope().put(ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS,
                configuration.getResourceIdNotSupportedSymbols());
        context.getFlowScope().put("awsRegions", getAwsRegions());
        context.getFlowScope().put("isEc2Instance", awsEc2MetadataClient.isEc2Instance());
        context.getFlowScope().put("suppressEc2CredentialsWarnings", awsProperties.isSuppressEc2CredentialsWarnings());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof TenantQualified) {
            if (((TenantQualified)principal).getTenantId() != null) {
                context.getFlowScope().put("tenantId", ((TenantQualified)principal).getTenantId());
            }
        }

        String requestParentFolder = context.getExternalContext().getRequestParameterMap().get(PARENT_FOLDER_URI);
        if (formObject.getReportDataSource() != null) {
            if (isBlank(requestParentFolder) || !repository.repositoryPathExists(null, requestParentFolder)) {
                requestParentFolder=null;
            }
            formObject.getReportDataSource().setParentFolder(requestParentFolder);
        }

        if (formObject.isSubflowMode() && formObject.getAllDatasources() == null) {
            //	context.getFlowScope().put(FORM_OBJECT_KEY, formObject);
            context.getFlowScope().put("constants", constants);
            //return success();
        }
        
        if (ds instanceof CustomReportDataSource) {
			CustomReportDataSource cds = (CustomReportDataSource) ds;
			// look up definition & use it to init defaults & set prop defs
			CustomDataSourceDefinition customDef = customDataSourceFactory.getDefinition(cds);
            if (customDef != null) {
                customDef.setDefaultValues(cds);
                formObject.setCustomProperties(customDef.getEditablePropertyDefinitions());
                formObject.setCustomDatasourceLabel(customDef.getLabelName());
                context.getFlowScope().put(TYPE, customDef.getName());
            }
        }

        passwordSubstitution = messages.getMessage("input.password.substitution", null, LocaleContextHolder.getLocale());

        context.getFlowScope().put(JDBC_DRIVERS_JSON_KEY, JSONUtil.toJSON(getAvailableJdbcDrivers()));
        context.getFlowScope().put(DYNAMIC_URL_PART_PATTERN, JSONUtil.toJSON(this.dynamicUrlPartPattern));
        context.getFlowScope().put(VALIDATION_PATTERNS, JSONUtil.toJSON(this.validationPatternsMap));
        context.getFlowScope().put(PASSWORD_SUBSTITUTION_KEY, passwordSubstitution);

        return success();
    }

    public Map<String, Map<String, Object>> getAvailableJdbcDrivers() {
        Map<String, Map<String, Object>> availableDrivers = new LinkedHashMap<String, Map<String, Object>>();

        for (Map.Entry<String, Map<String, Object>> entry: jdbcConnectionMap.entrySet()) {
            Map<String, Object> driverData = new HashMap<String, Object>();
            driverData.putAll(entry.getValue());
            driverData.put("available", jdbcDriverService.isRegistered(String.valueOf(entry.getValue().get("jdbcDriverClass"))));
            availableDrivers.put(entry.getKey(), driverData);
        }

        for (String registeredDriverClassName : jdbcDriverService.getRegisteredDriverClassNames()) {
            boolean defined = false;
            for (Map.Entry<String, Map<String, Object>> entry: jdbcConnectionMap.entrySet()) {
                if (registeredDriverClassName.equals(entry.getValue().get("jdbcDriverClass"))) {
                    defined = true;
                    break;
                }
            }
            if (!defined) {
                availableDrivers.put(registeredDriverClassName, null);
            }
        }

        return availableDrivers;
    }

    public String getTypeByFormObjectByType(String formObjectType) {
        if (JasperServerConstImpl.getJNDIDatasourceType().equals(formObjectType)){
            return DATASOURCE_JNDI;
        } else
        if (JasperServerConstImpl.getJDBCDatasourceType().equals(formObjectType)){
            return DATASOURCE_JDBC;
        } else
        if (JasperServerConstImpl.getBeanDatasourceType().equals(formObjectType)){
            return DATASOURCE_BEAN;
        }
        if (JasperServerConstImpl.getVirtualDatasourceType().equals(formObjectType)){
            return DATASOURCE_VIRTUAL;
        }
        if (JasperServerConstImpl.getAwsDatasourceType().equals(formObjectType)) {
            return DATASOURCE_AWS;
        }
        return formObjectType;
    }

    public Event prepareChooseType(RequestContext context) throws Exception
	{
        Map typeMap = new HashMap();

		// add custom types
		Iterator cdsi = customDataSourceFactory.getDefinitions().iterator();
		while (cdsi.hasNext()) {
			CustomDataSourceDefinition cds = (CustomDataSourceDefinition) cdsi.next();
			typeMap.put(cds.getName(), messages.getMessage(cds.getLabelName(), null, LocaleContextHolder.getLocale()));
		}

		context.getRequestScope().put("allTypes", typeMap);
		return success();
	}

    /**
     * Bind sub-datasources for virtual datasource
     *
     * @param context
     * @return
     * @throws Exception
     */
    public Event bindSubDatasources(RequestContext context) throws Exception {
        ReportDataSourceWrapper formObject = (ReportDataSourceWrapper) getFormObject(context);
        VirtualReportDataSource vds = (VirtualReportDataSource)formObject.getReportDataSource();
        String subDsJSON = (String)formObject.getNamedProperties().get(SUB_DATASOURCES_JSON_KEY);
        vds.getDataSourceUriMap().clear();
        if(isEmpty(subDsJSON)) {
            return success();
        }
        JsonNode subDsList = jsonMapper.readTree(subDsJSON);
        for(Iterator<JsonNode> i = subDsList.iterator(); i.hasNext(); ) {
            JsonNode subDs = i.next();
            String id = subDs.get(SUB_DATASOURCE_ID_KEY).asText();
            String uri = subDs.get(SUB_DATASOURCE_URI_KEY).asText();
            vds.getDataSourceUriMap().put(id, new ResourceReference(uri));
        }
        return success();
    }

    public Event saveDatasource(RequestContext context) throws Exception {
        ReportDataSourceWrapper wrapper = (ReportDataSourceWrapper) getFormObject(context);
        if (wrapper.isStandAloneMode()) {
            if (wrapper.getType() != null) {

                ReportDataSource ds = wrapper.getReportDataSource();

                if ((ds instanceof JdbcReportDataSource) && (((JdbcReportDataSource)ds).getDriverClass()).equalsIgnoreCase("VirtualDS"))  {
                    VirtualReportDataSourceImpl fds = new VirtualReportDataSourceImpl();
                    fds.setCreationDate(ds.getCreationDate());
                    fds.setDescription(ds.getDescription());
                    fds.setLabel(ds.getLabel());
                    fds.setName(ds.getName());
                    fds.setParentFolder(ds.getParentFolder());
               //   fds.setTimezone();
                    fds.setUpdateDate(ds.getUpdateDate());

                    try {
                        String[] paths = ds.getDescription().split("\\|");
                        String schemaList = ((JdbcReportDataSource)ds).getConnectionUrl();
                        String[] schemas = null;
                        if ((schemaList != null) && !schemaList.equals("")) {
                            schemas = schemaList.split("\\|");
                        }
                        Map<String, ResourceReference> uriMap = new HashMap<String, ResourceReference>();
                        for (int i = 0; i < paths.length; i++) {
                            String schemaName = null;
                            String path = paths[i].trim();
                            if ((schemas != null) && (i < schemas.length)) schemaName = schemas[i].trim();
                            else schemaName = path.substring(path.lastIndexOf("/") + 1);
                            uriMap.put(schemaName, new ResourceReference(path));
                        }
                        fds.setDataSourceUriMap(uriMap);
                        ds = fds;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (ds.getName() != null) {
                    try {

                        // On edit datasource we set the passwordSubstitution to the passwords form fields
                        // If we get the substitution from UI then set the password from original datasource (if it exists)
                        if (ds instanceof JdbcReportDataSource && ((JdbcReportDataSource) ds).getPassword().equals(passwordSubstitution)) {
                            JdbcReportDataSource existingDs = (JdbcReportDataSource) repository.getResource(null, ds.getURIString());
                            if (existingDs != null) {
                                ((JdbcReportDataSource) ds).setPassword(existingDs.getPassword());
                            }
                        }
                        if (ds instanceof AwsReportDataSource && ((AwsReportDataSource) ds).getAWSSecretKey().equals(passwordSubstitution)) {
                            AwsReportDataSource existingDs = (AwsReportDataSource) repository.getResource(null, ds.getURIString());
                            if (existingDs != null) {
                                ((AwsReportDataSource) ds).setAWSSecretKey(existingDs.getAWSSecretKey());
                            }
                        }
                        if (ds instanceof CustomReportDataSource) {
                            Object password = ((CustomReportDataSource) ds).getPropertyMap().get("password");
                            if (password != null && password.equals(passwordSubstitution)) {
                                CustomReportDataSource existingDs = (CustomReportDataSource) repository.getResource(null, ds.getURIString());
                                if (existingDs != null) {
                                    ((CustomReportDataSource) ds).getPropertyMap().put("password", existingDs.getPropertyMap().get("password"));
                                }
                            }
                        }

                        repository.saveResource(null, ds);
                        if (wrapper.isEditMode() && ds instanceof AwsReportDataSource) {
                            try {
                                awsDataSourceRecovery.createAwsDSSecurityGroup((AwsReportDataSource)ds);
                            } catch (Exception ex) {
                                logger.error("Exception creating of Aws Security Group", ex);
                            }
                        }
                    }
                    catch (JSDuplicateResourceException e) {
                        getFormErrors(context).rejectValue("reportDataSource.name", "ReportDataSourceValidator.error.duplicate");
                        return error();
                    }
                }
            }
            if (!wrapper.isEditMode()) {
                context.getExternalContext().getSessionMap().put("repositorySystemConfirm",
                        messages.getMessage("resource.file.fileAdded",
                                new String[] {wrapper.getReportDataSource().getName(),
                                wrapper.getReportDataSource().getParentFolder()},
                                LocaleContextHolder.getLocale()));
            }
            return yes();
        }
        return success();
    }

    public Event testCustomDataSource(RequestContext context) throws Exception {
        String connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_FAILED;
        if (customDataSourceFactory != null) {
            try {
                ReportDataSourceWrapper wrapper = (ReportDataSourceWrapper) getFormObject(context);
                CustomReportDataSource ds = (CustomReportDataSource) wrapper.getReportDataSource();

                // On edit datasource we set the passwordSubstitution to the passwords form fields
                // If we get the substitution from UI then set the password from original datasource (if it exists)
                if (ds.getPropertyMap().get("password") != null && ds.getPropertyMap().get("password").equals(passwordSubstitution)) {
                    CustomReportDataSource existingDs = (CustomReportDataSource) repository.getResource(null, ds.getURIString());
                    if (existingDs != null) {
                        ds.getPropertyMap().put("password", existingDs.getPropertyMap().get("password"));
                    }
                }

                ReportDataSourceService service = customDataSourceFactory.createService(ds);
                if (setTestAvailable(context, service) && ((CustomReportDataSourceService) service).testConnection()) {
                    connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED;
                }
            } catch (Exception e) {
                logger.error("exception testing custom data source", e);
            }
        }
        context.getRequestScope().put(AJAX_RESPONSE_MODEL,
                messages.getMessage(connectionStatusMessageKey, null, LocaleContextHolder.getLocale()));
        return success();
    }

    public Event testJdbcDataSource(RequestContext context) throws Exception
	{
        TestJdbcConnectionResponseBuilder response = new TestJdbcConnectionResponseBuilder().failed();
        ReportDataSourceWrapper wrapper=(ReportDataSourceWrapper) getFormObject(context);
		JdbcReportDataSource ds = (JdbcReportDataSource) wrapper.getReportDataSource();

 		Connection conn = null;
		try {
            jdbcDriverService.register(ds.getDriverClass());

            // On edit datasource we set the passwordSubstitution to the passwords form fields
            // If we get the substitution from UI then set the password from original datasource (if it exists)
            if (ds.getPassword().equals(passwordSubstitution)) {
                JdbcReportDataSource existingDs = (JdbcReportDataSource) repository.getResource(null, ds.getURIString());
                if (existingDs != null) {
                    ds.setPassword(existingDs.getPassword());
                }
            }
            Properties properties = new Properties();
            properties.put("user",ds.getUsername());
            properties.put("password",ds.getPassword());
            Driver driver = DriverManager.getDriver(ds.getConnectionUrl());
            conn = driver.connect(ds.getConnectionUrl(),properties);
			if (conn != null) {
                response.passed();
            }
		} catch (Exception e) {
            logger.error("exception testing jdbc data source", e);
            response.failed(e);
        } finally {
			if(conn != null)
				conn.close();
		}

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, response.buildJson());
        return success();
	}

    public void setInstanceProductTypeResolver(InstanceProductTypeResolver instanceProductTypeResolver) {
        this.instanceProductTypeResolver = instanceProductTypeResolver;
    }

    private class TestJdbcConnectionResponseBuilder {
        public static final String PASSED = "PASSED";
        public static final String FAILED = "FAILED";

        private Map<String, String> response = new HashMap<String, String>();

        public TestJdbcConnectionResponseBuilder passed() {
            response.put("status", PASSED);
            response.put("message",
                    messages.getMessage(RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED, null, LocaleContextHolder.getLocale()));
            return this;
        }

        public TestJdbcConnectionResponseBuilder failed() {
            response.put("status", FAILED);
            response.put("message",
                    messages.getMessage(RESOURCE_DATA_SOURCE_CONNECTION_STATE_FAILED, null, LocaleContextHolder.getLocale()));
            return this;
        }

        public TestJdbcConnectionResponseBuilder failed(Exception e) {
            this.failed();
            if (e != null) {
                response.put("message", e.getMessage());

                StringWriter result = new StringWriter();
                PrintWriter trace = new PrintWriter(result);
                e.printStackTrace(trace);

                response.put("details", result.toString());
            }
            return this;
        }

        public String buildJson() throws Exception {
            ObjectMapper mapper = new ObjectMapper();

            StringWriter result = new StringWriter();
            mapper.writeValue(result, response);
            return result.toString();
        }
    }

    public Event testAwsDataSource(RequestContext context) throws Exception {
        TestJdbcConnectionResponseBuilder response = new TestJdbcConnectionResponseBuilder().failed();
        ReportDataSourceWrapper wrapper=(ReportDataSourceWrapper) getFormObject(context);
        AwsReportDataSource ds = (AwsReportDataSource) wrapper.getReportDataSource();
        Connection conn = null;
        try {
            jdbcDriverService.register(ds.getDriverClass());

            // On edit datasource we set the passwordSubstitution to the passwords form fields
            // If we get the substitution from UI then set the password from original datasource (if it exists)
            if (ds.getPassword().equals(passwordSubstitution)) {
                AwsReportDataSource existingDs = (AwsReportDataSource) repository.getResource(null, ds.getURIString());
                if (existingDs != null) {
                    ds.setPassword(existingDs.getPassword());
                }
            }
            if (ds.getAWSSecretKey().equals(passwordSubstitution)) {
                AwsReportDataSource existingDs = (AwsReportDataSource) repository.getResource(null, ds.getURIString());
                if (existingDs != null) {
                    ds.setAWSSecretKey(existingDs.getAWSSecretKey());
                }
            }

            awsDataSourceRecovery.createAwsDSSecurityGroup(ds);
            conn = DriverManager.getConnection(ds.getConnectionUrl(), ds.getUsername(), ds.getPassword());
            if (conn != null) {
                response.passed();
            }
        } catch (Exception e) {
            logger.error("exception testing AWS data source", e);

            Throwable throwable = ExceptionUtils.getRootCause(e);
            if (throwable instanceof ConnectException || throwable instanceof SocketTimeoutException ||
                    (e instanceof SQLException && ((SQLException)e).getSQLState().
                            startsWith(AwsDataSourceService.SQL_STATE_CLASS))) {
                response.failed(new JSException(messages.getMessage("aws.exception.datasource.recovery.timeout"
                        , null, LocaleContextHolder.getLocale()), e));
           } else {
                response.failed(e);
            }
        } finally {
            if(conn != null)
                conn.close();
        }
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, response.buildJson());
        return success();
    }

    public Event testJndiDataSource(RequestContext context) throws Exception
	{
        String connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_FAILED;
        ReportDataSourceWrapper wrapper=(ReportDataSourceWrapper) getFormObject(context);
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) wrapper.getReportDataSource();
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/" + ds.getJndiName());
            conn = TibcoDriverManagerImpl.getInstance().unlockConnection(dataSource);
            if (conn != null) {
                connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED;
            }
		} catch(Exception e) {
            logger.error("exception testing jndi data source", e);
		} finally {
			if(conn != null)
				conn.close();
		}

        context.getRequestScope().put(AJAX_RESPONSE_MODEL,
                messages.getMessage(connectionStatusMessageKey, null, LocaleContextHolder.getLocale()));
		return success();
	}

	public Event testBeanDataSource(RequestContext context) throws Exception
	{
        String connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_FAILED;
        ReportDataSourceWrapper wrapper=(ReportDataSourceWrapper) getFormObject(context);
		BeanReportDataSource ds = (BeanReportDataSource) wrapper.getReportDataSource();

		try{
			Object bean = ctx.getBean(ds.getBeanName());

			if (bean == null) {
                // Return connection failed message - it's default behavior
			} else if (ds.getBeanMethod() == null) {
				// The bean had better be a ReportDataSourceService
                if (bean instanceof ReportDataSourceService) {
                    connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED;
                }
			} else {
				// The method on this bean returns a ReportDataSourceService
				Method serviceMethod;
				try {
					serviceMethod = bean.getClass().getMethod(ds.getBeanMethod(), null);
					Object obj = serviceMethod.invoke(bean, null);
                    if (obj != null) {
                        connectionStatusMessageKey = RESOURCE_DATA_SOURCE_CONNECTION_STATE_PASSED;
                    }
				} catch (SecurityException e) {
	                logger.error("exception testing bean data source", e);
				}
			}
		}catch (Exception e){
            logger.error("exception testing bean data source", e);
		}
        context.getRequestScope().put(AJAX_RESPONSE_MODEL,
                messages.getMessage(connectionStatusMessageKey, null, LocaleContextHolder.getLocale()));
		return success();
	}

	public Event validateDataSource(RequestContext context) throws Exception
	{
		Errors errors = getFormErrors(context);

		ReportDataSourceWrapper wrapper = (ReportDataSourceWrapper)getFormObject(context);

		getValidator().validate(wrapper, errors);

		List fieldErrors = errors.getFieldErrors();
		if (fieldErrors != null && !fieldErrors.isEmpty())
		{
			FieldError error = (FieldError)fieldErrors.get(0);
			String field = error.getField();

			if (
				"source".equals(field)
				|| "selectedUri".equals(field)
				)
			{
				return result("chooseSource");
			}
			else if ("type".equals(field))
			{
				return result("chooseType");
			}
			else if (JasperServerConstImpl.getJDBCDatasourceType().equals(wrapper.getType()))
			{
				return result("jdbcPropsForm");
			}
			else if (JasperServerConstImpl.getJNDIDatasourceType().equals(wrapper.getType()))
			{
				return result("jndiPropsForm");
			}
			else if (JasperServerConstImpl.getBeanDatasourceType().equals(wrapper.getType()))
			{
				return result("beanPropsForm");
			}
            else if (JasperServerConstImpl.getVirtualDatasourceType().equals(wrapper.getType()))
			{
				return result("virtualPropsForm");
			}
			else
			{
				return result("customPropsForm");
			}
		}

		return success();
	}

    /**
     * Init sub-datasources JSON in formObject from VDS object.
     * Used when opening existing VDS for edit
     *
     * @param formObject
     */
    public void initSubDatasources(ReportDataSourceWrapper formObject) {
        ArrayNode subDsList = jsonMapper.createArrayNode();
        VirtualReportDataSource vds = (VirtualReportDataSource)formObject.getReportDataSource();
        for(Map.Entry<String, ResourceReference> dsMapping: vds.getDataSourceUriMap().entrySet()) {
            ObjectNode subDsNode = subDsList.addObject();
            subDsNode.put(SUB_DATASOURCE_ID_KEY, dsMapping.getKey());
            subDsNode.put(SUB_DATASOURCE_URI_KEY, dsMapping.getValue().getReferenceURI());
            Resource res = repository.getResource(null, dsMapping.getValue().getReferenceURI());
            subDsNode.put(SUB_DATASOURCE_NAME_KEY, res.getName());
        }
        String subDsJson = JSONUtil.toJSON(subDsList);
        formObject.getNamedProperties().put(SUB_DATASOURCES_JSON_KEY, subDsJson);
    }

    public void initDependentResourcesList(ReportDataSourceWrapper formObject) {
        if(!formObject.isEditMode()) {
            return;
        }
        List<ResourceLookup> depRes = repository.getDependentResources(null, formObject.getReportDataSource().getURIString(), null, 0, MAX_DEPENDENT_RESOURCES);
        String depResJson = JSONUtil.toJSON(depRes);
        formObject.getNamedProperties().put(DEPENDENT_RESOURCES_JSON_KEY, depResJson);
    }

	public Object createFormObject(RequestContext context) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ReportDataSourceWrapper formObject = new ReportDataSourceWrapper();
		String resourceUri = context.getRequestParameters().get(DATASOURCEURI_PARAM);

		if (resourceUri != null && resourceUri.trim().length() != 0){
			Resource resource = repository.getResource(null,resourceUri);

			if (resource == null) {
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {resourceUri});
            }

			formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
			ReportDataSource dataSource = (ReportDataSource) resource;
			formObject.setType(getDataSourceMappings().getIdForClass(dataSource.getClass()));
            if (JasperServerConstImpl.getJNDIDatasourceType().equals(getDataSourceMappings().getIdForClass(dataSource.getClass()))) {
                context.getFlowScope().put(TYPE, DATASOURCE_JNDI);
            } else if (JasperServerConstImpl.getBeanDatasourceType().equals(getDataSourceMappings().getIdForClass(dataSource.getClass()))) {
                 context.getFlowScope().put(TYPE, DATASOURCE_BEAN);
            }
			formObject.setReportDataSource(dataSource);
            if(JasperServerConstImpl.getVirtualDatasourceType().equals(formObject.getType())) {
                initDependentResourcesList(formObject);
                initSubDatasources(formObject);
            }
		}
		if (formObject.getReportDataSource() == null){
			String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
			String type = context.getExternalContext().getRequestParameterMap().get(TYPE);

			if (parentFolder == null) {
				parentFolder = context.getRequestParameters().get(PARENT_FOLDER_URI);
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}

			formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
			//	set default options for datasource type
			formObject.setSource(JasperServerConstImpl.getFieldChoiceLocal());

            ReportDataSource source;

            //If JRS are in EC2 Instance than AWS data source will be setup as default(else JDBC is default).
            if ((type == null && instanceProductTypeResolver.isEC2()) || DATASOURCE_AWS.equals(type)) {
                source = newReportDataSource(JasperServerConstImpl.getAwsDatasourceType());
                context.getFlowScope().put(TYPE, DATASOURCE_AWS);
                formObject.setType(JasperServerConstImpl.getAwsDatasourceType());
                if (awsEc2MetadataClient.isEc2Instance()) {
                    ((AwsReportDataSource)source).setAWSRegion(awsEc2MetadataClient.getEc2InstanceRegion());
                }
            } else if (type == null) {
                source = newReportDataSource(JasperServerConstImpl.getJDBCDatasourceType());
                formObject.setType(JasperServerConstImpl.getJDBCDatasourceType());
            } else if (type.equals(DATASOURCE_JDBC)) {
                source = newReportDataSource(JasperServerConstImpl.getJDBCDatasourceType());
                formObject.setType(JasperServerConstImpl.getJNDIDatasourceType());
            } else if (type.equals(DATASOURCE_JNDI)) {
                source = newReportDataSource(JasperServerConstImpl.getJNDIDatasourceType());
                formObject.setType(JasperServerConstImpl.getJNDIDatasourceType());
            } else if (type.equals(DATASOURCE_BEAN)) {
                source = newReportDataSource(JasperServerConstImpl.getBeanDatasourceType());
                formObject.setType(JasperServerConstImpl.getBeanDatasourceType());
            } else if (type.equals(DATASOURCE_VIRTUAL)) {
                source = newReportDataSource(JasperServerConstImpl.getVirtualDatasourceType());
                formObject.setType(JasperServerConstImpl.getVirtualDatasourceType());
            } else {
			    source = newReportDataSource(type);
                formObject.setType(type);
            }
			source.setParentFolder(parentFolder);
			source.setVersion(Resource.VERSION_NEW);
			formObject.setReportDataSource(source);
		}

        if (formObject.getReportDataSource() instanceof CustomReportDataSource) {
            evaluateTestAvailability(context, (CustomReportDataSource) formObject.getReportDataSource());
        }

		return formObject;
	}

    private void evaluateTestAvailability(RequestContext context, CustomReportDataSource reportDataSource) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (logger.isDebugEnabled()) {
            logger.debug("Evaluating service class: " + reportDataSource.getServiceClass());
        }
        try {
            Class<?> serviceClass = Class.forName(reportDataSource.getServiceClass());
            // Instance created to evaluate if it implements the test interface
            Object newServiceClass = serviceClass.newInstance();
            setTestAvailable(context, newServiceClass);
            newServiceClass = null;
        } catch (ClassNotFoundException cnf) {
            setTestAvailable(context, cnf);
        }
    }

    private boolean setTestAvailable(RequestContext context, Object instance) {
        boolean isAvailable;
        context.getRequestScope().put("testAvailable",
                isAvailable = (instance instanceof CustomReportDataSourceService));
        return isAvailable;
    }

	private ReportDataSource newReportDataSource(String dsType) {
        if (customDataSourceFactory != null) {
			CustomDataSourceDefinition cdsd = customDataSourceFactory.getDefinitionByName(dsType);
			if (cdsd != null) {
				return customDataSourceFactory.createDataSource(dsType);
			}
		}

		return (ReportDataSource) getDataSourceMappings().newResource(null, dsType);
	}

	private ReportDataSource newReportDataSource(String dsType, ReportDataSource oldDS) {
		ReportDataSource newDS = newReportDataSource(dsType);
		newDS.setParentFolder(oldDS.getParentFolder());
		newDS.setName(oldDS.getName());
		newDS.setLabel(oldDS.getLabel());
		newDS.setDescription(oldDS.getDescription());
		newDS.setVersion(oldDS.getVersion());

		if (oldDS instanceof JdbcReportDataSource) {
			JdbcReportDataSource oldJdbc = (JdbcReportDataSource) oldDS;
			JdbcReportDataSource newJdbc = (JdbcReportDataSource) newDS;
			newJdbc.setDriverClass(oldJdbc.getDriverClass());
			newJdbc.setConnectionUrl(oldJdbc.getConnectionUrl());
			newJdbc.setPassword(oldJdbc.getPassword());
			newJdbc.setTimezone(oldJdbc.getTimezone());
			newJdbc.setUsername(oldJdbc.getUsername());
		}
		else if (oldDS instanceof JndiJdbcReportDataSource) {
			JndiJdbcReportDataSource oldJndi = (JndiJdbcReportDataSource) oldDS;
			JndiJdbcReportDataSource newJndi = (JndiJdbcReportDataSource) newDS;
			newJndi.setJndiName(oldJndi.getJndiName());
			newJndi.setTimezone(oldJndi.getTimezone());
		}
        else if (oldDS instanceof VirtualReportDataSource) {
			VirtualReportDataSource oldVirtual = (VirtualReportDataSource) oldDS;
			VirtualReportDataSource newVirtual = (VirtualReportDataSource) newDS;
			newVirtual.setDataSourceUriMap(new HashMap<String, ResourceReference>(oldVirtual.getDataSourceUriMap()));
            newVirtual.setTimezone(oldVirtual.getTimezone());
		}
		else if (oldDS instanceof BeanReportDataSource) {
			BeanReportDataSource oldBean = (BeanReportDataSource) oldDS;
			BeanReportDataSource newBean = (BeanReportDataSource) newDS;
			newBean.setBeanMethod(oldBean.getBeanMethod());
			newBean.setBeanName(oldBean.getBeanName());
		}
		return newDS;
	}

    public Event uploadJDBCDrivers(RequestContext context) throws Exception {
        String driverClassName = context.getRequestParameters().get("className");
        if (driverClassName == null || driverClassName.isEmpty()) {
            throw new Exception("Class name is empty.");
        }

        Map<String, byte[]> driverFilesData = new HashMap<String, byte[]>();

        int i = 0;
        MultipartFile multipartFile = context.getRequestParameters().getMultipartFile(UPLOAD_DRIVER_PREFIX + i);
        while (multipartFile != null) {
            driverFilesData.put(multipartFile.getOriginalFilename(), multipartFile.getBytes());

            i++;
            multipartFile = context.getRequestParameters().getMultipartFile(UPLOAD_DRIVER_PREFIX + i);
        }

        String errorMessage = "";
        try {
            jdbcDriverService.setDriver(driverClassName, driverFilesData);
        } catch(NoClassDefFoundError e) {
            errorMessage = messages.getMessage("resource.dataSource.jdbc.classNotFound", new String[] {e.getMessage()},
                    LocaleContextHolder.getLocale());
            logger.error(errorMessage);
        } catch(ClassNotFoundException e) {
            errorMessage = messages.getMessage("resource.dataSource.jdbc.classNotFound", new String[] {e.getMessage()},
                    LocaleContextHolder.getLocale());
            logger.error(errorMessage);
        } catch(Exception e) {
            errorMessage = e.getMessage();
            logger.error(errorMessage);
        }

        Map<String, Map<String, Object>> availableDrivers = getAvailableJdbcDrivers();

        JSONObject json = new JSONObject();
        json.put("result", isEmpty(errorMessage));
        json.put("errorMessage", errorMessage);
        json.put(JDBC_DRIVERS_JSON_KEY, JSONUtil.toJSON(availableDrivers));
        context.getRequestScope().put(AJAX_RESPONSE_MODEL, json.toString());
        context.getFlowScope().put(JDBC_DRIVERS_JSON_KEY, JSONUtil.toJSON(availableDrivers));

        return success();
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    /**
	 * @return Returns the dataSourceMappings.
	 */
	public ResourceFactory getDataSourceMappings() {
		return dataSourceMappings;
	}
	/**
	 * @param dataSourceMappings The dataSourceMappings to set.
	 */
	public void setDataSourceMappings(ResourceFactory dataSourceMappings) {
		this.dataSourceMappings = dataSourceMappings;
	}

	public JdkTimeZonesList getTimezones()
	{
		return timezones;
	}

	public void setTimezones(JdkTimeZonesList timezones)
	{
		this.timezones = timezones;
	}

    public List<String> getAwsRegions() {
        return awsRegions;
    }

    public void setAwsRegions(List<String> awsRegions) {
        this.awsRegions = awsRegions;
    }

    public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public CustomReportDataSourceServiceFactory getCustomDataSourceFactory() {
		return customDataSourceFactory;
	}

	public void setCustomDataSourceFactory(CustomReportDataSourceServiceFactory customDataSourceFactory) {
		this.customDataSourceFactory = customDataSourceFactory;
	}

    public String getQueryLanguageFlowAttribute() {
		return queryLanguageFlowAttribute;
	}

	public void setQueryLanguageFlowAttribute(String queryLanguageFlowAttribute) {
		this.queryLanguageFlowAttribute = queryLanguageFlowAttribute;
	}

    protected String getQueryLanguage(RequestContext context) {
		return context.getFlowScope().getString(getQueryLanguageFlowAttribute());
	}

	public EngineService getEngine() {
		return engine;
	}

	public void setEngine(EngineService engine) {
		this.engine = engine;
    }

    public void setJdbcDriverService(JdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }

    public void setAwsDataSourceRecovery(AwsDataSourceRecovery awsDataSourceRecovery) {
        this.awsDataSourceRecovery = awsDataSourceRecovery;
    }

    public void setAwsProperties(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    public void setAwsEc2MetadataClient(AwsEc2MetadataClient awsEc2MetadataClient) {
        this.awsEc2MetadataClient = awsEc2MetadataClient;
    }

    public void setJdbcConnectionMap(Map<String, Map<String, Object>> jdbcConnectionMap) {
        this.jdbcConnectionMap = jdbcConnectionMap;
    }

    public void setDynamicUrlPartPattern(String dynamicUrlPartPattern) {
        this.dynamicUrlPartPattern = dynamicUrlPartPattern;
    }

    public void setValidationPatternsMap(Map<String, String> validationPatternsMap) {
        this.validationPatternsMap = validationPatternsMap;
    }
}
