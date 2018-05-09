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

package com.jaspersoft.jasperserver.api.metadata.user.domain.impl;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ObjectRecipientIdentity {

	private Class recipientClass;
	private long id;

	public ObjectRecipientIdentity() {
	}
	
	public ObjectRecipientIdentity(Class recipientClass, long id) {
		this.recipientClass = recipientClass;
		this.id = id;
	}
	
	public ObjectRecipientIdentity(IdedObject object) {
		this(object.getClass(), object.getId());
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("recipientClass", recipientClass)
			.append("id", id)
			.toString();
	}
	
	public Class getRecipientClass() {
		return recipientClass;
	}

	public void setRecipientClass(Class recipientClass) {
		this.recipientClass = recipientClass;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
