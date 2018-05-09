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

package com.jaspersoft.jasperserver.search.action;


import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.state.State;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The instance of this class will be stored in the user session. It keeps all the data related to search functionality
 * which should be stored in the user session.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchHolder implements Serializable {
    private Map<SearchMode, State> states = new HashMap<SearchMode, State>(SearchMode.values().length);

    private SearchMode lastMode = SearchMode.BROWSE; // Default last mode.

    public State getState(SearchMode mode) {
        return states.get(mode);
    }

    public void putState(SearchMode mode, State state) {
        this.states.put(mode, state);
    }

    public SearchMode getLastMode() {
        return lastMode;
    }

    public void setLastMode(SearchMode lastMode) {
        this.lastMode = lastMode;
    }
}
