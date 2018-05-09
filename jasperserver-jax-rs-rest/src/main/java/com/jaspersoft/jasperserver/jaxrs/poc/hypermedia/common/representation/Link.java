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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedHashMap;

/**
 * @author Igor.Nesterenko
 * @version $Id$
 */

public class Link implements EmbeddedElement {


    private String name;

    private String profile;

    private String title;

    private String href;

    private Relation relation;

    private String type;

    private Boolean template;

    public String getName() {
        return name;
    }

    public Link setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Link setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getHref() {
        return href;
    }

    public Link setHref(String href) {
        this.href = href;
        return this;
    }

    public Relation getRelation() {
        return relation;
    }

    public Link setRelation(Relation relation) {
        this.relation = relation;
        return this;
    }

    public String getType() {
        return type;
    }

    public Link setType(String type) {
        this.type = type;
        return this;
    }

    public String getProfile() {
        return profile;
    }

    public Link setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    @Override
    public int hashCode() {
        return (new HashCodeBuilder()
                .append(name)
                .append(type)
                .append(title)
                .append(href)
                .append(relation)
                .append(template)
                .append(profile)
        ).toHashCode();
    }

    @Override
    public LinkedHashMap<Relation, EmbeddedElement> toMap() {
        LinkedHashMap<Relation, EmbeddedElement> map = new LinkedHashMap<Relation, EmbeddedElement>();
        map.put(relation, this);
        return map;
    }
}
