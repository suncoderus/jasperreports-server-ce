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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.RepoManager;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * 
 * @disabled_hibernate.class table="resource_folder"
 */
public class RepoFolder extends RepoResourceBase {
	
	private String uri;
	private boolean hidden;
	
	private Set children;
	private Set subFolders;
	
	public RepoFolder() {
		children = new HashSet();
	}

	/**
	 * @hibernate.property
	 * 		column="uri" type="string" length="200" not-null="true" unique="true"
	 */
	public String getURI()
	{
		return uri;
	}
	
	public void setURI(String newURIString)
	{
		uri = newURIString;
	}
	
	public void set(Folder folder, RepoFolder parent, RepoManager manager) {
		setName(folder.getName());
		setLabel(folder.getLabel());
		setDescription(folder.getDescription());
		setParent(parent);
		setHidden(false);
		refreshURI(manager);
	}

	public void refreshURI(RepoManager manager) {
		String newURI = getResourceURI();
		if (!newURI.equals(getURI())) {
			setURI(newURI);
			manager.lockPath(this);
		}
	}

	public String getResourceURI() {
		if (parent == null && Folder.SEPARATOR.equals(name)) {
			return Folder.SEPARATOR;
		}

		return super.getResourceURI();
	}
	
	protected Class getClientItf() {
		return Folder.class;
	}

	/**
	 * @hibernate.set inverse="true" cascade="save-update,delete,delete-orphan"
	 * @hibernate.key column="parent_folder"
	 * @hibernate.one-to-many class="com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource"
	 */
	public Set getChildren() {
		return children;
	}

	public void setChildren(Set children) {
		this.children = children;
	}
	
	public void addChild(RepoResource resource) {
		resource.setParent(this);
		children.add(resource);
	}
	
	public boolean removeChild(RepoResource resource) {
		return children.remove(resource);
	}


	/**
	 * @hibernate.set inverse="true" cascade="delete"
	 * @hibernate.key column="parent_folder"
	 * @hibernate.one-to-many class="com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder"
	 */
	public Set getSubFolders() {
		return subFolders;
	}

	public void setSubFolders(Set subFolders) {
		this.subFolders = subFolders;
	}

	protected void filterChildren(Set newChildren) {
		children.clear();
		if(newChildren!=null){
			children.addAll(newChildren);
		}
	}

	/**
	 * @hibernate.property column="hidden" type="boolean"
	 */
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isRoot() {
		return getURI().equals(Folder.SEPARATOR);
	}
	
	public Folder toClient() {
		Folder folder = new FolderImpl();
		copyTo(folder);
		return folder;
	}
	
	public Object toClient(ResourceFactory resourceFactory) {
		return toClient();
	}
	
	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		throw new JSException("jsexception.resource.copyTo.not.implemented");
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		throw new JSException("jsexception.resource.copyFrom.not.implemented");
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RepoFolder)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (isNew()) {
			return false;
		}
		RepoFolder res = (RepoFolder) obj;
		return new EqualsBuilder().append(getId(), res.getId()).isEquals();
	}

	public int hashCode() {
		if (isNew()) {
			return super.hashCode();
		}
		return new HashCodeBuilder().append(getId()).toHashCode();
	}

	public String toString() {
		return getURI();
	}
	
	public void moveTo(RepoFolder parent, RepoManager repoManager) {
		String oldParentURI = getParent().getResourceURI();
		setParent(parent);
		moved(oldParentURI, parent.getResourceURI(), repoManager);
	}

	protected void moved(String oldBaseURI, String newBaseURI, RepoManager repoManager) {
		refreshURI(repoManager);
		repoManager.update(this);
		
		repoManager.folderMoved(this, oldBaseURI, newBaseURI);
		
		Set subfolders = getSubFolders();
		if (subfolders != null && !subfolders.isEmpty()) {
			for (Iterator it = subfolders.iterator(); it.hasNext();) {
				RepoFolder subfolder = (RepoFolder) it.next();
				subfolder.moved(oldBaseURI, newBaseURI, repoManager);
			}
		}
		
		Set resources = getChildren();
		if (resources != null && !resources.isEmpty()) {
			for (Iterator it = resources.iterator(); it.hasNext();) {
				RepoResource resource = (RepoResource) it.next();
				resource.moved(oldBaseURI, newBaseURI, repoManager);
			}
		}
	}
	
}
