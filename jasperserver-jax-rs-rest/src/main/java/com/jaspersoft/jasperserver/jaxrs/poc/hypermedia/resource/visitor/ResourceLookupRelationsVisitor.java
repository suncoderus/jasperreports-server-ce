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

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.visitor.RelationsVisitor;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity.EditResourceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity.OpenResourceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity.RunResourceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.representation.ResourceLookupRepresentation;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

/**
 * TODO: there are three same methods, they should be moved to abstract relation visitor
 */
public class ResourceLookupRelationsVisitor extends RelationsVisitor<ResourceLookupRepresentation> {

    public void resolve(RunResourceActivity child, Relation relation, Boolean isLink) {

        if (isLink) {
            child.setData(representation.getBody());
            representation.addLink(child.buildLink());
        }
    }

    public void resolve(EditResourceActivity child, Relation relation, Boolean isLink) {

        if (isLink) {
            child.setData(representation.getBody());
            representation.addLink(child.buildLink());
        }
    }

    public void resolve(OpenResourceActivity child, Relation relation, Boolean isLink) {

        if (isLink) {
            child.setData(representation.getBody());
            representation.addLink(child.buildLink());
        }
    }

}


