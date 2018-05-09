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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.ListOfValuesDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class ListOfValuesValidator implements Validator
{
	private RepositoryService repository;

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
		return ListOfValuesDTO.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{
	}

	public void validateResourceData(ListOfValuesDTO listOfValuesDTO, Errors errors)
	{
		ListOfValues listOfValues = listOfValuesDTO.getListOfValues();
		if (listOfValues.getName() == null || size(listOfValues.getName()) == 0) {
			errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.not.empty");
		} else {
            if(listOfValues.getName().length()> JasperServerConst.MAX_LENGTH_NAME){
                errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.too.long",
                                   new Object[]{JasperServerConst.MAX_LENGTH_NAME_W}, null);
            }else{
                if(!JasperServerUtil.regExValidateName(listOfValues.getName())) {
                    errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.invalid.chars");
                }/*else{
                    // When in stand alone new mode check name uniquenesss in folder
                    List allResources=listOfValuesDTO.getAllResources();
                    if(listOfValuesDTO.isAloneNewMode() && allResources!=null){
                        for(int i=0;i<allResources.size();i++){
                            Resource res=(Resource)allResources.get(i);
                            if(res.getName().equals(listOfValues.getName().trim())){
                                errors.rejectValue("listOfValues.name",
                                        null, "A Resource with this name already exists");
                                break;
                            }
                        }
                    }
                }*/
            }
			if (listOfValuesDTO.isAloneNewMode()) {
				if (repository.repositoryPathExists(null, listOfValues.getURIString())) {
					errors.rejectValue("listOfValues.name", "ListOfValuesValidator.error.duplicate");
				}
			}
		}

		if (listOfValues.getLabel() == null || size(listOfValues.getLabel()) == 0) {
			errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.not.empty");
		} else {
			if(listOfValues.getLabel().length()> JasperServerConst.MAX_LENGTH_LABEL){
				errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.too.long",
								   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
			}else{
				if(!JasperServerUtil.regExValidateLabel(listOfValues.getLabel())) {
					errors.rejectValue("listOfValues.label", "ListOfValuesValidator.error.invalid.chars");
				}
			}
		}

		if (listOfValues.getDescription() != null && 
				size(listOfValues.getDescription()) >JasperServerConst.MAX_LENGTH_DESC) {
			errors.rejectValue("listOfValues.description", "ListOfValuesValidator.error.too.long",
							   new Object[]{JasperServerConst.MAX_LENGTH_DESC_W}, null);
		}
	}

	public void validateAddValue(ListOfValuesDTO listOfValuesDTO, Errors errors){
		String newLabel=listOfValuesDTO.getNewLabel();
		if( newLabel == null || size(newLabel) == 0){
			errors.rejectValue("newLabel", "ListOfValuesValidator.error.not.empty");
		}else{
			if(newLabel.length()>JasperServerConst.MAX_LENGTH_VARCHAR){
				errors.rejectValue("newLabel", "ListOfValuesValidator.error.too.long",
								   new Object[]{JasperServerConst.MAX_LENGTH_VARCHAR_W}, null);
			}else{
				if(!JasperServerUtil.regExValidateLabel(newLabel)) {
					errors.rejectValue("newLabel", "ListOfValuesValidator.error.invalid.chars");
				}
			}						
		}
			
		if(listOfValuesDTO.getNewValue() == null || size(listOfValuesDTO.getNewValue()) == 0){
			errors.rejectValue("newValue", "ListOfValuesValidator.error.not.empty");
		}
			
	}
	
	public void validateLovSave(ListOfValuesDTO listOfValuesDTO, Errors errors){
        // no validation required for now
	}

	private int size(String text){
		return text.trim().length();
	}
}

