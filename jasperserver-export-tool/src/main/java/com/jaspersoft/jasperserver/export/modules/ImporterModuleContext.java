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

package com.jaspersoft.jasperserver.export.modules;

import com.jaspersoft.jasperserver.export.ImportTask;
import org.dom4j.Element;

import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface ImporterModuleContext {

	ImportTask getImportTask();
	
	String getCharacterEncoding();

	Element getModuleIndexElement();
	
	Attributes getAttributes();

	/**
     * Returns a map, which links old tenant id to new unique tenant id
     */
	Map<String, String> getNewGeneratedTenantIds();
}
