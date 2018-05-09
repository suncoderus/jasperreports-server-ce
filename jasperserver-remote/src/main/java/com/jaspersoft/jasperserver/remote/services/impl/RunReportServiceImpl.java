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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportExecutionStatusInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.SchedulerReportExecutionStatusSearchCriteria;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.JSReportExecutionRequestCancelledException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import com.jaspersoft.jasperserver.remote.exception.ExportExecutionRejectedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.reports.HtmlExportStrategy;
import com.jaspersoft.jasperserver.remote.services.ExportExecution;
import com.jaspersoft.jasperserver.remote.services.ExportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecutor;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;
import com.jaspersoft.jasperserver.remote.services.ReportOutputResource;
import com.jaspersoft.jasperserver.remote.services.RunReportService;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import com.jaspersoft.jasperserver.war.action.ReportParametersUtils;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.GenericElementReportTransformer;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.web.servlets.AsyncJasperPrintAccessor;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportExecutionStatus;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run Report service
 * Run a report unit using the passing in parameters and options
 *
 * @author ykovalchyk
 * @version $Id$
 */
@Service("runReportService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RunReportServiceImpl implements RunReportService, Serializable, DisposableBean {
    private final static Log log = LogFactory.getLog(RunReportServiceImpl.class);
    private final static Pattern FILE_NAME_PATTERN = Pattern.compile(".*/([^/]+)$");
    @Resource
    private AuditHelper auditHelper;
    @Resource(name = "concreteEngineService")
    private EngineService engine;
    @Resource(name = "${bean.engineServiceTarget}")
    private EngineService unsecuredEngine;
    @Resource(name="loggableExecutorService")
    private Executor asyncExecutor;
    @Resource
    private InputControlsLogicService inputControlsLogicService;
    @Resource
    private ReportExecutor reportExecutor;
    @Resource(name = "concreteRepository")
    private RepositoryService repositoryService;
    @Resource
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;
    @Resource
    private Integer waitForFinalJasperPrintMs;
    @Resource
    private Map<String, HtmlExportStrategy> htmlExportStrategies;
    @Resource(name = "defaultHtmlExportStrategy")
    private HtmlExportStrategy defaultHtmlExportStrategy;
    @Resource(name = "concreteVirtualizerFactory")
    private VirtualizerFactory virtualizerFactory;
    @Resource
    private SecureExceptionHandler secureExceptionHandler;

    private final Map<String, ReportExecution> executions = new ConcurrentHashMap<String, ReportExecution>();

    public ReportExecution getReportExecution(String requestId) throws ResourceNotFoundException {
        final ReportExecution execution = executions.get(requestId);
        if (execution == null) {
            throw new ResourceNotFoundException(requestId);
        }
        actualizeExecutionStatus(execution);
        return execution;
    }

    protected void actualizeExecutionStatus(ReportExecution execution) {
        final ReportExecutionStatus reportStatus = execution != null && execution.getReportUnitResult() != null && execution.getReportUnitResult().getJasperPrintAccessor() != null
                ? execution.getReportUnitResult().getJasperPrintAccessor().getReportStatus() : null;
        if (reportStatus != null) {
            ExecutionStatus executionStatus = ExecutionStatus.queued;
            final ReportExecutionStatus.Status status = reportStatus.getStatus();
            switch (status) {
                case CANCELED:
                    executionStatus = ExecutionStatus.cancelled;
                    break;
                case ERROR: {
                    executionStatus = ExecutionStatus.failed;
                    execution.setErrorDescriptor(secureExceptionHandler.handleException(reportStatus.getError()));
                }
                break;
                case FINISHED: {
                    executionStatus = ExecutionStatus.ready;
                    execution.setTotalPages(reportStatus.getTotalPageCount());
                }
                break;
                case RUNNING: {
                    executionStatus = ExecutionStatus.execution;
                    execution.setCurrentPage(reportStatus.getCurrentPageCount());
                }
                break;
            }
            execution.setStatus(executionStatus);
        }
    }

    public void startReportExecution(ReportExecution reportExecution) {
        startReportExecution(reportExecution, null);
    }

    public void startReportExecution(final ReportExecution reportExecution,
            ReportExecutionOptions reportExecutionOptions) {
        final ReportExecutionOptions options = reportExecutionOptions != null
                ? reportExecutionOptions : reportExecution.getOptions();
        if(ExecutionStatus.execution == reportExecution.getStatus()){
            cancelReportExecution(reportExecution.getRequestId());
        }
        // if we are restarting reportExecution, then we need to remove link to relatedExecution. It will be ran again
        // if alternative pagination mode will be needed
        reportExecution.setRelatedExecution(null);
        reportExecution.setStatus(ExecutionStatus.queued);
        final Locale locale = LocaleContextHolder.getLocale();
        final TimeZone timeZone = TimeZoneContextHolder.getTimeZone();
        final SecurityContext context = SecurityContextHolder.getContext();
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LocaleContextHolder.setLocale(locale);
                TimeZoneContextHolder.setTimeZone(timeZone);
                SecurityContextHolder.setContext(context);
                ReportUnitResult reportUnitResult = null;
                ErrorDescriptor errorDescriptor = null;

                try {
                    final String reportUnitUri = reportExecution.getReportURI();
                    Map<String, Object> convertedParameters = reportExecution.getConvertedParameters();
                    if (convertedParameters == null) {
                        synchronized (reportExecution) {
                            convertedParameters = reportExecution.getConvertedParameters();
                            if (convertedParameters == null) {
                                // no cached converted parameters. Let's make them by running input controls logic
                                // on raw parameters
                                final Map<String, String[]> rawParameters = reportExecution.getRawParameters();
                                // convert parameters from raw strings to objects
                                try {
                                    convertedParameters = executeInputControlsCascadeWithRawInput(reportUnitUri, rawParameters);
                                } catch (CascadeResourceNotFoundException e) {
                                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                                } catch (InputControlsValidationException e) {
                                    // raw type is used in core class. Cast is safe.
                                    @SuppressWarnings("unchecked")
                                    List<ValidationError> errors = e.getErrors().getErrors();
                                    List<String> errorParameters = new ArrayList<String>();
                                    for (ValidationError error : errors) {
                                        errorParameters.add(error.toString());
                                    }
                                    throw new RemoteException(new ErrorDescriptor().setErrorCode("input.controls.validation.error")
                                            .setMessage("Input controls validation failure").addParameters(errorParameters.toArray()));
                                }
                                // forward parameters without input control defined
                                for (String currentKey : rawParameters.keySet()) {
                                    String[] currentValue = rawParameters.get(currentKey);
                                    if (currentValue != null && !convertedParameters.containsKey(currentKey)) {
                                        Object valueToForward;
                                        if (currentValue.length == 1) {
                                            // forward as single value
                                            valueToForward = currentValue[0];
                                        } else {
                                            // forward as collection
                                            Collection<String> collection = new ArrayList<String>();
                                            collection.addAll(Arrays.asList(currentValue));
                                            valueToForward = collection;
                                        }
                                        convertedParameters.put(currentKey, valueToForward);
                                    }
                                }
                            }
                            // cache converted parameter for feather runes.
                            reportExecution.setConvertedParameters(convertedParameters);
                        }
                    }
                    reportExecution.setStatus(ExecutionStatus.execution);
                    reportUnitResult = reportExecutor.runReport(reportUnitUri, convertedParameters, options);
                    //If reportExecutionOptions.ignorePagination wasn't set, then only here we can know what was a value
                    // for ignorePagination in current report execution. Let's cache it to use for further executions if
                    // not specified explicitly.
                    options.setDefaultIgnorePagination(!reportUnitResult.isPaginated());

                } catch (RemoteException e) {
                    errorDescriptor = e.getErrorDescriptor();
                } catch (JSReportExecutionRequestCancelledException e){
                    // do nothing in this thread. Correct status is set by the thread, cancelled the execution.
                } catch (Exception e) {
                    // if report execution is interrupted, then it's cancelled. Do nothing in this case.
                    if(!(e.getCause() instanceof InterruptedException)){
                        // if not the case, then let's build error descriptor
                        errorDescriptor = errorDescriptorBuildingService.buildErrorDescriptor(e);
                    }
                }
                if (errorDescriptor != null) {
                    reportExecution.setErrorDescriptor(errorDescriptor);
                }
                if (reportUnitResult != null) {
                    final String requestId = options.getRequestId();
                    if (!requestId.equals(reportUnitResult.getRequestId())) {
                        throw new IllegalStateException("Report unit request and report unit result should have the same ID. RequestId: "
                                + requestId + " ResultId: " + reportUnitResult.getRequestId());
                    }
                    reportExecution.setReportUnitResult(reportUnitResult);
                    // wait for report execution to complete for execution status actualization
                    reportUnitResult.getJasperPrint();
                    actualizeExecutionStatus(reportExecution);
                }
            }
        });
    }

    @Override
    public ReportExecution getReportExecutionFromRawParameters(final String reportUnitURI, final Map<String, String[]> rawParameters,
            ReportExecutionOptions inputOptions, ExportExecutionOptions exportOptions) throws RemoteException, JSValidationException {
        // basic synchronous validation:
        // check report URI for null
        if (reportUnitURI == null) {
            throw new MandatoryParameterNotFoundException("reportUnitUri");
        }
        // check report resource for existence
        final com.jaspersoft.jasperserver.api.metadata.common.domain.Resource resource = repositoryService
                .getResource(ExecutionContextImpl.getRuntimeExecutionContext(), reportUnitURI);
        if (resource == null) {
            throw new ResourceNotFoundException(reportUnitURI);
        } else if(!reportExecutor.isRunnableResource(resource)){
            throw new IllegalParameterValueException("reportURI", reportUnitURI);
        }
        
        final ReportExecutionOptions options = inputOptions != null ? inputOptions : new ReportExecutionOptions();
        
        String exportFormat = (exportOptions != null && exportOptions.getOutputFormat() != null 
        		&& !exportOptions.getOutputFormat().isEmpty()) ? exportOptions.getOutputFormat().toUpperCase() : null;
        if (exportFormat != null) {
            PaginationParameters exportPagination = getExportPaginationParameters(reportUnitURI, null, exportFormat);
            options.setPaginationDefaults(exportPagination);
        }
        
        final ReportExecution execution = new ReportExecution();
        final String requestId;
        if (options.getRequestId() == null) {
            requestId = UUID.randomUUID().toString();
            options.setRequestId(requestId);
        } else {
            requestId = options.getRequestId();
        }
        execution.setOptions(options);
        execution.setRequestId(requestId);
        execution.setReportURI(reportUnitURI);
        execution.setRawParameters(rawParameters);
        
        executions.put(requestId, execution);
        startReportExecution(execution);
        if(!options.isAsync()){
            // wait till report execution is complete
            try{
                execution.getFinalReportUnitResult().getJasperPrintAccessor().getFinalJasperPrint();
            } catch (RemoteException e){
                throw e;
            } catch (Exception e){
                // if report fails in non async mode, then send error immediately
                throw new IllegalParameterValueException(secureExceptionHandler.handleException(e));
            }
        }
        if (exportFormat != null) {
            final ExportExecution exportExecution = executeExport(exportOptions, execution);
            if (!options.isAsync()) {
                // wait till export is complete
                try {
                    exportExecution.getFinalOutputResource();
                } catch (ExportExecutionRejectedException e) {
                    // if report fails in non async mode, then send error immediately
                    throw new IllegalParameterValueException(e.getErrorDescriptor());
                }
            }
        }
        return getReportExecution(requestId);
    }

    public ExportExecution executeExport(final String executionId, final ExportExecutionOptions exportOptions) throws RemoteException {
        final ReportExecution execution = executions.get(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        return executeExport(exportOptions, execution);
    }

    protected ExportExecution executeExport(ExportExecutionOptions exportOptions, final ReportExecution reportExecution) throws RemoteException {
        final ExportExecution exportExecution = new ExportExecution();
        exportExecution.setSecureExceptionHandler(secureExceptionHandler);
        exportExecution.setStatus(ExecutionStatus.queued);
        exportExecution.setOptions(exportOptions);
        reportExecution.getExports().put(exportExecution);
        startExport(reportExecution, exportExecution);
        return exportExecution;
    }

    protected void startExport(final ReportExecution reportExecution, final ExportExecution exportExecution) {
        final SecurityContext context = SecurityContextHolder.getContext();
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SecurityContextHolder.setContext(context);
                try {
                    // wait for report unit result before start export.
                    // If report execution is failed or cancelled, then no need to start export.
                    final ReportUnitResult finalReportUnitResult = reportExecution.getFinalReportUnitResult();
                    if (finalReportUnitResult == null) {
                        exportExecution.setErrorDescriptor(new ErrorDescriptor()
                                .setErrorCode("export.failed")
                                .setMessage("Export can't be executed. Report execution status is '"
                                        + reportExecution.getStatus().toString() + "'."
                                        + (reportExecution.getErrorDescriptor() != null
                                        ? " " + reportExecution.getErrorDescriptor().toString() : "")));
                    } else {
                        executeExport(exportExecution, reportExecution);
                    }
                } catch (Exception e) {
                    exportExecution.setErrorDescriptor(secureExceptionHandler.handleException(e));
                }
            }
        });
    }

    /**
     * If client wants to get export with another pagination mode, then we have to run report again with
     * alternate ignorePagination flag value. This method is intended to do it.
     * @param originalReportExecution - the original report execution to use as a template for related execution.
     * @param ignorePagination - ignore pagination flag for report execution
     * @return related report execution instance
     */
    protected ReportExecution runRelatedExecution(ReportExecution originalReportExecution, PaginationParameters pagination){
        final ReportExecution relatedReportExecution = new ReportExecution();
        final String requestId = UUID.randomUUID().toString();
        relatedReportExecution.setRequestId(requestId);
        relatedReportExecution.setReportURI(originalReportExecution.getReportURI());
        relatedReportExecution.setRawParameters(originalReportExecution.getRawParameters());
        relatedReportExecution.setConvertedParameters(originalReportExecution.getConvertedParameters());
        relatedReportExecution.setOptions(new ReportExecutionOptions(originalReportExecution.getOptions()).setPaginationParameters(pagination));
        executions.put(requestId, relatedReportExecution);
        startReportExecution(relatedReportExecution);
        originalReportExecution.setRelatedExecution(relatedReportExecution);
        return relatedReportExecution;
    }

    protected void executeExport(ExportExecution exportExecution, ReportExecution reportExecution) {
        final ReportUnitResult reportUnitResult = reportExecution.getFinalReportUnitResult();
        JasperPrintAccessor jasperPrintAccessor = reportUnitResult.getJasperPrintAccessor();

        String rawOutputFormat = exportExecution.getOptions().getOutputFormat();
        String outputFormat = rawOutputFormat != null ? rawOutputFormat.toUpperCase() : Argument.RUN_OUTPUT_FORMAT_PDF;
        PaginationParameters exportPagination = getExportPaginationParameters(reportUnitResult.getReportUnitURI(), jasperPrintAccessor, 
        		outputFormat);
        
        final Boolean ignorePagination = exportExecution.getOptions().getIgnorePagination();
        if (ignorePagination != null) {
        	exportPagination = new PaginationParameters(exportPagination);
        	exportPagination.setPaginated(!ignorePagination);
        }
        
        if(!reportUnitResult.matchesPagination(exportPagination)){
            // oops, we have to provide export with another pagination mode.
            // Let's check if we already have jasperPrint with this mode generated:
            ReportExecution relatedExecution = reportExecution.getRelatedExecution();
            if(relatedExecution == null
            		|| !relatedExecution.getFinalReportUnitResult().matchesPagination(exportPagination)){
                synchronized (reportExecution){
                    relatedExecution = reportExecution.getRelatedExecution();
                    if(relatedExecution == null
                    		|| !relatedExecution.getFinalReportUnitResult().matchesPagination(exportPagination)){
                        // No. We don't have generated jasperPrint for this pagination mode.
                        // Let's run another report execution to generate it.
                    	//TODO use a data snapshot to refill the report with a different pagination
                        relatedExecution = runRelatedExecution(reportExecution, exportPagination);
                        // link it to current report execution to track it's status and to be able to cancel it.
                        reportExecution.setRelatedExecution(relatedExecution);
                    	//TODO keep a map of ReportExecution per PaginationParameters instead of replacing the single related execution
                        //not changing now (6.4.0 hotfix) because it would change the ReportExecution JSON/XML serialization
                    }
                }
            }
            // let's take a jasperPrintAccessor from related execution with appropriate ignorePagination flag value
            jasperPrintAccessor = relatedExecution.getFinalReportUnitResult().getJasperPrintAccessor();
        }
        try {
            ExportExecutionOptions exportOptions = exportExecution.getOptions();
            Integer exportEndPage = null;
            if (exportOptions.getPages() != null) {
                exportEndPage = exportOptions.getPages().getPage() != null ? exportOptions.getPages().getPage() : exportOptions.getPages().getEndPage();
            }
            JasperPrint jasperPrint;
            boolean isOutputFinal = true;
            if (exportEndPage != null) {
                // convert page number to pageIndex (0-based)
                exportEndPage = exportEndPage - 1;
                if(waitForFinalJasperPrintMs > 0 && exportEndPage == 0
                        && jasperPrintAccessor instanceof AsyncJasperPrintAccessor){
                    ((AsyncJasperPrintAccessor) jasperPrintAccessor).waitForFinalJasperPrint(waitForFinalJasperPrintMs);
                }
                // in case if jasperPrintAccessor is AsyncJasperPrintAccessor and pageStatus() method is invoked,
                // then current thread waits till requested page is generated.
                final ReportPageStatus reportPageStatus = jasperPrintAccessor.pageStatus(exportEndPage, null);
                isOutputFinal = reportPageStatus.isPageFinal();
                jasperPrint = jasperPrintAccessor.getJasperPrint();
            } else {
                jasperPrint = jasperPrintAccessor.getFinalJasperPrint();
                final String anchor = exportOptions.getAnchor();
                if(anchor != null && !anchor.isEmpty()){
                    final JRPrintAnchorIndex jrPrintAnchorIndex = jasperPrint.getAnchorIndexes().get(anchor);
                    if(jrPrintAnchorIndex != null){
                        exportOptions.setPages(new ReportOutputPages().setPage(jrPrintAnchorIndex.getPageIndex() + 1));
                    } else {
                        throw new IllegalParameterValueException("anchor", anchor);
                    }
                }
            }
            final ReportExecutionStatus.Status status = jasperPrintAccessor.getReportStatus().getStatus();
            switch (status){
                case CANCELED: exportExecution.setStatus(ExecutionStatus.cancelled);
                    break;
                case ERROR: exportExecution.setErrorDescriptor(errorDescriptorBuildingService
                        .buildErrorDescriptor(jasperPrintAccessor.getReportStatus().getError()));
                    break;
                default:{
                    exportExecution.setStatus(ExecutionStatus.execution);
                    if (Argument.RUN_OUTPUT_FORMAT_HTML.equalsIgnoreCase(outputFormat)) {
                        HtmlExportStrategy htmlExportStrategy = htmlExportStrategies.get(exportExecution.getOptions().getMarkupType()) != null
                                ? htmlExportStrategies.get(exportExecution.getOptions().getMarkupType()) : defaultHtmlExportStrategy;
                        htmlExportStrategy.export(reportExecution, exportExecution, jasperPrint);
                    } else {
                        generateReportOutput(reportExecution, jasperPrint, outputFormat,
                                exportExecution, exportExecution.getOptions().getPages());
                    }
                    exportExecution.getOutputResource().setOutputFinal(isOutputFinal);
                    exportExecution.setStatus(ExecutionStatus.ready);
                }
            }
        } catch (RemoteException ex) {
            exportExecution.setErrorDescriptor(ex.getErrorDescriptor());
        } catch (Exception ex) {
            log.debug("Unexpected error occurs during export", ex);
            exportExecution.setErrorDescriptor(secureExceptionHandler.handleException(ex));
        }
    }

    protected PaginationParameters getExportPaginationParameters(String reportURI, JasperPrintAccessor jasperPrintAccessor, 
    		String outputFormat) {
    	PaginationParameters pagination;
    	if (Argument.RUN_OUTPUT_FORMAT_HTML.equalsIgnoreCase(outputFormat)
    			|| Argument.RUN_OUTPUT_FORMAT_JRPRINT.equalsIgnoreCase(outputFormat)) {
    		//default params
    		pagination = new PaginationParameters();
    	} else {
    		pagination = reportExecutor.getExportPaginationParameters(reportURI, 
    				jasperPrintAccessor == null ? null : jasperPrintAccessor.getJasperPrint(), outputFormat);
    	}
		return pagination;
	}

    public ReportOutputResource getOutputResource(String executionId, ExportExecutionOptions exportExecutionOptions) throws RemoteException {
        final ReportExecution execution = executions.get(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        return executeExport(exportExecutionOptions, execution).getFinalOutputResource();
    }

    public ReportOutputResource getOutputResource(String executionId, String exportId) throws RemoteException {
        return getExportExecution(executionId, exportId).getFinalOutputResource();
    }

    public ExportExecution getExportExecution(String executionId, String exportId) throws ResourceNotFoundException {
        final ReportExecution execution = executions.get(executionId);
        if (execution == null) throw new ResourceNotFoundException(executionId);
        final ExportExecution exportExecution = execution.getExports().get(exportId);
        if (exportExecution == null) throw new ResourceNotFoundException(exportId);
        return exportExecution;
    }

    public ReportOutputResource getAttachment(String executionId, String exportId, String attachmentName) throws ResourceNotFoundException {
        ReportOutputResource outputResource = getExportExecution(executionId, exportId).getAttachments().get(attachmentName);
        if (outputResource == null) throw new ResourceNotFoundException(attachmentName);
        return outputResource;
    }

    @Override
    public ReportOutputResource getReportOutputFromRawParameters(String reportUnitURI, Map<String, String[]> rawParameters,
            ReportExecutionOptions executionOptions, ExportExecutionOptions exportOptions) throws RemoteException {
        final ReportExecution execution = getReportExecutionFromRawParameters(reportUnitURI, rawParameters, executionOptions, exportOptions);
        return getOutputResource(execution.getRequestId(), exportOptions);
    }

    protected Map<String, Object> executeInputControlsCascadeWithRawInput(String reportUnitUri, Map<String, String[]> rawInputParameters) throws CascadeResourceNotFoundException, InputControlsValidationException {
        final List<ReportInputControl> inputControlsForReport = inputControlsLogicService.getInputControlsWithValues(reportUnitUri, null, rawInputParameters);
        final Map<String, String[]> inputControlFormattedValues = ReportParametersUtils.getValueMapFromInputControls(inputControlsForReport);
        return inputControlsLogicService.getTypedParameters(reportUnitUri, inputControlFormattedValues);
    }

    /**
     * @param reportExecution - the report execution instance. Holds all execution context related things.
     * @param jasperPrint     - filled with data jasper print object
     * @param rawOutputFormat - output format in raw format
     * @param exportExecution - export execution model
     * @param pages           - what pages should be exported
     * @throws RemoteException
     */
    protected void generateReportOutput(ReportExecution reportExecution, JasperPrint jasperPrint,
            String outputFormat, ExportExecution exportExecution, ReportOutputPages pages) throws RemoteException {
        try {
            // Export...
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ReportExecutionOptions reportExecutionOptions = reportExecution.getOptions();
            if (outputFormat.equals(Argument.RUN_OUTPUT_FORMAT_JRPRINT)) {
                if (log.isDebugEnabled())
                    log.debug("Returning JasperPrint");
                if (reportExecutionOptions.getTransformerKey() != null) {
                    if (log.isDebugEnabled())
                        log.debug("Transforming JasperPrint generic element for key " + reportExecutionOptions.getTransformerKey());
                    GenericElementReportTransformer.transformGenericElements(reportExecutor.getJasperReportsContext(reportExecutionOptions.isInteractive()), jasperPrint, reportExecutionOptions.getTransformerKey());
                }
                JRSaver.saveObject(jasperPrint, bos);
                exportExecution.setOutputResource(new ReportOutputResource().setContentType("application/octet-stream").setData(bos.toByteArray()));
            } else {
                HashMap<String, Object> exportParameters = new HashMap<String, Object>(reportExecution.getRawParameters());
                if (pages != null) exportParameters.put(Argument.RUN_OUTPUT_PAGES, pages);
                Map<JRExporterParameter, Object> exporterParams;
                final String reportURI = reportExecution.getReportURI();
                auditHelper.createAuditEvent("export");
                auditHelper.addPropertyToAuditEvent("export", "uris", reportURI);
                try {
                    exporterParams = reportExecutor.exportReport(reportURI, jasperPrint, outputFormat, bos, exportParameters);
                    if (log.isDebugEnabled())
                        log.debug("Exporter params: " + Arrays.asList(exporterParams.keySet().toArray()));
                } catch (RemoteException e) {
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw e;
                } catch (Exception e) {
                    log.debug("Error exporting report", e);
                    auditHelper.addExceptionToAllAuditEvents(e);
                    throw new RemoteException(
                            new ErrorDescriptor()
                                    .setErrorCode("webservices.error.errorExportingReportUnit").setParameters(e.getMessage()), e
                    );
                } finally {
                    try {
                        bos.close();
                    } catch (IOException ex) {
                        log.error("caught exception: " + ex.getMessage(), ex);
                    }
                }
                final Matcher matcher = FILE_NAME_PATTERN.matcher(reportURI);
                exportExecution.setOutputResource(new ReportOutputResource()
                        .setContentType(reportExecutor.getContentType(outputFormat))
                        .setData(bos.toByteArray())
                        .setFileName((matcher.find() ? matcher.group(1) : "report") + "." + outputFormat.toLowerCase())
                        .setPages(pages != null ? pages.toString() : null));
            }
        } catch (RemoteException e) {
            throw e;
        } catch (ServiceException e) {
            log.error("caught exception: " + e.getMessage(), e);
        } catch (Throwable e) {
            log.error("caught Throwable exception: " + e.getMessage(), e);
        }
    }

    /**
     * @param searchCriteria - search criteria
     * @return set of currently running report's information
     */
    public Set<ReportExecutionStatusInformation> getCurrentlyRunningReports(SchedulerReportExecutionStatusSearchCriteria searchCriteria) {
        Set<ReportExecutionStatusInformation> result = null;
        List<ReportExecutionStatusInformation> reportExecutionStatusList = searchCriteria != null ?
                engine.getSchedulerReportExecutionStatusList(searchCriteria) : engine.getReportExecutionStatusList();
        if (reportExecutionStatusList != null && !reportExecutionStatusList.isEmpty()) {
            result = new HashSet<ReportExecutionStatusInformation>();
            result.addAll(reportExecutionStatusList);
        }
        return result;
    }

    public Boolean cancelReportExecution(String requestId) throws RemoteException {
        return cancelReportExecution(requestId, engine);
    }

    protected Boolean cancelReportExecution(String requestId, EngineService effectiveEngine) throws RemoteException {
        final boolean cancelled = effectiveEngine.cancelExecution(requestId);
        final ReportExecution reportExecution = executions.get(requestId);
        if(cancelled && reportExecution != null){
            // update report execution status
            reportExecution.setStatus(ExecutionStatus.cancelled);
        }
        return cancelled;
    }

    public Boolean deleteReportExecution(String requestId) throws RemoteException {
        try {
            if (executions.containsKey(requestId)) {
                cancelReportExecution(requestId);
                ReportExecution execution = executions.get(requestId);
                if (execution.getStatus() == ExecutionStatus.ready) {
                    virtualizerFactory.disposeReport(execution.getFinalReportUnitResult());
                }
            }
            return (executions.remove(requestId) != null);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e1) {
            throw new RemoteException(
                    new ErrorDescriptor().setErrorCode("Unexpected error while removing ReportExecution")
                            .setParameters(e1.getMessage()), e1);
        }
    }

    @Override
    public void destroy() throws Exception {
        for (String requestId : executions.keySet()) {
            ReportExecution execution = executions.get(requestId);
            try {
                cancelReportExecution(requestId, unsecuredEngine);
                if (execution.getStatus() == ExecutionStatus.ready) {
                    virtualizerFactory.disposeReport(execution.getFinalReportUnitResult());
                }
            } catch (RuntimeException ex) {
                log.warn("Report execution cleanup failed: ", ex);
            }
        }
        executions.clear();
    }
}
