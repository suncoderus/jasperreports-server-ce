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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.FolderWrapper;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class FolderValidator implements Validator
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

	public boolean supports(Class clazz) {
		return FolderWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object object, Errors errors) {
		FolderWrapper wrapper = (FolderWrapper)object;
		Folder folder = wrapper.getActualFolder();

		if (folder.getName() == null || size(folder.getName()) == 0) {
			errors.rejectValue("actualFolder.name", "error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateName(folder.getName())) {
				errors.rejectValue("actualFolder.name", "FolderValidator.error.invalid.chars");
			}else {
				if (folder.getName().trim().length() > JasperServerConst.MAX_LENGTH_NAME) {
					errors.rejectValue("actualFolder.name", "FolderValidator.error.too.long",
									   new Object[]{JasperServerConst.MAX_LENGTH_NAME_W}, null);
				}else{

					if (!wrapper.isEdit()) {
						if (repository.repositoryPathExists(null, folder.getURIString())) {
							errors.rejectValue("actualFolder.name", "FolderValidator.error.duplicate");
						}
					}
				}
			}
		}

		if (folder.getLabel() == null || size(folder.getLabel()) == 0) {
			errors.rejectValue("actualFolder.label", "FolderValidator.error.not.empty");
		} else {
			if(!JasperServerUtil.regExValidateLabel(folder.getLabel())) {
				errors.rejectValue("actualFolder.label", "FolderValidator.error.invalid.chars");
			}else {
				if (folder.getLabel().trim().length() > JasperServerConst.MAX_LENGTH_LABEL) {
					errors.rejectValue("actualFolder.label", "FolderValidator.error.too.long",
									   new Object[]{JasperServerConst.MAX_LENGTH_LABEL_W}, null);
				}
			}
		}
		
		if (folder.getDescription() != null && size(folder.getDescription()) > JasperServerConst.MAX_LENGTH_DESC) {
			errors.rejectValue("actualFolder.description", "FolderValidator.error.too.long",
							   new Object[]{JasperServerConst.MAX_LENGTH_DESC_W}, null);
		}
	}

	private int size(String text){
		return text.trim().length();
	}

}

