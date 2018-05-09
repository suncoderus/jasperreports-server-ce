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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public interface Activity<R extends  HypermediaRepresentation, D> {


    EmbeddedElement proceed();

    D findData(GenericRequest request);

    D getData();

    Activity setData(D data);

    Activity setGenericRequest(GenericRequest genericRequest);

    GenericRequest getGenericRequest();

    R getRepresentation();

    Activity setRepresentation(R representation);

    R buildRepresentation();

    EmbeddedElement buildLink();

    Relation getOwnRelation();
}
