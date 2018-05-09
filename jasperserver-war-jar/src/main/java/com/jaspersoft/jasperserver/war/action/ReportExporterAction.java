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

import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;


/**
 * @author sanda zaharia (szaharia@users.sourceforge.net)
 * @version $Id$
 */
public class ReportExporterAction extends FormAction {
	
	private static final Log log = LogFactory.getLog(ReportExporterAction.class);
	
	public static final String REPORT_EXPORT = "export";
	public static final String REPORT_EXPORT_PARAMS = "exportParams";
	public static final String REPORT_EXPORT_OPTION = "exportOption";
	public static final String PARAMETER_DIALOG_NAME = "parameterDialogName";
	public static final String OUTPUT = "output";
	public static final String REPORT_OUTPUT = "reportOutput";
	public static final String EXPORTER_TYPE = "exporterType";

	public static final String EXPORT_TYPE_NOT_SUPPORTED_MESSAGE_KEY = "report.output.type.not.supported";

	private Map configuredExporters;
	private Object exportParameters;
//	private String exporterType;
	private ConfigurationBean configurationBean;
	private MessageSource messageSource;
	private ViewReportAction viewReportAction;

    /**
     * Utility method which returns comma separated list of supported
     * export types. It is used for better formatting of error message
     * when some non-existent type was used.
     * @return string with comma separated list of supported types in canonical form.
     */
    private String getSupportedExportTypesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Object type: configuredExporters.keySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(type);
        }

        return sb.toString();
    }

    public Event exportStart(RequestContext context) {
		String type = getExporterType(context);
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(type);
		
		if (exporterConfiguration != null) 
		{
			ReportUnit reportUnit = getViewReportAction().getReportUnit(context);
			JasperReport jasperReport = getViewReportAction().getEngine().getMainJasperReport(ViewReportAction.getExecutionContext(context), reportUnit.getURI());;
			
			PaginationParameters paginationParams = exporterConfiguration.getCurrentExporter().getPaginationParameters(jasperReport); 
			if (paginationParams.hasParameters()) 
			{
			//setting the pagination parameters on the request so that 
			//the following report fill will be paginated accordingly.
			//note that this assumes the report fill will occur in the same request, 
			//which will not be the case when we'll have export options dialogs.
			if (log.isDebugEnabled()) {
				log.debug("requested report pagination parameters " + paginationParams);
			}
			
			context.getRequestScope().put(getViewReportAction().getAttributePaginationParameters(), paginationParams);
		}
		}

		return success();
    }
    
	public Event exportOptions(RequestContext context) throws Exception{
		MutableAttributeMap flowScope = context.getFlowScope();
		String type = getExporterType(context);

        // Return formatted message which informs user that
        // such export type is not supported and contains list of supported types.
        if (!configuredExporters.containsKey(type)) {
            throw new JSException(messageSource.getMessage(
                    EXPORT_TYPE_NOT_SUPPORTED_MESSAGE_KEY,
                    new String[]{getSupportedExportTypesAsString()},
                    LocaleContextHolder.getLocale()));
        }

//		if(type!= null){
//			exporterType = type;
//		}
		flowScope.put(ReportExporterAction.EXPORTER_TYPE, type);
//		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(exporterType);
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(type);
		if(type!= null && StringUtils.isNotEmpty(exporterConfiguration.getParameterDialogName())){
			exportParameters=exporterConfiguration.getExportParameters();
			String parameterDialogName = exporterConfiguration.getParameterDialogName();
			setFormObjectClass(exportParameters.getClass()); 
			setFormObjectName(parameterDialogName);
			setFormObjectScope(ScopeType.FLOW);
			if(configurationBean.isReportLevelConfigurable()){
				flowScope.put(ReportExporterAction.PARAMETER_DIALOG_NAME, parameterDialogName);
				ExportParameters formObject = (ExportParameters) getFormObject(context);
				formObject.setOverrideReportHints(true);
				context.getRequestScope().put(parameterDialogName, formObject);
				flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT_PARAMS);
				
			}else{
				flowScope.put(parameterDialogName, getFormObject(context));
				flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT);
			}
		}else{
			flowScope.put(ReportExporterAction.REPORT_EXPORT_OPTION, ReportExporterAction.REPORT_EXPORT);
		}
		return success();
	}
	
	public void export(RequestContext context) throws Exception{
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(context.getFlowScope().get(ReportExporterAction.EXPORTER_TYPE));
		exporterConfiguration.getCurrentExporter().export(context);
		context.getExternalContext().recordResponseComplete();
	}

	/**
	 * @return Returns the configuredExporters.
	 */
	public Map getConfiguredExporters() {
		return configuredExporters;
	}

	/**
	 * @param configuredExporters The configuredExporters to set.
	 */
	public void setConfiguredExporters(Map configuredExporters) {
		this.configuredExporters = configuredExporters;
	}
	
	private String getExporterType(RequestContext context)
	{
		String type = context.getRequestParameters().get(ReportExporterAction.OUTPUT) == null ?
				(String)context.getFlowScope().get(ReportExporterAction.REPORT_OUTPUT) :
				(String)context.getRequestParameters().get(ReportExporterAction.OUTPUT);	
		return getCanonicValue(type);
//		return getCanonicValue(context.getRequestParameters().get(ReportExporterAction.OUTPUT));
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public Object getExportParameters() {
		return exportParameters;
	}

	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(Object exportParameters) {
		this.exportParameters = exportParameters;
	}
	/**
	 *
	 */
	public Object createFormObject(RequestContext context) throws Exception
	{
		ExporterConfigurationBean exporterConfiguration = (ExporterConfigurationBean)getConfiguredExporters().get(context.getFlowScope().get(ReportExporterAction.EXPORTER_TYPE));
		if(null != exporterConfiguration){
			Object exportParams=exporterConfiguration.getExportParameters();
			if(null != exportParams){
				ExportParameters exp = (ExportParameters)exportParams.getClass().newInstance();
				exp.setPropertyValues(exportParams);
				return exp;
			}
		}
		return null;
	}
	
	private String getCanonicValue(String type){
		if("excel".equalsIgnoreCase(type)){
			return "xls";
		}
		return type;
	}

	/**
	 * @return Returns the configurationBean.
	 */
	public ConfigurationBean getConfigurationBean() {
		return configurationBean;
	}

	/**
	 * @param configurationBean The configurationBean to set.
	 */
	public void setConfigurationBean(ConfigurationBean configurationBean) {
		this.configurationBean = configurationBean;
	}

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

	public ViewReportAction getViewReportAction() {
		return viewReportAction;
	}

	public void setViewReportAction(ViewReportAction viewReportAction) {
		this.viewReportAction = viewReportAction;
	}
}
