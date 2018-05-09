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

package com.jaspersoft.jasperserver.war.action;

import java.io.Serializable;

/**
 * State class for entities list.
 *
 * @author Yuriy Plakosh
 */
public class EntitiesListState implements Serializable {
    private String text;
    private int resultIndex;
    private int resultsCount;

    public String getText() {
        return text;
    }

    public void updateText(String text) {
        this.text = text;

        updateResultState(0, 0);
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public void updateResultState(int resultIndex, int resultsCount) {
        this.resultIndex = resultIndex;
        this.resultsCount = resultsCount;
    }
}
