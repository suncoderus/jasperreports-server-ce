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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.InputControlWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportUnitWrapper;

public class InputControlsFlowValidator implements Validator {

	private RepositoryService repository;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class clazz) {
		return InputControlWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object arg0, Errors arg1) {
		// TODO Auto-generated method stub
	}

	public void validateControlNaming(InputControlWrapper dto, Errors errors) {

		InputControl inputControl = dto.getInputControl();
		if(inputControl.getName()==null || inputControl.getName().trim().length()==0) {
			errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(inputControl.getName())) {
				errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.invalid.chars");
			}else{
				Object parentObject=dto.getParentFlowObject();
				if(dto.isSubNewMode() && parentObject!=null && 
						ReportUnitWrapper.class.isAssignableFrom(parentObject.getClass())){
					//Parent flow obj is a ReportUnitWrapper
					ReportUnitWrapper ruWrapper=(ReportUnitWrapper)parentObject;
					List resources=ruWrapper.getReportUnit().getResources();
					if(resources!=null && !resources.isEmpty()){
						for(int i=0;i<resources.size();i++){
							ResourceReference resourceRef = (ResourceReference) resources.get(i);
							if(resourceRef.isLocal() && resourceRef.getLocalResource().getName().equals(inputControl.getName()))
									errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.duplicate");
						}
					}
					List controls=ruWrapper.getReportUnit().getInputControls();
					if(controls!=null && !controls.isEmpty()){
						for(int i=0;i<controls.size();i++){
							ResourceReference controlRef = (ResourceReference) controls.get(i);
							if(controlRef.isLocal() && controlRef.getLocalResource().getName().equals(inputControl.getName()))
								errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.duplicateControl");
						}
					}
					ResourceReference dataSourceRef = ruWrapper.getReportUnit().getDataSource();
					if (dataSourceRef != null && dataSourceRef.isLocal()) {
						ReportDataSource ds = (ReportDataSource) dataSourceRef.getLocalResource();
						if (ds != null
							&& ds.getName().equals(inputControl.getName()))
							errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.assigned.to.dataSource");
					}
				}else{
					// When in stand alone new mode check name uniquenesss in folder
					if (dto.isAloneNewMode()) {
						if (repository.repositoryPathExists(null, inputControl.getURIString())) {
							errors.rejectValue("inputControl.name", "InputControlsFlowValidator.error.already.exists");
						}
					}
				}
				
			}
		}

		if(inputControl.getLabel()==null || inputControl.getLabel().trim().length()==0) {
			errors.rejectValue("inputControl.label", "InputControlsFlowValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(inputControl.getLabel())) {
				errors.rejectValue("inputControl.label", "InputControlsFlowValidator.error.invalid.chars");
			}
		}

		if(inputControl.getDescription()==null || inputControl.getDescription().trim().length() > 250)
			errors.rejectValue("inputControl.description", "InputControlsFlowValidator.error.too.long");

	}

	public void dataTypeDetails(InputControlWrapper dto, Errors errors){
		ResourceReference dataTypeRef = dto.getInputControl().getDataType();
		if (!dataTypeRef.isLocal()) {
			return;
		}
		DataType dataType = (DataType) dataTypeRef.getLocalResource();
		if(dto.getSource()==null || dto.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())){
			if(dataType.getName()==null || dataType.getName().trim().length()==0) {
				errors.rejectValue("inputControl.dataType.name", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateName(dataType.getName())) {
					errors.rejectValue("inputControl.dataType.name", "InputControlsFlowValidator.error.invalid.chars");
				}
			}

			if(dataType.getLabel()==null || dataType.getLabel().trim().length()==0) {
				errors.rejectValue("inputControl.dataType.label", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateLabel(dataType.getLabel())) {
					errors.rejectValue("inputControl.dataType.label", "InputControlsFlowValidator.error.invalid.chars");
				}
			}

			if (dto.getDtMaxLength() != null && dto.getDtMaxLength().length() > 0)
				try {
					new Integer(dto.getDtMaxLength());
				} catch(NumberFormatException e) {
					errors.rejectValue("dtMaxLength", "InputControlsFlowValidator.error.integer");
				}

			if (dto.getDtDecimals() != null && dto.getDtDecimals().length() > 0)
				try {
					new Integer(dto.getDtDecimals());
				} catch(NumberFormatException e) {
					errors.rejectValue("dtDecimals", "InputControlsFlowValidator.error.integer");
				}
		} else {
			if(dto.getExistingPath() == null || dto.getExistingPath().trim().length()==0) {
				errors.rejectValue("existingPath", "InputControlsFlowValidator.error.no.data.type");
			}
		}
	}
	public void queryDetails(InputControlWrapper dto, Errors errors){
		ResourceReference queryRef = dto.getInputControl().getQuery();
		if (!queryRef.isLocal()) {
			return;
		}
		Query query = (Query) queryRef.getLocalResource();
		if(dto.getSource()==null || dto.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())){

			if(query.getName()==null || query.getName().trim().length()==0) {
				errors.rejectValue("inputControl.query.name", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateName(query.getName())) {
					errors.rejectValue("inputControl.query.name", "InputControlsFlowValidator.error.invalid.chars");
				}
			}

			if(query.getLabel()==null || query.getLabel().trim().length()==0) {
				errors.rejectValue("inputControl.query.label", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateLabel(query.getLabel())) {
					errors.rejectValue("inputControl.query.label", "InputControlsFlowValidator.error.invalid.chars");
				}
			}

			String language = query.getLanguage();
			if(language == null || language.trim().length() == 0) {
				errors.rejectValue("inputControl.query.language", "InputControlsFlowValidator.error.not.empty");
			}

			if(query.getSql()==null || query.getSql().trim().length()==0)
				errors.rejectValue("inputControl.query.sql", "InputControlsFlowValidator.error.not.empty");
//TODO: check this
//			if(query.getVisibleColumn()==null || query.getVisibleColumn().trim().length()==0)
//				errors.rejectValue("inputControl.query.sql",null,"Visible Columns field must not be blank");

		}else{
			if(dto.getExistingPath().trim().length()==0)
				errors.rejectValue("existingPath", "InputControlsFlowValidator.error.no.query");
		}
	}
	public void listOfValueDetails(InputControlWrapper dto, Errors errors){
		ResourceReference listOfValuesRef = dto.getInputControl().getListOfValues();
		if (!listOfValuesRef.isLocal()) {
			
		}
		ListOfValues listOfValues = (ListOfValues) listOfValuesRef.getLocalResource();
		if(dto.getSource()==null || dto.getSource().equals(JasperServerConstImpl.getFieldChoiceLocal())){
			if(listOfValues.getName()==null || listOfValues.getName().trim().length()==0) {
				errors.rejectValue("inputControl.listOfValues.name", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateName(listOfValues.getName())) {
					errors.rejectValue("inputControl.listOfValues.name", "InputControlsFlowValidator.error.invalid.chars");
				}
			}

			if(listOfValues.getLabel()==null || listOfValues.getLabel().trim().length()==0) {
				errors.rejectValue("inputControl.listOfValues.label", "InputControlsFlowValidator.error.not.empty");
			} else {
				if(!JasperServerUtil.regExValidateLabel(listOfValues.getLabel())) {
					errors.rejectValue("inputControl.listOfValues.label", "InputControlsFlowValidator.error.invalid.chars");
				}
			}
			
			if(listOfValues.getValues()==null || listOfValues.getValues().length==0){
				errors.rejectValue("inputControl.listOfValues.label", "InputControlsFlowValidator.error.invalid.chars");
			}

		}else{
			if(dto.getExistingPath()==null || dto.getExistingPath().trim().length()==0)
				errors.rejectValue("inputControl.listOfValues.description", "InputControlsFlowValidator.error.no.values");
		}
	}


	public void validateAddValue(InputControlWrapper dto, Errors errors){

		if(dto.getListItemLabel()==null || size(dto.getListItemLabel())==0) {
			errors.rejectValue("listItemLabel", "InputControlsFlowValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(dto.getListItemLabel())) {
				errors.rejectValue("listItemLabel", "InputControlsFlowValidator.error.invalid.chars");
			}
		}

		if(dto.getListItemValue()==null || size(dto.getListItemValue())==0) {
			errors.rejectValue("listItemValue", "InputControlsFlowValidator.error.not.empty");
		}
	}


	public void validateAddVisibleColumn(InputControlWrapper dto, Errors errors){

		if(dto.getNewVisibleColumn()==null || size(dto.getNewVisibleColumn())==0) {
			errors.rejectValue("newVisibleColumn", "InputControlsFlowValidator.error.not.empty");
		}
	}

	public void validateAddValueColumn(InputControlWrapper dto, Errors errors){

		InputControl inputControl = dto.getInputControl();
		if(inputControl.getQueryValueColumn()==null || size(inputControl.getQueryValueColumn())==0) {
			errors.rejectValue("inputControl.queryValueColumn", "InputControlsFlowValidator.error.not.empty");
		}
	}

    public void validateResourceExists(InputControlWrapper dto, Errors errors){
        if(dto.getSource()!=null && dto.getSource().equals(JasperServerConstImpl.getFieldChoiceRepo())) {
            if(dto.getExistingPath() == null || dto.getExistingPath().trim().length()==0 ||
                !repository.resourceExists(null, dto.getExistingPath())) {
				errors.rejectValue("existingPath", "InputControlsFlowValidator.error.no.data.type");
		    }
        }
	}

    public void validateQueryExists(InputControlWrapper dto, Errors errors) {
        if (dto.getSource() != null && dto.getSource().equals(JasperServerConstImpl.getFieldChoiceRepo()) &&
                (dto.getExistingPath() == null || dto.getExistingPath().trim().length() == 0 ||
                        !repository.resourceExists(null, dto.getExistingPath()))) {
            errors.rejectValue("existingPath", "InputControlsFlowValidator.error.no.query");
        }
    }

    private int size(String text){
		return text.trim().length();
	}

}
