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
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * 
 * From http://forum.springframework.org/showthread.php?t=30455&highlight=BeanFactoryPostProcessor :
 * 
 * "I saw that BeanFactoryPostProcessor-implementing beans that implement the Ordered interface get created 
 * and run their processing before BeanFactoryPostProcessor-implementing beans that do not implement the 
 * Ordered interface."
 * 
 * This turns out to be not to help in some cases. BeanPostProcessors like PropertyPlaceholderConfigurers 
 * will always run after BeanFactoryPostProcessors, so you will get unresolved substitutions for beans you directly
 * or indirectly try to update.
 * 
 * The workaround is to use EagerPropertyPlaceholderConfigurer http://opensource.atlassian.com/projects/spring/browse/SPR-1076
 * 
 */
public abstract class AbstractBeanPropertyProcessor implements BeanFactoryPostProcessor, Ordered {

	private static final Log log = LogFactory.getLog(AbstractBeanPropertyProcessor.class);
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	private String beanName;
	private String propertyName;
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(getBeanName());
		MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
		Object value = null;
		PropertyValue propertyValue = propertyValues.getPropertyValue(getPropertyName());
		if (propertyValue != null) {
			value = propertyValue.getValue();
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Original value of " + getBeanName() + "." + getPropertyName()
					+ ": " + value);
		}
		
		Object appendedValue = getProcessedPropertyValue(value);
		
		if (log.isDebugEnabled()) {
			log.debug("New value of " + getBeanName() + "." + getPropertyName()
					+ ": " + appendedValue);
		}
		
		propertyValues.addPropertyValue(getPropertyName(), appendedValue);
	}

	protected abstract Object getProcessedPropertyValue(Object originalValue);

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}


}
