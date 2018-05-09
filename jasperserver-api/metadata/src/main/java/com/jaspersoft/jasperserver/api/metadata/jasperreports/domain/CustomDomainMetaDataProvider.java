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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * @author ichan
 *  Interface for metadata layer provider of custom data source which needs to implement in either Custom Data Source Service/ Custom Data Source Definition.
 */
@JasperServerAPI
public interface CustomDomainMetaDataProvider {

    /*
     * This function is used for retrieving the metadata layer of custom data source in form of CustomDomainMetaData
     * CustomDomainMetaData contains information JRFields, query, query language and field name mapping (actual JRField name, name used in domain)
     *
     */
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customReportDataSource) throws Exception;
}
