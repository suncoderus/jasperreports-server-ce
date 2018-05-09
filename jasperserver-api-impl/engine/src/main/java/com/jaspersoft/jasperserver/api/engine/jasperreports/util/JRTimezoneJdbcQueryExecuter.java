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
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import org.quartz.TriggerUtils;
import org.quartz.DateBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.jaspersoft.jasperserver.api.security.validators.Validator.validateSQL;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class JRTimezoneJdbcQueryExecuter extends JRJdbcQueryExecuter {

	protected static class InheritableValue {
		private final Object value;
		private final boolean inherited;

		public InheritableValue(Object value, boolean inherited) {
			this.value = value;
			this.inherited = inherited;
		}

		public Object getValue() {
			return value;
		}

		public boolean isInherited() {
			return inherited;
		}
	}

	protected static class InheritableFlaggedThreadLocal extends InheritableThreadLocal {
		protected Object childValue(Object parentValue) {
			InheritableValue child;
			if (parentValue == null) {
				child = null;
			} else {
				InheritableValue parent = (InheritableValue) parentValue;
				child = new InheritableValue(parent.getValue(), true);
			}
			return child;
		}

		public Object get() {
			InheritableValue value = (InheritableValue) super.get();
			return value == null ? null : value.getValue();
		}

		public void set(Object value) {
			InheritableValue inheritableValue = new InheritableValue(value, false);
			super.set(inheritableValue);
		}

		public boolean isInherited() {
			InheritableValue value = (InheritableValue) super.get();
			return value == null ? false : value.isInherited();
		}
	}

	protected static class TimezoneAdjustInfo {
		protected final TimeZone timezone;
		protected final Set adjustedDates;

		public TimezoneAdjustInfo(TimeZone timezone) {
			this.timezone = timezone;
			this.adjustedDates = new HashSet();
		}
	}

	protected static class IdentityObjectWrapper {
		protected final Object object;

		public IdentityObjectWrapper(Object object) {
			this.object = object;
		}

		public int hashCode() {
			return System.identityHashCode(object);
		}

		public boolean equals(Object o) {
			if (!(o instanceof IdentityObjectWrapper)) {
				return false;
			}

			return object == ((IdentityObjectWrapper) o).object;
		}
	}

	private static InheritableFlaggedThreadLocal parentTimezone = new InheritableFlaggedThreadLocal();

	private final TimezoneAdjustInfo timezoneAdjust;
	private final boolean timezoneSet;

	public JRTimezoneJdbcQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map map) {
		super(jasperReportsContext, dataset, map);

		TimezoneAdjustInfo parentTimezoneAdjust = parentTimezone.isInherited()
				? (TimezoneAdjustInfo) parentTimezone.get()
				: null;
		TimeZone timezoneParam = (TimeZone) getValueParameter(
				JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE).getValue();
		if (parentTimezoneAdjust != null &&
				(timezoneParam == null || (parentTimezoneAdjust.timezone != null
						&& parentTimezoneAdjust.timezone.equals(timezoneParam)))) {
			timezoneSet = false;
			timezoneAdjust = parentTimezoneAdjust;
		} else {
			timezoneSet = true;
			timezoneAdjust = new TimezoneAdjustInfo(timezoneParam);
			parentTimezone.set(timezoneAdjust);
		}
	}


	protected JRValueParameter getValueParameter(String parameterName) {
		JRValueParameter param = super.getValueParameter(parameterName);

		Object value = param.getValue();
		if (value instanceof Date
				&& timezoneAdjust != null
				&& timezoneAdjust.timezone != null
				&& timezoneAdjust.adjustedDates.add(new IdentityObjectWrapper(value))) {
			Date initialDate = (Date) value;
			Date date = DateBuilder.translateTime(initialDate,
					timezoneAdjust.timezone, TimeZone.getDefault());
            //Date date = TriggerUtils.translateTime(initialDate,
			//		timezoneAdjust.timezone, TimeZone.getTimeZone("GMT"));
			initialDate.setTime(date.getTime());
		}
		return param;
	}

	public JRDataSource createDatasource() throws JRException {
        /* Checking that query is valid, throw a runtime exception */
        validateSQL(getQueryString());
		JRDataSource dataSource = super.createDatasource();
		return new JRTimezoneResultSetDataSource(dataSource,
				timezoneAdjust.timezone);
	}

	public synchronized void close() {
		if (timezoneSet) {
			parentTimezone.remove();
		}

		super.close();
	}

}
