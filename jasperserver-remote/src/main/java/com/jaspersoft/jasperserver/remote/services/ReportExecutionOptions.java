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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportExecutionOptions {

    private Boolean freshData = false;
    private Boolean saveDataSnapshot = false;
    private Boolean interactive = false;
    private PaginationParameters paginationParameters;
    private Boolean defaultIgnorePagination;
    private Boolean async = false;
    private String transformerKey;
    private String contextPath;
    private String defaultAttachmentsPrefixTemplate;
    private String requestId;
    private ReportContext reportContext;
    private JasperReportsContext jasperReportsContext;

    public ReportExecutionOptions(){
    	paginationParameters = new PaginationParameters();
    }
    
    public ReportExecutionOptions(ReportExecutionOptions source){
        freshData = source.isFreshData();
        saveDataSnapshot = source.isSaveDataSnapshot();
        interactive = source.isInteractive();
        paginationParameters = new PaginationParameters(source.paginationParameters);
        async = source.isAsync();
        transformerKey = source.getTransformerKey();
        contextPath = source.getContextPath();
        defaultAttachmentsPrefixTemplate = source.getDefaultAttachmentsPrefixTemplate();
        requestId = source.getRequestId();
        reportContext = source.getReportContext();
        jasperReportsContext = source.getJasperReportsContext();
        defaultIgnorePagination = source.getDefaultIgnorePagination();
    }

    public JasperReportsContext getJasperReportsContext() {
        return jasperReportsContext;
    }

    public ReportExecutionOptions setJasperReportsContext(JasperReportsContext jasperReportsContext) {
        this.jasperReportsContext = jasperReportsContext;
        return this;
    }

    public Boolean getDefaultIgnorePagination() {
        return defaultIgnorePagination;
    }

    /**
     * Setter for defaultIgnorePagination flag. It's required to hold ignorePagination flag value if it's not specified
     * in initial report execution options. In this case we can know it only after reportUnitResult is available
     * (reportUnitResult.isPaginated()). This value will be used as ignorePagination value
     * for all feather export without ignorePagination value.
     * @param defaultIgnorePagination
     * @return
     */
    public ReportExecutionOptions setDefaultIgnorePagination(Boolean defaultIgnorePagination) {
        this.defaultIgnorePagination = defaultIgnorePagination;
        return this;
    }

    public ReportContext getReportContext() {
        return reportContext;
    }

    public ReportExecutionOptions setReportContext(ReportContext reportContext) {
        this.reportContext = reportContext;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public ReportExecutionOptions setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getDefaultAttachmentsPrefixTemplate() {
        return defaultAttachmentsPrefixTemplate;
    }

    public ReportExecutionOptions setDefaultAttachmentsPrefixTemplate(String defaultAttachmentsPrefixTemplate) {
        this.defaultAttachmentsPrefixTemplate = defaultAttachmentsPrefixTemplate;
        return this;
    }

    public Boolean isAsync() {
        return async;
    }

    public ReportExecutionOptions setAsync(Boolean async) {
        this.async = async;
        return this;
    }

    public String getContextPath() {
        return contextPath;
    }

    public ReportExecutionOptions setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public Boolean isFreshData() {
        return freshData;
    }

    public ReportExecutionOptions setFreshData(Boolean freshData) {
        this.freshData = freshData;
        return this;
    }

    public Boolean isSaveDataSnapshot() {
        return saveDataSnapshot;
    }

    public ReportExecutionOptions setSaveDataSnapshot(Boolean saveDataSnapshot) {
        this.saveDataSnapshot = saveDataSnapshot;
        return this;
    }

    /**
     * ignorePagination is optional parameter. Can be null. Check for null is required.
     * @return the ignorePagination value
     */
    public Boolean getIgnorePagination() {
        return (paginationParameters == null || paginationParameters.getPaginated() == null) ? null 
        		: !paginationParameters.getPaginated();
    }

    public ReportExecutionOptions setIgnorePagination(Boolean ignorePagination) {
    	if (ignorePagination != null) {
    		if (paginationParameters == null) {
    			paginationParameters = new PaginationParameters();
    		}
    		paginationParameters.setPaginated(!ignorePagination);
    	}
        return this;
    }

    public PaginationParameters getPaginationParameters() {
		return paginationParameters;
	}
    
	public ReportExecutionOptions setPaginationParameters(PaginationParameters paginationParameters) {
		this.paginationParameters = paginationParameters != null ? paginationParameters : new PaginationParameters();
		return this;
	}

	public void setPaginationDefaults(PaginationParameters paginationDefaults) {
		if (paginationParameters == null) {
			paginationParameters = new PaginationParameters(paginationDefaults);
		} else {
			paginationParameters.setDefaults(paginationDefaults);
		}
	}

    public String getTransformerKey() {
        return transformerKey;
    }

    public ReportExecutionOptions setTransformerKey(String transformerKey) {
        this.transformerKey = transformerKey;
        return this;
    }

    public Boolean isInteractive() {
        return interactive;
    }

    public ReportExecutionOptions setInteractive(Boolean interactive) {
        this.interactive = interactive;
        return this;
    }
}