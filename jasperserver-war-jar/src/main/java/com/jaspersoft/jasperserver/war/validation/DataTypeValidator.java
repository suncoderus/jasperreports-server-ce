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
package com.jaspersoft.jasperserver.war.validation;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.DataTypeWrapper;
import com.jaspersoft.jasperserver.war.util.MessagesCalendarFormatProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class DataTypeValidator implements Validator
{
	private RepositoryService repository;
	private MessagesCalendarFormatProvider calendarFormatProvider;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class klass)
	{
		return DataTypeWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{
		DataTypeWrapper wrapper = (DataTypeWrapper) object;
		DataType dataType = wrapper.getDataType();
		if (dataType.getName() == null || dataType.getName().trim().length() == 0) {
			errors.rejectValue("dataType.name", "DataTypeValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(dataType.getName())) {
				errors.rejectValue("dataType.name", "DataTypeValidator.error.invalid.chars");
			}
			if (dataType.getName().length() > 100) {
				errors.rejectValue("dataType.name", "DataTypeValidator.error.too.long");
			}

			if (wrapper.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, dataType.getURIString())) {
					errors.rejectValue("dataType.name", "DataTypeValidator.error.duplicate");
				}
			}
		}

		if (dataType.getLabel() == null || dataType.getLabel().trim().length() == 0) {
			errors.rejectValue("dataType.label", "DataTypeValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(dataType.getLabel())) {
				errors.rejectValue("dataType.label", "DataTypeValidator.error.invalid.chars");
			}
			if (dataType.getLabel().length() > 100) {
				errors.rejectValue("dataType.label", "DataTypeValidator.error.too.long");
			}
		}

		if (dataType.getDescription() != null && dataType.getDescription().length() > 250) {
			errors.rejectValue("dataType.description", "DataTypeValidator.error.too.long");
		}
		String strMinValue = null;
		String strMaxValue = null;

		if(dataType.getDataTypeType() == DataType.TYPE_DATE || dataType.getDataTypeType() == DataType.TYPE_DATE_TIME
                || dataType.getDataTypeType() == DataType.TYPE_TIME)
		{
			strMinValue = wrapper.getMinValueText();
			strMaxValue = wrapper.getMaxValueText();
		}
		else
		{
			strMinValue = (String) dataType.getMinValue();
			strMaxValue = (String) dataType.getMaxValue();
		}

		if (strMinValue != null && strMinValue.length() == 0)
			dataType.setMinValue(null);
		if (strMaxValue != null && strMaxValue.length() == 0)
			dataType.setMaxValue(null);

		if (strMinValue != null && strMinValue.length() > 100) {
			errors.rejectValue("dataType.minValue", "DataTypeValidator.error.too.long");
		}
		if (strMaxValue != null && strMaxValue.length() > 100) {
			errors.rejectValue("dataType.maxValue", "DataTypeValidator.error.too.long");
		}
		if (dataType.getRegularExpr() != null && dataType.getRegularExpr().length() > 100) {
//			errors.rejectValue("dataType.regularExpr", null, "Pattern is longer than 100 characters");
			errors.rejectValue("dataType.regularExpr", "DataTypeValidator.error.too.long");
		}

		if (dataType.getDataTypeType() == DataType.TYPE_NUMBER) {
			BigDecimal minValue = null;
			BigDecimal maxValue = null;
			try {

				minValue = new BigDecimal(strMinValue);
			} catch(NumberFormatException e) {
				if (strMinValue.length() > 0)
					errors.rejectValue("dataType.minValue", "DataTypeValidator.error.invalid.number");
			}

			try {

				maxValue = new BigDecimal(strMaxValue);
			} catch(NumberFormatException e) {
				if (strMaxValue.length() > 0)
					errors.rejectValue("dataType.maxValue", "DataTypeValidator.error.invalid.number");
			}

			if (minValue != null && maxValue != null)
				if (minValue.compareTo(maxValue) >= 0)
					errors.rejectValue("dataType.minValue", "DataTypeValidator.error.larger.than.max");
		}
		
		DateFormat df = null;
		if (dataType.getDataTypeType() == DataType.TYPE_DATE) {
			df = calendarFormatProvider.getDateFormat();
			validateDateTime(df, strMinValue, strMaxValue, "DataTypeValidator.error.invalid.date.format", errors);
		}
		if (dataType.getDataTypeType() == DataType.TYPE_DATE_TIME) {
			df = calendarFormatProvider.getDatetimeFormat();
			validateDateTime(df, strMinValue, strMaxValue, "DataTypeValidator.error.invalid.date.time.format", errors);
		}
		
		if (dataType.getDataTypeType() == DataType.TYPE_TIME) {
			df = calendarFormatProvider.getTimeFormat();
			validateDateTime(df, strMinValue, strMaxValue, "DataTypeValidator.error.invalid.time.format", errors);
		}

		if (dataType.getDataTypeType() == DataType.TYPE_TEXT && dataType.getRegularExpr() != null && dataType.getRegularExpr().trim().length() > 0) {
			if (
				strMinValue != null
				&& strMinValue.trim().length() > 0
				&& !Pattern.matches(dataType.getRegularExpr(), strMinValue)
				)
			{
				errors.rejectValue("dataType.minValue", "DataTypeValidator.error.pattern");
			}
			if (
				strMaxValue != null
				&& strMaxValue.trim().length() > 0
				&& !Pattern.matches(dataType.getRegularExpr(), strMaxValue)
				)
			{
				errors.rejectValue("dataType.maxValue", "DataTypeValidator.error.pattern");
			}
		}
	}

	private void validateDateTime( DateFormat df, String strMinValue, String strMaxValue, String invalidFormatMessage, Errors errors){
		
		Date minValue = null;
		Date maxValue = null;
		df.setLenient(false);
		try {

			minValue = df.parse(strMinValue);
		} catch(ParseException e) {
			if (strMinValue != null && strMinValue.length() > 0)
				errors.rejectValue("minValueText", invalidFormatMessage);
		}

		try {

			maxValue = df.parse(strMaxValue);
		} catch(ParseException e) {
			if (strMaxValue != null && strMaxValue.length() > 0)
				errors.rejectValue("maxValueText", invalidFormatMessage);
		}

		if (minValue != null && maxValue != null)
			if (minValue.compareTo(maxValue) >= 0)
				errors.rejectValue("minValueText", "DataTypeValidator.error.larger.than.max");
	}
	
	/**
	 * @return Returns the calendarFormatProvider.
	 */
	public MessagesCalendarFormatProvider getCalendarFormatProvider() {
		return calendarFormatProvider;
	}

	/**
	 * @param calendarFormatProvider The calendarFormatProvider to set.
	 */
	public void setCalendarFormatProvider(
			MessagesCalendarFormatProvider calendarFormatProvider) {
		this.calendarFormatProvider = calendarFormatProvider;
	}
}
