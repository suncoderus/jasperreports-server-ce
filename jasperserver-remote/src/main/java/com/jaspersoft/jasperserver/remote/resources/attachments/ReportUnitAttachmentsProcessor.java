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
package com.jaspersoft.jasperserver.remote.resources.attachments;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.dto.resources.ResourceMultipartConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.remote.resources.attachments.AttachmentProcessingHelper.setDataToFileResourceWithValidation;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class ReportUnitAttachmentsProcessor<T extends ReportUnit> implements AttachmentsProcessor<T> {

    @Override
    public T processAttachments(T serverObject,
            Map<String, InputStream> parts) throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        setDataToFileResourceWithValidation(parts.get(ResourceMultipartConstants.REPORT_JRXML_PART_NAME),
                serverObject.getMainReport(), ResourceMultipartConstants.REPORT_JRXML_PART_NAME);
        // Raw type is used in core class. Cast is safe.
        @SuppressWarnings("unchecked")
        final List<ResourceReference> resources = serverObject.getResources();
        for(ResourceReference currentReference : resources){
            if(currentReference.isLocal() && currentReference.getLocalResource() instanceof FileResource
                    && !((FileResource)currentReference.getLocalResource()).isReference()){
                final FileResource fileResource = (FileResource) currentReference.getLocalResource();
                final String filePartName = ResourceMultipartConstants.REPORT_FILE_PART_NAME_PREFIX + fileResource.getName();
                setDataToFileResourceWithValidation(parts.get(filePartName), currentReference, filePartName);
            }
        }
        return serverObject;
    }
}
