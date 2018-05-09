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
package com.jaspersoft.jasperserver.war.action;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.web.JRInteractiveException;
import net.sf.jasperreports.web.actions.Action;
import net.sf.jasperreports.web.actions.ActionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class JSController {
	private static final Log log = LogFactory.getLog(JSController.class);

	/**
	 *
	 */
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
    public JSController(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}


	/**
     * @throws ActionException
	 */
	public void runAction(
		ReportContext reportContext,
		Action action
    ) throws JRInteractiveException {
        if (action != null) {
				action.run();
			}
		}

}
