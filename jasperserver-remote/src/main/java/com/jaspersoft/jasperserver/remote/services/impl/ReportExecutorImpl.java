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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.common.domain.Request;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataCacheProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControlsContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.remote.ReportExporter;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.ServicesConfiguration;
import com.jaspersoft.jasperserver.remote.exception.ExportExecutionRejectedException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.services.ReportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecutor;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import com.jaspersoft.jasperserver.remote.utils.RepositoryHelper;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimpleReportContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecutorImpl.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@Service
public class ReportExecutorImpl implements ReportExecutor {
    private final static Log log = LogFactory.getLog(ReportExecutorImpl.class);
    @javax.annotation.Resource
    private AuditHelper auditHelper;
    @javax.annotation.Resource(name = "engineService")
    private EngineService engine;
    @javax.annotation.Resource(name = "remoteServiceConfiguration")
    private ServicesConfiguration servicesConfiguration;
    @javax.annotation.Resource(name = "engineServiceDataCacheProvider")
    private DataCacheProvider dataCacheProvider;
    @javax.annotation.Resource(name = "dataSnapshotService")
    private DataSnapshotService dataSnapshotService;
    @javax.annotation.Resource
    private JasperReportsContext jasperReportsRemoteContext;
    @javax.annotation.Resource
    private CachedRepositoryService cachedRepositoryService;
    @javax.annotation.Resource(name = "concreteVirtualizerFactory")
    private VirtualizerFactory virtualizerFactory;

    @Override
    public boolean isRunnableResource(Resource resource) {
        boolean result = resource instanceof InputControlsContainer;
        if(result){
            result = getStrategyForReport(resource) != null;
        }
        return result;
    }

    /**
     * Running of the report happens here.
     *
     * @param reportUnitUri          - the URI of a report to run
     * @param parameters             - input parameters
     * @param reportExecutionOptions - report execution options
     * @return report unit execution result
     * @throws com.jaspersoft.jasperserver.remote.exception.RemoteException
     *          if any error occurs
     */
    public ReportUnitResult runReport(String reportUnitUri, Map<String, Object> parameters,
            ReportExecutionOptions reportExecutionOptions) throws RemoteException {
        InputControlsContainer report = getResource(InputControlsContainer.class, reportUnitUri);
        RunReportStrategy strategy = getStrategyForReport(report);
        if (strategy == null) {
            throw new RemoteException(new ErrorDescriptor()
                    .setErrorCode("webservices.error.errorExecutingReportUnit").setParameters(report.getURI()));
        }
        
        // if ignorePagination isn't set, then use value from defaultIgnorePagination, which become not null just after
        // reportUnitResult available.
        PaginationParameters pagination = reportExecutionOptions.getPaginationParameters();
        if ((pagination == null || pagination.getPaginated() == null) 
        		&& reportExecutionOptions.getDefaultIgnorePagination() != null) {
        	pagination = new PaginationParameters(pagination);
        	pagination.setPaginated(!reportExecutionOptions.getDefaultIgnorePagination());
        }
        if (pagination != null) {
        	pagination.setReportParameters(parameters);
        }
        
        // run the report
        ReportUnitResult reportUnitResult = strategy.runReport(report, parameters, engine,
                getJasperReportsContext(reportExecutionOptions.isInteractive()), reportExecutionOptions);

        if (reportUnitResult == null) {
            throw new RemoteException(new ErrorDescriptor()
                    .setErrorCode("webservices.error.errorExecutingReportUnit").setParameters(report.getURI()));
        }

        return reportUnitResult;
    }

    protected <T extends Resource> T getResource(Class<T> resourceClass, String resourceUri) throws ResourceNotFoundException {
        try {
            return cachedRepositoryService.getResource(resourceClass, resourceUri);
        } catch (CascadeResourceNotFoundException e) {
            throw new ResourceNotFoundException(resourceUri);
        }
    }


    protected ExecutionContext createExecutionContext() {
        ExecutionContextImpl ctx = new ExecutionContextImpl();
        ctx.setLocale(LocaleContextHolder.getLocale());
        ctx.setTimeZone(TimeZoneContextHolder.getTimeZone());
        return ExecutionContextImpl.getRuntimeExecutionContext(ctx);
    }

