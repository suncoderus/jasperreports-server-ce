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

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.remote.exception.ExportExecutionRejectedException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ExportExecution {
    private final static Log log = LogFactory.getLog(ExportExecution.class);
    private String id = UUID.randomUUID().toString();
    private ExportExecutionOptions options;
    private ExecutionStatus status;
    private ErrorDescriptor errorDescriptor;
    private ReportOutputResource outputResource;
    private Map<String, ReportOutputResource> attachments = new ConcurrentHashMap<String, ReportOutputResource>();
    private Lock lock;
    private Condition condition;
    private SecureExceptionHandler secureExceptionHandler;

    public ExportExecution() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlTransient
    public ExportExecutionOptions getOptions() {
        return options;
    }

    public void setOptions(ExportExecutionOptions options) {
        this.options = options;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        lock.lock();
        try {
            this.status = status;
            if (log.isDebugEnabled()) {
                log.debug("Status of export execution '" + getId() + "' is changed to " + status);
            }
            if (status != ExecutionStatus.queued && status != ExecutionStatus.execution) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        lock.lock();
        try {
            this.status = ExecutionStatus.failed;
            this.errorDescriptor = errorDescriptor;
            if (log.isDebugEnabled()) {
                log.debug("Status of export execution '" + getId() + "' is changed to " + status + ". Error descriptor: " + errorDescriptor);
            }
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public ReportOutputResource getOutputResource() {
        return outputResource;
    }

    @XmlTransient
    public ReportOutputResource getFinalOutputResource() throws RemoteException {
        lock.lock();
        ReportOutputResource result;
        try {
            while (status == ExecutionStatus.queued || status == ExecutionStatus.execution) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RemoteException(secureExceptionHandler.handleException(e));
                }
            }
            final ExecutionStatus currentStatus = status;
            if (currentStatus == null) throw new IllegalStateException("Status shouldn't be null");
            ErrorDescriptor descriptor = null;
            result = outputResource;
            switch (currentStatus) {
                case failed: {
                    descriptor = errorDescriptor != null ? errorDescriptor :
                            new ErrorDescriptor().setErrorCode("export.failed").setMessage("Export failed");
                }
                break;
                case cancelled: {
                    descriptor = new ErrorDescriptor().setErrorCode("export.cancelled")
                            .setMessage("Export cancelled");
                }
                break;
            }
            if (descriptor != null) throw new ExportExecutionRejectedException(descriptor);
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void setOutputResource(ReportOutputResource outputResource) {
        this.outputResource = outputResource;
    }

    @XmlTransient
    public Map<String, ReportOutputResource> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, ReportOutputResource> attachments) {
        this.attachments = attachments;
    }

    @XmlElementWrapper(name = "attachments")
    @XmlElement(name = "attachment")
    public Set<ReportOutputResource> getAttachmentsSet() {
        return attachments != null && !attachments.isEmpty() ? new HashSet<ReportOutputResource>(attachments.values()) : null;
    }

    public void setSecureExceptionHandler(SecureExceptionHandler secureExceptionHandler) {
        this.secureExceptionHandler = secureExceptionHandler;
    }
}
