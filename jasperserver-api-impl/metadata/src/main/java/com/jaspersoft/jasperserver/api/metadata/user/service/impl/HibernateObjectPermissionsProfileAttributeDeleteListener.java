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

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoProfileAttribute;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class HibernateObjectPermissionsProfileAttributeDeleteListener implements HibernateDeleteListener {
    private String objectPermissionsServiceBeanName;
    private ResourceFactory objectMappingFactory;

    public void setObjectPermissionsServiceBeanName(String objectPermissionsServiceBeanName) {
        this.objectPermissionsServiceBeanName = objectPermissionsServiceBeanName;
    }

    protected ObjectPermissionServiceInternal getObjectPermissionService() {
        return StaticApplicationContext.getApplicationContext().getBean(
                objectPermissionsServiceBeanName,
                ObjectPermissionServiceInternal.class);
    }

    public void setObjectMappingFactory(ResourceFactory objectMappingFactory) {
        this.objectMappingFactory = objectMappingFactory;
    }

    @Override
    public void onDelete(Object o) {
        if (o instanceof RepoProfileAttribute) {
            RepoProfileAttribute repoAttr = (RepoProfileAttribute) o;
            ProfileAttribute attribute = (ProfileAttribute) objectMappingFactory.newObject(ProfileAttribute.class);
            attribute.setUri(repoAttr.getAttrName(), repoAttr.getAttributeHolderUri(repoAttr.getPrincipal()));

            getObjectPermissionService().deleteObjectPermissionForRepositoryPath(null, attribute.getURI());
        }
    }
}
