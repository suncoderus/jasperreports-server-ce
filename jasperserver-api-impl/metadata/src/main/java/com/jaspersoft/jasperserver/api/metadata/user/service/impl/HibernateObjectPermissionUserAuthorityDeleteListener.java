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
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateObjectPermissionUserAuthorityDeleteListener implements
		HibernateDeleteListener, ApplicationContextAware {

	private String objectPermissionsServiceBeanName;

	private ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	protected ObjectPermissionServiceInternal getObjectPermissionService() {
		return context.getBean(
				getObjectPermissionsServiceBeanName(),
				ObjectPermissionServiceInternal.class);
	}
	
	public void onDelete(Object o) {
		if (o instanceof RepoUser) {
			deleteObjectPermissions((RepoUser) o);
		} else if (o instanceof RepoRole) {
			deleteObjectPermissions((RepoRole) o);
		}
	}
	
	protected void deleteObjectPermissions(IdedObject recipient) {
		ObjectRecipientIdentity recipientIdentity = new ObjectRecipientIdentity(recipient);
        ExecutionContext executionContext = new ExecutionContextImpl();
        executionContext.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
		getObjectPermissionService().deleteObjectPermissionsForRecipient(executionContext, recipientIdentity);
	}

	public String getObjectPermissionsServiceBeanName() {
		return objectPermissionsServiceBeanName;
	}

	public void setObjectPermissionsServiceBeanName(
			String objectPermissionsServiceBeanName) {
		this.objectPermissionsServiceBeanName = objectPermissionsServiceBeanName;
	}

}
