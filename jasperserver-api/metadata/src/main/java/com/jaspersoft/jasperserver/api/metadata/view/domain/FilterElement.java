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
package com.jaspersoft.jasperserver.api.metadata.view.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;


/**
 * Filter criterion interface.
 * 
 * @author Sherman Wood
 * @author Lucian Chirita
 * @version $Id$
 * @since 1.0
 * @see FilterElementCollection
 */
@JasperServerAPI
public interface FilterElement {

	/**
	 * Applies this criterion in a filter implementation.
	 * 
	 * @param filter the filter implementation
	 * @see Filter
	 */
	void apply(Filter filter);
	
	/**
	 * Creates a clone of the filter criterion.
	 * 
	 * @return a clone of this filter
	 * @since 3.5.0
	 */
	FilterElement cloneFilterElement();
	
}
