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

import org.springframework.context.MessageSource;
import org.springframework.ui.context.Theme;

/**
 * @author asokolnikov
 */
public class HierarchicalThemeProxy implements HierarchicalTheme {

    private String qualifiedThemeName;
    private ThemeCache themeCache;

    public HierarchicalThemeProxy(String qualifiedThemeName, ThemeCache themeCache) {
        this.qualifiedThemeName = qualifiedThemeName;
        this.themeCache = themeCache;
    }

    protected HierarchicalTheme getDelegate() {
        return themeCache.getThemeByQualifiedName(qualifiedThemeName);
    }

    public Theme getParentTheme() {
        return getDelegate().getParentTheme();
    }

    public void setParentTheme(Theme theme) {
        getDelegate().setParentTheme(theme);
    }

    public String getName() {
        return getDelegate().getName();
    }

    public MessageSource getMessageSource() {
        return getDelegate().getMessageSource();
    }
}
