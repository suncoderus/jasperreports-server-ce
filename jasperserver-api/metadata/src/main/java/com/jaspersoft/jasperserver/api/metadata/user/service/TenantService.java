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
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import java.util.List;
import java.util.Map;

/**
 * TenantService is the interface which is used to manage {@link Tenant} objects.
 *
 * @author achan
 * @version $Id$
 * @since 3.5.0
 */
@JasperServerAPI
public interface TenantService {

    /**
     * Root organization identifier.
     */
	public static final String ORGANIZATIONS = "organizations";

    /**
     * The name of template folder which is used to create the folders' structure of new organization.
     */
	public static final String ORG_TEMPLATE = "org_template";

    /**
     * Saves new or updates existing {@link Tenant} object.
     *
     * @param context the execution context.
     * @param aTenant the tenant.
     */
	public void putTenant(ExecutionContext context, Tenant aTenant);

    /**
     * Returns tenant by its identifier.
     *
     * @param context the execution context.
     * @param tenantId the identifier of the tenant.
     *
     * @return the tenant if it was found for the specified identifier or <code>null</code> otherwise.
     */
	public Tenant getTenant(ExecutionContext context, String tenantId);

    /**
     * Returns a list of sub tenants of the specified parent tenant identifier.
     *
     * @param context the execution context.
     * @param parentTenant the parent tenant.
     *
     * @return a list of sub tenants if the specified tenant exists or empty list otherwise.
     */
	public List getSubTenantList(ExecutionContext context, Tenant parentTenant);

    /**
     * Returns a list of sub tenants of the specified parent tenant.
     *
     * @param context the execution context.
     * @param parentTenantId the identifier of parent tenant.
     *
     * @return a list of sub tenants if the tenant with specified identifier exists or empty list otherwise.
     */
	public List getSubTenantList(ExecutionContext context, String parentTenantId);

    /**
     * Deletes the {@link Tenant} object by specified identifier.
     *
     * @param context the execution context.
     * @param tenantId the identifier of the tenant.
     */
	public void deleteTenant(ExecutionContext context, String tenantId);

    /**
     * Updates existing {@link Tenant} object.
     *
     * @param context the execution context.
     * @param aTenant the tenant.
     */
	public void updateTenant(ExecutionContext context, Tenant aTenant);

    /**
     * Returns a list of all sub tenants (including sub tenants of sub tenants) for the specified tenant identifier.
     *
     * @param context the execution context.
     * @param parentTenantId the identifier of parent tenant.
     *
     * @return a list of all sub tenants if the tenant with specified identifier exists or empty list otherwise.
     */
	public List getAllSubTenantList(ExecutionContext context, String parentTenantId);

