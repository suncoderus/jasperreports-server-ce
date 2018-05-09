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

import net.sf.jasperreports.engine.design.JasperDesign;

/**
 * If you need to modify JRXML to get around some kind of backwards compatibility or some other issue,
 * you can do so on the fly by implementing this interface.
 * It gets called after the JRXML is read into a JasperDesign and before it gets compiled.
 * If "fix" returns false, nothing more is done.
 * If "fix" returns true, the updated JasperDesign will be turned into JRXML and updated in the repository,
 * then compiled in preparation for running the report.
 *  
 * @author btinsman
 * @version $Id$
 */
public interface JRXMLFixer {
    /**
     * inspect and optionally modify the JasperDesign
     * 
     * return true if JRXML needs updating
     * 
     * @param design
     * @return
     */
    boolean fix(JasperDesign design);
}
