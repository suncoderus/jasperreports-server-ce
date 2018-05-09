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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanPropertyListAppender extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyListAppender.class);

	private Object appended;
	private int index = -1;

	protected Object getProcessedPropertyValue(Object originalValue) {
		List newValue;
		if (originalValue == null) {
			newValue = new ArrayList();
		} else {
			if (!(originalValue instanceof List)) {
				throw new JSException("jsexception.property.not.a.list", new Object[] {getPropertyName(), getBeanName()});
			}
			newValue = (List) originalValue;
		}
		if (appended instanceof List) {
			List appendedList = (List) appended;
			if (index >= 0) {
				newValue.addAll(index, appendedList);
				
				if (log.isInfoEnabled()) {
					log.info("Inserting " + appendedList.size() + " entries at index " + index 
							+ " into " + getBeanName() + "." + getPropertyName());
				}
			} else {
				newValue.addAll(appendedList);
				
				if (log.isInfoEnabled()) {
					log.info("Adding " + appendedList.size() + " entries to " 
							+ getBeanName() + "." + getPropertyName());
				}
			}
		} else {
			if (index >= 0) {
				newValue.add(index, appended);
				
				if (log.isInfoEnabled()) {
					log.info("Inserting 1 entry at index " + index 
							+ " into " + getBeanName() + "." + getPropertyName());
				}
			} else {
				newValue.add(appended);
				
				if (log.isInfoEnabled()) {
					log.info("Adding 1 entry to " 
							+ getBeanName() + "." + getPropertyName());
				}
			}
		}
		return newValue;
	}

	public Object getAppended() {
		return appended;
	}

	public void setAppended(Object appended) {
		this.appended = appended;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
