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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * A property resource configurer that resolves placeholders in bean property values of
 * context definitions, immediately after the bean has been initialized.
 * 
 * It is useful to resolve bean property placeholders in other <code>Ordered</code>
 * <code>BeanFactoryPostProcessor</code>. It must be noted that the other <code>Ordered</code>
 * <code>BeanFactoryPostProcessor</code> must be defined after this.
 * 
 * A caveat is that not all <code>ListableBeanFactory</code> implementations
 * return bean names in the order of definition as prescribed by the interface,
 * which is relied upon by this configurer.
 * 
 * <p>Example XML context definition:
 *
 * <pre>
 * &lt;bean id="systemPropertiesConfigurer"
 *      class="au.com.cardlink.common.spring.beans.factory.config.EagerPropertyPlaceholderConfigurer"&gt;
 *   &lt;property name="placeholderPrefix"&gt;&lt;value&gt;$${&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * &lt;bean id="configPropertiesConfigurer"
 *      class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer"&gt;
 *   &lt;property name="location"&gt;&lt;value&gt;$${config.properties.location}&lt;/value&gt;&lt;/property&gt;
 *   &lt;property name="fileEncoding"&gt;&lt;value&gt;$${cardlink.properties.encoding}&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 * 
 * @author Alex Wei (ozgwei@gmail.com)
 * @since 26/07/2006
 * @see Ordered
 * @see BeanFactoryPostProcessor
 * @see ListableBeanFactory
 * @see PropertyPlaceholderConfigurer
 */
public class EagerPropertyPlaceholderConfigurer
	extends PropertyPlaceholderConfigurer implements InitializingBean {

	private ConfigurableListableBeanFactory beanFactory;
	private boolean processingCompleted = false;
	
	/**
	 * Zero-argument constructor.
	 */
	public EagerPropertyPlaceholderConfigurer() {
		super();
	}

	/**
	 * Eagerly resolves property placeholders so that the bean definitions of other <code>BeanFactoryPostProcessor</code>
	 * can be modified before instantiation.
	 */
	public void afterPropertiesSet() throws Exception {
		if (this.beanFactory != null) {
			super.postProcessBeanFactory(this.beanFactory);
			this.processingCompleted = true;
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		// Obtains the BeanFactory where bean definitions with unresolved property placeholders are stored.
		if (beanFactory instanceof ConfigurableListableBeanFactory) {
			this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
		} else {
			this.beanFactory = null;
		}
		super.setBeanFactory(beanFactory);
	}

	/**
	 * Resolves property placeholders only if the post processing was not run in {@link #afterPropertiesSet}.
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
		throws BeansException {

		// Should beanFactory be compared with this.beanFactory to ensure they are the same factory?
		if (!processingCompleted) {
			super.postProcessBeanFactory(beanFactory);
			processingCompleted = true;
		}
	}

}

