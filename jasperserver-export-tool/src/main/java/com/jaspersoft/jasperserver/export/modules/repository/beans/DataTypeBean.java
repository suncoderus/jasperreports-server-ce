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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataTypeValueClassResolver;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import org.exolab.castor.types.AnyNode;
import org.exolab.castor.types.DateTime;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author tkavanagh
 * @version $Id$
 */

public class DataTypeBean extends ResourceBean {

	private byte type;
	private Integer maxLength;
	private Integer decimals;
	private String regularExpr;
	private Object minValue;
	private Object maxValue;
	private boolean strictMin;
	private boolean strictMax;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		DataType dt = (DataType) res;
		setType(dt.getDataTypeType());
		setMaxLength(dt.getMaxLength());
		setDecimals(dt.getDecimals());
		setRegularExpr(dt.getRegularExpr());
		setMinValue(dt.getMinValue());
		setMaxValue(dt.getMaxValue());
		setStrictMin(dt.isStrictMin());
		setStrictMax(dt.isStrictMax());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		DataType dt = (DataType) res;
		dt.setDataTypeType(getType());
		dt.setMaxLength(getMaxLength());
		dt.setDecimals(getDecimals());
		dt.setRegularExpr(getRegularExpr());
		dt.setMinValue(fixCastorAnyNode(getMinValue(), dt));
		dt.setMaxValue(fixCastorAnyNode(getMaxValue(), dt));
		dt.setStrictMin(isStrictMin());
		dt.setStrictMax(isStrictMax());
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Object getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}

	public Object getMinValue() {
		return minValue;
	}

	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}

	public String getRegularExpr() {
		return regularExpr;
	}

	public void setRegularExpr(String regularExpr) {
		this.regularExpr = regularExpr;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public boolean isStrictMax() {
		return strictMax;
	}

	public void setStrictMax(boolean strictMax) {
		this.strictMax = strictMax;
	}

	public boolean isStrictMin() {
		return strictMin;
	}

	public void setStrictMin(boolean strictMin) {
		this.strictMin = strictMin;
	}


    /*
     *  See http://bugzilla.jaspersoft.com/show_bug.cgi?id=35083 and
     *      http://jira.codehaus.org/browse/CASTOR-1887
     */
    private Comparable fixCastorAnyNode(Object value, DataType dt) {
        if (value instanceof AnyNode) {
            final String stringValue = ((AnyNode) value).getStringValue();
            final Class<?> type = DataTypeValueClassResolver.getValueClass(dt);

            if (Time.class.equals(type)) {
                value = convertSqlTime(stringValue);
            } else if (Timestamp.class.equals(type)) {
                value = convertSqlTimestamp(stringValue);
            } else {
                throw new IllegalStateException("Cannot fix Castor " + (type != null ?  "for class" + type.getSimpleName() : "for type "+dt.getDataTypeType()));
            }
        }
        return (Comparable) value;
    }

    /**
     *  Extracted from SQLTimeFieldHandler
     */
    private Comparable convertSqlTime(String value){
        if (value == null) {
            return null;
        }

        String str = value.toString();

        Time time = null;
        // if ':' exists at index 2, then we probably have a valid time format: HH:MM:SS
        if (str.indexOf(':') == 2) {
            time = Time.valueOf(str);
        } else {
            //-- Try a full date YYYY-MM-DDTHH:MM:SS
            try {
                Date date = new DateTime(str.trim()).toDate();
                time = new Time(date.getTime());
            } catch (java.text.ParseException px) {
                throw new IllegalStateException(px.getMessage());
            }
        }
        return time;
    }

    /**
     *  Extracted from SQLTimestampFieldHandler
     */
    private Comparable convertSqlTimestamp(String value){
        Timestamp timestamp = null;

        // XML Schema compatibility: If 'T' exists at the correct spot,
        // then we most likely have an XML Schema dateTime format.
        String str = value.toString();
        if (str.indexOf('T') == 10) {
            try {
                Date date = new DateTime(str.trim()).toDate();
                timestamp = new Timestamp(date.getTime());
            } catch (java.text.ParseException px) {
                throw new IllegalStateException(px.getMessage());
            }
        } else {
            timestamp = Timestamp.valueOf(str);
        }
        return timestamp;
    }
}
