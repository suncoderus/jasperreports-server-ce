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

import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PermissionRecipient extends TenantQualifiedName {
	
	private String recipientType;

	public PermissionRecipient() {
		super();
	}

	public PermissionRecipient(String recipientType, String tenantId, String name) {
		super(tenantId, name);
		
		this.recipientType = recipientType;
	}

	public String getRecipientType() {
		return recipientType;
	}
	
	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PermissionRecipient)) return false;
		if (!super.equals(o)) return false;

		PermissionRecipient that = (PermissionRecipient) o;

		if (!recipientType.equals(that.recipientType)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + recipientType.hashCode();
		return result;
	}
}
