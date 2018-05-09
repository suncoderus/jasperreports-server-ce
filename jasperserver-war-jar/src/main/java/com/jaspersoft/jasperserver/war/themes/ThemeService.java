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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

import java.io.IOException;

/**
 * An interface for theme operations
 * @author asokolnikov
 */
public interface ThemeService {

    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";

    /**
     * Returns an active theme name for a given organization
     * @param executionContext
     * @param tenantId
     * @return
     */
    public String getActiveTheme(ExecutionContext executionContext, String tenantId);

    /**
     * Sets an active theme for a given organization
     * @param executionContext
     * @param tenantId
     * @param themeName
     */
    public void setActiveTheme(ExecutionContext executionContext, String tenantId, String themeName);

    /**
     * Returns true if a given folder is theme folder for the current or any child tenant
     * @param executionContext
     * @param folderUri
     * @return
     */
    public boolean isThemeFolder(ExecutionContext executionContext, String folderUri);

    /**
     * Returns true if a given folder is theme root folder for the current or any child tenant
     * @param executionContext
     * @param folderUri
     * @return
     */
    public boolean isThemeRootFolder(ExecutionContext executionContext, String folderUri);

    /**
     * Returns true if a given folder represents the currently active theme for a corresponding tenant
     * @param executionContext
     * @param folderUri
     * @return
     */
    public boolean isActiveThemeFolder(ExecutionContext executionContext, String folderUri);

    /**
     * Returns a zip file which contains theme files
     * @param executionContext
     * @param folderUri
     * @return
     * @throws IOException
     */
    public byte[] getZipedTheme(ExecutionContext executionContext, String folderUri) throws IOException;

    /**
     * Adds a new theme to a given folder
     * @param executionContext
     * @param folderUri folder to add a theme under, must be a tenant root theme folder ([tenant]/themes by default)
     * @param themeName a name for the new theme
     * @param themeZip a zip file that contains all theme files
     * @throws Exception
     */
    public void addZippedTheme(ExecutionContext executionContext, String folderUri, String themeName, byte[] themeZip) throws Exception;

    /**
     * Adds a new theme to a given folder
     * @param executionContext
     * @param folderUri folder to add a theme under, must be a tenant root theme folder ([tenant]/themes by default)
     * @param themeName a name for the new theme
     * @param themeZip a zip file that contains all theme files
     * @param overwrite mark for overwriting action
     * @throws Exception
     */
    public void addZippedTheme(ExecutionContext executionContext, String folderUri, String themeName, byte[] themeZip, boolean overwrite) throws Exception;

}
