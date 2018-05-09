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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

/**
 * Created by stas on 7/23/14.
 */
public class ImportResourceReference extends ResourceReference {
    public ImportResourceReference(Resource localResource) {
        super(localResource);
    }

    public ImportResourceReference(String referenceURI) {
        super(referenceURI);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportResourceReference that = (ImportResourceReference) o;

        if (getTargetURI() != null ? !getTargetURI().equals(that.getTargetURI()) : that.getTargetURI() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getTargetURI() != null ? getTargetURI().hashCode() : 0;
    }
}
