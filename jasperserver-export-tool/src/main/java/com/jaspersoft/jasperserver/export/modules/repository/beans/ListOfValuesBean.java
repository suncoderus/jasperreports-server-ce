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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;


/**
 * @author tkavanagh
 * @version $Id$
 */


public class ListOfValuesBean extends ResourceBean {


	private ListOfValuesItemBean[] items;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsFrom(lov);
	}

	protected void copyItemsFrom(ListOfValues lov) {
		ListOfValuesItem[] lovItems = lov.getValues();
		if (lovItems == null || lovItems.length == 0) {
			items = null;
		} else {
			items = new ListOfValuesItemBean[lovItems.length];
			for (int i = 0; i < lovItems.length; i++) {
				ListOfValuesItem lovItem = lovItems[i];
				Object value = lovItem.getValue();
				if (value instanceof String && PasswordCipherer.getInstance().isEncrypted((String) value)) {
					String rawValue = PasswordCipherer.getInstance().decryptSecureAttribute((String)value);
					lovItem.setValue(encryptSecureAttribute(rawValue));
				}

				ListOfValuesItemBean itemBean = new ListOfValuesItemBean(lovItem);
				items[i] = itemBean;
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsTo(lov);
	}

	protected void copyItemsTo(ListOfValues lov) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				ListOfValuesItemBean item = items[i];
				Object value = item.getValue();
				if (value != null && value instanceof String && isEncrypted((String) value)) {
					item.setValue(PasswordCipherer.getInstance().encryptSecureAttribute(decryptSecureAttribute((String) value)));
				}
				ListOfValuesItem lovItem = new ListOfValuesItemImpl();
				item.copyTo(lovItem);
				lov.addValue(lovItem);
			}
		}
	}

	public ListOfValuesItemBean[] getItems() {
		return items;
	}

	public void setItems(ListOfValuesItemBean[] values) {
		this.items = values;
	}

}
