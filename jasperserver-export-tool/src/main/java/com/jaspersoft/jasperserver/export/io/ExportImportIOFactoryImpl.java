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

package com.jaspersoft.jasperserver.export.io;

import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ExportImportIOFactoryImpl implements ExportImportIOFactory {
	
	private static final CommandOut commandOut = CommandOut.getInstance();
	
	private List outputFactories;
	private List inputFactories;

	public ImportInput createInput(Parameters parameters) {
		ImportInputFactory matchingFactory = null;
		for (Iterator it = inputFactories.iterator(); it.hasNext();) {
			ImportInputFactory factory = (ImportInputFactory) it.next();
			if (factory.matches(parameters)) {
				matchingFactory = factory;
				break;
			}
		}
		
		if (matchingFactory == null) {
            // Adding non localized message cause import-export tool does not support localization.
            throw new JSException("No input parameter was specified");
		}
		
		commandOut.debug("Using " + matchingFactory.getClass().getName() + " input factory");
		
		return matchingFactory.createInput(parameters);
	}

	public ExportOutput createOutput(Parameters parameters) {
		ExportOutputFactory matchingFactory = null;
		for (Iterator it = outputFactories.iterator(); it.hasNext();) {
			ExportOutputFactory factory = (ExportOutputFactory) it.next();
			if (factory.matches(parameters)) {
				matchingFactory = factory;
				break;
			}
		}
		
		if (matchingFactory == null) {
			throw new JSException("jsexception.no.output.parameter.specified");
		}

		commandOut.debug("Using " + matchingFactory.getClass().getName() + " output factory");
		
		return matchingFactory.createOutput(parameters);
	}

	public List getOutputFactories() {
		return outputFactories;
	}

	public void setOutputFactories(List outputFactories) {
		this.outputFactories = outputFactories;
	}

	public List getInputFactories() {
		return inputFactories;
	}

	public void setInputFactories(List inputFactories) {
		this.inputFactories = inputFactories;
	}

}
