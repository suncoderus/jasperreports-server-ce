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
package com.jaspersoft.jasperserver.dto.adhoc.query;

/**
 * @author Andriy Godovanets
 */
public interface ClientAggregate {

    /**
     * Name of the simple summary function
     *
     * @return aggregate function name
     */
    String getAggregateFunction();

    /**
     * Name of the simple summary function
     *
     * @return aggregate function name
     */
    String getAggregateFirstLevelFunction();

    /**
     * Aggregate expression string
     *
     * @return aggregate expression
     */
    String getAggregateExpression();


    /**
     * Aggregate expression argument variable
     *
     * @return expression argument
     */
    String getAggregateArg();

    /**
     * Aggregate expression resulting data type
     *
     * @return Java type
     */
    String getAggregateType();

}
