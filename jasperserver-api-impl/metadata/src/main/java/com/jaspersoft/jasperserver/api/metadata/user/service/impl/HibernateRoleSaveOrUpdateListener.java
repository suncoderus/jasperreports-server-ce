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
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateSaveOrUpdateListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.security.EhCacheBasedJasperServerAclCache;
import org.hibernate.engine.EntityEntry;
import org.hibernate.event.SaveOrUpdateEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by ybobyk on 8/9/2017.
 */
public class HibernateRoleSaveOrUpdateListener implements HibernateSaveOrUpdateListener, ApplicationContextAware {

    private String objectPermissionsServiceBeanName;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected ObjectPermissionServiceInternal getObjectPermissionService() {
        return applicationContext.getBean(
                getObjectPermissionsServiceBeanName(),
                ObjectPermissionServiceInternal.class);
    }

    @Override
    public void beforeSaveOrUpdate(SaveOrUpdateEvent event) {

    }

    @Override
    public void afterSaveOrUpdate(SaveOrUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof RepoRole) {
            ExecutionContext executionContext = new ExecutionContextImpl();
            executionContext.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
            ObjectRecipientIdentity identity = new ObjectRecipientIdentity(entity.getClass(), ((RepoRole) entity).getId());
            getObjectPermissionService().deleteObjectCachePermissionsForRecipient(executionContext, identity);
        }
    }

    public String getObjectPermissionsServiceBeanName() {
        return objectPermissionsServiceBeanName;
    }

    public void setObjectPermissionsServiceBeanName(
            String objectPermissionsServiceBeanName) {
        this.objectPermissionsServiceBeanName = objectPermissionsServiceBeanName;
    }
}
