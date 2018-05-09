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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecution.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@XmlRootElement
public class ReportExecution {
    private final static Log log = LogFactory.getLog(ReportExecution.class);
    private ExecutionStatus status;
    private Integer totalPages;
    private Integer currentPage;
    private ErrorDescriptor errorDescriptor;
    private final ExportsContainer exports = new ExportsContainer();
    private ReportUnitResult reportUnitResult;
    private Map<String, String[]> rawParameters;
    private String requestId;
    private String reportURI;
    private ReportExecutionOptions options;
    private final Lock lock;
    private final Condition resultExist;
    private volatile Map<String, Object> convertedParameters;
    private volatile ReportExecution relatedExecution;

    public ReportExecution() {
        lock = new ReentrantLock();
        resultExist = lock.newCondition();
    }

    public ReportExecution getRelatedExecution() {
        return relatedExecution;
    }

    /**
     * Setter of relatedExecution. It is required to get export with alternate pagination mode.
     * @param relatedExecution - related report execution
     */
    public void setRelatedExecution(ReportExecution relatedExecution) {
        this.relatedExecution = relatedExecution;
    }

    @XmlTransient
    public Map<String, Object> getConvertedParameters() {
        return convertedParameters;
    }

    /**
     * Setter for converted parameters. It's derivation of input control logic processed raw parameters.
     * They are cached here to be reused if report rerun is required.
     * @param convertedParameters - converted parameters to cache
     */
    public void setConvertedParameters(Map<String, Object> convertedParameters) {
        this.convertedParameters = convertedParameters;
    }

    @XmlTransient
    public Map<String, String[]> getRawParameters() {
        return rawParameters;
    }

    public void setRawParameters(Map<String, String[]> rawParameters) {
        // reset cached converted parameters if new raw parameters are here.
        this.convertedParameters = null;
        this.rawParameters = rawParameters;
    }

    @XmlTransient
    public ReportExecutionOptions getOptions() {
        return options;
    }

    public void setOptions(ReportExecutionOptions options) {
        this.options = options;
    }

    @XmlTransient
    public ReportUnitResult getFinalReportUnitResult() {
        lock.lock();
        try {
            while (reportUnitResult == null && status != ExecutionStatus.cancelled && status != ExecutionStatus.failed) {
                if(log.isDebugEnabled()){
                    log.debug("Waiting for report unit result of report execution '" + getRequestId() +"'");
                }
                resultExist.await();
            }
            ErrorDescriptor descriptor = null;
            switch (status){
                case failed:{
                    descriptor = errorDescriptor != null ? errorDescriptor :
                            new ErrorDescriptor().setErrorCode("report.execution.failed").setMessage("Report execution failed");
                }
                break;
                case cancelled:{
                    descriptor = new ErrorDescriptor().setErrorCode("report.execution.cancelled")
                            .setMessage("Report execution cancelled");
                }
                break;
            }
            if (descriptor != null) throw new RemoteException(descriptor);
        } catch (InterruptedException e) {
            throw new JSException(e);
        } finally {
            lock.unlock();
        }
        if(log.isDebugEnabled()){
            log.debug("About to get report unit result of report execution '" + getRequestId() +"'");
        }
        return reportUnitResult;
    }

    @XmlTransient
    public ReportUnitResult getReportUnitResult() {
        return reportUnitResult;
    }

    public void setReportUnitResult(ReportUnitResult reportUnitResult) {
        lock.lock();
        try {
            this.reportUnitResult = reportUnitResult;
            if(log.isDebugEnabled()){
                log.debug("Report unit result is provided for report execution '" + getRequestId() +"'");
            }
            resultExist.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @XmlElement(name = "export")
    @XmlElementWrapper(name = "exports")
    public Set<ExportExecution> getExportsSet() {
        return exports != null && !exports.isEmpty() ? new HashSet<ExportExecution>(exports.values()) : null;
    }

    public void setExportsSet(Set<ExportExecution> exportsSet) {
        exports.clear();
        if (exportsSet != null) {
            Map<ExportExecutionOptions, ExportExecution> exports = new HashMap<ExportExecutionOptions, ExportExecution>();
            for (ExportExecution currentExport : exportsSet) {
                exports.put(currentExport.getOptions(), currentExport);
            }
            exports.putAll(exports);
        }
    }

    @XmlTransient
    public ExportsContainer getExports() {
        return exports;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        lock.lock();
        try {
            this.errorDescriptor = errorDescriptor;
            if (errorDescriptor != null) {
                this.status = ExecutionStatus.failed;
                if (log.isDebugEnabled()) {
                    log.debug("Status of report execution '" + getRequestId() + "' is changed to " + status + ". Error descriptor: " + errorDescriptor);
                }
                if (!exports.isEmpty()) {
                    for (ExportExecution exportExecution : exports.values()) {
                        exportExecution.setErrorDescriptor(errorDescriptor);
                    }
                }
            }
            // error happened. Let's signal to react.
            resultExist.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String getReportURI() {
        return reportURI;
    }

    public void setReportURI(String reportURI) {
        this.reportURI = reportURI;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        lock.lock();
        try {
            if (this.status != status) {
                this.status = status;
                if (log.isDebugEnabled()) {
                    log.debug("Status of report execution '" + getRequestId() + "' is changed to " + status);
                }
                if (status == ExecutionStatus.cancelled || status == ExecutionStatus.ready || status == ExecutionStatus.queued) {
                    if (status == ExecutionStatus.queued) {
                        setReportUnitResult(null);
                        setErrorDescriptor(null);
                    }
                    if (!exports.isEmpty()) {
                        for (ExportExecution exportExecution : exports.values()) {
                            if (status == ExecutionStatus.cancelled) {
                                exportExecution.setStatus(ExecutionStatus.cancelled);
                            }
                        }
                    }
                }
                // status is changed, let's signal to react
                resultExist.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
