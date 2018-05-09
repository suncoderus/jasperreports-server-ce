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

package com.jaspersoft.jasperserver.jaxrs.job;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobStateXmlAdapter;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.dto.job.ClientJobState;
import com.jaspersoft.jasperserver.dto.job.ClientJobStateType;
import com.jaspersoft.jasperserver.dto.job.ClientJobSummary;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
@Component
public class ReportJobSummaryConverter implements ToClientConverter<ReportJobSummary, ClientJobSummary, Object> {
    protected static final Log log = LogFactory.getLog(ReportJobSummaryConverter.class);

    @Override
    public ClientJobSummary toClient(ReportJobSummary serverObject, Object options) {
        ClientJobSummary clientJobSummary = null;
        if (serverObject != null) {
            clientJobSummary = new ClientJobSummary();
            clientJobSummary.
                    setId(serverObject.getId()).
                    setVersion((long) serverObject.getVersion()).
                    setLabel(serverObject.getLabel()).
                    setReportUnitURI(serverObject.getReportUnitURI()).
                    setReportLabel(serverObject.getReportLabel()).
                    setDescription(serverObject.getDescription()).
                    setUsername(serverObject.getUsername());
            if (serverObject.getRuntimeInformation() != null) {
                clientJobSummary.
                        setState(toClientJobState(serverObject.getRuntimeInformation()));
            }
        }
        return clientJobSummary;
    }

    public ClientJobState toClientJobState(ReportJobRuntimeInformation serverJobState) {
        ClientJobState clientJobState = new ClientJobState();
        Date nextFireTime = serverJobState.getNextFireTime();
        if (nextFireTime != null) clientJobState.setNextFireTime(new Date(nextFireTime.getTime()));
        Date previousFireTime = serverJobState.getPreviousFireTime();
        if (previousFireTime != null) clientJobState.setPreviousFireTime(new Date(previousFireTime.getTime()));
        Byte stateCode = serverJobState.getStateCode();
        if (stateCode != null) {
            try {
                clientJobState.setState(ClientJobStateType.valueOf(new ReportJobStateXmlAdapter().marshal(stateCode)));
            } catch (Exception e) {
                log.error("Error interpreting parameter " + stateCode + " of job state ", e);
            }
        }
        return clientJobState;
    }

    @Override
    public String getClientResourceType() {
        return ClientJobSummary.class.getName();
    }
}
