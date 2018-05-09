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
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanDefinitionOverrider implements BeanFactoryPostProcessor,
		Ordered {

	private static final Log log = LogFactory.getLog(BeanDefinitionOverrider.class);
	
	private int order;
	private String originalBeanName;
	private String overridingBeanName;
	private boolean mergeProperties = true;
	
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		overrideDefinition(beanFactory);
	}
	
	protected void overrideDefinition(ConfigurableListableBeanFactory beanFactory) {
		BeanDefinition originalBean = beanFactory.getBeanDefinition(originalBeanName);
		BeanDefinition overridingBean = beanFactory.getBeanDefinition(overridingBeanName);
		
		if (log.isDebugEnabled()) {
			log.debug("Overriding " + originalBeanName + " bean definition with "
					+ overridingBeanName);
		}
		
		if (!originalBean.getBeanClassName().equals(overridingBean.getBeanClassName())) {
			if (log.isDebugEnabled()) {
				log.debug("Setting " + originalBeanName + " class name to "
						+ " to " + overridingBean.getBeanClassName());
			}
			
			originalBean.setBeanClassName(overridingBean.getBeanClassName());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Adding " + overridingBeanName +" properties to " + originalBeanName);
		}
		
		MutablePropertyValues originalProps = originalBean.getPropertyValues();
		MutablePropertyValues overridingProps = overridingBean.getPropertyValues();
		
		if (!mergeProperties) {
            originalProps.getPropertyValueList().clear();
		}
		
		originalProps.addPropertyValues(overridingProps);
	}

	public String getOriginalBeanName() {
		return originalBeanName;
	}

	public void setOriginalBeanName(String originalBeanName) {
		this.originalBeanName = originalBeanName;
	}

	public String getOverridingBeanName() {
		return overridingBeanName;
	}

	public void setOverridingBeanName(String overridingBeanName) {
		this.overridingBeanName = overridingBeanName;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public boolean isMergeProperties() {
		return mergeProperties;
	}

	public void setMergeProperties(boolean mergeProperties) {
		this.mergeProperties = mergeProperties;
	}

}
