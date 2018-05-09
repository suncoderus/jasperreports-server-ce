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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoQuery.java 2343 2006-03-10 14:54:32Z lucian $
 * 
 * @hibernate.joined-subclass table="ListOfValues"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoListOfValues extends RepoResource
{

	/**
	 * 
	 */
	private List values = null;


	/**
	 * @hibernate.list table="ListOfValuesItem"
	 * @hibernate.key column="id"
	 * @hibernate.composite-element class="com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoListOfValuesItem"
	 * @hibernate.list-index
	 */
	public List getValues() 
	{
		return values;
	}

	/**
	 * 
	 */
	public void setValues(List values) 
	{
		this.values = values;
	}

	protected Class getClientItf() {
		return ListOfValues.class;
	}

	/**
	 * 
	 */
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) 
	{
		super.copyFrom(clientRes, referenceResolver);
		
		ListOfValues listOfValues = (ListOfValues) clientRes;
		List repoValues = new ArrayList();
		if (listOfValues != null) 
		{
			ListOfValuesItem[] items = listOfValues.getValues();
			if (items != null && items.length > 0) 
			{
				for (int i = 0; i < items.length; i++) 
				{
					ListOfValuesItem item = items[i];
					RepoListOfValuesItem value = new RepoListOfValuesItem();
					value.setLabel(item.getLabel());
					value.setValue(item.getValue());
					repoValues.add(value);
				}
			}
		}
		setValues(repoValues);	
	}

	/**
	 * 
	 */
	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		ListOfValues listOfValues = (ListOfValues) clientRes;
		for (Iterator it = getValues().iterator(); it.hasNext();) 
		{
			RepoListOfValuesItem value = (RepoListOfValuesItem)it.next();
			ListOfValuesItem item = new ListOfValuesItemImpl();
			item.setLabel(value.getLabel());
			item.setValue(value.getValue());
			listOfValues.addValue(item);
		}
	}

}
