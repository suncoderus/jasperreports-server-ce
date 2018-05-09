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
package com.jaspersoft.jasperserver.dto.adhoc.query.group;

import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;

import java.util.List;

/**
 * Query Grouping clause
 *
 * @author Andriy Godovanets
 */
public interface ClientGroupBy<A extends ClientAxis> {

    /**
     * Get concrete axis by its name
     *
     * @return Axis
     */
    A getAxis(ClientGroupAxisEnum name);

    /**
     * Get axis by index
     *
     * @param index axis index
     * @return Axis
     */
    A getAxis(int index);

    /**
     * Get all grouping axes
     *
     * @return Grouping axes
     */
    List<A> getAxes();

}
