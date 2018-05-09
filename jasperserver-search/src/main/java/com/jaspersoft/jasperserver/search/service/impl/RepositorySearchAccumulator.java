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
package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.PaginationConstants;
import com.jaspersoft.jasperserver.search.service.ItemProcessor;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchResult;

import java.util.*;

/**
 * Created by stas on 4/9/14.
 *
 * This abstraction represent repository search result.
 * It was designed to support case when resources list after search query is filtered (removed for result) in memory (by ACL security).
 * Because client expect to get some amount of resources we have to do more repo queries to fill resources list.
 *
 * This class keeps track of the offset and limit conditions for current result set.
 * Also it provides methods to check are search result is complete or methods to identify offset for next query to fill missing resources.
 *
 * Note: Special case when client specify 0 limit  we expect to have full result set in first fill call
 */
public class RepositorySearchAccumulator<T> implements RepositorySearchResult<T> {
    List<T> items;
    private int clientOffset = -1;
    private int clientLimit;
    private int offset;
    private int nextOffset;
    private int nextLimit;
    private int totalCount;
    private boolean full;

    public static final com.jaspersoft.jasperserver.search.service.RepositorySearchResult EMPTY_RESULT =
            new RepositorySearchAccumulator(0, 0, 0).fill(0, 0, Collections.EMPTY_LIST);

    /**
     * This constructor used to define client spec on result set and keep information about totals (to avoid extra queries)
     *
     * @param limit expected amount of resources in result set.
     *              Special case when limit is 0, we expect to have full result set in first fill call
     * @param totalCount total amount of resources in database, with security ignored
     */
    public RepositorySearchAccumulator(final int offset, final int limit, final int totalCount) {
        if (offset < 0) { throw negativeNumberException(PaginationConstants.PARAM_OFFSET); }
        if (limit < 0) { throw negativeNumberException(PaginationConstants.PARAM_LIMIT); }
        if (totalCount < 0) { throw negativeNumberException("totalCount"); }

        this.clientOffset = offset;
        this.clientLimit = limit;
        this.nextLimit = limit;
        this.totalCount = totalCount;
        this.items = new ArrayList<T>(limit < 100000 ? limit : 100000);
    }

    /**
     * Append current result page to the result set
     *
     * @param criteria
     * @param list
     * @return
     */
    public RepositorySearchResult fill(final RepositorySearchCriteria criteria,
                                                     final List<T> list) {

        return fill(criteria.getStartIndex(), criteria.getMaxCount(), list);
    }

    /**
     * Append current result page to the result set
     *
     * @param offset from the start of all result set
     * @param limit
     * @param list
     * @return
     */
    public RepositorySearchResult fill(final int offset, final int limit, final List<T> list) {

        if (offset < 0) { throw negativeNumberException(PaginationConstants.PARAM_OFFSET); }
        if (offset < this.clientOffset) { throw new IllegalArgumentException("offset can't be less than client expects."); }
//        if (offset > this.totalCount) { throw new IllegalArgumentException("offset can't bbe bigger than total count of resources."); }

        if (limit < 0) { throw negativeNumberException(PaginationConstants.PARAM_LIMIT); }
        if (limit != 0 && this.clientLimit == 0) { throw new IllegalArgumentException("Expected to be called once with 0 limit and full result set."); }

        if (list == null) { throw new IllegalArgumentException("items list can't be null"); }

        if (limit > 0 && list.size() > limit) { throw new IllegalArgumentException("items list can't contain more items then specified in the limit."); }

        if (isFull()) { throw new JSException("Search result is already full."); }

        this.items.addAll(list);
        this.offset = offset;
        this.nextOffset = offset + limit;
//        this.lastFillMissing = limit - list.size();
//        this.nextLimit = (this.clientLimit > 0) ? calculateNextLimit(list.size()) : this.clientLimit;
        this.full = (this.clientLimit == 0 && limit == 0) || (size() == this.clientLimit) || size() == this.totalCount;

        return this;
    }

    protected void setNextLimit(int limit) {
        this.nextLimit = limit;
    }

    /**
     * Used to check is client result spec is fulfilled or all available items was retrieved from DB
     *
     * @return
     */
    @Override
    public boolean isFull() {
        return this.full;
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public int getClientLimit() {
        return clientLimit;
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public int getClientOffset() {
        return this.clientOffset;
    }

    @Override
    public int getNextOffset() {
        return this.nextOffset;
    }

    @Override
    public int getNextLimit() {
        return nextLimit;
    }

    @Override
    public List<T> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    @Override
    public <U> RepositorySearchResult<U> transform(ItemProcessor<T, U> transformer) {
        RepositorySearchAccumulator<U> result =
                new RepositorySearchAccumulator<U>(this.clientOffset, this.clientLimit, this.totalCount);

        result.nextOffset = this.nextOffset;
        result.nextLimit = this.nextLimit;
        result.offset = this.offset;
        result.full = this.full;

        for (T lookup : this.items) {
            result.items.add(transformer.call(lookup));
        }

        return result;
    }

    private RuntimeException negativeNumberException(final String attrName) {
        return new IllegalArgumentException(attrName + " can't be negative number.");
    }

	@Override
	public void append(RepositorySearchResult<T> r) {
		this.totalCount += r.getTotalCount();
		this.items.addAll(r.getItems());
		
	}
}
