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

import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ParameterContributor;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.ParameterContributorFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonSeriesParameterContributorFactory implements ParameterContributorFactory {
	
	public static final String PROPERTY_JSON_FOLDER = "com.jaspersoft.jasperserver.json.series.folder";

	public static final String PROPERTY_JSON_RESOURCE_PATTERN = "com.jaspersoft.jasperserver.json.series.resource.pattern";
	
	private static final JsonSeriesParameterContributorFactory INSTANCE = new JsonSeriesParameterContributorFactory();
	
	public static JsonSeriesParameterContributorFactory instance() {
		return INSTANCE;
	}

	public JsonSeriesParameterContributorFactory() {
	}

	@Override
	public List<ParameterContributor> getContributors(ParameterContributorContext context) throws JRException {
		JRPropertiesUtil props = JRPropertiesUtil.getInstance(context.getJasperReportsContext());
		String folder = props.getProperty(context.getDataset(), PROPERTY_JSON_FOLDER);
		String resourcePattern = props.getProperty(context.getDataset(), PROPERTY_JSON_RESOURCE_PATTERN);
		if (folder != null && resourcePattern != null) {
			JsonSeriesParameterContributor contributor = new JsonSeriesParameterContributor(folder, resourcePattern);
			return Collections.<ParameterContributor>singletonList(contributor);
		}
		return null;
	}

}
