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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanPropertyOverrider extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyOverrider.class);

	private Object override;
	private String overrideReference;

	protected Object getProcessedPropertyValue(Object originalValue) {
		if (log.isInfoEnabled()) {
			log.info("Overriding property " + getBeanName() + "." + getPropertyName() + " with " + getOverride());
		}
		
		return getOverrideValue();
	}

	protected Object getOverrideValue() {
		Object value;
		if (overrideReference == null) {
			value = override;
		} else {
			value = new RuntimeBeanReference(overrideReference);
		}
		return value;
	}

	public Object getOverride() {
		return override;
	}

	public void setOverride(Object override) {
		this.override = override;
	}
	
	public String getOverrideReference() {
		return overrideReference;
	}

	public void setOverrideReference(String overrideReference) {
		this.overrideReference = overrideReference;
	}

}
