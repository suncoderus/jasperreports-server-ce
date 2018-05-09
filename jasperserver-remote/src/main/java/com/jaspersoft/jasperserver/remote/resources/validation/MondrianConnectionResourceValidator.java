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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import org.springframework.stereotype.Component;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class MondrianConnectionResourceValidator<ConnectionType extends MondrianConnection> extends GenericResourceValidator<ConnectionType> {
    @Override
    protected void internalValidate(ConnectionType resource, ValidationErrors errors) {
        if (resource.getDataSource() == null || empty(resource.getDataSource().getTargetURI())) {
            addMandatoryParameterNotFoundError(errors, "dataSource");
        }

        final ResourceReference schema = resource.getSchema();
        if (schema == null || empty(schema.getTargetURI())) {
            addMandatoryParameterNotFoundError(errors, "schema");
        } else if(schema.isLocal() && FileResource.VERSION_NEW == schema.getLocalResource().getVersion()
                && ((FileResource)schema.getLocalResource()).getData() == null){
            // if file resource doesn't exist yet, then it is local file creation.
            // in this case file content is mandatory
            addMandatoryParameterNotFoundError(errors, "schema.content");

        }
    }
}
