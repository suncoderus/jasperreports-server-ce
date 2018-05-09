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
package com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl;

import java.io.Serializable;
import java.util.Map;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PaginationParameters implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	private Boolean paginated;
	private Integer maxPageHeight;
	private Integer maxPageWidth;
	
	public PaginationParameters() {
	}
	
	public PaginationParameters(PaginationParameters params) {
		if (params != null) {
			paginated = params.getPaginated();
			maxPageHeight = params.getMaxPageHeight();
			maxPageWidth = params.getMaxPageWidth();
		}
	}

	public Boolean getPaginated() {
		return paginated;
	}

	public void setPaginated(Boolean paginated) {
		this.paginated = paginated;
	}

	public Integer getMaxPageHeight() {
		return maxPageHeight;
	}

	public void setMaxPageHeight(Integer maxPageHeight) {
		this.maxPageHeight = maxPageHeight;
	}
	
	public Integer getMaxPageWidth() {
		return maxPageWidth;
	}

	public void setMaxPageWidth(Integer maxPageWidth) {
		this.maxPageWidth = maxPageWidth;
	}

	public boolean hasParameters() {
		return paginated != null || maxPageHeight != null || maxPageWidth != null;
	}

	public void setReportParameters(Map<String, Object> parameterValues) {
		if (paginated != null) {
			parameterValues.put(JRParameter.IS_IGNORE_PAGINATION, !paginated);
		}
		if (maxPageHeight != null) {
			parameterValues.put(JRParameter.MAX_PAGE_HEIGHT, maxPageHeight);
		}
		if (maxPageWidth != null) {
			parameterValues.put(JRParameter.MAX_PAGE_WIDTH, maxPageWidth);
		}
	}
	
	public void setDefaults(PaginationParameters paginationDefaults) {
		if (paginated == null) {
			paginated = paginationDefaults.getPaginated();
		}
		if (maxPageHeight == null) {
			maxPageHeight = paginationDefaults.getMaxPageHeight();
		}
		if (maxPageWidth == null) {
			maxPageWidth = paginationDefaults.getMaxPageWidth();
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = paginated == null ? 2017 : paginated.hashCode();
		hashCode = hashCode * 31 + (maxPageHeight == null ? 0 : maxPageHeight.hashCode());
		hashCode = hashCode * 31 + (maxPageWidth == null ? 0 : maxPageWidth.hashCode());
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PaginationParameters)) {
			return false;
		}
		
		PaginationParameters p = (PaginationParameters) obj;
		return ObjectUtils.equals(paginated, p.paginated)
				&& ObjectUtils.equals(maxPageHeight, p.maxPageHeight)
				&& ObjectUtils.equals(maxPageWidth, p.maxPageWidth);
	}

	@Override
	public String toString() {
		return "{paginated: " + paginated 
				+ ", maxPageHeight: " + maxPageHeight 
				+ ", maxPageWidth: " + maxPageWidth 
				+ "}";
	}
}
