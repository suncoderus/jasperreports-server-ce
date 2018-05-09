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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.dto.authority.hypermedia.Relation;

import java.util.Set;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id $
 */
public class HypermediaOptions {
    private Set<Relation> links;
    private Set<Relation> embedded;

    public static HypermediaOptions getDefault(){
        return new HypermediaOptions();
    }

    public Set<Relation> getLinks() {
        return links;
    }

    public HypermediaOptions setLinks(Set<Relation> links) {
        this.links = links;
        return this;
    }

    public Set<Relation> getEmbedded() {
        return embedded;
    }

    public HypermediaOptions setEmbedded(Set<Relation> embedded) {
        this.embedded = embedded;
        return this;
    }

}
