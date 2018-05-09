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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id$
 */
public interface PermissionsService {
    public String REPO_URI_PREFIX = Resource.URI_PROTOCOL + ":";
    public String REPO_URI_ROOT = REPO_URI_PREFIX + "/";

    public List<ObjectPermission> getPermissionsForObject(String targetURI) throws RemoteException;

    public List<ObjectPermission> getPermissions(String resourceURI) throws RemoteException;

    public List<ObjectPermission> getPermissions(String kresourceURI, Class<?> recipientType, String recipientId, boolean effectivePermissions, boolean resolveAll) throws RemoteException;

    public ObjectPermission getPermission(String resourceURI, Class<?> recipientType, String recipientId) throws RemoteException;

    public ObjectPermission createPermission(ObjectPermission objectPermission) throws RemoteException;

    public ObjectPermission putPermission(ObjectPermission objectPermission) throws RemoteException;

    public List<ObjectPermission> putPermissions(InternalURI internalURI, List<ObjectPermission> objectPermissions) throws RemoteException;

    public List<ObjectPermission> createPermissions(List<ObjectPermission> objectPermissions) throws RemoteException;

    public void deletePermission(ObjectPermission objectPermission) throws RemoteException;

    public ObjectPermission newObjectPermission();

    public int getAppliedPermissionMaskForObjectAndCurrentUser(String targetURI) throws RemoteException;

    public ObjectPermission getEffectivePermission(InternalURI internalURI, Role role);

    public ObjectPermission getEffectivePermission(InternalURI internalURI, User user);

    public ObjectPermission getEffectivePermission(InternalURI internalURI, Authentication authentication);
}
