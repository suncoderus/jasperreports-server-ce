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

package example.cdspro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AbstractTextDataSourceDefinition;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDomainMetaDataImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomDomainMetaData;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

/**
 * A definition of an XMLA datasource.
 */
public class XmlaQueryDataSourceDefinition extends AbstractTextDataSourceDefinition {

    private static final long serialVersionUID = -9093395922486182432L;

    @Override
    public CustomDomainMetaData getCustomDomainMetaData(CustomReportDataSource customReportDataSource) throws Exception {
        Map<String, Object> properties = getDataSourceServicePropertyMap(customReportDataSource, new HashMap<String, Object>());

        XmlaQueryDataSourceHelper helper = new XmlaQueryDataSourceHelper(properties);
        List<JRField> fields = helper.getFields();

        setFieldTypes(helper.getDataSource(), fields);

        CustomDomainMetaDataImpl md = (CustomDomainMetaDataImpl) CustomDomainMetadataUtils.createCustomDomainMetaData(getQueryLanguage(), fields);
        md.setQueryText((String) properties.get("query"));

        return md;
    }

    private void setFieldTypes(JRDataSource jrDataSource, List<JRField> fields) throws JRException {
        List<String> types = getFieldTypes(jrDataSource, fields);
        Iterator<String> typeIter = types.iterator();
        Iterator<JRField> fieldIter = fields.iterator();
        while (typeIter.hasNext() && fieldIter.hasNext()) {
            ((JRDesignField) fieldIter.next()).setValueClassName(typeIter.next());
        }
    }

    @Override
    public Map<String, Object> customizePropertyValueMap(CustomReportDataSource customReportDataSource, Map<String, Object> propertyValueMap) {
        return propertyValueMap;
    }

}