    /**
     * Returns a list of all sub tenants (including sub tenants of sub tenants) of the specified parent tenant identifier.
     * Allows to search by id, name, alias and description
     *
     * @param context the execution context.
     * @param parentTenantId the parent tenant id.
     * @param text the search string to filter results
     *
     * @return a list of all sub tenants if the tenant with specified identifier exists or empty list otherwise.
     */
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text);

    /**
     * Returns a list of sub tenants (including sub tenants of sub tenants with corresondint depth) of the specified parent tenant identifier.
     * Allows to search by id, name, alias and description
     *
     * @param context the execution context.
     * @param parentTenantId the parent tenant id.
     * @param text the search string to filter results
     * @param depth max depth of affected subtree
     *
     * @return a list of all sub tenants if the tenant with specified identifier exists or empty list otherwise.
     */
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth);

    /**
     * Returns a list of sub tenants (including sub tenants of sub tenants with corresondint depth) of the specified parent tenant identifier.
     * Allows to search by id, name, alias and description
     *
     * @param context the execution context.
     * @param parentTenantId the parent tenant id.
     * @param text the search string to filter results
     * @param depth max depth of affected subtree
     * @param sortBy field to sortBy
     *
     * @return a list of all sub tenants if the tenant with specified identifier exists or empty list otherwise.
     */
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth, String sortBy);

    /**
     * Returns the number of all tenants.
     *
     * @param context the execution context.
     *
     * @return the number of all tenants.
     */
	public int getNumberOfTenants(ExecutionContext context);

    /**
     * Returns the sub tenant of root organization if we have only two tenants in the system: root tenant and its sub
     * tenant. If there are more than two tenants in the system then <code>null</code> is returned.
     *
     * @param context the execution context.
     *
     * @return the sub tenant of root organization if we have only two tenants in the system (root tenant and its sub
     * tenant), <code>null</code> otherwise.
     */
	public Tenant getDefaultTenant(ExecutionContext context);

    /**
     * Returns the {@link Tenant} object by the specified tenant URI. If there is not tenants with such URI then
     * <code>null</code> is returned.
     *
     * @param context the execution context.
     * @param tenantUri the URI of the tenant.
     *
     * @return the tenant by the specified tenant URI if tenant with such URI exists, <code>null</code> otherwise.
     */
	public Tenant getTenantBasedOnTenantUri(ExecutionContext context, String tenantUri);

    /**
     * Returns the Tenant Id to which the specified repository URI belongs to. If URI belongs to root
     * tenant then <code>null</code> is returned.
     *
     * @param context the execution context.
     * @param uri the repository URI.
     *
     * @return the tenant Id to which the specified repository URI belongs to, or <code>null</code> if URI belongs to root
     * tenant.
     */
    public String getTenantIdBasedOnRepositoryUri(ExecutionContext context, String uri);

    /**
     * Returns the {@link Tenant} object to which the specified repository URI belongs to. If URI belongs to root
     * tenant then <code>null</code> is returned.
     *
     * @param context the execution context.
     * @param uri the repository URI.
     *
     * @return the tenant to which the specified repository URI belongs to, or <code>null</code> if URI belongs to root
     * tenant.
     */
    public Tenant getTenantBasedOnRepositoryUri(ExecutionContext context, String uri);

    /**
     * Returns the delimiter of username and organization (tenant) identifier. This delimiter is used for tenant
     * qualified usernames (example, "jasperadmin|organization_1").
     *
     * @return the delimiter of username and organization (tenant) identifier.
     */
	public String getUserOrgIdDelimiter();

    /**
     * Sets the delimiter of username and organization (tenant) identifier. See {@link #getUserOrgIdDelimiter()}.
     *
     * @param userOrgIdDelimiter the delimiter of username and organization (tenant) identifier.
     */
	public void setUserOrgIdDelimiter(String userOrgIdDelimiter);

    /**
     * Shows if we are in multi-tenancy environment.
     *
     * @param context
     *
     * @return <code>true</code> if we are in multi-tenancy environment, <code>false</code> otherwise.
     */
	public boolean isMultiTenantEnvironment(ExecutionContext context);

    /**
     * Returns unique tenant identifier based on the proposed tenant identifier.
     *
     * @param proposedTenantId the proposed tenant identifier.
     *
     * @return unique tenant identifier based on the proposed tenant identifier.
     */
    public String getUniqueTenantId(String proposedTenantId);

    /**
     * Returns unique tenant alias based on the proposed tenant identifier.
     *
     * @param proposedTenantId the proposed tenant identifier.
     *
     * @return unique tenant alias based on the proposed tenant identifier.
     */
    public String getUniqueTenantAlias(String proposedTenantId);
	
	public int getNumberOfUsers(ExecutionContext context, String tenantId);

    public int getNumberOfRoles(ExecutionContext context, String tenantId);

    public int getSubTenantsCount(ExecutionContext context, String parentTenantId, String text);
    
    public List<Tenant> getSubTenants(ExecutionContext context, String parentTenantId, String text, int firstResult,
            int maxResults);

    public Map<String, Integer> getSubTenantsCountMap(List<String> tenantIds);

    public List<String> getAllSubTenantIdList(ExecutionContext context, String parentTenantId);

    /**
     * Sets an active theme name for a tenant
     * @param context
     * @param tenantId
     * @param themeName
     */
    public void setTenantActiveTheme(ExecutionContext context, String tenantId, String themeName);

}
