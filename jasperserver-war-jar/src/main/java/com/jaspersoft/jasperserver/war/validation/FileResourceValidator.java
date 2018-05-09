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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapClientConnectionWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportUnitWrapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileResourceValidator implements Validator {

	private RepositoryService repository;
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    private String fileNameRegexp;
    private Long maxFileSize;
    private String fileNameValidationMessageKey;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

    public void setRepositoryServiceSecurityChecker(RepositorySecurityChecker repositoryServiceSecurityChecker) {
        this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
    }

    public void setFileNameRegexp(String fileNameRegexp) {
        this.fileNameRegexp = fileNameRegexp;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setFileNameValidationMessageKey(String fileNameValidationMessageKey) {
        this.fileNameValidationMessageKey = fileNameValidationMessageKey;
    }

    public boolean supports(Class clazz) {
		return FileResourceWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object arg0, Errors arg1) {
		// TODO Auto-generated method stub
	}

	public void validateNaming(FileResourceWrapper wrapper, Errors errors) {
		if (wrapper.getFileResource().getLabel() == null
				|| wrapper.getFileResource().getLabel().trim().length() == 0) {
			errors.rejectValue("fileResource.label", "FileResourceValidator.error.not.empty");
		} else {
			if (wrapper.getFileResource().getLabel().length() > 100) {
				errors.rejectValue("fileResource.label", "FileResourceValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateLabel(wrapper
					.getFileResource().getLabel())) {
				errors.rejectValue("fileResource.label", "FileResourceValidator.error.invalid.chars");
			}
		}
		if (wrapper.getFileResource().getName() == null
				|| wrapper.getFileResource().getName().trim().length() == 0) {
			errors.rejectValue("fileResource.name", "FileResourceValidator.error.not.empty");
		} else {
			wrapper.getFileResource().setName(
					wrapper.getFileResource().getName().trim());
            if (FileResource.TYPE_RESOURCE_BUNDLE.equals(wrapper.getFileResource().getFileType())) {
                String name = wrapper.getFileResource().getName();
                if (!name.endsWith(".properties"))
                    wrapper.getFileResource().setName(name + ".properties");
            }
            
			if (wrapper.getFileResource().getName().length() > 100) {
				errors.rejectValue("fileResource.name", "FileResourceValidator.error.too.long");
			} else if (!JasperServerUtil.regExValidateName(wrapper
					.getFileResource().getName())) {
				errors.rejectValue("fileResource.name", "FileResourceValidator.error.invalid.chars");
			} else if (wrapper.isSubNewMode()) {
				// must check if the resource by this name is already added in
				// the subflow mode
				Object parentObject = wrapper.getParentFlowObject();
				if (parentObject != null
						&& ReportUnitWrapper.class
								.isAssignableFrom(parentObject.getClass())) {
					ReportUnitWrapper parent = (ReportUnitWrapper) parentObject;
					List resources = parent.getReportUnit().getResources();
					if (resources != null && !resources.isEmpty())
						for (int i = 0; i < resources.size(); i++) {
							ResourceReference resRef = (ResourceReference) resources
									.get(i);
							Resource res = null;
							if (resRef.isLocal()) {
								res = resRef.getLocalResource();
                                //don't check resource name if parent folder isn't set. It means, that file isn't exist yet, but empty reference only.
								if (res.getParentFolder() != null && wrapper.getFileResource().getName().equals(
										res.getName())) {
									errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
									break;
								}
							}
						}
					List controls = parent.getReportUnit().getInputControls();
					if (controls != null && !controls.isEmpty())
						for (int i = 0; i < controls.size(); i++) {
							ResourceReference resRef = (ResourceReference) controls
									.get(i);
							if (resRef.isLocal()) {
								Resource res = resRef.getLocalResource();
								if (wrapper.getFileResource().getName().equals(
										res.getName())) {
									errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate.inputControl");
									break;
								}
							}
						}
				}
			}
//			else if (wrapper.isNewMode())
//					&& wrapper.getExistingResources() != null) {
//				// When in stand alone new mode check for name uniqueness
//				List res = wrapper.getExistingResources();
//				for (int i = 0; i < res.size(); i++) {
//					String preExtName = (String) res.get(i);
//					if (preExtName.equalsIgnoreCase(wrapper.getFileResource()
//							.getName().trim())) {
//						errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
//						break;
//					}
//				}


            if (wrapper.getFileResource().getCreationDate() == null) {
                if (wrapper.getParentFlowObject() instanceof OlapClientConnectionWrapper) {
                    OlapClientConnectionWrapper parentObject =  ((OlapClientConnectionWrapper)wrapper.getParentFlowObject());
                    if (wrapper.getFileResource().getURIString().equals(parentObject.getParentFolder()+"/"+parentObject.getConnectionName())) {
                        errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
                    }
                    if (wrapper.getFileResource().getFileType().equals(ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA)) {
                        if (wrapper.getFileResource().getURIString().equals(((OlapClientConnectionWrapper) wrapper.getParentFlowObject()).getSchemaUri())
                                || wrapper.getFileResource().getURIString().equals(((OlapClientConnectionWrapper) wrapper.getParentFlowObject()).getDatasourceUri())) {
                            errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
                        }
                    }
                }

                if (repository.repositoryPathExists(null, wrapper.getFileResource().getURIString())) {
                    errors.rejectValue("fileResource.name", "FileResourceValidator.error.duplicate");
                }
            }

			if (FileResource.TYPE_RESOURCE_BUNDLE.equals(wrapper.getFileResource().getFileType())) {
				String name = wrapper.getFileResource().getName();
				if (!name.endsWith(".properties"))
					errors.rejectValue("fileResource.name", "FileResourceValidator.error.resourceBundle");
			}
		}
		if (wrapper.getFileResource().getDescription() != null
				&& wrapper.getFileResource().getDescription().length() > 250)
			errors.rejectValue("fileResource.description", "FileResourceValidator.error.too.long");
	}

	public void validateUpload(FileResourceWrapper wrapper, Errors errors) {
		if (!wrapper.isSubflowMode()) {
			// There is only a upload field on the JSP, edit or new mode
			if (!wrapper.isLocated()) {
				if (wrapper.getNewData() == null
						|| wrapper.getNewData().length == 0)
					errors.rejectValue("newData", "FileResourceValidator.error.not.uploaded");
			}
		} else {
			if (wrapper.getSource() == null) {
				errors.rejectValue("source", "FileResourceValidator.error.no.file");
                return;
			}
            if (wrapper.isLocated()) {
                return;
            }
            if (wrapper.getSource().equals(JasperServerConst.FIELD_CHOICE_NONE)) {
                return;
            } else if (wrapper.getSource().equals(
                    JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)) {
                if (wrapper.getFileResource().getData() == null || wrapper
                                .getFileResource().getData().length == 0) {
                    errors.rejectValue("newData", "FileResourceValidator.error.not.uploaded");
                } else if (maxFileSize >= 0 && wrapper.getFileResource().getData().length > maxFileSize){
                    wrapper.setNewData(null);
                    errors.rejectValue("newData", "FileResourceValidator.error.too.big.newData", new Object[] {maxFileSize}, null);
                } else if (!isFileNameValid()) {
                    wrapper.setNewData(null);
                    String validationMessage = fileNameValidationMessageKey != null
                            ? fileNameValidationMessageKey : "FileResourceValidator.error.wrong.name.newData";
                    errors.rejectValue("newData", validationMessage, new Object[] {fileNameRegexp}, null);
                }
            } else {
                String newUri = wrapper.getNewUri();
                if (newUri == null || newUri.trim().length() == 0) {
                    if (wrapper.getParentFlowObject() instanceof OlapClientConnectionWrapper) {
                        errors.rejectValue("newUri", "FileResourceValidator.error.no.file.source");
                    } else {
                        errors.rejectValue("newUri", "FileResourceValidator.error.no.folder");
                    }
                } else {
                    Resource resource = null;
                    try {
                         resource = repository.getResource(null, newUri);
                    } finally {
                        if (resource == null) {
                            errors.rejectValue("newUri", "FileResourceValidator.error.not.exist");
                        }
                    }
                }
            }
		}
	}

    protected boolean isFileNameValid() {
        String fullName = getUploadedFileName();
        if (fullName == null || fullName.isEmpty()) {
            return false;
        }

        Matcher matcher = Pattern.compile(fileNameRegexp).matcher(fullName);
        return matcher.matches();
    }

    private String getUploadedFileName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String fileName = (String) request.getAttribute(JasperServerConst.UPLOADED_FILE_NAME);
        String fileExt = (String) request.getAttribute(JasperServerConst.UPLOADED_FILE_EXT);

        return (fileName != null ? fileName : "") + (fileExt != null ? "." + fileExt : "");
    }

    public void validateFolderURI(FileResourceWrapper wrapper, Errors errors) {
        String folderURI = wrapper.getFileResource().getParentFolder();
        if (!getRepository().folderExists(null, folderURI)) {

            errors.rejectValue("fileResource.parentFolder", "FileResourceValidator.error.folder.inexistent",
                    new Object[]{folderURI}, "");

        } else if (SecurityContextHolder.getContext().getAuthentication() != null &&
                !repositoryServiceSecurityChecker.isEditable(getRepository().getFolder(null, folderURI))) {

            errors.rejectValue("fileResource.parentFolder", "FileResourceValidator.error.folder.notwriteable",
                    new Object[]{folderURI}, "");
        }
    }

    public void validateAll(FileResourceWrapper wrapper, Errors errors) {
        validateNaming(wrapper, errors);
        validateUpload(wrapper, errors);
        validateFolderURI(wrapper, errors);
    }
}
