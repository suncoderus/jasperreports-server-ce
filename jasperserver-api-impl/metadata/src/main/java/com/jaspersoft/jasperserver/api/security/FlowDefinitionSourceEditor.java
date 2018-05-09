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

package com.jaspersoft.jasperserver.api.security;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.ConfigAttributeEditor;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FlowDefinitionSourceEditor extends PropertyEditorSupport {

    public void setAsText(String s) throws IllegalArgumentException {
        FlowDefinitionSource source = new FlowDefinitionSource();

        if (s != null && s.length() > 0) {
            PropertiesEditor propertiesEditor = new PropertiesEditor();
            propertiesEditor.setAsText(s);
            Properties props = (Properties) propertiesEditor.getValue();

            ConfigAttributeEditor configAttribEd = new ConfigAttributeEditor();
            for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Entry) it.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();

                configAttribEd.setAsText(value);
                Collection<ConfigAttribute> attr = (Collection<ConfigAttribute>) configAttribEd.getValue();

                source.addFlow(name, attr);
            }
        }

        setValue(source);
    }

}
