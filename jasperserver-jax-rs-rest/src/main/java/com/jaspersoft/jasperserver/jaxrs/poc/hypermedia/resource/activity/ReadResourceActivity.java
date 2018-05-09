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

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.Activity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.AbstractActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.representation.ResourceLookupRepresentation;

import java.util.List;
import java.util.Map;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class ReadResourceActivity extends AbstractActivity<ResourceLookupRepresentation, ClientResourceLookup> {

    public ReadResourceActivity() {
        super();
    }

    public ReadResourceActivity(Map<Relation, Activity> relations, List<Relation> linkRelations) {
        super(relations, linkRelations);
    }

    @Override
    public ClientResourceLookup findData(GenericRequest request) {
        //haven't api to get only one resource lookup
        return null;
    }

    @Override
    public ResourceLookupRepresentation buildRepresentation() {
        return new ResourceLookupRepresentation(data);
    }

    @Override
    public Relation getOwnRelation() {
        return Relation.resource;
    }
}
