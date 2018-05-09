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
package com.jaspersoft.jasperserver.api.engine.scheduling.security;

import com.jaspersoft.jasperserver.api.engine.scheduling.ReportJobsInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.InternalURIDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

/**
 * @author Oleg Gavavka
 *         02.10.2014.
 */
public class ReportJobObjectIdentityRetrievalStrategyImpl implements ObjectIdentityRetrievalStrategy {
    private static final Log log = LogFactory.getLog(ReportJobObjectIdentityRetrievalStrategyImpl.class);

    private ReportJobsInternalService reportJobsInternalService;

    @Override
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        long jobId;
        if (domainObject instanceof ReportJob) {
            jobId = ((ReportJob) domainObject).getId();
            return new ReportJobObjectIdentity(jobId);

        }
        if (domainObject instanceof ReportJobIdHolder) {
            jobId = ((ReportJobIdHolder) domainObject).getId();
            return new ReportJobObjectIdentity(jobId);
        }

        if (domainObject instanceof ReportJobSummary) {
            jobId = ((ReportJobSummary) domainObject).getId();
            return new ReportJobObjectIdentity(jobId);
        }
        return null;
    }


    public ReportJobsInternalService getReportJobsInternalService() {
        return reportJobsInternalService;
    }

    public void setReportJobsInternalService(ReportJobsInternalService reportJobsInternalService) {
        this.reportJobsInternalService = reportJobsInternalService;
    }
}
