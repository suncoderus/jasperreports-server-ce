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
package com.jaspersoft.jasperserver.api.metadata.user.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;

import java.util.List;

import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.ObjectIdentity;

/**
 * ObjectPermissionService is the interface which is used to manage
 * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} and
 * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role} permission.
 *
 * @author swood
 * @version $Id$
 * @since 1.0.1
 */
@JasperServerAPI
public interface ObjectPermissionService {

    /**
     * To be used as an attribute of execution context and mark the current operation as privileged
     */
    // TODO Spring Security Upgrade: rethink if we rely need PRIVILEGED_OPERATION if ACL is complete
    public static final String PRIVILEGED_OPERATION = "PRIVILEGED_OPERATION";

    /**
     * Creates a new instance of {@link ObjectPermission} object.
     *
     * @param context the execution context.
     *
     * @return a new instance of ObjectPermission object.
     */
	public ObjectPermission newObjectPermission(ExecutionContext context);

    /**
     * Returns {@link ObjectPermission} object where the resource URI and recipient
     * ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} or
     * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role}) is the same as in
     * <code>objPerm</code> parameter. If such ObjectPermission was not found then <code>null</code> is returned.
     *
     * @param context the execution context.
     * @param objPerm the object permission.
     * @return ObjectPermission object where the resource URI and recipient is the same as in
     * <code>objPerm</code> parameter. If such ObjectPermission was not found then <code>null</code> is returned.
     */
	public ObjectPermission getObjectPermission(ExecutionContext context, ObjectPermission objPerm);

    /**
     * Returns a list of {@link ObjectPermission} objects for the specified recipient
     * ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} or
     * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role}).
     *
     * @param context the execution context.
     * @param recipient the recipient object.
     * @return a list of ObjectPermission objects.
     */
	public List getObjectPermissionsForRecipient(ExecutionContext context, Object recipient);


    /**
     * Returns a list of {@link ObjectPermission} objects for the specified repository object.
     *
     * @param context the execution context.
     * @param targetObject the repository object.
     * @return a list of ObjectPermission objects.
     */
	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject);

	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject, final Object[] existingAcl);

	
	/**
     * Returns a list of {@link ObjectPermission} objects for the specified repository object.
     *
     * @param context the execution context.
     * @param targetObject the repository object.
     * @return a list of ObjectPermission objects.
     */
	public List<ObjectPermission> getObjectPermissionsForSubtree(final String uri);

	
    /**
     * Returns a list of effective {@link ObjectPermission} objects for the specified repository object.
     *
     * @param context      the execution context.
     * @param targetObject the repository object.
     * @return a list of ObjectPermission objects.
     */
    public List<ObjectPermission> getEffectivePermissionsForObject(ExecutionContext context, Object targetObject);

    /**
     * Returns a list of {@link ObjectPermission} objects for the specified repository object and the specified recipient
     * ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} or
     * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role}.
     *
     * @param context the execution context.
     * @param targetObject the repository object.
     * @param recipient the recipient.
     * @return Returns a list of ObjectPermission objects.
     */
	public List getObjectPermissionsForObjectAndRecipient(ExecutionContext context, Object targetObject, Object recipient);

    /**
     * Shows if the specified repository object can be administrated by current(authenticated/session) user.
     *
     * @param context the execution context.
     * @param targetObject the repository object.
     * @return <code>true</code> if the repository object can be administrated, <code>false</code> otherwise.
     */
    public boolean isObjectAdministrable(ExecutionContext context, Object targetObject);

    /**
     * Saves the {@link com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission ObjectPermission} object.
     *
     * @param context the execution context.
     * @param objPerm the object permission.
     * @throws IllegalArgumentException in case if ObjectPermission instance is illegal (i.e. is null or illegal permissionRecipient)
     */
	public void putObjectPermission(ExecutionContext context, ObjectPermission objPerm) throws IllegalArgumentException;

    /**
     * Deletes the {@link com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission ObjectPermission} object.
     *
     * @param context the execution context.
     * @param objPerm the object permission.
     */
	public void deleteObjectPermission(ExecutionContext context, ObjectPermission objPerm);

    /**
     * Deletes all permissions
     * ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission ObjectPermission} objects) for the
     * specified repository object.
     *
     * @param context the execution context.
     * @param targetObject the repository object.
     */
	public void deleteObjectPermissionForObject(ExecutionContext context, Object targetObject);

    /**
     * Deletes all permissions
     * ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission ObjectPermission} objects) for the
     * specified recipient ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} or
     * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role}).
     *
     * @param context the execution context.
     * @param recipient the recipient.
     */
	public void deleteObjectPermissionsForRecipient(ExecutionContext context, Object recipient);

	/**
	 * Returns the permission mask inherited by the specified repository object from its ancestors
	 * for the specified recipient ({@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} or
     * {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role Role}).
	 *
	 * @param context the execution context
	 * @param targetObject the repository object.
	 * @param recipient the recipient.
	 * @return the inherited permission mask of the repository object for the recipient.
	 */
	public int getInheritedObjectPermissionMask(ExecutionContext context,
			Object targetObject, Object recipient);

	public Acl getFromCache(final ObjectIdentity oId);
	public Acl putInCache(final Acl acl);
}
