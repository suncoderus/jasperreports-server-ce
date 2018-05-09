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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.OlapDataSourceWrapper;

/**
 * 
 * OlapDataSourceValidator provides validation methods for the 
 * olapDataSourceFlow
 *
 * @author jshih
 */
public class OlapDataSourceValidator extends ReportDataSourceValidator implements Validator {
	private JasperServerConstImpl constants=new JasperServerConstImpl();
	public boolean supports(Class clazz) {
		return OlapDataSourceWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object arg0, Errors arg1) {
		// TODO Auto-generated method stub
	}

	public void chooseType(OlapDataSourceWrapper wrapper, Errors errors){
		if(wrapper.getSource()==null)
			errors.rejectValue("source", "OlapDataSourceValidator.error.not.empty");
	}

	public void jndiPropsForm(OlapDataSourceWrapper wrapper, Errors errors){
		// TODO
		JndiJdbcReportDataSource jndiSource=(JndiJdbcReportDataSource)wrapper.getOlapDataSource();
		if(jndiSource.getJndiName()==null || jndiSource.getJndiName().trim().length()==0) {
			errors.rejectValue("olapDataSource.jndiName", "OlapDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateJndiServiceName(jndiSource.getJndiName())) {
				errors.rejectValue(
						"olapDataSource.jndiName", "OlapDataSourceValidator.error.invalid.chars");
			}
		}
		namingForm(wrapper, errors);
	}

	public void namingForm(OlapDataSourceWrapper wrapper, Errors errors){
		// TODO OlapDataSource
		ReportDataSource ds=wrapper.getOlapDataSource();
		if(ds.getName()==null || ds.getName().trim().length()==0) {
			errors.rejectValue("olapDataSource.name", "OlapDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(ds.getName())) {
				errors.rejectValue("olapDataSource.name", "OlapDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getLabel()==null || ds.getLabel().trim().length()==0) {
			errors.rejectValue("olapDataSource.label", "OlapDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(ds.getLabel())) {
				errors.rejectValue("olapDataSource.label", "OlapDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getDescription()==null || ds.getDescription().trim().length()>300)
			errors.rejectValue("olapDataSource.description", "OlapDataSourceValidator.error.too.long");
	}

	public void jdbcPropsForm(OlapDataSourceWrapper wrapper, Errors errors){
		JdbcReportDataSource ds=(JdbcReportDataSource)wrapper.getOlapDataSource();
		if(ds.getDriverClass()==null || ds.getDriverClass().trim().length()==0) {
			errors.rejectValue("olapDataSource.driverClass", "OlapDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateDbDriver(ds.getDriverClass())) {
				errors.rejectValue(
						"olapDataSource.driverClass", "OlapDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getConnectionUrl()==null || ds.getConnectionUrl().trim().length()==0) {
			errors.rejectValue("olapDataSource.connectionUrl", "OlapDataSourceValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateJdbcURL(ds.getConnectionUrl())) {
				errors.rejectValue(
						"olapDataSource.connectionUrl", "OlapDataSourceValidator.error.invalid.chars");
			}
		}

		if(ds.getUsername()==null || ds.getUsername().trim().length()==0)
			errors.rejectValue("olapDataSource.username", "OlapDataSourceValidator.error.not.empty");

		if(ds.getPassword()==null || ds.getPassword().trim().length()==0)
			errors.rejectValue("olapDataSource.password", "OlapDataSourceValidator.error.not.empty");

		namingForm(wrapper, errors);
	}

	public void validateSource(OlapDataSourceWrapper wrapper, Errors errors){
		if(constants.getFieldChoiceRepo().equals(wrapper.getSource())
				&&( wrapper.getSelectedUri()==null || wrapper.getSelectedUri().trim().length()==0))
			errors.rejectValue("selectedUri", "OlapDataSourceValidator.error.not.empty");
	}
}
