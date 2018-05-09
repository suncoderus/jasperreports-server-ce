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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.visitor;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.visitor.RelationsVisitor;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity.ReadResourceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.representation.ResourceLookupCollectionRepresentation;

import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class SearchRelationVisitor extends RelationsVisitor<ResourceLookupCollectionRepresentation> {

    public void resolve(ReadResourceActivity child, Relation relation, Boolean isLink){

        if (!isLink){
            List<ClientResourceLookup> resourceLookups = representation.getBody();

            for (ClientResourceLookup resourceLookup : resourceLookups) {
                child.setData(resourceLookup);
                HypermediaRepresentation childRepresentation = (HypermediaRepresentation) child.proceed();
                representation.addEmbedded(relation, childRepresentation);
            }
        }

    }

}
