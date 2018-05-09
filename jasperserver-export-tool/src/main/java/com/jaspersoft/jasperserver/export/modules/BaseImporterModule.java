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

package com.jaspersoft.jasperserver.export.modules;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.AttributedObject;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.repository.beans.PermissionRecipient;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseImporterModule extends BasicExporterImporterModule implements ImporterModule {

	private static final Log log = LogFactory.getLog(BaseImporterModule.class);
	
	protected static final CommandOut commandOut = CommandOut.getInstance();

	private String id;
	
	protected ImporterModuleContext importContext;
	protected Parameters params;
	protected ExecutionContext executionContext;
	protected ImportInput input;
	protected Element indexElement;

	private UserAuthorityService authorityService;

	private Map roles = new HashMap();
	private Map users = new HashMap();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	protected void initProcess() {
		roles = new HashMap();
		users = new HashMap();
	}

	public void init(ImporterModuleContext moduleContext) {
		this.importContext = moduleContext;
		this.params = moduleContext.getImportTask().getParameters();
		this.executionContext = moduleContext.getImportTask().getExecutionContext();
		this.input = moduleContext.getImportTask().getInput();
		this.indexElement = moduleContext.getModuleIndexElement();
	}
	
	protected String getParameterValue(String name) {
		return params.getParameterValue(name);
	}
	
	protected boolean hasParameter(String name) {
		return params.hasParameter(name);
	}

    protected String getSourceJsVersion() {
        return (String) importContext.getAttributes().getAttribute("sourceJsVersion");
    }

    protected String getTargetJsVersion() {
        return (String) importContext.getAttributes().getAttribute("targetJsVersion");
    }
    
    protected boolean isUpgrade() {
        String sourceJsVersion = getSourceJsVersion();
        String targetJsVersion = getTargetJsVersion();

        // upgrade false if target version can not be detected (just in case)
        if (targetJsVersion == null) {
            return false;
        }

        // upgrade true if no jsVersion in input archive is specified
        if (sourceJsVersion == null) {
            return true;
        }

        // upgrade true if upgrading CE to PRO. Multitenancy repo structure is needed
        if (sourceJsVersion.contains("CE") && targetJsVersion.contains("PRO")) {
            return true;
        }
        
        // return false
        return false;
    }

	protected final Object deserialize(String parentPath, String fileName, ObjectSerializer serializer) {
		InputStream in = getFileInput(parentPath, fileName);
		boolean closeIn = true;
		try {
			Object object = serializer.read(in, importContext);
			
			closeIn = false;
			in.close();

			return object;
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeIn) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	protected InputStream getFileInput(String parentPath, String fileName) {
		try {
			return input.getFileInputStream(parentPath, fileName);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected Attributes getContextAttributes() {
		return importContext.getAttributes();
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	protected void setPermissions(InternalURI object, RepositoryObjectPermissionBean[] permissions, boolean checkExisting) {
		if (permissions != null) {
			for (RepositoryObjectPermissionBean permissionBean : permissions) {
				setPermission(object, permissionBean, checkExisting);
			}
		}
	}

	protected void setPermission(InternalURI object, RepositoryObjectPermissionBean permissionBean, boolean checkExisting) {

		try {
			ObjectPermissionService permissionsService = getPermissionService();

			PermissionRecipient permissionRecipient = handleNewTenantIds(permissionBean.getRecipient());
			Object recipient;
			String recipientType = permissionRecipient.getRecipientType();
			if (recipientType.equals(getPermissionRecipientRole())) {
                recipient = getRole(permissionRecipient);
                if (recipient == null) {
                    commandOut.warn("Role " + permissionRecipient + " not found, skipping permission of " + object.getURI());
                }
            } else if (recipientType.equals(getPermissionRecipientUser())) {
                recipient = getUser(permissionRecipient);
                if (recipient == null) {
                    commandOut.warn("User " + permissionRecipient + " not found, skipping permission of " + object.getURI());
                }
            } else {
                recipient = null;
                commandOut.warn("Unknown object permission recipient type " + recipientType + ", skipping permission of " + object.getURI());
            }

			if (recipient != null) {
                boolean existing;
                if (checkExisting) {
                    List permissions = permissionsService.getObjectPermissionsForObjectAndRecipient(executionContext, object, recipient);
                    existing = permissions != null && !permissions.isEmpty();
                } else {
                    existing = false;
                }
                if (existing) {
                    if (log.isInfoEnabled()) {
                        log.info("Permission on " + object.getURI() + " for " + permissionRecipient + " already exists, skipping.");
                    }
                } else {
                    ObjectPermission permission = permissionsService.newObjectPermission(executionContext);
                    permission.setURI(object.getURI());
                    permission.setPermissionMask(permissionBean.getPermissionMask());
                    permission.setPermissionRecipient(recipient);

                    permissionsService.putObjectPermission(executionContext, permission);
                }
            }
		} catch (AccessDeniedException e) {
			commandOut.warn("Access denied. Cannot put permission for resource " +
					object.getURI() + " with " +
					permissionBean.getRecipient().getRecipientType() + " " +
					makeRecipientId(permissionBean.getRecipient()));
		}

	}

	protected String makeRecipientId(TenantQualifiedName tenantQualifiedName) {
		return tenantQualifiedName.getName();
	}

	protected Role getRole(TenantQualifiedName roleName) {
		Role role;
		if (roles.containsKey(roleName)) {
			role = (Role) roles.get(roleName);
		} else {
			role = loadRole(roleName);
			roles.put(roleName, role);
		}
		return role;
	}

	protected Role loadRole(TenantQualifiedName roleName) {
		return getAuthorityService().getRole(executionContext, roleName.getName());
	}

	protected User getUser(TenantQualifiedName username) {
		User user;
		if (users.containsKey(username)) {
			user = (User) users.get(username);
		} else {
			user = loadUser(username);
			users.put(username, user);
		}
		return user;
	}

	protected User loadUser(TenantQualifiedName username) {
		return getAuthorityService().getUser(executionContext, username.getName());
	}

	protected void saveProfileAttributes(ProfileAttributeService attributeService,
										 AttributedObject principal, ProfileAttributeBean[] attributes) {
		if (attributes != null && attributes.length > 0) {
			for (ProfileAttributeBean profileAttributeBean : attributes) {
				saveProfileAttribute(attributeService, principal, profileAttributeBean);
			}
		}
	}

	private void saveProfileAttribute(ProfileAttributeService attributeService,
									   AttributedObject principal, ProfileAttributeBean attributeBean) {
		ProfileAttribute attribute = attributeService.newProfileAttribute(executionContext);
		attribute.setPrincipal(principal);
		attributeBean.copyTo(attribute);
		String holderUri = attributeService.generateAttributeHolderUri(attribute.getPrincipal());
		attribute.setUri(attribute.getAttrName(), holderUri == null ? "" : holderUri);

		boolean saved = false;
		try {
			attributeService.putProfileAttribute(executionContext, attribute);
			saved = true;
			setPermissions(attribute, attributeBean.getPermissions(), false);
		} catch (AccessDeniedException e) {
			commandOut.warn("Access denied for profile attribute " + attributeBean.getName());
			if (saved) {
				throw new RuntimeException("Could not set permissions for created profile attribute "
						+ attributeBean.getName());
			}
		}
	}

	private PermissionRecipient handleNewTenantIds(PermissionRecipient permissionRecipient) {
		if (!importContext.getNewGeneratedTenantIds().isEmpty()) {
			permissionRecipient.setTenantId(TenantStrHolderPattern.TENANT_ID.replaceWithNewTenantIds(
					importContext.getNewGeneratedTenantIds(), permissionRecipient.getTenantId()));
		}

		return permissionRecipient;
	}

	protected void logWarning(ExportImportWarningCode warningCode, String[] parameters, String message) {
		if (importContext.getImportTask().getWarnings() != null) {
			importContext.getImportTask().getWarnings()
					.add(new WarningDescriptor(warningCode.toString(), parameters, message));
		}
		commandOut.warn(message);
	}
}
