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

package com.jaspersoft.jasperserver.api.common.properties;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class DecryptingPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private static final Map<String, String> springImportedProperties = new Hashtable<String, String>();

	protected void convertProperties(Properties properties) {
		super.convertProperties(properties);

		//store spring props from properties files
		for (Object propKey : properties.keySet())
			springImportedProperties.put(propKey.toString(), properties.getProperty(propKey.toString()));

	}

	/**
	 * Convert the given property value from the properties source to the value
	 * which should be applied.
	 * <p>The default implementation simply returns the original value.
	 * Can be overridden in subclasses, for example to detect
	 * encrypted values and decrypt them accordingly.
	 *
	 * @param originalValue the original value from the properties source
	 *                      (properties file or local "properties")
	 * @return the converted value, to be used for processing
	 * @see #setProperties
	 * @see #setLocations
	 * @see #setLocation
	 * @see #convertProperty(String, String)
	 */
	@Override
	protected String convertPropertyValue(String originalValue) {
		originalValue = originalValue.trim();
		if (EncryptionEngine.isEncrypted(originalValue)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			originalValue = EncryptionEngine.decrypt(ksManager.getBuildKey(), originalValue);
		}

		return originalValue;
	}

	public static Map<String, String> getSpringImportedProperties() {
		return Collections.unmodifiableMap(springImportedProperties);
	}
}
