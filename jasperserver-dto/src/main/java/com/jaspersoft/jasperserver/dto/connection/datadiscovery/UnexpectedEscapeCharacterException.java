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
package com.jaspersoft.jasperserver.dto.connection.datadiscovery;

import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class UnexpectedEscapeCharacterException extends RuntimeException {
    private final List<Integer> indexes;
    private final String string;
    public UnexpectedEscapeCharacterException(String string, List<Integer> indexes){
        super("Unexpected escape character in string [" + string + "]. Character indexes: " + indexes.toString());
        this.string = string;
        this.indexes = indexes;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public String getString() {
        return string;
    }
}
