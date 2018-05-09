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
import net.sf.jasperreports.engine.JRField;
import java.util.List;
import java.util.Map;


/**
 * @author ichan
 *  Interface for metadata layer of custom data source which contains JRField (name, type description), query language,
 * query text, and field name mapping <JRField name, Name in domain>
 */
@JasperServerAPI
public interface CustomDomainMetaData {

    /**
     *  return list of JRField Name (name, type, description) for custom data source
     */
    public List<JRField> getJRFieldList();

    /**
     *  return query that uses in query executer
     */
    public String getQueryText();


    /**
     *  return query languages
     */
    public String getQueryLanguage();


    /**
     *  return field mapping relationship between data source field names and domain display names
     */
    public Map<String, String> getFieldMapping();     // <JRField Name, Domain Names>
}
