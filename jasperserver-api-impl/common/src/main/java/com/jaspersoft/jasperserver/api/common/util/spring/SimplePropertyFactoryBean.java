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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import org.springframework.beans.factory.FactoryBean;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita
 *
 */
public class SimplePropertyFactoryBean implements FactoryBean{

	private Class objectType;
	private String value;
	
	public boolean isSingleton() {
		return true;
	}

	public Object getObject() {
		PropertyEditor editor = PropertyEditorManager.findEditor(objectType);
		if (editor == null) {
			throw new JSException("No property editor was found for class " + objectType.getName());
		}
		editor.setAsText(getValue());
		return editor.getValue();
	}

	public Class getObjectType() {
		return objectType;
	}

	public void setObjectType(Class objectType) {
		this.objectType = objectType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
