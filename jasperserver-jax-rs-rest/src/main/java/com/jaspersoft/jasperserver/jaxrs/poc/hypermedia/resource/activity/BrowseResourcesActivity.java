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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.Link;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.representation.ResourceLookupCollectionRepresentation;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;


/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class BrowseResourcesActivity extends SearchResourcesActivity {

    @Override
    public ResourceLookupCollectionRepresentation buildRepresentation() {
        return new ResourceLookupCollectionRepresentation(data);
    }

    @Override
    public Relation getOwnRelation() {
        return  Relation.folder;
    }

    @Override
    public EmbeddedElement buildLink() {

        Link link = null;

        String url = null;

        if (criteria != null){

            url = buildRepositoryUrl();

            if (url != null){

                link = new Link()
                        .setHref(url)
                        .setTitle(getMessage("view.repository"))
                        .setType(MediaType.TEXT_HTML)
                        .setProfile("GET")
                        .setRelation(getOwnRelation());
            }

        }else{
            throw new IllegalStateException("Search criteria isn't initialized, wrong state of activity");
        }

        return link;
    }

    protected String buildRepositoryUrl(){
        return MessageFormat.format(
                "{0}flow.html?_flowId=searchFlow",
                requestInfoProvider.getBaseUrl()
        );

    }

}
