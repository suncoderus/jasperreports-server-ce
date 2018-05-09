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

import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

/**
 * An implementation of theme source which provides an instance of
 * RepositoryFolderTheme for a given theme name.
 * @author asokolnikov
 */
public class RepositoryFolderThemeSourceImpl implements HierarchicalThemeSource {

    private ThemeSource parentThemeSource;
    private ThemeCache themeCache;

    public Theme getTheme(String themeName) {
        Theme theme = themeCache.getTheme(themeName);
        if (theme == null && parentThemeSource != null) {
            return parentThemeSource.getTheme(themeName);
        }
        return theme;
    }

    public ThemeCache getThemeCache() {
        return themeCache;
    }

    public void setThemeCache(ThemeCache themeCache) {
        this.themeCache = themeCache;
    }

    public ThemeSource getParentThemeSource() {
        return parentThemeSource;
    }

    public void setParentThemeSource(ThemeSource parentThemeSource) {
        this.parentThemeSource = parentThemeSource;
    }
}