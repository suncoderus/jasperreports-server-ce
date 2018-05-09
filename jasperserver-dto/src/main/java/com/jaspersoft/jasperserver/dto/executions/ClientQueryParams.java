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
package com.jaspersoft.jasperserver.dto.executions;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 20.01.2016
 */
public class ClientQueryParams {
    @NotNull
    private int[] offset;
    @NotNull
    private int[] pageSize;

    public ClientQueryParams() {
    }

    public ClientQueryParams(ClientQueryParams queryParams) {
        if (queryParams.getOffset() != null) {
            offset = Arrays.copyOf(queryParams.getOffset(), queryParams.getOffset().length);
        }
        if (queryParams.getPageSize() != null) {
            pageSize = Arrays.copyOf(queryParams.getPageSize(), queryParams.getPageSize().length);
        }
    }

    public int[] getOffset() {
        return offset;
    }

    public ClientQueryParams setOffset(int[] offset) {
        this.offset = offset;
        return this;
    }

    public int[] getPageSize() {
        return pageSize;
    }

    public ClientQueryParams setPageSize(int[] pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientQueryParams that = (ClientQueryParams) o;

        if (!Arrays.equals(offset, that.offset)) return false;
        return Arrays.equals(pageSize, that.pageSize);

    }

    @Override
    public int hashCode() {
        int result = offset != null ? Arrays.hashCode(offset) : 0;
        result = 31 * result + (pageSize != null ? Arrays.hashCode(pageSize) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientQueryParams{" +
                "offset=" + Arrays.toString(offset) +
                ", pageSize=" + Arrays.toString(pageSize) +
                '}';
    }
}
