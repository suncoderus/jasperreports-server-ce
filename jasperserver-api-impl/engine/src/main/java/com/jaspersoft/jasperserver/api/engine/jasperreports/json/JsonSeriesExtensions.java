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
package com.jaspersoft.jasperserver.api.engine.jasperreports.json;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.ParameterContributorFactory;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonSeriesExtensions implements ExtensionsRegistryFactory {

	private static final ExtensionsRegistry REGISTRY = 
			new SingletonExtensionRegistry<ParameterContributorFactory>(
					ParameterContributorFactory.class, JsonSeriesParameterContributorFactory.instance());
	
	public JsonSeriesExtensions() {
	}

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
		return REGISTRY;
	}

}
