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

package com.jaspersoft.jasperserver.search.mode.impl;

import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettings;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p>Tests for {@link SearchModeSettingsResolverImpl}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeSettingsResolverImplTest extends UnitilsJUnit4 {
    @Test
    public void getSettings() {
        SearchModeSettingsResolverImpl searchModeSettingsResolver = new SearchModeSettingsResolverImpl();

        Map<SearchMode, SearchModeSettings> settingsMap = new HashMap<SearchMode, SearchModeSettings>();

        SearchModeSettings actualSearchModeSettings = new SearchModeSettings();

        settingsMap.put(SearchMode.BROWSE, actualSearchModeSettings);

        searchModeSettingsResolver.setSettingsMap(settingsMap);

        assertEquals(actualSearchModeSettings, searchModeSettingsResolver.getSettings(SearchMode.BROWSE));
    }

    @Test
    public void getSettingsForEmptySettingsMap() {
        SearchModeSettingsResolverImpl searchModeSettingsResolver = new SearchModeSettingsResolverImpl();

        Map<SearchMode, SearchModeSettings> settingsMap = new HashMap<SearchMode, SearchModeSettings>();
        searchModeSettingsResolver.setSettingsMap(settingsMap);

        assertNull(searchModeSettingsResolver.getSettings(SearchMode.BROWSE));
    }
}
