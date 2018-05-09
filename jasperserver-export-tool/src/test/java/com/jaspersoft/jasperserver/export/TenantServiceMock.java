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
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantServiceMock implements TenantService {

    public void putTenant(ExecutionContext context, Tenant aTenant) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Tenant getTenant(ExecutionContext context, String tenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getSubTenantList(ExecutionContext context, Tenant parentTenant) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getSubTenantList(ExecutionContext context, String parentTenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteTenant(ExecutionContext context, String tenantId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateTenant(ExecutionContext context, Tenant aTenant) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List getAllSubTenantList(ExecutionContext context, String parentTenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text) {
        return null;
    }

    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth) {
        return null;
    }

    @Override
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth, String sortBy) {
        return null;
    }

    public int getNumberOfTenants(ExecutionContext context) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Tenant getDefaultTenant(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Tenant getTenantBasedOnTenantUri(ExecutionContext context, String tenantUri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTenantIdBasedOnRepositoryUri(ExecutionContext context, String uri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Tenant getTenantBasedOnRepositoryUri(ExecutionContext context, String uri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getUserOrgIdDelimiter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUserOrgIdDelimiter(String userOrgIdDelimiter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isMultiTenantEnvironment(ExecutionContext context) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getUniqueTenantId(String proposedTenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getUniqueTenantAlias(String proposedTenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumberOfUsers(ExecutionContext context, String tenantId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumberOfRoles(ExecutionContext context, String tenantId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getSubTenantsCount(ExecutionContext context, String parentTenantId, String text) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Tenant> getSubTenants(ExecutionContext context, String parentTenantId, String text, int firstResult, int maxResults) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<String, Integer> getSubTenantsCountMap(List<String> tenantIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getAllSubTenantIdList(ExecutionContext context, String parentTenantId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTenantActiveTheme(ExecutionContext context, String tenantId, String themeName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
