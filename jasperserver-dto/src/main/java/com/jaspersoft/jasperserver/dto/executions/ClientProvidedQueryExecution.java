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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 15.02.2016
 */
@XmlRootElement(name = "queryExecution")
public class ClientProvidedQueryExecution extends AbstractClientExecution<ClientProvidedQueryExecution> {

    public ClientProvidedQueryExecution() {
    }

    public ClientProvidedQueryExecution(String dataSourceUri) {
        setDataSourceUri(dataSourceUri);
    }

    public ClientProvidedQueryExecution(ClientProvidedQueryExecution clientExecution) {
        super(clientExecution);
    }

    @Override
    public ClientQueryParams getParams() {
        return super.getParams();
    }

    @Override
    public ClientProvidedQueryExecution setDataSourceUri(String dataSourceUri) {
        return super.setDataSourceUri(dataSourceUri);
    }

    @XmlElement
    @Override
    public String getDataSourceUri() {
        return super.getDataSourceUri();
    }
}
