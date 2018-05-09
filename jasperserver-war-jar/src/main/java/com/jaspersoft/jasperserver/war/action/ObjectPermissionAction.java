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
package com.jaspersoft.jasperserver.war.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class ObjectPermissionAction extends FormAction
{
	protected final String RESOURCE = "resource";
	protected final String DECODE = "decode";
	protected final String ATTRIBUTE_PERMISSIONS = "permissions";
	protected final String ATTRIBUTE_ROLES = "roles";
	protected final String ATTRIBUTE_USERS = "users";
	protected final int NO_ACCESS = 0;
	protected final int NO_PERMISSION_SET = -1;
	protected final int INHERITED = 0x100;
	protected final String PERMISSION_PARM = "permission_";
	protected final String PREV_PERMISSION_PARM = "prev";
    protected final String TENANT_SEPARATOR = "tenantSeparator";

    public static final String CURRENT_PAGE_REQUEST_PARAMETER = "currentPage";

	protected final Log log = LogFactory.getLog(this.getClass());

	private ObjectPermissionService objectPermissionService;
	private RepositoryService repository;
	private UserAuthorityService userService;
    private TenantService tenantService;
    private ConfigurationBean configuration;
    private AuditContext auditContext;
    private int paginatorItemsPerPage;


    private List filterUserOrRoleList(ExecutionContext executionContext, List userOrRoleList, Resource resource) {
        String resourceUri = resource.getURIString();
        List roles = new ArrayList();
        for (Iterator i = userOrRoleList.iterator(); i.hasNext();) {
            TenantQualified userOrRole = (TenantQualified)i.next();
            Tenant tenant = tenantService.getTenant(executionContext, userOrRole.getTenantId());
            String tenantFolderUri = tenant != null ? tenant.getTenantFolderUri() : "/";
            if (resourceUri.startsWith(tenantFolderUri)) {
                roles.add(userOrRole);
            }
        }

        return roles;
    }

	public Event loadPermissionsForRoles(RequestContext context)
	{
		ExecutionContext executionContext = getExecutionContext(context);
		List roleList = userService.getRoles(executionContext, null);
		Map permissionsMap = new HashMap();

		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		if (resource == null) {
            String resourceUri = getResourceUri(context);
			resource = repository.getResource(executionContext, resourceUri);
			if (resource == null) {
				resource = repository.getFolder(executionContext, resourceUri);
			}
			context.getFlowScope().put(RESOURCE, resource);
		}

        roleList = filterUserOrRoleList(executionContext, roleList, resource);
        
        // find target sublist
        int allItemsCount = roleList.size();
        int allPagesCount = allItemsCount / paginatorItemsPerPage;
        if (allItemsCount % paginatorItemsPerPage > 0)
            allPagesCount += 1;

        List paginatedRoles = null;

        int crtPage = 1;
        String strCrtPage = context.getRequestParameters().get(CURRENT_PAGE_REQUEST_PARAMETER);

        //if(strCrtPage == null || strCrtPage.trim().length() == 0) 
        //    strCrtPage = page;

        if(strCrtPage != null && strCrtPage.trim().length() > 0) 
            crtPage = Integer.parseInt(strCrtPage);

        if(allItemsCount <= crtPage * paginatorItemsPerPage) 
        {
            crtPage = allPagesCount;
        }

        if(allItemsCount < crtPage * paginatorItemsPerPage)
            paginatedRoles = roleList.subList((crtPage - 1) * paginatorItemsPerPage, allItemsCount);
        else
            paginatedRoles = roleList.subList((crtPage - 1) * paginatorItemsPerPage, crtPage * paginatorItemsPerPage);
        
//		for (int i = 0; i < roleList.size(); i++) {
//			Role role = (Role) roleList.get(i);
        for (int i = 0; i < paginatedRoles.size(); i++) {
            Role role = (Role) paginatedRoles.get(i);
			Integer permissionToDisplay = new Integer(NO_PERMISSION_SET);
			List permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(executionContext, resource, role);
			if (permissions != null && permissions.size() > 0) {			
				ObjectPermission objectPermission = (ObjectPermission) permissions.get(0);

				if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
					permissionToDisplay = new Integer(objectPermission.getPermissionMask() + goUpFolderChainToGetPermission(executionContext, resource, role));
					
				}
			}
			// if no permission is set, go up the folder chain until hitting a folder with permission set.
			if (permissionToDisplay.intValue() == NO_PERMISSION_SET) {
				permissionToDisplay = new Integer(goUpFolderChainToGetPermission(executionContext, resource, role) + INHERITED); 
			} 
			permissionsMap.put(role, permissionToDisplay);
		}

		context.getRequestScope().put(ATTRIBUTE_PERMISSIONS, permissionsMap);
		context.getRequestScope().put(ATTRIBUTE_ROLES, roleList);
        context.getRequestScope().put(TENANT_SEPARATOR, tenantService.getUserOrgIdDelimiter());
		return success();
	}

    private String getResourceUri(RequestContext context) {
        String decode = context.getRequestParameters().get(DECODE);
        String resourceUri;
        if (decode != null && "true".equals(decode)) {
            try {
                resourceUri = URLDecoder.decode(context.getRequestParameters().get(RESOURCE), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            resourceUri = context.getRequestParameters().get(RESOURCE);
        }
        return resourceUri;
    }

    private int goUpFolderChainToGetPermission(ExecutionContext context, Resource resource, Object roleOrUser) {
		
		int permission = 
			objectPermissionService.getInheritedObjectPermissionMask(
					context, resource, roleOrUser);
		int inheritedPermission = permission << 9;
		return inheritedPermission;
	}

	public Event loadPermissionsForUsers(RequestContext context)
	{

		ExecutionContext executionContext = getExecutionContext(context);
		List userList = userService.getUsers(executionContext, null);
		Map permissionsMap = new HashMap();

		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		if (resource == null) {
			String resourceUri = getResourceUri(context);
			resource = repository.getResource(executionContext, resourceUri);
			if (resource == null) {
				resource = repository.getFolder(executionContext, resourceUri);
			}
			context.getFlowScope().put(RESOURCE, resource);
		}

        String resourceUri = resource.getURIString();
        String publicFolderUri = configuration.getPublicFolderUri();

        if (!(resourceUri.equals(publicFolderUri) || resourceUri.startsWith(publicFolderUri + "/"))) {
            userList = filterUserOrRoleList(executionContext, userList, resource);
        }

        // find target sublist
        int allItemsCount = userList.size();
        int allPagesCount = allItemsCount / paginatorItemsPerPage;
        if (allItemsCount % paginatorItemsPerPage > 0)
            allPagesCount += 1;

        List paginatedUsers = null;

        int crtPage = 1;
        String strCrtPage = context.getRequestParameters().get(CURRENT_PAGE_REQUEST_PARAMETER);

        //if(strCrtPage == null || strCrtPage.trim().length() == 0) 
        //    strCrtPage = page;

        if(strCrtPage != null && strCrtPage.trim().length() > 0) 
            crtPage = Integer.parseInt(strCrtPage);

        if(allItemsCount <= crtPage * paginatorItemsPerPage) 
        {
            crtPage = allPagesCount;
        }

        if(allItemsCount < crtPage * paginatorItemsPerPage)
            paginatedUsers = userList.subList((crtPage - 1) * paginatorItemsPerPage, allItemsCount);
        else
            paginatedUsers = userList.subList((crtPage - 1) * paginatorItemsPerPage, crtPage * paginatorItemsPerPage);
        
		
		//for (int i = 0; i < userList.size(); i++) {
        //    User user = (User) userList.get(i);
        for (int i = 0; i < paginatedUsers.size(); i++) {
            User user = (User) paginatedUsers.get(i);
			Integer permissionToDisplay = new Integer(NO_PERMISSION_SET);
			List permissions = objectPermissionService.getObjectPermissionsForObjectAndRecipient(executionContext, resource, user);
			if (permissions != null && permissions.size() > 0) {			
				ObjectPermission objectPermission = (ObjectPermission) permissions.get(0);

				if (objectPermission != null && objectPermission.getPermissionRecipient() != null) {
					permissionToDisplay = new Integer(objectPermission.getPermissionMask() + goUpFolderChainToGetPermission(executionContext, resource, user));
				}
			}
			// if no permission is set, go up the folder chain until hitting a folder with permission set.
			if (permissionToDisplay.intValue() == NO_PERMISSION_SET) {
				permissionToDisplay = new Integer(goUpFolderChainToGetPermission(executionContext, resource, user) + INHERITED); 
			} 
			permissionsMap.put(user, permissionToDisplay);
		}
		
		context.getRequestScope().put(ATTRIBUTE_PERMISSIONS, permissionsMap);
		context.getRequestScope().put(ATTRIBUTE_USERS, userList);
        context.getRequestScope().put(TENANT_SEPARATOR, tenantService.getUserOrgIdDelimiter());
		return success();
	}

    private void createPermissionAuditEvent(final String auditEventType) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent(auditEventType);
            }
        });
    }

    private void closePermissionAuditEvent(String auditEventType) {
        auditContext.doInAuditContext(auditEventType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

	public Event setRolePermission(RequestContext context)
	{		
		Map parameters = context.getRequestParameters().asMap();
		Iterator iter = parameters.keySet().iterator();
		ArrayList parmList = new ArrayList();
		while (iter.hasNext()) {
			String currentParameterName = (String)iter.next();
			if (currentParameterName.startsWith(PERMISSION_PARM)) {
			   parmList.add(currentParameterName);
			}
		}
		// nothing to do
		if (parmList.size() <= 0) {
			return success();
		}		
		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		for (int i=0; i<parmList.size(); i++) {
			String roleName = ((String)parmList.get(i)).substring(PERMISSION_PARM.length());
			Role role = userService.getRole(getExecutionContext(context), roleName);
			String permission = context.getRequestParameters().get((String)parmList.get(i));
			String prevPermission = context.getRequestParameters().get(PREV_PERMISSION_PARM + (String)parmList.get(i));
			int permissionValue = Integer.parseInt(permission);
			int prevPermissionValue = Integer.parseInt(prevPermission);
			if (prevPermissionValue != permissionValue){
				if (permissionValue <= 0xff) {
					if (permissionValue == 0){
						int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, role);
						if (upChainPermission == NO_ACCESS) {
                  	       if (!performObjectPermissionDelete(getExecutionContext(context), resource, role)) {
						       throw new RuntimeException(" Error occurred in object-permission delete. ");
                  	       }
						} else {
  				          if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) { 				        	  
					          throw new RuntimeException(" Error occurred in object-permission Save. ");  
  				          }                 	    	
                  	    }
					} else {
					
				      int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, role);
				      if (upChainPermission == (permissionValue << 9)) {
				    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, role)) {
							  throw new RuntimeException(" Error occurred in object-permission delete. ");
	                 	  }	
				      } else {
				          if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) {
					          throw new RuntimeException(" Error occurred in object-permission Save. ");
				          }
				      }
					}
				} else {
					permissionValue = permissionValue / 512;
					if (!performObjectPermissionSave(getExecutionContext(context), resource, role, permissionValue)) {
						throw new RuntimeException(" Error occurred in object-permission Save. ");
					}
				}
			}		
		}	
		return success();
	}

	public Event setUserPermission(RequestContext context)
	{
		
		Map parameters = context.getRequestParameters().asMap();
		Iterator iter = parameters.keySet().iterator();
		ArrayList parmList = new ArrayList();
		while (iter.hasNext()) {
			String currentParameterName = (String)iter.next();
			if (currentParameterName.startsWith(PERMISSION_PARM)) {
			   parmList.add(currentParameterName);
			}
		}
		// nothing to do
		if (parmList.size() <= 0) {
			return success();
		}		
		Resource resource = (Resource) context.getFlowScope().get(RESOURCE);
		for (int i=0; i<parmList.size(); i++) {
			String userName = ((String)parmList.get(i)).substring(PERMISSION_PARM.length());
			User user = userService.getUser(getExecutionContext(context), userName);
			String permission = context.getRequestParameters().get((String)parmList.get(i));
			String prevPermission = context.getRequestParameters().get(PREV_PERMISSION_PARM + (String)parmList.get(i));
			int permissionValue = Integer.parseInt(permission);
			int prevPermissionValue = Integer.parseInt(prevPermission);
			if (prevPermissionValue != permissionValue){
				if (permissionValue <= 0xff) {
					if (permissionValue == 0){
						int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, user);
						if (upChainPermission == NO_ACCESS) {
                  	       if (!performObjectPermissionDelete(getExecutionContext(context), resource, user)) {
						       throw new RuntimeException(" Error occurred in object-permission delete. ");
                  	       }
						} else {
  				          if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) { 				        	  
					          throw new RuntimeException(" Error occurred in object-permission Save. ");  
  				          }                 	    	
                  	    }
					} else {
					
				      int upChainPermission = goUpFolderChainToGetPermission(getExecutionContext(context), resource, user);
				      if (upChainPermission == (permissionValue << 9)) {
				    	  if (!performObjectPermissionDelete(getExecutionContext(context), resource, user)) {
							  throw new RuntimeException(" Error occurred in object-permission delete. ");
	                 	  }	
				      } else {
				          if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) {
					          throw new RuntimeException(" Error occurred in object-permission Save. ");
				          }
				      }
					}
				} else {
					permissionValue = permissionValue / 512;
					if (!performObjectPermissionSave(getExecutionContext(context), resource, user, permissionValue)) {
						throw new RuntimeException(" Error occurred in object-permission Save. ");
					}
				}
			}		
		}			
		return success();
	}

	private boolean performObjectPermissionSave(ExecutionContext context, Resource targetObject, Object recipientObject, int permission) {

		if (recipientObject == null) {
			log.warn("performObjectPermissionDelete: recipient is null");
			return false;
		}

		if (targetObject == null) {
			log.warn("performObjectPermissionDelete: target is null");
			return false;
		}

		ObjectPermission objectPermission = null;

		List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, targetObject, recipientObject);

		if (lstObjPerms != null && lstObjPerms.size() > 0)
			objectPermission = (ObjectPermission) lstObjPerms.get(0);

        String auditEventType = "createPermission";
		if (objectPermission == null) {
			objectPermission = objectPermissionService.newObjectPermission(context);
            createPermissionAuditEvent(auditEventType);
		} else {
            auditEventType = "updatePermission";
            createPermissionAuditEvent(auditEventType);
        }

		// Because of default permissions, we could get something that has no recipient

		objectPermission.setURI(targetObject.getProtocol() + ":" + targetObject.getURIString());
		objectPermission.setPermissionMask(permission);
		objectPermission.setPermissionRecipient(recipientObject);

		objectPermissionService.putObjectPermission(context, objectPermission);
        closePermissionAuditEvent(auditEventType);
		return true;
	}

	/*
	 * Function to perform the Delete action on Object Permissions
	 * @args
	 * @return boolean
	 */
	private boolean performObjectPermissionDelete(ExecutionContext context, Resource targetObject, Object recipientObject) {

		if (recipientObject == null) {
			log.warn("performObjectPermissionDelete: recipient is null");
			return false;
		}

		if (targetObject == null) {
			log.warn("performObjectPermissionDelete: target is null");
			return false;
		}

		ObjectPermission objectPermission = null;
		List lstObjPerms = objectPermissionService.getObjectPermissionsForObjectAndRecipient(context, targetObject, recipientObject);

		if (lstObjPerms != null && lstObjPerms.size() > 0)
			objectPermission = (ObjectPermission) lstObjPerms.get(0);
		// Because of default permissions, we could get something that has no recipient
		if (objectPermission == null || objectPermission.getPermissionRecipient() == null) {
			log.warn("performObjectPermissionDelete: no permission for target and recipient");
			return true;
		}

        createPermissionAuditEvent("deletePermission");
		objectPermissionService.deleteObjectPermission(context, objectPermission);
        closePermissionAuditEvent("deletePermission");
		return true;
	}

	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}

	public ObjectPermissionService getObjectPermissionService()
	{
		return objectPermissionService;
	}

	public void setObjectPermissionService(ObjectPermissionService objectPermissionService)
	{
		this.objectPermissionService = objectPermissionService;
	}

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}

	protected ExecutionContext getExecutionContext(RequestContext context) {
		return JasperServerUtil.getExecutionContext(context);
	}

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public int getPaginatorItemsPerPage() {
        return paginatorItemsPerPage;
    }

    public void setPaginatorItemsPerPage(int paginatorItemsPerPage) {
        this.paginatorItemsPerPage = paginatorItemsPerPage;
    }

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }
}
