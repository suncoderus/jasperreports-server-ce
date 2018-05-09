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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class ListOfValuesImpl extends ResourceImpl implements ListOfValues
{
	private List values = new ArrayList();

	public ListOfValuesItem[] getValues()
	{
		return (ListOfValuesItem[]) values.toArray(new ListOfValuesItem[values.size()]);
	}

	public void addValue(ListOfValuesItem item)
	{
		values.add(item);
	}

	public void removeValue(ListOfValuesItem item)
	{
		values.remove(item);
	}

	protected Class getImplementingItf() {
		return ListOfValues.class;
	}
}
