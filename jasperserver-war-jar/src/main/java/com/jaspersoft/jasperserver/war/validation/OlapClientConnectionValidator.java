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

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.InputControlWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapClientConnectionWrapper;

/**
 * 
 * OlapClientConnectionValidator provides validation methods for the
 * olapClientConnectionFlow
 *
 * @author jshih
 */
public class OlapClientConnectionValidator implements Validator {

    private RepositoryService repository;

	public void setRepository(RepositoryService repository){
		this.repository = repository;
	}

	public boolean supports(Class clazz) {
		return OlapClientConnectionWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object object, Errors errors) {
		OlapClientConnectionWrapper details = (OlapClientConnectionWrapper) object;
		validateNameLabelDesc(details, errors); // Step 1
		validateSchemaUpload(details, errors); // Step 2
	}

	public void validateNameLabelDesc(OlapClientConnectionWrapper wrapper, Errors errors) {
		if (wrapper.getConnectionLabel() == null || 
				wrapper.getConnectionLabel().trim().length() == 0) {
			errors.rejectValue("connectionLabel", "OlapClientConnectionValidator.error.not.empty");
		} else {
			if (wrapper.getConnectionLabel().length() > 100) {
				errors
						.rejectValue("connectionLabel", "OlapClientConnectionValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateLabel(wrapper.getConnectionLabel()))
				errors.rejectValue("connectionLabel", "OlapClientConnectionValidator.error.invalid.chars");
		}

		if (wrapper.getConnectionName() == null || 
				wrapper.getConnectionName().trim().length() == 0) {
			errors.rejectValue("connectionName", "OlapClientConnectionValidator.error.not.empty");
		} else {
			if (wrapper.getConnectionName().length() > 100) {
				errors.rejectValue("connectionName", "OlapClientConnectionValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateName(wrapper.getConnectionName()))
				errors.rejectValue("connectionName", "OlapClientConnectionValidator.error.invalid.chars");
			else {
				if (wrapper.isNewMode()
						&& wrapper.getExistingResources() != null) {
					List res = wrapper.getExistingResources();
					for (int i = 0; i < res.size(); i++) {
						String preExtName = (String) res.get(i);
						if (preExtName.equalsIgnoreCase(wrapper.getConnectionName().trim())) {
							errors.rejectValue("connectionName", "OlapClientConnectionValidator.error.duplicate");
							break;
						}
					}
				}
			}
		}

		if (wrapper.getConnectionDescription() != null && 
				wrapper.getConnectionDescription().length() > 300)
			errors.rejectValue("connectionDescription", "OlapClientConnectionValidator.error.too.long");
	}
	
	public void validateNameLabelDescAndXmlaSource(OlapClientConnectionWrapper wrapper, Errors errors) {
		validateNameLabelDesc(wrapper, errors);
		// catalog
		if (wrapper.getXmlaDatasource() == null || wrapper.getXmlaCatalog().trim().length() == 0) {
			errors.rejectValue("xmlaCatalog", "OlapClientConnectionValidator.error.not.empty");
		}
		// datasource
		if (wrapper.getXmlaDatasource() == null || wrapper.getXmlaDatasource().trim().length() == 0) {
			errors.rejectValue("xmlaDatasource", "OlapClientConnectionValidator.error.not.empty");
		}
		// uri
		if (wrapper.getXmlaConnectionUri() == null || 
				wrapper.getXmlaConnectionUri().trim().length() == 0) {
			errors.rejectValue("xmlaConnectionUri", "OlapClientConnectionValidator.error.not.empty");
		}
		/* USERNAME AND PASSWORD ARE NOW OPTIONAL, 
		   IF NOT PRESENT THEN LOGGED-IN USER's CREDENTIALS WILL BE TESTED FOR ACCESS
		// username
		if (wrapper.getUsername() == null || wrapper.getUsername().trim().length() == 0) {
			errors.rejectValue("username", "OlapClientConnectionValidator.error.not.empty");
		}
		// password
		if (wrapper.getPassword() == null || wrapper.getPassword().trim().length() == 0) {
			errors.rejectValue("password", "OlapClientConnectionValidator.error.not.empty");
		}
		*/
	}
	
	public void validateURIString(OlapClientConnectionWrapper olapUnit, Errors errors) {
		if (olapUnit.getSource() == null) {
			errors.rejectValue("source", "OlapClientConnectionValidator.error.invalid.schema");
		} else {
			if (olapUnit.getSource().equals(
					JasperServerConst.FIELD_CHOICE_CONT_REPO)) {
				if (olapUnit.getSchemaUri() == null
						|| olapUnit.getSchemaUri().length() == 0) {
					errors.rejectValue("schemaUri", "OlapClientConnectionValidator.error.not.reusable");
				}
			}
		}

	}

	public void validateSchemaUpload(OlapClientConnectionWrapper wrapper, Errors errors) {
		// TODO
	}

	public void validateResources(OlapClientConnectionWrapper wrapper, Errors errors) {
		boolean allResLocated = true;
		boolean allControlsLocated = true;
		List sugRes = wrapper.getSuggestedResources();
		if (sugRes != null && !sugRes.isEmpty()) {
			for (int i = 0; i < sugRes.size(); i++) {
				FileResourceWrapper resWrap = (FileResourceWrapper) sugRes
						.get(i);
				if (!resWrap.isLocated()) {
					allResLocated = false;
					break;
				}
			}
		}
		List sugContr = wrapper.getSuggestedControls();
		if (sugContr != null && !sugContr.isEmpty()) {
			for (int i = 0; i < sugContr.size(); i++) {
				InputControlWrapper icWrap = (InputControlWrapper) sugContr
						.get(i);
				if (!icWrap.isLocated()) {
					allControlsLocated = false;
					break;
				}
			}
		}
		if (!allControlsLocated && !allResLocated)
			errors.rejectValue("validationMessage", "OlapClientConnectionValidator.error.controls.resources.located");
		else if (!allControlsLocated)
			errors.rejectValue("validationMessage", "OlapClientConnectionValidator.error.controls.located");
		else if (!allResLocated)
			errors.rejectValue("validationMessage", "OlapClientConnectionValidator.error.resources.located");
	}

	public void validateConnectionType(OlapClientConnectionWrapper wrapper, Errors errors){
		if(wrapper.getType() == null)
			errors.rejectValue("source", "OlapClientConnectionValidator.error.no.connection.type");
	}
	
	public void validateConnectionSource(OlapClientConnectionWrapper wrapper, Errors errors){
		if(wrapper.getSource() == null)
			errors.rejectValue("source", "OlapClientConnectionValidator.error.no.connection.source");
	}

     public void validateResourceExists(OlapClientConnectionWrapper wrapper, Errors errors){
        if(wrapper.getSource() !=null && wrapper.getSource().equals(JasperServerConstImpl.getFieldChoiceRepo())) {
            if(wrapper.getConnectionUri() == null || wrapper.getConnectionUri().trim().length()==0 ||
                !repository.resourceExists(null, wrapper.getConnectionUri())) {
				errors.rejectValue("connectionUri", "OlapClientConnectionValidator.error.not.empty");
		    }
        }
	}


}
 