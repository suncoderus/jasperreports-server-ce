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

package com.jaspersoft.jasperserver.war.themes;

import org.springframework.ui.context.Theme;

/**
 * This interface extends org.springframework.ui.context.Theme.
 * It allows to keep the reference to a parent theme to be used if
 * this theme cannot resolve a resource.
 * @author asokolnikov
 */
public interface HierarchicalTheme extends Theme {

    /**
     * Returns parent theme instance
     * @return
     */
    public Theme getParentTheme();

    /**
     * Sets the parent theme instance
     * @param theme
     */
    public void setParentTheme(Theme theme);
}
