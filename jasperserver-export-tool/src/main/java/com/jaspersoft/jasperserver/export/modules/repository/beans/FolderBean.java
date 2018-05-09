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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;

import java.util.Date;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.metadata.common.domain.Folder.SEPARATOR;
import static com.jaspersoft.jasperserver.api.metadata.user.service.TenantService.ORGANIZATIONS;

/**
 * @author tkavanagh
 * @version $Id$
 */

public class FolderBean {

	/*
	 * The following come from the Resource interface 
	 */
	private String name;
	private String label;
	private String description;
	private String parent;
	private Date creationDate;
	private Date updateDate;
	private String[] subFolders;
	private String[] resources;
	private RepositoryObjectPermissionBean[] permissions;

    /*
    *  Export specific data
    */
    private boolean exportedWithPermissions;
	
	public void copyFrom(Folder folder) {
		setName(folder.getName());
		setLabel(folder.getLabel());
		setDescription(folder.getDescription());
		setParent(folder.getParentFolder());
		setCreationDate(folder.getCreationDate());
		setUpdateDate(folder.getUpdateDate());
	}
	
	public void copyTo(Folder folder, ImporterModuleContext context) {
		folder.setName(getName());
		folder.setLabel(getLabel());
		folder.setDescription(getDescription());
		folder.setParentFolder(getParent());

		if (!context.getNewGeneratedTenantIds().isEmpty()) {
			handleNewTenantIds(folder, context);
		}
	}

	// ^(/organizations/[^/]+)*/organizations/?
	private static final Pattern ORGANIZATIONS_FOLDER_PATTERN =
			Pattern.compile("^(" + SEPARATOR + ORGANIZATIONS + SEPARATOR +
					"[^" + SEPARATOR + "]+)*" + SEPARATOR + ORGANIZATIONS + SEPARATOR + "?");

	private void handleNewTenantIds(Folder folder, ImporterModuleContext context) {
		if (ORGANIZATIONS_FOLDER_PATTERN.matcher(folder.getParentFolder()).matches()) {
			// it is organization folder
			String name = folder.getName();
			folder.setName(TenantStrHolderPattern.TENANT_ID
					.replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), folder.getName()));
			if (name != null && !name.equals(folder.getName())) {
				folder.setLabel(folder.getName());
			}
		}

		folder.setParentFolder(TenantStrHolderPattern.TENANT_FOLDER_URI
				.replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), folder.getParentFolder()));
	}
	
	/*
	 * getters and setters
	 */
	
	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String folderUri) {
		this.parent = folderUri;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String[] getResources() {
		return resources;
	}

	public void setResources(String[] resources) {
		this.resources = resources;
	}

	public String[] getSubFolders() {
		return subFolders;
	}

	public void setSubFolders(String[] subFolders) {
		this.subFolders = subFolders;
	}

	public RepositoryObjectPermissionBean[] getPermissions() {
		return permissions;
	}

	public void setPermissions(RepositoryObjectPermissionBean[] permissions) {
		this.permissions = permissions;
	}

    public boolean isExportedWithPermissions() {
        return exportedWithPermissions;
    }

    public void setExportedWithPermissions(boolean exportedWithPermissions) {
        this.exportedWithPermissions = exportedWithPermissions;
    }
}
