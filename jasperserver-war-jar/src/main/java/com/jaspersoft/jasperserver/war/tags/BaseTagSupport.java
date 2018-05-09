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

package com.jaspersoft.jasperserver.war.tags;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.jaspersoft.jasperserver.war.common.ConfigurationBean;

/**
 * @author Lucian Chirita
 *
 */
public abstract class BaseTagSupport extends RequestContextAwareTag {

	private static final Log log = LogFactory.getLog(BaseTagSupport.class);
	
	private static final Object NO_ATTRIBUTE = new Object();
	
	private static final String BEAN_CONFIGURATION = "configurationBean";
	
	protected void includeNested(String path) throws JspException {
		BodyContent nestedContent = pageContext.pushBody();
		boolean popped = false;
		try {
			pageContext.include(path);
			
			popped = true;
			pageContext.popBody();
			nestedContent.writeOut(pageContext.getOut());
		} catch (ServletException e) {
			log.error(e, e);
			throw new JspException(e);
		} catch (IOException e) {
			log.error(e, e);
			throw new JspException(e);
		} finally {
			if (!popped) {
				pageContext.popBody();
			}
		}
	}

	protected void includeNested(String path, Map attributes) throws JspException {
		Map restoreAttrs = setRequestAttributes(attributes);
		try {
			includeNested(path);
		} finally {
			restoreRequestAttributes(restoreAttrs);
		}
	}
	
	protected Map setRequestAttributes(Map attributes) {
		ServletRequest request = pageContext.getRequest();
		Set attributeNames = new HashSet();
		for (Enumeration it = request.getAttributeNames(); it.hasMoreElements();) {
			String attribute = (String) it.nextElement();
			attributeNames.add(attribute);
		}
		
		Map restoreMap = new HashMap();
		for (Iterator it = attributes.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String attribute = (String) entry.getKey();
			Object value = entry.getValue();
			
			Object restoreValue;
			if (attributeNames.contains(attribute)) {
				restoreValue = request.getAttribute(attribute);
			} else {
				restoreValue = NO_ATTRIBUTE;
			}
			restoreMap.put(attribute, restoreValue);
			
			request.setAttribute(attribute, value);
		}
		return restoreMap;
	}
	
	protected void restoreRequestAttributes(Map restoreMap) {
		ServletRequest request = pageContext.getRequest();
		for (Iterator it = restoreMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String attribute = (String) entry.getKey();
			Object value = entry.getValue();
			if (value == NO_ATTRIBUTE) {
				request.removeAttribute(attribute);
			} else {
				request.setAttribute(attribute, value);
			}
		}
	}
	
	protected ConfigurationBean getConfiguration() {
		return (ConfigurationBean) getRequestContext().getWebApplicationContext()
				.getBean(BEAN_CONFIGURATION, ConfigurationBean.class);
	}
}
