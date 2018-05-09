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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.util.RepositoryLabelIDHelper;

/**
 * Test class to an end point to connect across SOAP
 * @author achan 
 *
 */
public class HibernateUtil {

	
	private RepositoryService repo;
	
	public HibernateUtil(RepositoryService repository) {
		repo = repository;
	}
	
	
	public RepositoryService getRepo() {
		return repo;
	}


	public void setRepo(RepositoryService repo) {
		this.repo = repo;
	}


	public boolean resourceDisplayNameExists(String parentUri, String name, String displayName) {
		
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri.startsWith("repo:") ? parentUri.substring(5) : parentUri));

		List resources = repo.loadResourcesList(null, criteria); 
		for (int i=0; i<resources.size(); i++) {
			ResourceLookupImpl res = (ResourceLookupImpl)resources.get(i);
			if (res.getLabel().equalsIgnoreCase(displayName) && (!res.getName().equalsIgnoreCase(name))) {
				return true;                   
			}
		}
		return false;
	}	
	

	public boolean folderDisplayNameExists(String parentUri, String displayName) {
		List repoFolderList = repo.getSubFolders(null, parentUri.startsWith("repo:") ? parentUri.substring(5) : parentUri);
		for (int i=0; i<repoFolderList.size(); i++) {
			FolderImpl repoFolder = (FolderImpl)repoFolderList.get(i);
			if (displayName.equalsIgnoreCase(repoFolder.getLabel())) {
				return true;                   
			}
		}
		return false;
	}
	
	public boolean doesDisplayNameExist(String parentFolderUri, String name, String displayName) {
		return resourceDisplayNameExists(parentFolderUri, name, displayName) || folderDisplayNameExists(parentFolderUri, displayName);
	}
	
	public String generateUniqueID(String parentFolderUri, String displayName) {
		return RepositoryLabelIDHelper.generateIdBasedOnLabel(repo, parentFolderUri, displayName);
	}

	
}
