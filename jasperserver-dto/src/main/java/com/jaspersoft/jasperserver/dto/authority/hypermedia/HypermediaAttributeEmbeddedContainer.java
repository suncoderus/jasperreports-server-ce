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
package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id $
 */
@XmlRootElement(name = "_embedded")
public class HypermediaAttributeEmbeddedContainer {
    private List<RepositoryPermission> repositoryPermissions;

    public HypermediaAttributeEmbeddedContainer() {
    }

    public HypermediaAttributeEmbeddedContainer(HypermediaAttributeEmbeddedContainer other) {
        if (other.repositoryPermissions != null) {
            repositoryPermissions = new ArrayList<RepositoryPermission>(other.repositoryPermissions.size());
            for (RepositoryPermission permission : other.repositoryPermissions) {
                RepositoryPermission newPermission = null;
                if (permission != null) {
                    newPermission = new RepositoryPermission(permission);
                }
                repositoryPermissions.add(newPermission);
            }
        }
    }

    @XmlElement(name = "permission")
    public List<RepositoryPermission> getRepositoryPermissions() {
        return repositoryPermissions;
    }

    public HypermediaAttributeEmbeddedContainer setRepositoryPermissions(List<RepositoryPermission> repositoryPermissions) {
        this.repositoryPermissions = repositoryPermissions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof HypermediaAttributeEmbeddedContainer) {
            HypermediaAttributeEmbeddedContainer other = (HypermediaAttributeEmbeddedContainer) o;
            if (repositoryPermissions == other.repositoryPermissions) {
                return true;
            }
            return repositoryPermissions != null && repositoryPermissions.equals(other.repositoryPermissions);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return repositoryPermissions != null ? repositoryPermissions.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        if (repositoryPermissions != null) {
            for (RepositoryPermission permission : repositoryPermissions) {
                if (content.length() > 0) {
                    content.append(", ");
                }
                content.append(permission.toString());
            }
        }

        return getClass().getSimpleName() + "{" +
                "repositoryPermissions=[" + content +
                "]}";
    }
}
