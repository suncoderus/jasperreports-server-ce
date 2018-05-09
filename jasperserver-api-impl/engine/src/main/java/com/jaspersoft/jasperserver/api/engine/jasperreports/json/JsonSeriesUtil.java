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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonSeriesUtil {

	public static int matchingEntryIndex(ArrayNode selectedEntries, Map<String, String> values) {
		int idx = 0;
		for (JsonNode entry : selectedEntries) {
			if (mathesEntry(entry, values)) {
				return idx;
			}
			++idx;
		}
		return -1;
	}

	protected static boolean mathesEntry(JsonNode entry, Map<String, String> values) {
		JsonNode paramsNode = entry.get("params");
		for (Entry<String, String> valueEntry : values.entrySet()) {
			String name = valueEntry.getKey();
			String value = valueEntry.getValue();
			JsonNode valueNode = paramsNode.get(name);
			String entryValue = valueNode == null ? null : valueNode.asText();
			boolean matches = value == null || value.isEmpty() ? (entryValue == null || entryValue.isEmpty()) : value.equals(entryValue);
			if (!matches) {
				return false;
			}
		}
		return true;
	}

	public static List<String> findJsonResources(RepositoryService repository, String folder, String resourcePattern) {
		FilterCriteria filter = FilterCriteria.createFilter(ContentResource.class);
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(folder));
		filter.addFilterElement(FilterCriteria.createPropertyLikeFilter("name", resourcePattern));
		ResourceLookup[] resources = repository.findResource(null, filter);
		
		List<String> locations = new ArrayList<String>(resources.length);
		for (ResourceLookup resource : resources) {
			locations.add(resource.getURI());
		}
		return locations;
	}
	
}
