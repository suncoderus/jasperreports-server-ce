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

package com.jaspersoft.jasperserver.search.mode;

import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.state.InitialStateResolver;
import com.jaspersoft.jasperserver.search.state.impl.StateImpl;
import com.jaspersoft.jasperserver.search.util.JSONConverter;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p>Tests for {@link SearchModeSettings}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeSettingsTest extends UnitilsJUnit4 {
    @Test
    public void testGettersAndSetters() {
        SearchModeSettings searchModeSettings = new SearchModeSettings();

        assertNull(searchModeSettings.getJsonConverter());
        assertNull(searchModeSettings.getInitialStateResolver());
        assertNull(searchModeSettings.getRepositorySearchConfiguration());

        JSONConverter actualJsonConverter = new JSONConverter();
        InitialStateResolver actualInitialStateResolver = new StateImpl();
        RepositorySearchConfiguration actualRepositorySearchConfiguration = new RepositorySearchConfiguration();

        searchModeSettings.setJsonConverter(actualJsonConverter);
        searchModeSettings.setInitialStateResolver(actualInitialStateResolver);
        searchModeSettings.setRepositorySearchConfiguration(actualRepositorySearchConfiguration);

        assertEquals(actualJsonConverter, searchModeSettings.getJsonConverter());
        assertEquals(actualInitialStateResolver, searchModeSettings.getInitialStateResolver());
        assertEquals(actualRepositorySearchConfiguration, searchModeSettings.getRepositorySearchConfiguration());
    }
}
