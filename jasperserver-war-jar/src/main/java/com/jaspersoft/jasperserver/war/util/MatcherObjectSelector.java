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

package com.jaspersoft.jasperserver.war.util;

import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MatcherObjectSelector<C, R> implements ObjectSelector<C, R> {

	public static class Entry<C, R> {
		private ObjectMatcher<C> matcher;
		private R object;
		
		public ObjectMatcher<C> getMatcher() {
			return matcher;
		}
		
		public void setMatcher(ObjectMatcher<C> matcher) {
			this.matcher = matcher;
		}

		public R getObject() {
			return object;
		}

		public void setObject(R object) {
			this.object = object;
		}
	}
	
	private List<Entry<C, R>> entries;
	
	public R select(C criteria) {
		R selected = null;
		if (entries != null && !entries.isEmpty()) {
			for (Entry<C, R> entry : entries) {
				if (entry.getMatcher().matches(criteria)) {
					selected = entry.getObject();
					break;
				}
			}
		}
		return selected;
	}

	public List<Entry<C, R>> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry<C, R>> entries) {
		this.entries = entries;
	}

}