    /**
     * Look for the ReportExporter configured for the named format and export
     * the report.
     *
     * @param reportUnitURI    - the report unit URI
     * @param jasperPrint      - jasperPring object
     * @param format           - format
     * @param output           - output stream to write to
     * @param exportParameters - parameters for export procedure
     * @return map with export results
     * @throws com.jaspersoft.jasperserver.remote.ServiceException
     *
     */
    public Map<JRExporterParameter, Object> exportReport(String reportUnitURI, JasperPrint jasperPrint, String format, OutputStream output,
            HashMap exportParameters) throws ServiceException {
        ReportExporter exporter = servicesConfiguration.getExporter(format.toLowerCase());
        if (exporter == null) {
            throw new ServiceException(3, "Export format " + format.toLowerCase() + " not supported or misconfigured");
        }
        try {
            InputControlsContainer report = getResource(InputControlsContainer.class, reportUnitURI);
            final RunReportStrategy strategyForReport = getStrategyForReport(report);
            return exporter.exportReport(jasperPrint, output, engine, exportParameters, createExecutionContext(),
                    strategyForReport.getConcreteReportURI(report));
        } catch (JRRuntimeException e){
            if(JRAbstractExporter.EXCEPTION_MESSAGE_KEY_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())
            		|| JRAbstractExporter.EXCEPTION_MESSAGE_KEY_START_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())
            		|| JRAbstractExporter.EXCEPTION_MESSAGE_KEY_END_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())){
                final String pages = exportParameters.get(Argument.RUN_OUTPUT_PAGES).toString();
                final int totalPages = jasperPrint.getPages().size();
                throw new ExportExecutionRejectedException(new ErrorDescriptor().setMessage(
                        "Page number out of range : " + pages + " of " + totalPages + " (while exporting the report)")
                        .setErrorCode("page.number.out.of.range").setParameters(pages, "" + totalPages));
            }
            throw new ExportExecutionRejectedException(e.getMessage());
        } catch (Exception e) {
            throw new ExportExecutionRejectedException(e.getMessage());
        }
    }

    /**
     * @param report - the report resource from a repository
     * @return strategy for running of a report of concrete type
     */
    protected RunReportStrategy getStrategyForReport(Resource report) {
        return report instanceof ReportUnit ? new RunReportUnitStrategy() : null;
    }

    /**
     * Generic run report strategy. Contains generic run report functionality.
     *
     * @param <ReportType> - concrete type of report
     */
    protected abstract class GenericRunReportStrategy<ReportType extends Resource> implements RunReportStrategy {
        /**
         * Runs report of concrete type
         *
         * @param reportResource - the report resource from a repository
         * @param parameters     - input parameters
         * @param engine         - engine service
         * @return result of report execution
         */
        // report type should correspond to concrete type of strategy. getStrategyForReport() assure that.
        // So, unchecked cast is safe
        @SuppressWarnings("unchecked")
        public ReportUnitResult runReport(Resource reportResource, Map<String, Object> parameters, EngineService engine, JasperReportsContext context,
                ReportExecutionOptions options) {
            ReportType report = (ReportType) reportResource;
            Map<String, Object> convertedParameters = parameters != null ? RepositoryHelper.convertParameterValues(getConcreteReportURI(report), parameters, engine) : new HashMap<String, Object>();
            ReportUnitRequest request = getReportUnitRequest(report, convertedParameters, context, options);
            ExecutionContext executionContext = createExecutionContext();
            ReportUnitResult result = (ReportUnitResult) engine.execute(executionContext, request);
            persistDataSnapshot(executionContext, options, reportResource, request.getReportContext());
            return result;
        }

        protected void persistDataSnapshot(ExecutionContext executionContext, ReportExecutionOptions options,
                Resource reportResource, ReportContext reportContext) {
            DataCacheProvider.SnapshotSaveStatus snapshotSaveStatus = dataCacheProvider.getSnapshotSaveStatus(reportContext);
            switch (snapshotSaveStatus) {
                case NEW:
                    // automatic save
                    if (log.isDebugEnabled()) {
                        log.debug("saving initial data snapshot for " + reportResource.getURIString());
                    }

                    saveAutoDataSnapshot(executionContext, reportResource, reportContext);
                    break;
                case UPDATED:
                    if (options.isSaveDataSnapshot()) {
                        // requested save
                        if (log.isDebugEnabled()) {
                            log.debug("saving updated data snapshot for " + reportResource.getURIString());
                        }

                        saveDataSnapshot(executionContext, reportResource, reportContext);
                    }
                    break;
                case NO_CHANGE:
                default:
                    //NOP
                    break;
            }
        }

        protected void saveAutoDataSnapshot(ExecutionContext executionContext, Resource reportResource,
                ReportContext reportContext) {
            ReportUnit reportUnit = getReportUnit(reportResource);
            try {
                dataSnapshotService.saveAutoReportDataSnapshot(executionContext, reportContext, reportUnit);
            } catch (Exception e) {
                // catching any exceptions for automatic and requested save
                log.error("Error while saving data snapshot for " + reportUnit.getURIString(), e);
            }
        }

        protected void saveDataSnapshot(ExecutionContext executionContext, Resource reportResource,
                ReportContext reportContext) {
            ReportUnit reportUnit = getReportUnit(reportResource);
            try {
                dataSnapshotService.saveReportDataSnapshot(executionContext, reportContext, reportUnit);
            } catch (Exception e) {
                // catching any exceptions for automatic and requested save
                log.error("Error while saving data snapshot for " + reportUnit.getURIString(), e);
            }
        }

        protected ReportUnitRequest getReportUnitRequest(ReportType reportResource, Map<String, Object> parameters, JasperReportsContext context,
                ReportExecutionOptions options) {
            final ReportExecutionOptions executionOptions = options != null ? options : new ReportExecutionOptions();
            Map<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.putAll(parameters);
            requestParams.put(JRParameter.REPORT_VIRTUALIZER, virtualizerFactory.getVirtualizer());

            // we need a report context for snapshots
            if(executionOptions.getReportContext() == null){
                final SimpleReportContext reportContext = new SimpleReportContext();
                reportContext.setParameterValue(JRParameter.REPORT_LOCALE, LocaleContextHolder.getLocale());
                executionOptions.setReportContext(reportContext);
            }
            if(executionOptions.getJasperReportsContext() == null){
                executionOptions.setJasperReportsContext(context);
            }
            requestParams.put(JRParameter.REPORT_CONTEXT, executionOptions.getReportContext());
            if(options.isFreshData()){
                requestParams.put(Request.PARAM_NAME_FRESH_DATA, "true");
            }
            ReportUnitRequest request = new ReportUnitRequest(getConcreteReportURI(reportResource), requestParams);
            if(options.getRequestId() != null){
                // request ID is defined in report execution options. Let's use it.
                request.setId(options.getRequestId());
            } else {
                // no request ID is defined so far. Seems legacy code ;)
                // set generated request ID to options for consistency reason.
                options.setRequestId(request.getId());
            }
            request.setReportContext(executionOptions.getReportContext());
            request.setJasperReportsContext(executionOptions.getJasperReportsContext());

            // recording is enabled for first-time saves
            request.setRecordDataSnapshot(dataSnapshotService.isSnapshotPersistenceEnabled());

            // fresh data if requested or saving
            request.setUseDataSnapshot(!(options.isFreshData() || options.isSaveDataSnapshot()));

            request.setAsynchronous(options.isAsync());
            request.setCreateAuditEvent(true);

            return request;
        }
    }

    public JasperReportsContext getJasperReportsContext(Boolean interactive) {
        return jasperReportsRemoteContext;
    }

    /**
     * Get content type for resource type.
     *
     * @param outputFormat - resource output format
     * @return content type
     */
    public String getContentType(String outputFormat) {
        return servicesConfiguration.getExporter(outputFormat.toLowerCase()).getContentType();
    }

    /**
     * Strategy to run report of ReportUnit type
     */
    protected class RunReportUnitStrategy extends GenericRunReportStrategy<ReportUnit> {

        public String getConcreteReportURI(Resource reportResource) {
            return reportResource.getURIString();
        }

        // report type is ReportUnit for this strategy. Therefore cast is safe.
        @SuppressWarnings("unchecked")
        public ReportUnit getReportUnit(Resource report) {
            return (ReportUnit) report;
        }
    }

    protected interface RunReportStrategy {
        ReportUnitResult runReport(Resource reportResource, Map<String, Object> parameters, EngineService engine, JasperReportsContext context,
                ReportExecutionOptions options);

        ReportUnit getReportUnit(Resource reportResource);

        String getConcreteReportURI(Resource reportResource);
    }

	@Override
	public PaginationParameters getExportPaginationParameters(String reportURI, JasperPrint jasperPrint, String outputFormat) {
        PaginationParameters pagination = null;
        ReportExporter exporter = servicesConfiguration.getExporter(outputFormat.toLowerCase());
        if (exporter != null) {
        	JRPropertiesHolder propHolder = jasperPrint;
        	if (propHolder == null) {
                propHolder = getMainReport(reportURI);
        	}
        	
        	pagination = exporter.getPaginationParameters(propHolder);
        } else {
        	//avoid dealing with exceptions in calling code, returning default pagination
        	if (log.isDebugEnabled()) {
        		log.debug("did not find exporter " + outputFormat + " for pagination parameters");
        	}
        }
        
        if (pagination == null) {
        	//non null object expected
        	pagination = new PaginationParameters();
        }
		return pagination;
	}

	protected JasperReport getMainReport(String reportURI) {
		InputControlsContainer report = getResource(InputControlsContainer.class, reportURI);
		RunReportStrategy strategy = getStrategyForReport(report);
		String reportUnitURI = strategy.getConcreteReportURI(report);
		return engine.getMainJasperReport(createExecutionContext(), reportUnitURI);
	}
}
