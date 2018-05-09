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

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by borys.kolesnykov on 9/22/2014.
 */
public class HypermediaResourceLookup extends ClientResourceLookup {

    private HypermediaResourceLinks links;
    private HypermediaEmbeddedContainer embedded;

    public HypermediaResourceLookup(){
    	super();
    }
    
    public HypermediaResourceLookup(ClientResourceLookup other) {
        super(other);
    }

    @XmlElement(name = "_embedded")
    public HypermediaEmbeddedContainer getEmbedded() {
        return embedded;
    }

    public void setEmbedded(HypermediaEmbeddedContainer embedded) {
        this.embedded = embedded;
    }

    @XmlElement(name = "_links")
    public HypermediaResourceLinks getLinks() {
        return links;
    }

    public void setLinks(HypermediaResourceLinks links) {
        this.links = links;
    }
}
