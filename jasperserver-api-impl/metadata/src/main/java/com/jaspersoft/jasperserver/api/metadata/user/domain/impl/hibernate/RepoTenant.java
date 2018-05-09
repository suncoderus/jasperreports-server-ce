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

package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.tenant.service.TenantPersistenceResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityPersistenceService;

/**
 * @author achan
 *
 */
public class RepoTenant implements IdedObject {
	
	private long id;
	private String tenantId = null;
    private String tenantAlias = null;
	private RepoTenant parent = null;
	private String tenantName = null;
	private List attributes = new ArrayList();
    private String tenantDesc = null;
    private String tenantNote = null;
    private String tenantUri = null;
    private String tenantFolderUri = null;
    private String theme = null;
    private Set subTenants;
    private Set users;
    private Set roles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public String getTenantId() {
		return tenantId;
	}
	
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

    public String getTenantAlias() {
        return tenantAlias;
    }

    public void setTenantAlias(String tenantAlias) {
        this.tenantAlias = tenantAlias;
    }

    public RepoTenant getParent() {
		return parent;
	}
	
	public void setParent(RepoTenant parent) {
		this.parent = parent;
	}
	
	public String getTenantName() {
		return tenantName;
	}
	
	public void setTenantName(String tName) {
	    tenantName = tName;	
	}
	
	public List getAttributes() {
		return attributes;
	}

    public void setAttributes(List attrs) {
        attributes = attrs;
    }	
    
	public void copyFromClient(Tenant r, TenantPersistenceResolver persistenceService) {
		// r -> this
		setTenantId(r.getId());
		setTenantAlias(r.getAlias());

		RepoTenant parentTenant = persistenceService.getPersistentTenant(
				r.getParentId(), true);
		setParent(parentTenant);
		
		setTenantName(r.getTenantName());
		setAttributes(r.getAttributes());
		setTenantDesc(r.getTenantDesc());
		setTenantNote(r.getTenantNote());
		setTenantUri(r.getTenantUri());
		setTenantFolderUri(r.getTenantFolderUri());
        setTheme(r.getTheme());
	}
	
	public void copyToClient(Tenant r) {
		// this -> r
		r.setId(getTenantId());
		r.setAlias(getTenantAlias());
		r.setParentId(getParentId());
		r.setTenantName(getTenantName());
		r.setAttributes(getAttributes());
		r.setTenantDesc(getTenantDesc());
		r.setTenantNote(getTenantNote());
		r.setTenantUri(getTenantUri());
		r.setTenantFolderUri(getTenantFolderUri());
        r.setTheme(getTheme());
	}

    public Object toClient(ResourceFactory clientMappingFactory) {
        // this -> r
        Tenant r = (Tenant) clientMappingFactory.newObject(Tenant.class);

        r.setId(getTenantId());
        r.setAlias(getTenantAlias());
        r.setParentId(getParentId());
        r.setTenantName(getTenantName());
        r.setAttributes(getAttributes());
        r.setTenantDesc(getTenantDesc());
        r.setTenantNote(getTenantNote());
        r.setTenantUri(getTenantUri());
        r.setTenantFolderUri(getTenantFolderUri());
        r.setTheme(getTheme());

        return r;
    }

    @Override
    public void copyFromClient(Object objIdent, PersistentObjectResolver resolver) {
        Tenant r = (Tenant) objIdent;
        setTenantId(r.getId());
        setTenantAlias(r.getAlias());

        RepoTenant parentTenant = ((UserAuthorityPersistenceService) resolver).getPersistentTenant(tenantId, true);
        setParent(parentTenant);

        setTenantName(r.getTenantName());
        setAttributes(r.getAttributes());
        setTenantDesc(r.getTenantDesc());
        setTenantNote(r.getTenantNote());
        setTenantUri(r.getTenantUri());
        setTenantFolderUri(r.getTenantFolderUri());
        setTheme(r.getTheme());
    }

    protected String getParentId() {
		return parent == null ? null : parent.getTenantId();
	}
	
	/**
	 * @return Returns the tenantDesc.
	 */
	public String getTenantDesc() {
		return tenantDesc;
	}
	/**
	 * @param tenantDesc The tenantDesc to set.
	 */
	public void setTenantDesc(String tenantDesc) {
		this.tenantDesc = tenantDesc;
	}
	/**
	 * @return Returns the tenantNote.
	 */
	public String getTenantNote() {
		return tenantNote;
	}
	/**
	 * @param tenantNote The tenantNote to set.
	 */
	public void setTenantNote(String tenantNote) {
		this.tenantNote = tenantNote;
	}

	/**
	 * @return Returns the tenantUri.
	 */
	public String getTenantUri() {
		return tenantUri;
	}

	/**
	 * @param tenantUri The tenantUri to set.
	 */
	public void setTenantUri(String tenantUri) {
		this.tenantUri = tenantUri;
	}

	public String getTenantFolderUri() {
		return tenantFolderUri;
	}

	public void setTenantFolderUri(String tenantFolderUri) {
		this.tenantFolderUri = tenantFolderUri;
	}
	
	public Set getSubTenants() {
		return subTenants;
	}

	public void setSubTenants(Set subTenants) {
		this.subTenants = subTenants;
	}

	public Set getUsers() {
		return users;
	}

	public void setUsers(Set users) {
		this.users = users;
	}

	public Set getRoles() {
		return roles;
	}

	public void setRoles(Set roles) {
		this.roles = roles;
	}

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
