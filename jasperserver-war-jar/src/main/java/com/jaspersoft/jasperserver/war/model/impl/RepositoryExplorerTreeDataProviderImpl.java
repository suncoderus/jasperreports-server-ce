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

import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.text.DateFormat;

import com.jaspersoft.jasperserver.war.themes.ThemeService;
import org.json.JSONException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author achan
 *
 */
public class RepositoryExplorerTreeDataProviderImpl implements TreeDataProvider {
	
    private RepositoryService repositoryService;
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    private ThemeService themeService;
    private TreeDataFilter filter;
    private boolean skipResources;
    
    private static class Properties implements JSONObject {
        public String desc = null;
        public Date date = null;
        public boolean isWritable = false;
        public boolean isRemovable = false;
        public boolean isAdministrable = false;
        public boolean isThemeFolder = false;
        public boolean isActiveThemeFolder = false;
        public boolean isThemeRootFolder = false;
        public String toJSONString() {
            org.json.JSONObject o = new org.json.JSONObject();
            try {
                o.put("isWritable", isWritable);
                o.put("isRemovable", isRemovable);
                o.put("isAdministrable", isAdministrable);
                if (desc != null) {
                    o.put("desc", desc);
                }
                if (date != null) {
                    DateFormat formatter = DateFormat.getDateTimeInstance(
                            DateFormat.SHORT, DateFormat.SHORT, LocaleContextHolder.getLocale());

                    o.put("date", formatter.format(date));
                }
                if (isThemeFolder) {
                    o.put("isThemeFolder", isThemeFolder);
                }
                if (isActiveThemeFolder) {
                    o.put("isActiveThemeFolder", isActiveThemeFolder);
                }
                if (isThemeRootFolder) {
                    o.put("isThemeRootFolder", isThemeRootFolder);
                }
            } catch (JSONException e) { }

            return o.toString();
        }
    }

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.model.TreeDataProvider#getChildren(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, int)
	 */
	public List getChildren(ExecutionContext executionContext,
			String parentUri, int depth) {
        TreeNode n = getNode(executionContext, parentUri, depth + 1);
        if (n != null) {
            return n.getChildren();
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.model.TreeDataProvider#getNode(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String, int)
	 */
	public TreeNode getNode(ExecutionContext executionContext, String uri,
			int depth) {
				
        Resource resource = repositoryService.getResource(executionContext, uri);

        if (resource != null) {
            return createNode(resource, false);
        }
      
        Folder folder = repositoryService.getFolder(executionContext, uri);
        if (folder != null) {

            TreeNode node = createNode(folder, true);
            if (depth > 0) {
                processFolder(node, depth - 1);
            }

            return node;
        }
        
        return null;
	}
	
    protected TreeNode createNode(Resource resource, boolean isFolder) {
        Properties extraProperty = new Properties();
        extraProperty.isWritable = repositoryServiceSecurityChecker.isEditable(resource);
        extraProperty.isRemovable = repositoryServiceSecurityChecker.isRemovable(resource);
        extraProperty.isAdministrable = repositoryServiceSecurityChecker.isAdministrable(resource);

        if (isFolder) {
            extraProperty.desc = resource.getDescription();
            extraProperty.date = resource.getCreationDate();

            extraProperty = addThemeProperties(resource, extraProperty);

            return new TreeNodeImpl(this,
                    resource.getName(), resource.getLabel(), 
                    resource.getResourceType(), resource.getURIString(),
                    1, extraProperty);
        }
        return new TreeNodeImpl(this, 
                resource.getName(), resource.getLabel(), 
                resource.getResourceType(), resource.getURIString(),
                extraProperty);
    }

    protected Properties addThemeProperties(Resource resource, Properties extraProperty) {
        String uri = resource.getURIString();
        if (themeService.isThemeFolder(null, uri)) {
            extraProperty.isThemeFolder = true;
            if (themeService.isActiveThemeFolder(null, uri)) {
                extraProperty.isActiveThemeFolder = true;
            }
        }
        else if (themeService.isThemeRootFolder(null, resource.getURIString())) {
            extraProperty.isThemeRootFolder = true;
        }
        return extraProperty;
    }
    
    private void processFolder(TreeNode folder, int depth) {
        
        String folderURI = folder.getUriString();
        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));
        
        List folders = repositoryService.getSubFolders(null, folderURI);
        
        List resources = null;
        if (!isSkipResources()) {
            resources = repositoryService.loadResourcesList(null, criteria);
        }
        
        /*List allResources = new ArrayList();
        allResources.addAll(folders);
        allResources.addAll(resources);*/
        
        if (folders != null) {
            for (Iterator iter = folders.iterator(); iter.hasNext(); ) {
                Folder f = (Folder) iter.next();
                TreeNode n = createNode(f, true);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
                    if (depth > 0) {
                        processFolder(n, depth - 1);
                    }
                }
            }
        }
        if (resources != null) {
            for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
                Resource r = (Resource) iter.next();
                TreeNode n = createNode(r, false);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
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

    public ThemeService getThemeService() {
        return themeService;
    }

    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    public boolean isSkipResources() {
        return skipResources;
    }

    public void setSkipResources(boolean skipResources) {
        this.skipResources = skipResources;
    }
}
