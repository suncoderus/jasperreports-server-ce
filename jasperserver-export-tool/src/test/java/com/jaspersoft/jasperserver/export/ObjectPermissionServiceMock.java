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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectPermissionServiceMock implements ObjectPermissionService {
    public ObjectPermission newObjectPermission(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ObjectPermission getObjectPermission(ExecutionContext context, ObjectPermission objPerm) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject, final Object[] existingAcl) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject) {
    	return null;
    }   
    
    @Override
    public List<ObjectPermission> getEffectivePermissionsForObject(ExecutionContext context, Object targetObject) {
        return null; // TODO: implement.
    }

    public List getObjectPermissionsForObjectAndRecipient(ExecutionContext context, Object targetObject, Object recipient) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isObjectAdministrable(ExecutionContext context, Object targetObject) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void putObjectPermission(ExecutionContext context, ObjectPermission objPerm) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteObjectPermission(ExecutionContext context, ObjectPermission objPerm) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteObjectPermissionForObject(ExecutionContext context, Object targetObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getInheritedObjectPermissionMask(ExecutionContext context, Object targetObject, Object recipient) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public List<ObjectPermission> getObjectPermissionsForSubtree(String uri) {
		return null;
	}

	@Override
	public Acl getFromCache(final ObjectIdentity oId) {
		return null;
	}

	@Override
	public Acl putInCache(final Acl acl) {
		return acl;
	}

}
