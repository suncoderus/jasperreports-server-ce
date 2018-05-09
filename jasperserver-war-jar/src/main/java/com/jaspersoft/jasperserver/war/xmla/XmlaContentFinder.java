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

package com.jaspersoft.jasperserver.war.xmla;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import mondrian.xmla.DataSourcesConfig;

import java.util.Map;
import java.util.Properties;

/**
 * Callback to get the exposed data sources from the repository.
 *
 * @author vsabadosh
 * @version $Id$
 */
public interface XmlaContentFinder {

    DataSourcesConfig.DataSources getDataSources();

    Properties getMondrianConnectionProperties(Map<String, Object> dataSourceProperties, String role);

    MondrianConnection lookupXmlaConnection(ExecutionContext context, String dataSourceInfo);

    DataSourcesConfig.Catalog createCatalogConfigOutOfDefinition(MondrianXMLADefinition def);

}
