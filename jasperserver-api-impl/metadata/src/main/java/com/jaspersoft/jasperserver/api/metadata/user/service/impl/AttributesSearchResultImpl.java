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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;

import java.util.Collections;
import java.util.List;

/**
 * @author askorodumov
 * @version $Id$
 */
public class AttributesSearchResultImpl<T> implements AttributesSearchResult<T> {
    private List<T> list = Collections.emptyList();
    private int totalCount = 0;

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public void addTotalCount(int value) {
        this.totalCount += value;
    }
}
