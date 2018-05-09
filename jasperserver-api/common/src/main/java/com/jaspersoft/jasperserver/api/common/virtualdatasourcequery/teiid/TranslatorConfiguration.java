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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid;

import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public interface TranslatorConfiguration {

    /*
     * Return the data source product version
     */
    public String getProductVersion();

    /*
    * Return the data source product name
    */
    public String getProductName();

    /*
    * Return translator factory
    */
    public ExecutionFactory getTranslatorFactory() throws TranslatorException;

    /*
    * Return translator name
    */
    public String getTranslatorName();

    /*
    * Return translator factory class
    */
    public String getTranslatorFactoryClass();

}
