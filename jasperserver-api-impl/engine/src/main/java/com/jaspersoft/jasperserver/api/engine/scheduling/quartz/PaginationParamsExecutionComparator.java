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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.util.Comparator;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PaginationParamsExecutionComparator implements Comparator<PaginationParameters> {

	private static final PaginationParamsExecutionComparator INSTANCE = 
			new PaginationParamsExecutionComparator();
	
	public static PaginationParamsExecutionComparator instance() {
		return INSTANCE;
	}
	
	protected PaginationParamsExecutionComparator() {
	}

	@Override
	public int compare(PaginationParameters p1, PaginationParameters p2) {
		//default pagination is first, explicit pagination is second, unpaginated are last
		int pagination1 = p1.getPaginated() == null ? 0 : (p1.getPaginated() ? 1 : 2);
		int pagination2 = p2.getPaginated() == null ? 0 : (p2.getPaginated() ? 1 : 2);
		int order = Integer.compare(pagination1, pagination2);
		if (order == 0) {
			//same pagination, compare max width/height
			//higher max height/width is before lower
			//note that null max page height/width means +infinity
			int maxHeight1 = p1.getMaxPageHeight() == null ? Integer.MAX_VALUE : p1.getMaxPageHeight();
			int maxHeight2 = p2.getMaxPageHeight() == null ? Integer.MAX_VALUE : p2.getMaxPageHeight();
			order = Integer.compare(maxHeight2, maxHeight1);
			if (order == 0) {
				int maxWidth1 = p1.getMaxPageWidth() == null ? Integer.MAX_VALUE : p1.getMaxPageWidth();
				int maxWidth2 = p2.getMaxPageWidth() == null ? Integer.MAX_VALUE : p2.getMaxPageWidth();
				order = Integer.compare(maxWidth2, maxWidth1);
			}
		}
		return order;
	}

}
