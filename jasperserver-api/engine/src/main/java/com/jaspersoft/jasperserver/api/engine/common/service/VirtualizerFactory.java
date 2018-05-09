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

import com.jaspersoft.jasperserver.api.engine.common.domain.ReportResult;

import net.sf.jasperreports.engine.JRVirtualizer;


/**
 * An interface to provide instances of JRVirtualizer to ReportUnits being executed on the server
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: LocalesListImpl.java 5713 2006-11-21 11:44:34Z lucian $
 */
public interface VirtualizerFactory
{
	
	/**
	 * Return a new JRVirtualizer instance
	 * @return new virtualizer
	 */
	public JRVirtualizer getVirtualizer();

	/**
	 * Notify the virtualizer that a report has been completed and that no further modifications are expected.
	 * 
	 * @param report
	 */
	public void setReadOnly(ReportResult report);
	
	/**
	 * Notify the factory that a report is no longer being used, so any resources consumed by it can be released.
	 * 
	 * @param report a report execution result which is no longer in use
	 */
	public void disposeReport(ReportResult report);

}
