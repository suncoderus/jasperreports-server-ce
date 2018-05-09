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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.quartz.DateBuilder;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRTimezoneResultSetDataSource implements JRDataSource
{
	private TimeZone timezone;
	private JRDataSource dataSource;


	public JRTimezoneResultSetDataSource(JRDataSource dataSource, TimeZone timezone)
	{
		this.dataSource = dataSource;
		this.timezone = timezone;
	}

	public boolean next() throws JRException
	{
		boolean hasNext = false;
		if (dataSource != null) {
			try {
				hasNext = dataSource.next();
			} catch (JRException e) {
				throw new JRException("Unable to get next record.", e);
			}
		}

		return hasNext;
	}

	public Object getFieldValue(JRField field) throws JRException
	{
		Object value = null;
		if (field != null && dataSource != null) {
            try {
    			value = dataSource.getFieldValue(field);
            } catch (JRException ex) {
                // try to pump up data type if it is overflow exception
                if ((ex.getMessage().indexOf("Unable to get value for field") > 0) && (ex.getMessage().indexOf("of class") > 0) && (field instanceof JRDesignField)) {
                    try {
                        String originalType = field.getValueClassName();
                        // pump up the data type
                        String newType = DataConverterFactory.getPumpUpType(originalType);
                        // if fail to pump up the data type, return original exception
                        if (newType == null) throw ex;
                        // assign the new data type to JRField and try to retrieve the data again
                        ((JRDesignField)field).setValueClassName(newType);
                        value = dataSource.getFieldValue(field);
                        ((JRDesignField)field).setValueClassName(originalType);
                        // convert the data back to original type.  It avoids crashing, but the data is still not correct.
                        // User needs to the the column type in order to view the correct data
                        DataConverterFactory.DataConverter dataConverter = (DataConverterFactory.DataConverter) DataConverterFactory.createConverter(originalType);

                        if (dataConverter != null) value = dataConverter.convert(value);
                        else throw ex;
                    } catch (Exception ex2) {
                        // if fail in conversion, throw the original exception
                        throw ex;
                    }
                }
            }
			if (value instanceof Date && timezone != null) {
				Date initialDate = (Date) value;
                Date date = DateBuilder.translateTime(initialDate, TimeZone.getDefault(), timezone);
				initialDate.setTime(date.getTime());
				return initialDate;
			}

			return value;
		}

		return value;
	}
}
