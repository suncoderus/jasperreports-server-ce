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

import com.jaspersoft.jasperserver.api.common.domain.ValidationDetail;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;

import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.OlapUnitWrapper;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;

/**
 *
 * OlapUnitValidator provides validation methods for the
 * olapUnitFlow
 *
 * @author jshih
 * @revision $Id$
 */
public class OlapUnitValidator implements Validator {

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
	return OlapUnitWrapper.class.isAssignableFrom(clazz);
    }

    public void validate(Object o, Errors errors) {
	OlapUnitWrapper olapDetails = (OlapUnitWrapper) o;
	validateNameLabelDesc(olapDetails, errors);
    }

    public void validateNameLabelDesc(OlapUnitWrapper ouWrapper, Errors errors) {
	if (ouWrapper.getOlapUnitLabel() == null ||
	    ouWrapper.getOlapUnitLabel().trim().length() == 0) {
	    errors.rejectValue("olapUnitLabel", "OlapUnitValidator.error.not.empty");
	} else {
	    if (ouWrapper.getOlapUnitLabel().length() > 100) {
		errors.rejectValue("olapUnitLabel", "OlapUnitValidator.error.too.long");
	    } else if (!JasperServerUtil.regExValidateLabel(ouWrapper.getOlapUnitLabel()))
		errors.rejectValue("olapUnitLabel", "OlapUnitValidator.error.invalid.chars");
	}

	if (ouWrapper.getOlapUnitName() == null ||
	    ouWrapper.getOlapUnitName().trim().length() == 0) {
	    errors.rejectValue("olapUnitName", "OlapUnitValidator.error.not.empty");
	} else {
	    if (ouWrapper.getOlapUnitName().length() > 100) {
		errors.rejectValue("olapUnitName", "OlapUnitValidator.error.too.long");
	    } else if (!JasperServerUtil.regExValidateName(ouWrapper.getOlapUnitName()))
		errors.rejectValue("olapUnitName", "OlapUnitValidator.error.invalid.chars");
	    else {
//		if (ouWrapper.isNewMode()
//		    && ouWrapper.getExistingResources() != null) {
//		    List res = ouWrapper.getExistingResources();
//		    for (int i = 0; i < res.size(); i++) {
//			String preExtName = (String) res.get(i);
//			if (preExtName.equalsIgnoreCase(ouWrapper.getOlapUnitName().trim())) {
//			    errors.rejectValue("olapUnitName", "OlapUnitValidator.error.duplicate");
//			    break;
//			}
//		    }
//		}

			if (ouWrapper.isAloneNewMode()) {
				OlapUnit olapUnit = ouWrapper.getOlapUnit();
				olapUnit.setName(ouWrapper.getOlapUnitName());
				if (repository.repositoryPathExists(null, ouWrapper.getOlapUnit().getURIString())) {
					errors.rejectValue("olapUnitName", "OlapUnitValidator.error.duplicate");
				}
			}

		}
	}

	if (ouWrapper.getOlapUnitDescription() != null &&
                ouWrapper.getOlapUnitDescription().length() > 250)
	    errors.rejectValue("olapUnitDescription", "OlapUnitValidator.error.too.long",
                                                   new Object[]{ new Integer(250) }, null);
    }

    public void validateMdxQuery(OlapUnitWrapper ouWrapper, Errors errors) {
	// at this point the unit and connection have not been saved.
	// (apparently the action named saveOlapClientConnection does not
	//  actually save anything... it updates olap unit wrapper with connectioninfo
    //  that is left for saveOlapUnit).
	// this works out ok if we are editing an existing olap unit,
	// but if we are making a new one, its connection must be a
	// local reference if it is to be retrieved...
	ouWrapper.getOlapUnit().
	    setMdxQuery(ouWrapper.getOlapUnitMdxQuery());
	if (ouWrapper.isNewMode()) {
	    ouWrapper.getOlapUnit().
		setOlapClientConnection(ouWrapper.getOlapClientConnection());
	}

	if (ouWrapper.getOlapUnitMdxQuery() == null ||
	    ouWrapper.getOlapUnitMdxQuery().trim().length() == 0) {
	    errors.rejectValue("olapUnitMdxQuery", "OlapUnitValidator.error.not.empty");
	}
	else if (ouWrapper.getOlapUnitMdxQuery() !=
		// setting Mdx query length to Mysql varchar limit, per customer request
		 null && ouWrapper.getOlapUnitMdxQuery().length() > 65536) {
	    errors.rejectValue("olapUnitMdxQuery", "OlapUnitValidator.error.too.long");
	}
	else {
	    OlapUnit ou = ouWrapper.getOlapUnit();
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		FileResource schema = ouWrapper.getOlapClientSchema();
		OlapClientConnection conn = ouWrapper.getOlapClientConnection();
		ReportDataSource datasource = ouWrapper.getOlapClientDatasource();
	    ValidationResult result = null;
		try {
		    result = getConnectionService().validate(executionContext,
		    				ouWrapper.getOlapUnit(),
		    				schema,
		    				conn,
		    				datasource);
		    ouWrapper.setResult(result.getValidationState().
				      equals(ValidationResult.STATE_VALID));
		    //if (result.getValidationState().equals(ValidationResult.STATE_ERROR)) {
			//return error();
		    //}
		}
		catch (Exception e) {
		    // TODO fix
			ouWrapper.setResult(result.getValidationState().equals(
									 ValidationResult.STATE_ERROR));
		    //log.error(e.getStackTrace());
		}
		ouWrapper.setOlapUnit(ou);

	    if (result.getValidationState().equals(ValidationResult.STATE_ERROR)) {
		List details = result.getResults();
		String causeMsg = null;
		for( int i = 0; i < details.size(); i++ ) {
		    ValidationDetail detail = (ValidationDetail)details.get(i);
		    Throwable e = detail.getException();
		    while (e != null) {
			causeMsg = e.getMessage();
			e = e.getCause();
		    }
		}
		errors.rejectValue("olapUnitMdxQuery", "OlapUnitValidator.error.invalid.olapUnitMdxQuery", new Object[]{causeMsg}, null);
	    }
	}
    }

    public void validateURIString(OlapUnitWrapper ouWrapper, Errors errors) {
	if (ouWrapper.getSource() == null) {
	    errors.rejectValue("source", "OlapUnitValidator.error.invalid.schema");
	} else {
	    if (ouWrapper.getSource().equals(
					     JasperServerConst.FIELD_CHOICE_CONT_REPO)) {
		if (ouWrapper.getSchemaUri() == null
		    || ouWrapper.getSchemaUri().length() == 0) {
		    errors.rejectValue("schemaUri", "OlapUnitValidator.error.not.reusable");
		}
	    }
	}

    }

    public void validateConnectionType(OlapUnitWrapper ouWrapper, Errors errors){
	if(ouWrapper.getSource() == null)
	    errors.rejectValue("source", "OlapUnitValidator.error.no.connection.type");
    }

    public void validateConnectionSource(OlapUnitWrapper wrapper, Errors errors){
	if(wrapper.getType() == null)
	    errors.rejectValue("source", "OlapUnitValidator.error.no.connection.source");
    }

    /**
     * property: olapConnectionService
     */
    private OlapConnectionService mConnectionService;
    public OlapConnectionService getConnectionService() {
        return mConnectionService;
    }
    public void setConnectionService( OlapConnectionService cs ) {
        mConnectionService = cs;
    }

}
