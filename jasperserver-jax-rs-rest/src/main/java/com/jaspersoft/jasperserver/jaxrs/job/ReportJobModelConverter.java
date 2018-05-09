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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobAlertModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobMailNotificationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRepositoryDestinationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import com.jaspersoft.jasperserver.dto.job.model.ClientReportJobModel;
import java.sql.Timestamp;
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
public class ReportJobModelConverter extends ReportJobConverter {

    @Override
    public ReportJobModel toServer(ClientReportJob clientObject, Object options) {
        return toServer(clientObject, new ReportJobModel(), null);
    }

    @Override
    public ReportJobModel toServer(ClientReportJob clientObject, ReportJob serverObject, Object options) {
        ClientReportJobModel clientJobModel = (ClientReportJobModel) clientObject;
        ReportJobModel resultToUpdate = (ReportJobModel) serverObject;
        if (clientJobModel.isSourceModified())
            resultToUpdate.setSourceModel((ReportJobSourceModel)toServerJobSource(clientObject.getSource(), clientJobModel.getOutputTimeZone()));
        if (clientJobModel.isCreationDateModified())
            resultToUpdate.setCreationDate(new Timestamp(clientObject.getCreationDate().getTime()));
        if (clientJobModel.isTriggerModified())
            resultToUpdate.setTriggerModel(toServerJobTrigger(clientObject.getTrigger()));
        if (clientJobModel.isMailNotificationModified())
            resultToUpdate.setMailNotificationModel((ReportJobMailNotificationModel) toServerJobMailNotification(clientObject.getMailNotification()));
        if (clientJobModel.isAlertModified())
            resultToUpdate.setAlertModel((ReportJobAlertModel) toServerJobAlert(clientObject.getAlert()));
        if (clientJobModel.isContentRespositoryDestinationModified())
            resultToUpdate.setContentRepositoryDestinationModel((ReportJobRepositoryDestinationModel) toServerJobRepositoryDestination(clientObject.getRepositoryDestination()));
        if (clientJobModel.isDescriptionModified())
            resultToUpdate.setDescription(clientObject.getDescription());
        if (clientJobModel.isLabelModified())
            resultToUpdate.setLabel(clientObject.getLabel());
        if (clientJobModel.isBaseOutputFileNameModified())
            resultToUpdate.setBaseOutputFilename(clientObject.getBaseOutputFilename());
        if (clientJobModel.isOutputFormatsModified())
            resultToUpdate.setOutputFormatsSet(toServerOutputFormats(clientObject.getOutputFormats()));
        if (clientJobModel.isUsernameModified())
            resultToUpdate.setUsername(clientObject.getUsername());
        if (clientJobModel.isOutputLocaleModified())
            resultToUpdate.setOutputLocale(clientObject.getOutputLocale());

        return (ReportJobModel) resultToUpdate;
    }


    @Override
    public String getServerResourceType() {
        return ReportJobModel.class.getName();
    }
}
