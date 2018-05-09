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
package com.jaspersoft.jasperserver.war.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.ReferenceMap;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.war.dto.StringOption;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JdkTimeZonesList implements TimeZonesList, InitializingBean {
	
	private List timeZonesIds;
	private final Map userTimeZonesLists;
	
	public JdkTimeZonesList() {
		userTimeZonesLists = Collections.synchronizedMap(new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT));
	}

	public void afterPropertiesSet() throws Exception {
		if (timeZonesIds == null) {
			String[] availableIDs = TimeZone.getAvailableIDs();
			timeZonesIds = Arrays.asList(availableIDs);
		}
	}

	public List getTimeZones(Locale userLocale) {
		List timeZones = (List) userTimeZonesLists.get(userLocale);
		if (timeZones == null) {
			timeZones = createTimeZones(userLocale);
			userTimeZonesLists.put(userLocale, timeZones);
		}
		return timeZones;
	}

	protected List createTimeZones(Locale userLocale) {
		List timeZones = new ArrayList(timeZonesIds.size() + 1);
		
		if (findExistingDefaultTZ() == null) {
			TimeZone defaultTz = getSystemDefault();
			timeZones.add(createOption(userLocale, defaultTz));
		}
		
		for (Iterator it = timeZonesIds.iterator(); it.hasNext();) {
			String id = (String) it.next();
			TimeZone timeZone = loadTimeZone(id);
			timeZones.add(createOption(userLocale, timeZone));
		}
		
		return timeZones;
	}

	protected TimeZone loadTimeZone(String id) {
		TimeZone timeZone = TimeZone.getTimeZone(id);
		if (timeZone == null) {
			String quotedTimezone = "\"" + id + "\"";
			throw new JSException("jsexception.unknown.timezone", new Object[] {quotedTimezone});
		}
		return timeZone;
	}

	public String getDefaultTimeZoneID() {
		TimeZone existingTz = findExistingDefaultTZ();
		if (existingTz == null) {
			existingTz = getSystemDefault();
		}
		return existingTz.getID();
	}

	protected TimeZone findExistingDefaultTZ() {
		TimeZone defaultTz = getSystemDefault();
		TimeZone existingTz = null;
		for (Iterator it = timeZonesIds.iterator(); it.hasNext();) {
			String id = (String) it.next();
			if (id.equals(defaultTz.getID())) {//exact match
				existingTz = defaultTz;
				break;
			} else if (existingTz == null) {
				TimeZone timeZone = loadTimeZone(id);
				if (defaultTz.hasSameRules(timeZone)) {
					existingTz = timeZone;
				}
			}
		}
		return existingTz;
	}

	protected TimeZone getSystemDefault() {
		TimeZone defaultTz = TimeZone.getDefault();
		return defaultTz;
	}

	protected StringOption createOption(Locale userLocale, TimeZone tz) {
		String description = getTimeZoneDescription(tz, userLocale);
		StringOption option = new StringOption(tz.getID(), description);
		return option;
	}

	protected String getTimeZoneDescription(TimeZone timeZone, Locale userLocale) {
		return timeZone.getDisplayName(userLocale);
	}

	public List getTimeZonesIds() {
		return timeZonesIds;
	}

	public void setTimeZonesIds(List timeZonesIds) {
		this.timeZonesIds = timeZonesIds;
		userTimeZonesLists.clear();
	}

}
