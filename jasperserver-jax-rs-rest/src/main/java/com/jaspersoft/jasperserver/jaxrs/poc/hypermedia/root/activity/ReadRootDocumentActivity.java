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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.AbstractActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.Activity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root.dto.RootDocument;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root.representation.RootDocumentRepresentation;

import java.util.List;
import java.util.Map;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class ReadRootDocumentActivity extends
        AbstractActivity<RootDocumentRepresentation, RootDocument> {


    public ReadRootDocumentActivity(Map<Relation, Activity> relations, List<Relation> linkRelations) {
        super(relations, linkRelations);
    }

    @Override
    public RootDocument findData(GenericRequest request) {
        return new RootDocument()
                .setTitle(getMessage("home.title"))
                .setWelcomeMessage(getMessage("home.getting.started"));
    }

    @Override
    public RootDocumentRepresentation buildRepresentation() {
        return new RootDocumentRepresentation(data);
    }

    @Override
    public Relation getOwnRelation() {
        return Relation.root;
    }
}

