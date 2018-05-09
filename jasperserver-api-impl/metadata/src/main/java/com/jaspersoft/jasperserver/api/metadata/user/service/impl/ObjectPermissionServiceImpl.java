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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.security.JasperServerAclHelper;
import com.jaspersoft.jasperserver.api.security.NonMutableAclCache;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author swood
 *
 */
public class ObjectPermissionServiceImpl extends HibernateDaoImpl implements
				ObjectPermissionService, ObjectPermissionServiceInternal,
				ApplicationContextAware, PersistentObjectResolver, InitializingBean {

	//

	protected static final Log log = LogFactory.getLog(ObjectPermissionServiceImpl.class);
    public static final String RECIPIENT_USED_FOR_INHERITANCE_MARKER = "___INHERITANCE_MARKER_ONLY___";

    protected static final String RESOURCE_URI_PREFIX = Resource.URI_PROTOCOL + ":";
    protected static final int RESOURCE_URI_PREFIX_LENGTH = RESOURCE_URI_PREFIX.length();

    /**
     * Marker added to the cache to indicate an AclObjectIdentity has no
     * corresponding BasicAclEntry[]s
     */
    static String RECIPIENT_FOR_CACHE_EMPTY = "RESERVED_RECIPIENT_NOBODY";

    private HibernateRepositoryService repoService;
    private UserAuthorityService userService;

	private ResourceFactory objectFactory;
	private ResourceFactory persistentClassFactory;
    private NonMutableAclCache nonMutableAclCache;
    private AclService aclService;

	private AttributePathTransformer attributePathTransformer;

	@SuppressWarnings("unused")
	private ApplicationContext appContext;
    private AuditContext auditContext;

	/**
	 * @return Returns the repoService.
	 */
	public HibernateRepositoryService getRepositoryService() {
		return repoService;
	}

	/**
	 * @param repoService The repoService to set.
	 */
	public void setRepositoryService(HibernateRepositoryService repoService) {
		this.repoService = repoService;
	}

	/**
	 * @return Returns the userService.
	 */
	public UserAuthorityService getUserAuthorityService() {
		return userService;
	}

	/**
	 * @param userService The userService to set.
	 */
	public void setUserAuthorityService(UserAuthorityService userService) {
		this.userService = userService;
	}

	public ResourceFactory getObjectMappingFactory() {
		return objectFactory;
	}

	public void setObjectMappingFactory(ResourceFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public ResourceFactory getPersistentClassFactory() {
		return persistentClassFactory;
	}

	public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
		this.persistentClassFactory = persistentClassFactory;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// Not needed really?
		appContext = arg0;
	}

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

	public void setAttributePathTransformer(AttributePathTransformer attributePathTransformer) {
		this.attributePathTransformer = attributePathTransformer;
	}

	/**
     * Get the parent URI of the given URI, by slicing off the last part of the path
     *
     * @param currentURI
     * @return
     */
    private String getParentURI(String currentURI) {
    	if (currentURI == null || currentURI.trim().length() == 0) {
    		return null;
    	} else {

    		// Deal with URIs that come with "repo:" on the front

    		String workUri = currentURI.startsWith(RESOURCE_URI_PREFIX)
    							? currentURI.substring(RESOURCE_URI_PREFIX_LENGTH).trim()
    							: currentURI.trim();

    		/*
    		 * If we have a URI like "repo:/folder1/object", return "repo:/folder1".
    		 *
    		 * repo:/folder1 returns repo:/
    		 *
    		 * repo:/ returns null
    		 */
    		int lastSeparator = workUri.lastIndexOf(Folder.SEPARATOR);

    		// no separator
    		if (lastSeparator < 0) {
    			return null;
    		} else if (lastSeparator == 0) {
    			// if we are the root: no parent
    			if (workUri.length() == 1) {
    				return null;
    			} else {
    				return RESOURCE_URI_PREFIX + Folder.SEPARATOR;
    			}
    		} else {
    			return RESOURCE_URI_PREFIX + workUri.substring(0, lastSeparator);
    		}
    	}
    }

    public void deleteObjectPermissionForObject(ExecutionContext context, Object targetObject) {
		if (targetObject == null || !(targetObject instanceof InternalURI)) {
			return;
		}
		InternalURI res = (InternalURI) targetObject;
		deleteObjectPermissionForRepositoryPath(context, res.getPath());
    }


    @SuppressWarnings("rawtypes")
	public void deleteObjectPermissionForRepositoryPath(ExecutionContext context, String path) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for repository path " + path);
    	}

		List permissions = getRepoObjectPermissions(context, repositoryURI(path), null);
        deleteObjectPermissions(context, permissions, false);
    }

	@SuppressWarnings("rawtypes")
	public void deleteObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for recipient " + recipient);
    	}

		List permissions = getRepoObjectPermissions(context, null, recipient);
		deleteObjectPermissions(context, permissions);
	}

	@SuppressWarnings("rawtypes")
	public void deleteObjectPermissionsForRecipient(ExecutionContext context, ObjectRecipientIdentity recipientIdentity) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for recipient " + recipientIdentity);
    	}

		List permissions = getRepoObjectPermissions(recipientIdentity);
		deleteObjectPermissions(context, permissions);
	}

	@Override
	public void deleteObjectCachePermissionsForRecipient(ExecutionContext context, ObjectRecipientIdentity recipientIdentity) {
		if (log.isDebugEnabled()) {
			log.debug("Deleting object permissions for recipient " + recipientIdentity);
		}

		List permissions = getRepoObjectPermissions(recipientIdentity);

		boolean checkAdministerAccess = true;

		if (permissions != null && !permissions.isEmpty()) {
			for (RepoObjectPermission permission: (List<RepoObjectPermission>)permissions) {
				if (!isPrivilegedOperation(context) && checkAdministerAccess && !isObjectAdministrable(null, permission.getURI())) {
					throw new AccessDeniedException("Access is denied");
				}
				clearAclEntriesCache(permission.getURI());
			}
		}
	}

    @SuppressWarnings("rawtypes")
	protected void deleteObjectPermissions(ExecutionContext context, List permissions) {
        deleteObjectPermissions(context, permissions, true);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void deleteObjectPermissions(ExecutionContext context, List permissions, boolean checkAdministerAccess) {
        if (permissions != null && !permissions.isEmpty()) {
            for (RepoObjectPermission permission: (List<RepoObjectPermission>)permissions) {
                deleteObjectPermission(context, permission, checkAdministerAccess);
            }
        }
    }

    /**
	 * A key for the cache
	 *
	 * @author swood
	 *
	 */
    public static class URIObjectIdentity implements ObjectIdentity {
    	/**
		 * Thanks, Eclipse!
		 */
		private static final long serialVersionUID = 1L;
		String uri;
    	public URIObjectIdentity(String uri) {
    		String fullUri = uri;

    		// Catch things that do not have a protocol
    		if (uri != null && uri.startsWith(Folder.SEPARATOR)) {
    			fullUri = RESOURCE_URI_PREFIX + uri;
    		}
    		this.uri = fullUri;
    	}
    	public String getURI() {
    		return uri;
    	}

    	public String toString() {
    		return new ToStringBuilder(this)
    			.append("uri", getURI())
    			.toString();
    	}

        public boolean equals(Object other) {
            if ( !(other instanceof URIObjectIdentity) ) return false;
            URIObjectIdentity castOther = (URIObjectIdentity) other;
            return new EqualsBuilder()
                .append(this.getURI(), castOther.getURI())
                .isEquals();
        }

        @Override
        public Serializable getIdentifier() {
            return getURI();
        }

        @Override
        public String getType() {
            return Resource.class.getName();
        }

        public int hashCode() {
            return new HashCodeBuilder()
                .append(getURI())
                .toHashCode();
        }
    }

	/**
	 * We can authorize InternalURI
	 *
	 */
	public boolean supports(Object obj) {
		return obj instanceof InternalURI || obj instanceof String;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#newObjectPermission(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
	 */
	public ObjectPermission newObjectPermission(ExecutionContext context) {
		return (ObjectPermission) getObjectMappingFactory().newObject(ObjectPermission.class);
	}

    private void addParamsToSetPermissionAuditEvent(final ExecutionContext context, final ObjectPermission objPermission, final int previousPermissionValue) {
        String[] eventNames = new String[]{"createPermission", "updatePermission", "deletePermission"};
        auditContext.doInAuditContext(eventNames, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                Resource resource = repoService.getResource(context, objPermission.getURI());
                if (resource != null) {
					auditContext.setResourceTypeToAuditEvent(resource.getResourceType(), auditEvent);
				} else if (objPermission.getURI().startsWith(PermissionUriProtocol.ATTRIBUTE.getProtocolPrefix())) {
					auditContext.setResourceTypeToAuditEvent(ProfileAttribute.class.getName(), auditEvent);
                } else {
                    auditContext.setResourceTypeToAuditEvent(Folder.class.getName(), auditEvent);
                }
                auditEvent.setResourceUri(objPermission.getURI());
                String recipientType = "UNKNOWN";
                String recipientName = null;
                String recipientTenantId = null;
                if (objPermission.getPermissionRecipient() instanceof User) {
                    recipientType = "USER";
                    recipientName = ((User) objPermission.getPermissionRecipient()).getUsername();
                    recipientTenantId = ((User) objPermission.getPermissionRecipient()).getTenantId();
                } else if (objPermission.getPermissionRecipient() instanceof Role) {
                    recipientType = "ROLE";
                    recipientName = ((Role) objPermission.getPermissionRecipient()).getRoleName();
                    recipientTenantId = ((Role) objPermission.getPermissionRecipient()).getTenantId();
                }
                auditContext.addPropertyToAuditEvent("recipientType", recipientType, auditEvent);
                auditContext.addPropertyToAuditEvent("recipientName", recipientName, auditEvent);
                auditContext.addPropertyToAuditEvent("recipientTenantId", recipientTenantId, auditEvent);
                if ("updatePermission".equals(auditEvent.getEventType())) {
                    auditContext.addPropertyToAuditEvent("previousPermissionValue", previousPermissionValue, auditEvent);
                }
                auditContext.addPropertyToAuditEvent("permissionValue", objPermission.getPermissionMask(), auditEvent);
            }
        });
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#putObjectPermission(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission)
	 */
	@SuppressWarnings("rawtypes")
	public void putObjectPermission(ExecutionContext context, ObjectPermission objPermission) throws IllegalArgumentException{
        if(objPermission == null){
            throw new IllegalArgumentException("Permission can't be null");
        }

        // checking administrative access
        if (!isPrivilegedOperation(context) && !isObjectAdministrable(context, objPermission.getURI())) {
            throw new AccessDeniedException("Access is denied");
        }
        final Object permissionRecipient = objPermission.getPermissionRecipient();
        if(permissionRecipient == null){
            throw new IllegalArgumentException("Permission recipient can't be null");
        }
        if(permissionRecipient instanceof User){
            final User user = (User) permissionRecipient;
            final String username = user.getUsername();
            if(username == null || username.isEmpty()){
                throw new IllegalArgumentException("User name can't be null");
            }

            Set<String> tenantSet = new HashSet<String>(1);
            tenantSet.add(user.getTenantId());
            final List tenantUsers = userService.getTenantUsers(null, tenantSet, username);
            if(tenantUsers == null || tenantUsers.isEmpty()){
                throw new IllegalArgumentException("User '" + username + "' doesn't exists and can't be used as permissionRecipient for permission");
            }
        } else if(permissionRecipient instanceof Role){
            final Role role = (Role) permissionRecipient;
            final String roleName = role.getRoleName();
            if(roleName == null || roleName.isEmpty()){
                throw new IllegalArgumentException("Role name can't be null");
            }
            Set<String> tenantSet = new HashSet<String>();
            tenantSet.add(role.getTenantId());
            final List roles = userService.getTenantRoles(null, tenantSet, roleName);
            if(roles == null || roles.isEmpty()){
                throw new IllegalArgumentException("Role '" + roleName + "' doesn't exists and can't be used as permissionRecipient for permission");
            }
        } else {
            throw new IllegalArgumentException("Unknown type of permissionRecipient. Invalid permissionRecipient: " + permissionRecipient.toString());
        }
        // Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			existingPerm = (RepoObjectPermission) getPersistentClassFactory().newObject(ObjectPermission.class);
		}

        addParamsToSetPermissionAuditEvent(context, objPermission, existingPerm.getPermissionMask());

		existingPerm.copyFromClient(objPermission, this);
		getHibernateTemplate().saveOrUpdate(existingPerm);

		clearAclEntriesCache(objPermission.getURI());
	}



	public void deleteObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			throw new JSException("jsexception.no.such.object.permission");
		}
		// and delete
        addParamsToSetPermissionAuditEvent(context, objPermission, existingPerm.getPermissionMask());
		deleteObjectPermission(context, existingPerm);
	}

    protected void deleteObjectPermission(ExecutionContext context, RepoObjectPermission permission) {
        deleteObjectPermission(context, permission, true);
    }
	protected void deleteObjectPermission(ExecutionContext context, RepoObjectPermission permission, boolean checkAdministerAccess) {
        // checking administrable access
        if (!isPrivilegedOperation(context) && checkAdministerAccess && !isObjectAdministrable(null, permission.getURI())) {
            throw new AccessDeniedException("Access is denied");
        }
		getHibernateTemplate().delete(permission);
		clearAclEntriesCache(permission.getURI());
	}

    protected boolean isPrivilegedOperation(ExecutionContext context){
        return context != null && context.getAttributes() != null && context.getAttributes().contains(PRIVILEGED_OPERATION);
    }

	public ObjectPermission getObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			return null;
		} else {
			return (ObjectPermission) existingPerm.toClient(getObjectMappingFactory());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List parseList(final List objList){
		// list of object permissions
		List<ObjectPermission> result = new ArrayList(objList.size());
		
		for(Object obj: objList){
			if(obj instanceof ObjectPermission){
				ObjectPermission op = (ObjectPermission) obj;
				result.add(op);
			} else {
				if(log.isDebugEnabled()){
					log.debug("----->>>> SKIPPING: " + obj.toString());
				}
			}
		}
		Collections.sort(result, new Comparator<ObjectPermission>() {
			
			@Override
			public int compare(ObjectPermission o1, ObjectPermission o2) {
				return o1.getURI().compareTo(o2.getURI());
			}
		});
		return result;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private List getRepoObjectPermissions(ExecutionContext context, final String uri, Object recipient) {
		return getRepoObjectPermissions(context, uri, recipient, null);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private List getRepoObjectPermissions(ExecutionContext context, final String uri,  final Object recipient, final Object[] existingAcl) {
		// Given the object and the recipient, find the permission
		final IdedObject recipientObject = (IdedObject) getPersistentObject(recipient);

		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);
        final Class recipientObjectClassName = recipient == null ? null : getPersistentClassFactory().getImplementationClass(recipient.getClass());

		List objList = null;

		if (uri == null && recipientObject == null) {
			//throw new JSException("jsexception.no.uri.or.recipient.given");
			// this is to get all permissions
			final String queryString = "from " + objPermissionClassName;

			objList = getHibernateTemplate().execute(new HibernateCallback<List>() {
	            public List doInHibernate(Session session) throws HibernateException {
	                return session.createQuery(queryString)
	                	.setCacheable(true)
	                	.list();
	            }
	        });
		}

		
		final boolean useBulk = uri!=null && uri.startsWith("repo:") && (existingAcl!=null);

		// split uri into uncached acls and existing acl.
		// we'll search only for uncached acl and return both parts to combine later 
		// in the final acl
		final List<String> path = new ArrayList<String>();
		if(uri!=null){
			path.add(uri); // this is obviously not in a cache as we wouldn't be in this method
			if(useBulk) {
				for(String parentURI=getParentURI(uri); parentURI!=null; parentURI=getParentURI(parentURI)){
					Acl acl = getCached(parentURI);
					if(acl==null){
						path.add(parentURI);
					} else { 
						// ok we've found existing path, all parents should be already in the cache too
						// or there is a problem with cache integrity (not our problem)
						existingAcl[0]=parentURI;
						existingAcl[1]=acl;
						break;
					}
				}
			}
		}
		//*****************//

		if (uri != null && recipientObject != null) {
			// split uri and get all permissions in 1 scoop
						
			// Select on both identity and recipient
			final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.URI in (:path) and " +
			"      objPermission.permissionRecipient.id = :id and objPermission.permissionRecipient.class = :class ";
			objList = getHibernateTemplate().execute(new HibernateCallback<List>() {
	            public List doInHibernate(Session session) throws HibernateException {
	                List result = session
	                	.createQuery(queryString)
	                	.setParameterList("path", path)
	                	.setParameter("id", new Long(recipientObject.getId()), Hibernate.LONG)
	                	.setParameter("class", recipientObjectClassName, Hibernate.CLASS)
	                	.setCacheable(true)
	                	.list();
	                return result;
	            }
	        });
			return useBulk ?parseList(objList):objList; //objList;
		} else if (uri != null) {
			// Select on identity
			final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.URI in (:path)";
			HibernateTemplate template = getHibernateTemplate();
			template.setCacheQueries(true);
			objList = template.findByNamedParam(queryString,"path",path);
			return useBulk ?parseList(objList):objList; //objList;
			
		} else if (recipientObject != null) {
			// Select on recipient
			ObjectRecipientIdentity recipientIdentity = new ObjectRecipientIdentity(recipientObject);
			objList = getRepoObjectPermissions(recipientIdentity);

		}
		return objList;
	}

	@SuppressWarnings({ "rawtypes" })
	protected List getRepoObjectPermissions(final ObjectRecipientIdentity recipientIdentity) {
		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);

		final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.permissionRecipient.id = ? and objPermission.permissionRecipient.class = ?";

		List objList = getHibernateTemplate().execute(new HibernateCallback<List>() {
		    public List doInHibernate(Session session) throws HibernateException {
		        return session.createQuery(queryString)
		        	.setParameter(0, new Long(recipientIdentity.getId()), Hibernate.LONG)
		        	.setParameter(1, recipientIdentity.getRecipientClass(), Hibernate.CLASS)
		        	.setCacheable(true)
		        	.list();
		    }
		});
		return objList;
	}

	@SuppressWarnings("rawtypes")
	private List getRepoObjectPermissions(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and parent, find the objectIdentity
		return getRepoObjectPermissions(context, objPermission.getURI(), objPermission.getPermissionRecipient());
	}

	@SuppressWarnings("rawtypes")
	private RepoObjectPermission getRepoObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		List objList = getRepoObjectPermissions(context, objPermission);
		RepoObjectPermission objPerm = null;
		if (objList == null || objList.isEmpty()) {
			log.debug("ObjectPermission not found with object \"" +
					objPermission.getURI() + "\", recipient \"" + objPermission.getPermissionRecipient() + "\"");
		} else {
			if (log.isDebugEnabled()) {
				log.debug("ObjectPermission FOUND with object \"" +
						objPermission.getURI() + "\", recipient \"" + objPermission.getPermissionRecipient() + "\"");
				log.debug("Size: " + objList.size());
			}
			objPerm = (RepoObjectPermission) objList.get(0);
		}
		return objPerm;

	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForObject(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject, final Object[] existingAcl) {
		if (targetObject == null || !(targetObject instanceof InternalURI)) {
			return Collections.EMPTY_LIST;
		}
		InternalURI res = (InternalURI) targetObject;
		String uriWithProtocol = res.getProtocol()+ ":" + res.getPath();
		List objList = getRepoObjectPermissions(context, uriWithProtocol, null, existingAcl);
		return makeObjectPermissionClientList(objList);
	}

	@SuppressWarnings("rawtypes")
	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject) {
		return getObjectPermissionsForObject(context, targetObject, null);

	}
	
	
    @SuppressWarnings("unchecked")
	@Override
    public List<ObjectPermission> getEffectivePermissionsForObject(ExecutionContext context, Object targetObject) {
        if (!(targetObject instanceof ObjectIdentity)) {
            return Collections.EMPTY_LIST;
        }
        List<ObjectPermission> result;
        result = new ArrayList<ObjectPermission>();
        Acl effectiveAcl = aclService.readAclById((ObjectIdentity) targetObject);
        Set<Sid> sids = JasperServerAclHelper.locateSids(effectiveAcl, true);
        AccessControlEntry ace;
        for (Sid sid : sids) {
            ace = JasperServerAclHelper.locateAceForSid(effectiveAcl, sid);
            if (ace != null) {

                ObjectPermission op = new ObjectPermissionImpl();
                op.setPermissionMask(ace.getPermission().getMask());
                op.setURI(extractUriFromAcl(ace.getAcl()));
                op.setPermissionRecipient(ace.getSid());
                result.add(op);
            }
        }

        // objectPermissionService.getEffectivePermissionsForObject(makeExecutionContext(), resource);
        return result;
    }
    protected String extractUriFromAcl(Acl entry) {
        final ObjectIdentity objectIdentity = entry.getObjectIdentity();
        return objectIdentity instanceof ObjectPermissionServiceImpl.URIObjectIdentity
                ? ((ObjectPermissionServiceImpl.URIObjectIdentity) objectIdentity).getURI()
                : objectIdentity.getIdentifier().toString();
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForRecipient(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
      */
	@SuppressWarnings("rawtypes")
	public List getObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
		List objList = getRepoObjectPermissions(context, null, recipient);
		return makeObjectPermissionClientList(objList);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForObjectAndRecipient(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	public List getObjectPermissionsForObjectAndRecipient(ExecutionContext context, Object targetObject, Object recipient) {
		if (!(targetObject instanceof InternalURI)) {
			return new ArrayList();
		}
		InternalURI res = (InternalURI) targetObject;
		List objList = getRepoObjectPermissions(context, RESOURCE_URI_PREFIX + res.getPath(), recipient);
		return makeObjectPermissionClientList(objList);
	}


    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#isObjectAdministrable(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean isObjectAdministrable(ExecutionContext context, Object targetObject) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((targetObject == null) || (auth == null)) {
            return true;
        }
        JasperServerSidRetrievalStrategyImpl localStrategy =  new JasperServerSidRetrievalStrategyImpl();
        List<Sid> sids = localStrategy.getSids(auth);
        List objectPermissions = new ArrayList();
        InternalURI targetURI=null;
        if (targetObject instanceof String) {
            String path = (String) targetObject;

            targetURI = new InternalURIDefinition(PermissionUriProtocol.removePrefix(path),
					PermissionUriProtocol.getProtocol(path));
        } else if (targetObject instanceof InternalURI) {
            targetURI =(InternalURI) targetObject;
        }

		String protocol = targetURI != null ? targetURI.getProtocol() : null;
		if (protocol != null && protocol.equals(PermissionUriProtocol.ATTRIBUTE.toString())) {
			return isAttributeAdministrable(context, targetURI.getPath());
		}

        if (targetURI!=null) {
            // Look if it is really a resouce...
            Resource res = ((RepositoryUnsecure)getRepositoryService()).getResourceLookupUnsecure(null, "repo:" + targetURI.getPath());
            if (res == null) {
                res = ((RepositoryUnsecure)getRepositoryService()).getFolderUnsecure(null, "repo:" + targetURI.getPath());
            }
            if (res == null)
            {
                if (logger.isDebugEnabled()) {
                    logger.debug("Did not find resource, switching to parent");
                }
                // Look directly to the parent...
                targetURI = new InternalURIDefinition(getParentURI(targetURI.getPath()));
            }

            while (targetURI.getPath()!=null) {
                objectPermissions.addAll(getObjectPermissionsForObject(context,targetURI));
                targetURI = new InternalURIDefinition(targetURI.getParentPath());
            }
        } else {
            objectPermissions = getObjectPermissionsForObject(context,targetObject);
        }

        for(Object obj:objectPermissions) {
            if (obj instanceof ObjectPermission) {
                Sid permissionSid = localStrategy.getSid(((ObjectPermission) obj).getPermissionRecipient());
                if (sids.contains(permissionSid)) {
                    if (JasperServerPermission.ADMINISTRATION.getMask()==((ObjectPermission) obj).getPermissionMask()) {
                        return true;
                    } else {
                        // Remove current SID from future checks - because we already found that this Sid don`t have Admin access
                        sids.remove(permissionSid);
                    }
                }
            }
        }
        return false;
    }

	public boolean isAttributeAdministrable(ExecutionContext context, String attributePath) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String checkPermissionUri = attributePathTransformer.transformPath(attributePath, authentication);
		SidRetrievalStrategy sidStrategy = new JasperServerSidRetrievalStrategyImpl();

		Arrays.asList(JasperServerPermission.ADMINISTRATION);
		List<Sid> checkSids = sidStrategy.getSids(authentication);

		Acl acl;
		try {
			acl = aclService.readAclById(new InternalURIDefinition(checkPermissionUri,
					PermissionUriProtocol.ATTRIBUTE), checkSids);
		} catch (JSException e) {
			// in some cases we are trying to reach not reachable resource, this will throw error
			acl = null;
		}

		return acl != null && acl.isGranted(Arrays.asList(JasperServerPermission.ADMINISTRATION), checkSids, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List makeObjectPermissionClientList(List objList) {
		List resultList = new ArrayList(objList.size());
		for (RepoObjectPermission repoPerm : (List<RepoObjectPermission>)objList) {
			ObjectPermission clientPermission = (ObjectPermission) repoPerm.toClient(getObjectMappingFactory());
			resultList.add(clientPermission);
		}
		return resultList;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver#getPersistentObject(java.lang.Object)
	 */
	public Object getPersistentObject(Object clientObject) {
		// If already persisted, just return it
		if (clientObject == null) {
			return null;
		} else	if (clientObject instanceof IdedObject) {
			return clientObject;
		} else if (clientObject instanceof Role || clientObject instanceof User) {
			return ((PersistentObjectResolver) userService).getPersistentObject(clientObject);
		} else if (clientObject instanceof Resource) {
			// TODO Hack! Make it an interface!
			String uri = ((Resource) clientObject).getPath();
			return repoService.findByURI(RepoResource.class, uri, false);
		} else if (clientObject instanceof ObjectPermission) {
			return getRepoObjectPermission(null, (ObjectPermission) clientObject);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getExternalReference(java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
		return ((ReferenceResolver) repoService).getExternalReference(uri, persistentReferenceClass);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getReference(com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource, com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public RepoResource getReference(RepoResource owner, Resource resource, Class persistentReferenceClass) {
		return ((ReferenceResolver) repoService).getReference(owner, resource, persistentReferenceClass);
	}

	protected String repositoryURI(String repositoryPath) {
		return PermissionUriProtocol.addDefaultPrefixIfNotExist(repositoryPath);
	}

	protected void clearAclEntriesCache(String uri) {
		InternalURIDefinition objId = new InternalURIDefinition(uri);
		if (log.isDebugEnabled()) {
			log.debug("Removing " + objId + " from permissions");
		}
		if (nonMutableAclCache!=null) {
			nonMutableAclCache.evictFromCache(objId);
		}
	}
	
	private Acl getCached(String uri){
		ObjectPermissionServiceImpl.URIObjectIdentity objId = new URIObjectIdentity(uri);
		Acl existingAcl = null;
		if (nonMutableAclCache!=null) {
			existingAcl = nonMutableAclCache.getFromCache(objId);
		}
		return existingAcl;
	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateObjectPermissionRepositoryPath(String oldPath, String newPath) {
		String oldURI = repositoryURI(oldPath);
		String newURI = repositoryURI(newPath);

		List permissions = getRepoObjectPermissions(null, oldURI, null);
		if (permissions != null) {
			for (RepoObjectPermission permission : (List<RepoObjectPermission>)permissions) {
				permission.setURI(newURI);
				getHibernateTemplate().update(permission);

				if (log.isDebugEnabled()) {
					log.debug("Updated URI of permission for " + oldURI + " and " + permission.getPermissionRecipient()
							+ " to " + newURI);
				}
			}
		}

		clearAclEntriesCache(oldURI);
	}

	@SuppressWarnings("rawtypes")
	public int getInheritedObjectPermissionMask(ExecutionContext context,
			Object targetObject, Object recipient) {
		if (!(targetObject instanceof InternalURI)) {
			if (log.isDebugEnabled()) {
				log.debug("Requested inherited permission non InternalURI object "
						+ targetObject);
			}

			return 0;
		}

		InternalURI resource = (InternalURI) targetObject;
		int permissionMask = 0;

		String folderURI = getParentURI(resource.getPath());
		while (folderURI != null) {
			List permissions = getRepoObjectPermissions(context,
					folderURI, recipient);
			if (permissions != null && !permissions.isEmpty()) {
				RepoObjectPermission permissionObject =
					(RepoObjectPermission) permissions.get(0);
				permissionMask = permissionObject.getPermissionMask();
				break;
			}

			folderURI = getParentURI(folderURI);
		}

		if (log.isDebugEnabled()) {
			if (folderURI == null) {
				log.debug("No inherited permission found for object "
						+ resource.getPath() + " and recipient " + recipient);
			} else {
				log.debug("Inherited permission " + permissionMask + " found at "
						+ folderURI + " for object " + resource.getPath()
						+ " and recipient " + recipient);
			}
		}

		return permissionMask;
	}

    public void setNonMutableAclCache(NonMutableAclCache nonMutableAclCache) {
        this.nonMutableAclCache = nonMutableAclCache;
    }

    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<ObjectPermission> getObjectPermissionsForSubtree(final String uri) {
		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);
		// Select on identity
		final String queryString = "from " + objPermissionClassName + " as objPermission " +
		"where objPermission.URI like ?";

		List<ObjectPermission>objList = getHibernateTemplate()
			.execute(new HibernateCallback<List<ObjectPermission>>() {
				public List<ObjectPermission> doInHibernate(Session session) throws HibernateException {
					return session.createQuery(queryString)
						.setParameter(0, uri+'%', Hibernate.STRING)
						.setCacheable(true)
						.list();
				}
			}
        );
		return objList;
	}

	@Override
	public Acl getFromCache(final ObjectIdentity oId) {
		if(nonMutableAclCache==null || oId == null){
			return null;
		} else {
			return nonMutableAclCache.getFromCache(oId);
		}
	}

	@Override
	public Acl putInCache(final Acl acl) {
		if(nonMutableAclCache!=null && acl!=null){
			nonMutableAclCache.putInCache(acl);
		}
		return acl;
	}
}
