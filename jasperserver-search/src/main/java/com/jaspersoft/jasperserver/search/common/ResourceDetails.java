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

package com.jaspersoft.jasperserver.search.common;

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 */
@JsonSerialize(using = ResourceDetailsSerializer.class)
public class ResourceDetails extends ResourceLookupImpl {
    private boolean readable;
    private boolean editable;
    private boolean removable;
    private boolean administrable;
    private boolean scheduled;

    private int resourceNumber;

    private boolean hasChildren;

    public ResourceDetails() {
    }

    public ResourceDetails(Resource resource) {
        this.setName(resource.getName());
        this.setLabel(resource.getLabel());
        this.setDescription(resource.getDescription());
        this.setParentFolder(resource.getParentFolder());
        this.setURI(resource.getURI());
        this.setURIString(resource.getURIString());
        this.setCreationDate(resource.getCreationDate());
        this.setUpdateDate(resource.getUpdateDate());
        this.setResourceType(resource.getResourceType());
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isAdministrable() {
        return administrable;
    }

    public void setAdministrable(boolean administrable) {
        this.administrable = administrable;
    }

    public int getResourceNumber() {
        return resourceNumber;
    }

    public void setResourceNumber(int resourceNumber) {
        this.resourceNumber = resourceNumber;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public static String getParentFolderFromUri(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);

        if (lastSeparator <= 0) {
            return null;
        }

        return s.substring(0, lastSeparator);
    }

    public static String getNameFromUri(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);

        if (lastSeparator < 0 || lastSeparator == s.length() - 1) {
            return null;
        }

        return s.substring(lastSeparator + 1, s.length());
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
