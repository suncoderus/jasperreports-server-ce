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

package com.jaspersoft.jasperserver.api.engine.common.service;

import org.jdom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: Papanii
 * Date: Feb 10, 2010
 * Time: 2:55:29 PM
 */

/**
 * All pages/viewmodels which require a menu need to implement this interface.
 * see NavigationActionModelSupport for an example
 */
public interface ActionModelSupport {
    /**
     * This method gets the json representation of a action model document
     * @return json object
     * @throws Exception
     */
    public String getClientActionModelDocument() throws Exception;

    /**
     * Method used for i18n conversion
     * @param label i18n code
     * @return i18n converted string
     */
    public String getMessage(String label);

}
