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

package com.jaspersoft.jasperserver.war.action.reportManager;

import java.util.List;
import java.io.Serializable;

/**
 * @author achan
 *
 */
public class ResourceListModel implements Serializable {
	
	private List rows;
	private int size = 0;
	private int currentPageNumber;
	private int totalPages;
	private boolean first;
	private boolean previous;
	private boolean next;
	private boolean last;
	private String parentUri = null;

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public boolean isPrevious() {
		return previous;
	}

	public void setPrevious(boolean previous) {
		this.previous = previous;
	}

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public String getParentUri() {
		return parentUri;
	}

	public void setParentUri(String parentUri) {
		this.parentUri = parentUri;
	}
	
}
