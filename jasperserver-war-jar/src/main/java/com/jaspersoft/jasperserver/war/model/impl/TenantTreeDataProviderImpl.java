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

package com.jaspersoft.jasperserver.war.model.impl;

import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;

/**
 * @author achan
 *
 */
public class TenantTreeDataProviderImpl implements TreeDataProvider {

    private RepositoryService repositoryService;
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    private TreeDataFilter filter;
    private TenantService tenantService;
	
    private static class TenantProperties implements JSONObject {
        public String tenantUri = null;
        public String toJSONString() {
//        	StringBuffer str = new StringBuffer("{");
//        	str.append("\"tenantUri\":"+ "\"" + tenantUri + "\"");
//        	str.append("}");
//
//            return str.toString();

            org.json.JSONObject jsonObject = new org.json.JSONObject();
            try {
                jsonObject.put("tenantUri", tenantUri);
            } catch (org.json.JSONException ignored) { }

            return jsonObject.toString();
        }
    }
    
	public List getChildren(ExecutionContext executionContext, String parentUri, int depth) {	
        TreeNode n = getNode(executionContext, parentUri, depth + 1);
        if (n != null) {
            return n.getChildren();
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.model.TreeDataProvider#getNode(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, int)
	 */
	public TreeNode getNode(ExecutionContext executionContext, String tenantUri,
			int depth) {     
		Tenant tenant = tenantService.getTenantBasedOnTenantUri(null, tenantUri);
		if (tenant != null) {
			TreeNode node = createNode(tenant, true);
            if (depth > 0) {
                processFolder(node, depth - 1);
            }
            return node;
		}
        return null;
	}
	
    private TreeNode createNode(Tenant tenant, boolean isFolder) {
    	TenantProperties extraProperty = new TenantProperties();
        extraProperty.tenantUri = tenant.getTenantUri();
        //extraProperty.isRemovable = repositoryServiceSecurityChecker.isRemovable(resource);
        return new TreeNodeImpl(this, 
                    tenant.getId(), tenant.getTenantName(), 
                    "com.jaspersoft.jasperserver.api.metadata.common.domain.Folder", tenant.getTenantUri(),
                    1, extraProperty);


    }
    
    private void processFolder(TreeNode node, int depth) {
        
    	List folders = tenantService.getSubTenantList(null, node.getId());
       
        
        /*List allResources = new ArrayList();
        allResources.addAll(folders);
        allResources.addAll(resources);*/
        
        if (folders != null) {
            for (Iterator iter = folders.iterator(); iter.hasNext(); ) {
                Tenant f = (Tenant) iter.next();
                TreeNode n = createNode(f, true);
                if (filter == null || filter.filter(n)) {
                    node.getChildren().add(n);
                    if (depth > 0) {
                        processFolder(n, depth - 1);
                    }
                }
            }
        }
        
    }	
	
	public TreeDataFilter getFilter() {
		return filter;
	}

	public void setFilter(TreeDataFilter filter) {
		this.filter = filter;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public RepositorySecurityChecker getRepositoryServiceSecurityChecker() {
		return repositoryServiceSecurityChecker;
	}

	public void setRepositoryServiceSecurityChecker(
			RepositorySecurityChecker repositoryServiceSecurityChecker) {
		this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
	}

	/**
	 * @return Returns the tenantService.
	 */
	public TenantService getTenantService() {
		return tenantService;
	}

	/**
	 * @param tenantService The tenantService to set.
	 */
	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}
	
	
}
