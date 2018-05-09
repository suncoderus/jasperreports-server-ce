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

package com.jaspersoft.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.DefaultNamingStrategy;

/**
 * Hibernate has a NamingStrategy interface which provides a hook for modifying Hibernate mappings.
 * This means that you can keep a single set of mappings but customize them at runtime (and DDL generation time).
 * In Spring, this can be passed to the session factory by setting the namingStrategy property of 
 * org.springframework.orm.hibernate3.LocalSessionFactoryBean.
 * 
 * This particular implementation takes a columnMap property, which contains column names that should be used 
 * instead of the ones specified by the "column" attributes in the Hibernate mapping file.
 * @author bob
 *
 */
public class HibernateColumnRenamingStrategy extends DefaultNamingStrategy {
	
	private Map<String, String> columnMap = new HashMap<String, String>();

	/**
	 * rename the actual table used to map a class from the one given in the "table" attribute
	 * Placeholder here in case this comes in handy later.
	 */
	@Override
	public String tableName(String tableName) {
		return super.tableName(tableName);
	}

	/**
	 * rename the actual field name used to map a property, instead of the one given in the "column" attribute
	 * look up the original column as the key in the columnMap
	 */
	@Override
	public String columnName(String columnName) {
		String newColumn = columnMap.get(columnName);
		if (newColumn != null) {
			return newColumn;
		}
		return super.columnName(columnName);
	}

	public void setColumnMap(Map<String, String> columnMap) {
		this.columnMap = columnMap;
	}

	public Map<String, String> getColumnMap() {
		return columnMap;
	}
}
