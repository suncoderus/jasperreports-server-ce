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
package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Container for data that are visible to a list of the {@link ExternalUserProcessor}'s
 *
 * User: dlitvak
 * Date: 8/22/12
 */
@JasperServerAPI
public class ProcessorData  {
	public enum Key {
		ERROR_DURING_SYNCHRONIZATION, EXTERNAL_AUTH_DETAILS, EXTERNAL_LOADED_DETAILS,
		EXTERNAL_AUTHORITIES, EXTERNAL_JRS_USER_TENANT_ID
	}

	private static ProcessorData processorData = new ProcessorData();

	private ProcessorData() {	}

	private static final ThreadLocal<Map<Key, Object>> dataMapHolder = new ThreadLocal<Map<Key, Object>>() {
		protected Map<Key, Object> initialValue() {
			return new HashMap<Key, Object>();
		}
	};

	public static ProcessorData getInstance() {
		if (processorData == null)
			processorData = new ProcessorData();

		return processorData;
	}

	public void addData(Key key, Object value) {
		Map<Key, Object> dataMap = dataMapHolder.get();
		dataMap.put(key, value);
	}

	public Object getData(Key key) {
		Map<Key, Object> dataMap = dataMapHolder.get();
		return dataMap.get(key);
	}

	public void removeData(Key key) {
		Map<Key, Object> dataMap = dataMapHolder.get();
		dataMap.remove(key);
	}

	public Set<Key> getDataKeys() {
		Map<Key, Object> dataMap = dataMapHolder.get();
		return dataMap.keySet();
	}

	public void clearData() {
		dataMapHolder.remove();
	}
}
