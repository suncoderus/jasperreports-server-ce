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
package com.jaspersoft.jasperserver.api.security;

import org.apache.log4j.spi.LoggerFactory;
import org.owasp.esapi.LogFactory;
import org.owasp.esapi.reference.Log4JLogFactory;
import org.owasp.esapi.reference.Log4JLoggerFactory;

/**
 * This is a custom ESAPI org.owasp.esapi.reference.Log4JLogFactory.
 * It was introduced to avoid collision with jboss 7 log4j handling.
 * The collision happened in org.owasp.esapi.reference.Log4JLogFactory#getLogger(String): ClassCastException
 * This happened because jboss 7 cause log4j's LogManager to return org.jboss.logging.Logger instead of esapi one.
 *
 * (bugzilla 28508)
 *
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 6/22/12
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSESAPILog4JLogFactory implements LogFactory {

	private static volatile LogFactory singletonInstance;

	//The Log4j logger factory to use
	LoggerFactory factory = new Log4JLoggerFactory();

	public static LogFactory getInstance() {
		if ( singletonInstance == null ) {
			synchronized ( Log4JLogFactory.class ) {
				if ( singletonInstance == null ) {
					singletonInstance = new JSESAPILog4JLogFactory();
				}
			}
		}
		return singletonInstance;
	}

	protected JSESAPILog4JLogFactory() {}

	public org.owasp.esapi.Logger getLogger(Class clazz) {
		return (org.owasp.esapi.Logger) factory.makeNewLoggerInstance(clazz.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	public org.owasp.esapi.Logger getLogger(String moduleName) {
		return (org.owasp.esapi.Logger) factory.makeNewLoggerInstance(moduleName);
	}
}
