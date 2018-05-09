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

package com.jaspersoft.jasperserver.remote.handlers;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.remote.ResourceDataSource;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


/**
 * @author gtoffoli
 * @version $Id$
 */
@Service("fileHandler")
public class FileResourceHandler extends RepositoryResourceHandler {
    public static final String MAIN_ATTACHMENT_ID = "attachment";

    private static final Log log = LogFactory.getLog(FileResourceHandler.class);

    public Class getResourceType() {
        return FileResource.class;
    }

    @Override
    public SearchFilter getSearchFilter(String uri, String queryString, final String wsType, boolean recursive, int maxItems, int startIndex) {
        return new SearchFilter() {
            @Override
            public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
                criteria.add(Restrictions.eq("fileType", getFileType(wsType)));
            }
        };
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        FileResource fileResource = (FileResource) resource;
        descriptor.setWsType(fileResource.getFileType());
        descriptor.setHasData(fileResource.hasData());
        descriptor.setIsReference(fileResource.isReference());
        if (descriptor.getIsReference()) {
            descriptor.setReferenceUri(fileResource.getReferenceURI());
        }

        String resourceType = fileResource.getFileType();

        String contentType = "application/octet-stream";

        if (descriptor.getIsReference()) {
            descriptor.setWsType(ResourceDescriptor.TYPE_REFERENCE);
        } else if (resourceType == null)
            descriptor.setWsType(ResourceDescriptor.TYPE_UNKNOW);
        else if (resourceType.equals(FileResource.TYPE_JRXML)) {
            descriptor.setWsType(ResourceDescriptor.TYPE_JRXML);
            contentType = "text/xml";
        } else if (resourceType.equals(FileResource.TYPE_IMAGE)) {
            descriptor.setWsType(ResourceDescriptor.TYPE_IMAGE);
        } else if (resourceType.equals(FileResource.TYPE_FONT))
            descriptor.setWsType(ResourceDescriptor.TYPE_FONT);
        else if (resourceType.equals(FileResource.TYPE_JAR))
            descriptor.setWsType(ResourceDescriptor.TYPE_CLASS_JAR);
        else if (resourceType.equals(FileResource.TYPE_RESOURCE_BUNDLE)) {
            descriptor.setWsType(ResourceDescriptor.TYPE_RESOURCE_BUNDLE);
            contentType = "text/plain";
        } else if (resourceType.equals(FileResource.TYPE_STYLE_TEMPLATE)) {
            descriptor.setWsType(ResourceDescriptor.TYPE_STYLE_TEMPLATE);
            contentType = "text/xml";
        } else if (resourceType.equals(FileResource.TYPE_XML)) {
            descriptor.setWsType(ResourceDescriptor.TYPE_XML_FILE);
            contentType = "text/xml";
        }

        descriptor.setHasData(false);
        if (options == null || !options.containsKey(Argument.NO_RESOURCE_DATA_ATTACHMENT)) {
            //Retrieve resource data...
            descriptor.setHasData(true);
            FileResourceData fileData = getRepository().getResourceData(null, descriptor.getUriString());
            ResourceDataSource attachmentSource = new ResourceDataSource(descriptor.getName(), fileData);
            attachmentSource.setContentType(contentType);
            getRunReportService().getReportAttachments(descriptor.getUriString()).put(MAIN_ATTACHMENT_ID, attachmentSource);
            if (log.isWarnEnabled()) {
                log.warn("Placed attachment for " + descriptor.getName() + " in the output attachments list");
            }
            descriptor.setResourceProperty("PROP_ATTACHMENT_ID", MAIN_ATTACHMENT_ID);
        }
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {
        try {
            FileResource fResource;
            if (!(resource instanceof FileResource)) {
                throw new IllegalStateException("resource:" + resource.getName() + " is not a File Resource");
            } else {
                fResource = (FileResource) resource;
            }

            super.updateResource(fResource, descriptor, options);

            fResource.setLabel(descriptor.getLabel());
            fResource.setDescription(descriptor.getDescription());
            if (ResourceDescriptor.TYPE_REFERENCE.equals(descriptor.getWsType())
                    || descriptor.getIsReference()) {
                // check if the reference uri is valid...
                String referenceUri = descriptor.getReferenceUri();

                try {
                    getRepository().getResource(null,
                            referenceUri);
                } catch (Exception ex) {
                    throw new ServiceException(ServiceException.GENERAL_ERROR2,
                            getMessageSource().getMessage(
                                    "webservices.error.resourceNotFoundOrInvalid",
                                    new Object[]{referenceUri}, LocaleContextHolder.getLocale()));
                }

                fResource.setReferenceURI(referenceUri);
            } else {
                fResource.setFileType(descriptor.getWsType());
                if (getRunReportService().getInputAttachments().containsKey(descriptor.getUriString())) {
                    InputStream ds = getRunReportService().getInputAttachments().get(descriptor.getUriString()).getInputStream();
                    fResource.setData(IOUtils.toByteArray(ds));
                } else {
                    throw new IllegalStateException("could not find the binary content for resource: " + descriptor.getUriString());
                }
            }
        }  catch (IOException e) {
            throw new ServiceException(ServiceException.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    protected String getFileType(String wsType) {
        String type = wsType;
        if (wsType.equals(ResourceDescriptor.TYPE_IMAGE))
            type = FileResource.TYPE_IMAGE;
        else if (wsType.equals(ResourceDescriptor.TYPE_FONT))
            type = FileResource.TYPE_FONT;
        else if (wsType.equals(ResourceDescriptor.TYPE_CLASS_JAR))
            type = FileResource.TYPE_JAR;
        else if (wsType.equals(ResourceDescriptor.TYPE_JRXML))
            type = FileResource.TYPE_JRXML;
        else if (wsType.equals(ResourceDescriptor.TYPE_RESOURCE_BUNDLE))
            type = FileResource.TYPE_RESOURCE_BUNDLE;
        else if (wsType.equals(ResourceDescriptor.TYPE_STYLE_TEMPLATE))
            type = FileResource.TYPE_STYLE_TEMPLATE;
        else if (wsType.equals(ResourceDescriptor.TYPE_XML_FILE))
            type = FileResource.TYPE_XML;
        return type;
    }
}
