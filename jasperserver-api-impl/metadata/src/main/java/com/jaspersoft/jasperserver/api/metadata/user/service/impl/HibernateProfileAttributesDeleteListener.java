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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class HibernateProfileAttributesDeleteListener implements  HibernateDeleteListener, ApplicationContextAware  {
    private ApplicationContext context;
    private String profileAttributesServiceBeanName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void onDelete(Object o) {
        if (o instanceof RepoUser || o instanceof RepoTenant) {
            deleteProfileAttributes((IdedObject)o);
        }
    }

    @SuppressWarnings("unchecked")
    protected void deleteProfileAttributes(IdedObject recipient) {
        ObjectRecipientIdentity recipientIdentity = new ObjectRecipientIdentity(recipient);
        ExecutionContext executionContext = new ExecutionContextImpl();
        executionContext.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);

        getProfileAttributeService().deleteProfileAttributesForRecipient(executionContext, recipientIdentity);
    }

    public String getProfileAttributesServiceBeanName() {
        return profileAttributesServiceBeanName;
    }

    public void setProfileAttributesServiceBeanName(String profileAttributesServiceBeanName) {
        this.profileAttributesServiceBeanName = profileAttributesServiceBeanName;
    }

    protected ProfileAttributeServiceInternal getProfileAttributeService() {
        return context.getBean(getProfileAttributesServiceBeanName(), ProfileAttributeServiceInternal.class);
    }

}
