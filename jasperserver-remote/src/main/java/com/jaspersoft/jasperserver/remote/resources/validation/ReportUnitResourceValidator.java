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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.List;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addIllegalParameterValueError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class ReportUnitResourceValidator<T extends ReportUnit> extends GenericResourceValidator<T> {

    @Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @Override
    protected void internalValidate(T resource, ValidationErrors errors) {
        final ResourceReference jrxmlReference = resource.getMainReport();
        if (empty(jrxmlReference)) {
            addMandatoryParameterNotFoundError(errors, "JRXML");
        } else if (jrxmlReference.isLocal()
                && ((FileResource) jrxmlReference.getLocalResource()).getData() == null
                && FileResource.VERSION_NEW == jrxmlReference.getLocalResource().getVersion()) {
            // if file resource doesn't exist yet, then it is local file creation.
            // in this case file content is mandatory
            addMandatoryParameterNotFoundError(errors, "JRXML.content");
        } else if (!isJrxmlValid(jrxmlReference)) {
            addIllegalParameterValueError(errors, "JRXML.content", "JRXML.content", "Invalid JRXML");
        }

        // raw type is used by core class. Cast is safe
        @SuppressWarnings("unchecked")
        List<ResourceReference> resources = resource.getResources();
        if (resources != null && !resources.isEmpty()) {
            for (ResourceReference currentReference : resources) {
                FileResource fileResource = currentReference.isLocal() ? (FileResource) currentReference.getLocalResource() : null;
                if (fileResource != null && !fileResource.isReference() && fileResource.getData() == null
                        && FileResource.VERSION_NEW == fileResource.getVersion()) {
                    // if file resource doesn't exist yet, then it is local file creation.
                    // in this case file content is mandatory
                    addMandatoryParameterNotFoundError(errors, "resources." + fileResource.getName() + ".content");
                }
            }
        }

        String inputControlRenderingView = resource.getInputControlRenderingView();
        if (inputControlRenderingView != null && inputControlRenderingView.length() > 0) {
            if (inputControlRenderingView.length() > 100) {
                addIllegalParameterValueError(errors, "resources.inputControlRenderingView", inputControlRenderingView,
                        "The JSP reference for input controls is too long. The maximum length is 100 characters");
            }
        }

        String reportRenderingView = resource.getReportRenderingView();
        if (reportRenderingView != null && reportRenderingView.length() > 0) {
            if (reportRenderingView.length() > 100) {
                addIllegalParameterValueError(errors, "resources.reportRenderingView", reportRenderingView,
                        "The JSP reference for the report display is too long. The maximum length is 100 characters");
            }
        }
    }

    protected boolean isJrxmlValid(ResourceReference jrxmlReference) {
        boolean isValid = true;
        try {
            byte[] data = null;
            if (jrxmlReference.isLocal()) {
                data = ((FileResource) jrxmlReference.getLocalResource()).getData();
            }
            if (data == null) {
                data = repositoryService.getResourceData(null, jrxmlReference.getTargetURI()).getData();
            }
            loadJasperDesign(data);
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }

    protected JasperDesign loadJasperDesign(byte[] data) throws JRException {
        return JRXmlLoader.load(new ByteArrayInputStream(data));
    }
}
