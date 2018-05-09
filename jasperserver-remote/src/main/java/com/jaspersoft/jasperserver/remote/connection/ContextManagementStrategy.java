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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ContextManagementStrategy<ContextType, InternalContextType> {
    /**
     * Try to create the in-memory context by the given descriptor.
     * @param contextDescription - description of the context to create
     * @return internal representation of the context description after successful creation
     * @throws IllegalParameterValueException in case if any description parameter is incorrect.
     */
    InternalContextType createContext(ContextType contextDescription, Map<String, Object> contextData) throws IllegalParameterValueException;

    /**
     * Do clean actions (if needed) before context description is removed from a memory.
     * @param contextDescription - description of the context to delete
     */
    void deleteContext(InternalContextType contextDescription, Map<String, Object> contextData);

    /**
     * Prepare connection description object to be returned to the client. E.g. here is the place to return description copy without password.
     * @param contextDescription - the original context description to process.
     * @return context description without authentication data.
     */
    ContextType getContextForClient(InternalContextType contextDescription, Map<String, Object> contextData);
}
