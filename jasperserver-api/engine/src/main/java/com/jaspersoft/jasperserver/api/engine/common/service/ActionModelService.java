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
 * Time: 1:01:49 PM
 */

/**
 * Action model service interface. 
 */
public interface ActionModelService {
    /**
     * Method called to create menu data using the action model files.
     */
    public void generateActionModelMenus();
    /**
     * Used to get the document object from the action model map
     * @param context action model name
     * @return document object
     */
    public Document getActionModelMenu(String context);

}
