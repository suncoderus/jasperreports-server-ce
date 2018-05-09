/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.buildomatic;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tools.ant.PropertyHelper;

/**
 * User: dlitvak
 * Date: 8/13/13
 */
public class JSPropertyEvaluator implements PropertyHelper.PropertyEvaluator {
	private static final String ESCAPE_XML_PREFIX = "escapeXML:";
	private static final String DECRYPT_PREFIX = "decrypt:";

	public Object evaluate(String property, PropertyHelper propertyHelper) {
		Object o = null;
		if (propertyHelper.getProject() != null) {
			if (property.startsWith(ESCAPE_XML_PREFIX)) {
				String oStr = propertyHelper.getProject().getProperty(property.substring(ESCAPE_XML_PREFIX.length()));
				if (oStr != null)
					o = StringEscapeUtils.escapeXml(oStr);
			}
			else if (property.startsWith(DECRYPT_PREFIX)) {
				String oStr = propertyHelper.getProject().getProperty(property.substring(DECRYPT_PREFIX.length()));
				if (EncryptionEngine.isEncrypted(oStr)) {
					KeystoreManager ksm = KeystoreManager.getInstance();
					o = EncryptionEngine.decrypt(ksm.getBuildKey(), oStr);
				}
				else
					o = oStr; //not encrypted: remove decrypt: namespace
			}
		}

		return o == null ? null : o.toString();
	}
}
