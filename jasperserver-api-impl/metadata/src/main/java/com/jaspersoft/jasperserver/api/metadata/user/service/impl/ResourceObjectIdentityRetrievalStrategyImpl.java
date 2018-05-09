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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

/**
 * @author Oleg.Gavavka
 */

public class ResourceObjectIdentityRetrievalStrategyImpl implements ObjectIdentityRetrievalStrategy {

    @Override
    public ObjectIdentity getObjectIdentity(Object domainObject) {
        if (domainObject instanceof InternalURIDefinition) {
            return (InternalURIDefinition) domainObject;
        }

        // Creating new InternalURI for object because we have many implementations of InternalURI which are not consistent
        if (domainObject instanceof InternalURI) {
            PermissionUriProtocol protocol = PermissionUriProtocol.fromString(((InternalURI) domainObject).getProtocol());
            return new InternalURIDefinition(((InternalURI) domainObject).getPath(), protocol);
        }
        if (domainObject instanceof String) {
            return new InternalURIDefinition((String)domainObject);
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
