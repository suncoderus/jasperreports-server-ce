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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.JSException;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CommandMetadataImpl implements CommandMetadata, InitializingBean {

	private List argumentNames;
	private Set argumentNameSet;

	public void afterPropertiesSet() {
		argumentNameSet = new HashSet();
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_FILES);
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_RESOURCES);
		argumentNameSet.add(BaseExportImportCommand.ARG_COMMAND_BEAN);
		if (argumentNames != null) {
			argumentNameSet.addAll(argumentNames);
		}
	}
	
	public List getArgumentNames() {
		return argumentNames;
	}

	public void validateParameters(Parameters parameters) {
		for (Iterator it = parameters.getParameterNames(); it.hasNext();) {
			String argument = (String) it.next();
			if (!argumentNameSet.contains(argument)) {
                // Adding non localized message cause import-export tool does not support localization.
                StringBuilder message = new StringBuilder("Option ");
                message.append(argument);
                message.append(" is not recognized.");
                throw new JSException(message.toString());
			}
		}
	}

	public void setArgumentNames(List argumentNames) {
		this.argumentNames = argumentNames;
	}

}
