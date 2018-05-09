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

package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiAxisQuery;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 15.02.2016
 */
@XmlRootElement(name = "queryExecution")
@XmlType(propOrder = {"query", "dataSourceUri"})
public class ClientMultiAxesQueryExecution extends ClientQueryExecution<ClientMultiAxisQuery,
        ClientMultiAxesQueryExecution> {
    @Valid
    private ClientMultiAxisQuery query;

    public ClientMultiAxesQueryExecution() {
    }

    public ClientMultiAxesQueryExecution(ClientMultiAxesQueryExecution clientExecution) {
        super(clientExecution);
        setQuery(new ClientMultiAxisQuery(clientExecution.getQuery()));
    }

    public ClientMultiAxesQueryExecution(ClientMultiAxisQuery clientQuery, String dataSourceUri) {
        setQuery(clientQuery);
        setDataSourceUri(dataSourceUri);
    }

    @XmlElement
    @Override
    public ClientMultiAxisQuery getQuery() {
        return query;
    }

    public ClientMultiAxesQueryExecution setQuery(ClientMultiAxisQuery query) {
        this.query = query;
        return this;
    }

    /**
     * This method is required for proper JAXB serialization in XML.
     *
     * @return
     */
    @XmlElement
    @Override
    public String getDataSourceUri() {
        return super.getDataSourceUri();
    }

    /**
     * This method is required for proper JAXB serialization in XML.
     *
     * @param dataSourceUri
     * @return
     */
    @Override
    public ClientMultiAxesQueryExecution setDataSourceUri(String dataSourceUri) {
        return super.setDataSourceUri(dataSourceUri);
    }
}
