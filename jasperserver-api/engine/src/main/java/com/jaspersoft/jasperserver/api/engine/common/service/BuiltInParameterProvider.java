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

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import java.util.List;
import java.util.Map;

/**
 * Parameters for reports and queries that are available automatically when run.
 *
 * @author Sherman Wood (sgwood@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface BuiltInParameterProvider {

    /**
     * Generate a set of standard parameters that are available for every report
     *
     * Each element is a JRParameter, value
     *
     * @param context
     * @param jrParameters unchanged
     * @param parameters unchanged
     * @return List<Object[]> [JRParameter, value]
     */
    public List<Object[]> getParameters(ExecutionContext context, List jrParameters, Map parameters);

    /**
     * Generate parameters can be requested by name that are not part of the standard set
     * 
     * @param context
     * @param jrParameters unchanged, can be null
     * @param parameters unchanged
     * @param name of parameter
     * @return List<Object[]> [JRParameter, value] or null if the given name is not set by this generator
     */
    public Object[] getParameter(ExecutionContext context, List jrParameters, Map parameters, String name);

}
