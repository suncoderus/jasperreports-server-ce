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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by borys.kolesnykov on 9/22/2014.
 */
@XmlRootElement(name = "_embedded")
public class HypermediaEmbeddedContainer {

    private List<ClientResourceLookup> resourceLookup;

    public List<ClientResourceLookup> getResourceLookup() {
        return resourceLookup;
    }

    public void setResourceLookup(List<ClientResourceLookup> resourceLookup) {
        this.resourceLookup = resourceLookup;
    }
}
