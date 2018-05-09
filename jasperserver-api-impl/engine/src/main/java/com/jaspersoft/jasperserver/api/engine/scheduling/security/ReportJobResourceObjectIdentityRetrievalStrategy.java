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
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ResourceObjectIdentityRetrievalStrategyImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.model.ObjectIdentity;

/**
 * @author Oleg.Gavavka
 */

public class ReportJobResourceObjectIdentityRetrievalStrategy extends ResourceObjectIdentityRetrievalStrategyImpl {
    private static final Log log = LogFactory.getLog(ReportJobResourceObjectIdentityRetrievalStrategy.class);

    private ReportJobsInternalService reportJobsInternalService;

    @Override
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        if (domainObject instanceof ReportJob) {
            String resourceUri = ((ReportJob) domainObject).getSource().getReportUnitURI();
            InternalURI internalURI = new InternalURIDefinition(resourceUri);
            return internalURI;

        }
        if (domainObject instanceof ReportJobIdHolder) {
            Long jobId = ((ReportJobIdHolder) domainObject).getId();
            String resourceUri = getReportJobsInternalService().getSourceUriByJobId(jobId);
            InternalURI internalURI = new InternalURIDefinition(resourceUri);
            return internalURI;
        }

        if (domainObject instanceof ReportJobSummary) {
            String resourceUri = ((ReportJobSummary) domainObject).getReportUnitURI();
            InternalURI internalURI = new InternalURIDefinition(resourceUri);
            return internalURI;
        }
        return super.getObjectIdentity(domainObject);
    }

    public ReportJobsInternalService getReportJobsInternalService() {
        return reportJobsInternalService;
    }

    public void setReportJobsInternalService(ReportJobsInternalService reportJobsInternalService) {
        this.reportJobsInternalService = reportJobsInternalService;
    }
}
