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
import org.springframework.beans.factory.config.TypedStringValue;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanPropertyTextAppender extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyTextAppender.class);

	private String appended;
	
	protected Object getProcessedPropertyValue(Object originalValue) {
		Object appendedValue;
		if (originalValue == null) {
			appendedValue = getAppended();
		} else {
			if (originalValue instanceof String) {
                appendedValue = originalValue + getAppended();
            } else if (originalValue instanceof TypedStringValue) {
                TypedStringValue newValue = (TypedStringValue) originalValue;
                newValue.setValue(newValue.getValue() + getAppended());

                appendedValue = newValue;
            } else {
                throw new JSException("jsexception.property.not.a.text", new Object[] {getPropertyName(), getBeanName()});
            }
		}
		
		if (log.isInfoEnabled()) {
			log.info("Appending " + getBeanName() + "." + getPropertyName() + " with " + getAppended());
		}
		
		return appendedValue;
	}

	public String getAppended() {
		return appended;
	}

	public void setAppended(String appendedText) {
		this.appended = appendedText;
	}

}
