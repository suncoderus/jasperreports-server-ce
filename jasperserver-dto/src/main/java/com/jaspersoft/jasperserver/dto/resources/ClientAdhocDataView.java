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
package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.adhoc.component.ClientGenericComponent;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiAxisQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientQuery;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <p>AdhocDataView belongs to PRO codebase, but ClientAdhocDataView should be placed to CE because of usage in AbstractClientDataSourceHolder</p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ClientAdhocDataView.java 27624 2013-03-01 09:55:15Z ykovalchyk $
 */
@XmlRootElement(name = ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE)
public class ClientAdhocDataView extends AbstractClientDataSourceHolder<ClientAdhocDataView> implements ClientReferenceableDataSource {

    private ClientQuery query;
    private ClientGenericComponent component;

    private ClientAdhocDataViewSchema schema;

    private List<ClientBundle> bundles;

    public ClientAdhocDataView() {
        super();
    }

    public ClientAdhocDataView(String uri) {
        this();
        setUri(uri);
    }

    @XmlElement(name = "schema")
    public ClientAdhocDataViewSchema getSchema() {
        return schema;
    }

    public void setSchema(ClientAdhocDataViewSchema schema) {
        this.schema = schema;
    }

    public ClientAdhocDataView(ClientAdhocDataView other) {
        super(other);
    }

    public ClientAdhocDataView setQuery(ClientQuery clientQuery) {
        this.query = clientQuery;
        return this;
    }

    @XmlElements({
            @XmlElement(name = "multiLevel", type = ClientMultiLevelQuery.class),
            @XmlElement(name = "multiAxes", type = ClientMultiAxisQuery.class)
    })
    public ClientQuery getQuery() {
        return query;
    }

    @XmlElementWrapper(name = "bundles")
    @XmlElement(name = "bundle")
    public List<ClientBundle> getBundles() {
        return bundles;
    }

    public ClientAdhocDataView setBundles(List<ClientBundle> bundles) {
        this.bundles = bundles;
        return this;
    }


    public ClientGenericComponent getComponent() {
        return component;
    }

    public ClientAdhocDataView setComponent(ClientGenericComponent component) {
        this.component = component;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientAdhocDataView that = (ClientAdhocDataView) o;

        if (bundles != null ? !bundles.equals(that.bundles) : that.bundles != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (bundles != null ? bundles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientAdhocDataView{" +
                "query=" + query +
                ", schema=" + schema +
                ", bundles=" + bundles +
                "} " + super.toString();
    }
}
