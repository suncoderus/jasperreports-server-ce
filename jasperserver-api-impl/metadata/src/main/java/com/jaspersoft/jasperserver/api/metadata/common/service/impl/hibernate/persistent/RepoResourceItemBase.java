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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedRepoObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;

import java.io.Serializable;
import java.util.Date;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public abstract class RepoResourceItemBase implements IdedRepoObject, Serializable {
    protected long id;
    protected int version;

    protected Date creationDate;
    protected Date updateDate;

    protected String name = null;
    protected String label = null;
    protected String description = null;

    protected RepoFolder parent;

    @Override
    public final ResourceLookup toClientLookup() {
        ResourceLookup resourceLookup = new ResourceLookupImpl();

        resourceLookup.setParentFolder(this.getParent().getResourceURI());
        resourceLookup.setName(getName());

        resourceLookup.setVersion(version);

        resourceLookup.setLabel(label);
        resourceLookup.setCreationDate(creationDate);
        resourceLookup.setDescription(description);

        resourceLookup.setCreationDate(creationDate);
        resourceLookup.setUpdateDate(updateDate);

        resourceLookup.setResourceType(this.getResourceType());

        return resourceLookup;
    }

    @Override
    public final void copyFromClient(Object objIdent, PersistentObjectResolver resolver) {
        throw new IllegalStateException("RepoResourceItemBase objects intended to be read only. Use RepoResourceBase based hierarchy to save items");
    }

    @Override
    public final Object toClient(ResourceFactory clientMappingFactory) {
        return this.toClientLookup();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public String getName() {
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

    public RepoFolder getParent() {
        return parent;
    }

    public void setParent(RepoFolder parent) {
        this.parent = parent;
    }

    public abstract String getResourceType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepoResourceItemBase that = (RepoResourceItemBase) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.getResourceURI().equals(that.parent.getResourceURI()) : that.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.getResourceURI().hashCode() : 0);
        return result;
    }
}
