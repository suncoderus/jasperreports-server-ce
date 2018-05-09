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

package com.jaspersoft.jasperserver.api.metadata.user.service;

/**
 * Profile attribute level relative to target level
 *
 * @author askorodumov
 * @version $Id$
 */
public enum ProfileAttributeLevel {
    /**
     * When concerned attribute is on upper level than target level
     */
    PARENT,

    /**
     * When concerned attribute is on the target level
     */
    TARGET_ASSIGNED,

    /**
     * When concerned attribute is on lower level than target level
     */
    CHILD
}
