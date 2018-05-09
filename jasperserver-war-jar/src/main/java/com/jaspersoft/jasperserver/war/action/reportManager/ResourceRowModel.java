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

package com.jaspersoft.jasperserver.war.action.reportManager;

import java.util.Date;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author achan
 *
 */
public class ResourceRowModel implements Serializable {

	private String name;
	private String hiddenName;
	private String id;
	private String resourceUrl;
	private String description;
	private String type;
	private String resourceType;
	private Date creationDate;
	private Date updateDate;
	private boolean isScheduled;
	private boolean selected;
	private boolean hasSavedOptions = false;
	private boolean isContentType = false;
	private boolean isWritable = false;
	private boolean isDeletable = false;
	private boolean isAdministrable = false;
	private List listOfOptions = new ArrayList();
	
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
    public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isHasSavedOptions() {
		return hasSavedOptions;
	}
	public void setHasSavedOptions(boolean hasSavedOptions) {
		this.hasSavedOptions = hasSavedOptions;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isScheduled() {
		return isScheduled;
	}
	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	/**
	 * @return Returns the hiddenName.
	 */
	public String getHiddenName() {
		return hiddenName;
	}
	/**
	 * @param hiddenName The hiddenName to set.
	 */
	public void setHiddenName(String hiddenName) {
		this.hiddenName = hiddenName;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public boolean isContentType() {
		return isContentType;
	}
	public void setContentType(boolean isContentType) {
		this.isContentType = isContentType;
	}
	public boolean isDeletable() {
		return isDeletable;
	}
	public void setDeletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
	}
	public boolean isWritable() {
		return isWritable;
	}
	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}
    public boolean isAdministrable() {
        return isAdministrable;
    }

    public void setAdministrable(boolean administrable) {
        isAdministrable = administrable;
    }

    public List getListOfOptions() {
		return listOfOptions;
	}
	public void setListOfOptions(List listOfOptions) {
		this.listOfOptions = listOfOptions;
	}
	public void addOption(ResourceRowModel res) {
		this.listOfOptions.add(res);
	}
	
	
	
	
	
}
