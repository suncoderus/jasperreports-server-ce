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

import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Oleg.Gavavka
 */
public class RepositoryAclServiceImpl implements AclService {
    protected static final Log log = LogFactory.getLog(RepositoryAclServiceImpl.class);

    public static final String RECIPIENT_USED_FOR_INHERITANCE_MARKER = "___INHERITANCE_MARKER_ONLY___";

    protected static final String RESOURCE_URI_PREFIX = Resource.URI_PROTOCOL + ":";
    protected static final int RESOURCE_URI_PREFIX_LENGTH = RESOURCE_URI_PREFIX.length();
    protected static final String ROOT_FOlDER=RESOURCE_URI_PREFIX.concat(Folder.SEPARATOR);
    static String RECIPIENT_FOR_CACHE_EMPTY = "RESERVED_RECIPIENT_NOBODY";

    private ObjectPermissionService permissionService;
    private JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy;
    private AclService aclLookupStrategy;
    
//    private boolean grantedSuperUser(){
//    	try{
//        	List<Sid> sids = sidRetrievalStrategy.getSids(SecurityContextHolder.getContext().getAuthentication());
//        	for(Sid s:sids){
//        		if( s instanceof GrantedAuthoritySid){
//        			if(((GrantedAuthoritySid)s).getGrantedAuthority().equals("ROLE_SUPERUSER")){
//        				return false; // TODO: RETURN TRUE!!!! EGS
//        			}
//        		}
//        	}
//        	return false;
//        } catch(Exception e){
//        	return false;
//        }
//        
//    }

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        return null;  
    }
    
    private Acl unwindObjectPermissions(final InternalURI uri, final Collection<ObjectPermission> permissions, final Object[] existingAcl){
    	Acl result = null;
        String existingAclsURI = null;
        
        if(existingAcl != null && existingAcl.length==2){
        		existingAclsURI = (String)existingAcl[0];
        		result = (Acl)existingAcl[1];
        }

    	// sorted uri -> permissions map to store direct permissions per level
    	Map<String, List<AccessControlEntry>> directAces = new TreeMap<String, List<AccessControlEntry>>();
        PermissionUriProtocol permissionsURIProtocol = PermissionUriProtocol.fromString(uri.getProtocol());

        // prepare map:
        InternalURI tempUri = uri;
        for(String parentPath = tempUri.getParentPath(); parentPath!=null; parentPath = tempUri.getParentPath()){
        	if(tempUri.getURI().equals(existingAclsURI)){
        		break;
        	} 
        	if(log.isDebugEnabled()){
        		log.debug("=====>>>> producing the following chain: ==> " + uri.getPath() + ": ==> " + tempUri.getURI());
        	}
        	directAces.put(tempUri.getURI(), new ArrayList<AccessControlEntry>());
        	tempUri = new InternalURIDefinition(parentPath, PermissionUriProtocol.fromString(tempUri.getProtocol()));
        }
        
        if(permissions != null){
	       	int size = permissions.size();
           	// break down all permissions by levels: uri -> list of permissions 
	        for(ObjectPermission op: permissions) {
	            if (log.isDebugEnabled()) {
	                log.debug("ObjectPermission: "+op.toString());
	            }
	            String currentURI = op.getURI();
	            List<AccessControlEntry> aces = directAces.get(currentURI);
	            if(aces == null){
	            	if(log.isDebugEnabled()){
	            		log.debug(" *********** MISSING URI ******** : " + currentURI);
	            	}
	            	aces = new ArrayList<AccessControlEntry>(size);
	            	directAces.put(currentURI,aces);
	            }
	            
	            JasperServerPermission permission = new JasperServerPermission(op.getPermissionMask());
	            Object recipient = op.getPermissionRecipient();
	            if(log.isDebugEnabled()){
	            	log.debug("processing permission:"+permission.toString()+" for "+recipient.toString());
	            }
	            Sid sid = sidRetrievalStrategy.getSid(recipient);
	        	AccessControlEntry accessControlEntry = new AccessControlEntryImpl(null, new JasperServerAclImpl(new InternalURIDefinition(currentURI, permissionsURIProtocol),null),sid ,permission,true,false,false);
	        	aces.add(accessControlEntry);
	        }
        }
        
        // go by levels bottom-> top (parent -> child) and construct final uri
        for(Map.Entry<String, List<AccessControlEntry>> entry: directAces.entrySet()){
        	if(log.isDebugEnabled()){
        		log.debug("*********final assembly******** creating new result for " + entry.getKey() + ": parent will be " + (result==null?"<null>":result.toString()));
        	}
    		result = new JasperServerAclImpl(
    			new InternalURIDefinition(entry.getKey(), permissionsURIProtocol),
    			entry.getValue(),
    			result
    		);
        }
       
        return result;
    }
    
	
 	
//	public Acl compare_readAclById(ObjectIdentity object) throws NotFoundException {
// 		Acl result = null;
// 		long startnew = System.currentTimeMillis();
//    	result = readAclById(object); // false = use new method
//    	long endnew = System.currentTimeMillis();
// 		result = null;
// 		long start=System.currentTimeMillis();
// 		result = _old_readAclById(object);
// 		long end = System.currentTimeMillis();
//    	log.error("****** READBYID ("+object.getIdentifier().toString() +") ******: " + (end-start) + "ms / " + (endnew-startnew) + "ms - saved " + (end-start - (endnew-startnew)) + "ms");
//    	return result;
//    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Acl readAclById(ObjectIdentity object) throws NotFoundException {
		String identifier = object.getIdentifier().toString();
        if (identifier.contains(PermissionUriProtocol.DOBLE_FOLDER_SEPARATOR)) {
            log.warn("RepositoryACLService: incorrect URI detected !!!");
            Thread.dumpStack();
        }

        InternalURI uri = object instanceof InternalURI && !identifier.contains(PermissionUriProtocol.DOBLE_FOLDER_SEPARATOR) ? (InternalURI) object : new InternalURIDefinition(identifier);
        // cut "repo:" or "attr:" from internalURI, because getObjectPermissionsForObject always adds "repo:" to uri
        
        for (PermissionUriProtocol protocol : PermissionUriProtocol.values()) {
            if (uri.getPath().startsWith(protocol.getProtocolPrefix())) {
                uri = new InternalURIDefinition(PermissionUriProtocol.removePrefix(uri.getPath()),
                        PermissionUriProtocol.getProtocol(uri.getPath()));
            }
        }
        
        if(!uri.getProtocol().startsWith("repo")){
        	return _old_readAclById(object);
//        	return readAclById(object);
        }

        //temporary ACL to create ACE
        Acl result = new JasperServerAclImpl(uri,null);
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        // Special case for Import
        if (isImportRunning()) {
            Sid sid = sidRetrievalStrategy.getSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            aces.add(new AccessControlEntryImpl(null,result,sid,JasperServerPermission.ADMINISTRATION,true,false,false));
            return new JasperServerAclImpl(uri,aces,true);
        }

        Object[] existingAcl = new Object[2];
        List objectPermisions = permissionService.getObjectPermissionsForObject(null,uri, existingAcl);
        result = unwindObjectPermissions(uri, (Collection<ObjectPermission>)objectPermisions, existingAcl);
        if(result == null){
            result = new JasperServerAclImpl(uri,aces);
        }

        validateAcl(result);
        return result;
    }

    
    @SuppressWarnings("rawtypes")
	private Acl _old_readAclById(ObjectIdentity object) throws NotFoundException {
    	if(log.isDebugEnabled()){
    		log.debug("+++++++++++ OLD READING ACL FOR " + object.toString());
    	}
    	String identifier = object.getIdentifier().toString();
        InternalURI uri = object instanceof InternalURI ? (InternalURI) object : new InternalURIDefinition(identifier);
        // cut "repo:" or "attr:" from internalURI, because getObjectPermissionsForObject always adds "repo:" to uri
        for (PermissionUriProtocol protocol : PermissionUriProtocol.values()) {
            if (uri.getPath().startsWith(protocol.getProtocolPrefix())) {
                uri = new InternalURIDefinition(PermissionUriProtocol.removePrefix(uri.getPath()),
                        PermissionUriProtocol.getProtocol(uri.getPath()));
            }
        }

        if(log.isDebugEnabled()){
        	log.debug("trying to find ACL by Object for: "+ identifier);
        }
        //temporary ACL to create ACE
        Acl result = new JasperServerAclImpl(uri,null);
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        // Special case for Import
        if (isImportRunning()) {
            Sid sid = sidRetrievalStrategy.getSid(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            aces.add(new AccessControlEntryImpl(null,result,sid,JasperServerPermission.ADMINISTRATION,true,false,false));
            return new JasperServerAclImpl(uri,aces,true);
        }

        if (uri!=null) {
            List objectPermisions = permissionService.getObjectPermissionsForObject(null,uri);
            if(log.isDebugEnabled()){
            	log.debug("get objectPermissions, count: "+objectPermisions.size()+" for URI "+uri.getURI());
            }

            for(Object op: objectPermisions) {
                if (op instanceof ObjectPermission) {
                    if (log.isDebugEnabled()) {
                        log.debug("ObjectPermission: "+op.toString());
                    }
                    JasperServerPermission permission = new JasperServerPermission(((ObjectPermission) op).getPermissionMask());
                    Object recipient = ((ObjectPermission) op).getPermissionRecipient();
                    if(log.isDebugEnabled()){
                    	log.debug("processing permission:"+permission.toString()+" for "+recipient.toString());
                    }
                    Sid sid = sidRetrievalStrategy.getSid(recipient);
                    AccessControlEntry accessControlEntry = new AccessControlEntryImpl(null,result,sid ,permission,true,false,false);
                    aces.add(accessControlEntry);
                }
            }
        }

        String parentPath = uri.getParentPath();
        Acl parentAcl=null;
        if (parentPath!=null) {
            InternalURI parentUri = new InternalURIDefinition(parentPath,
                    PermissionUriProtocol.fromString(uri.getProtocol()));
        	parentAcl= getAclLookupStrategy().readAclById(parentUri);
        } else if(log.isDebugEnabled()){
            log.debug("No parent found for URI "+uri.getURI());
        }
        result = new JasperServerAclImpl(uri,aces,parentAcl);
        if(log.isDebugEnabled()){
        	log.debug("returning ACL: "+result.toString());
        }

        return result;
    }    
    
    
    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> incomeSids) throws NotFoundException {
        List<Sid> sids = incomeSids != null ? incomeSids : new ArrayList<Sid>();
        if(log.isDebugEnabled()){
        	log.debug("trying to find ACL by Object and List<Sid>, Object:"+ object.getIdentifier().toString() );
        	log.debug("Sid list:"+ sids.toString());
        }
        Acl result = getAclLookupStrategy().readAclById(object);
        return result;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
        for(ObjectIdentity oid: objects) {
            result.put(oid,getAclLookupStrategy().readAclById(oid));
        }

        return result;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
        for(ObjectIdentity oid: objects) {
            result.put(oid,getAclLookupStrategy().readAclById(oid,sids));
        }

        return result;
    }

    public void setPermissionService(ObjectPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setSidRetrievalStrategy(JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }

    public void setAclLookupStrategy(AclService aclLookupStrategy) {
        this.aclLookupStrategy = aclLookupStrategy;
    }

    public AclService getAclLookupStrategy() {
        // if we don`t have AclLookupStrategy use this bean
        return aclLookupStrategy!=null ? aclLookupStrategy: this;
    }
    private Boolean isImportRunning() {
        if (ImportRunMonitor.isImportRun()){
            //Add special permission for current user
            //to allow updating special resources like themes etc
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (GrantedAuthority authority : authentication.getAuthorities()){
                if (authority.getAuthority().equals(ObjectPermissionService.PRIVILEGED_OPERATION)){
                    return true;
                }
            }
        }
        return false;
    }

    // Validation is required because any change in Permission cache behaviour can make ACL invalid.
    private void validateAcl(Acl acl) {
        if (isImportRunning()) {
            return;
        }
        if (acl.getObjectIdentity() instanceof InternalURIDefinition) {
            InternalURIDefinition uri = (InternalURIDefinition) acl.getObjectIdentity();
            if (PermissionUriProtocol.getProtocol(uri.getURI()).equals(PermissionUriProtocol.RESOURCE)) {
                // check if parentAcl is there, if not - fail
                Acl validatedAcl = acl;
                while (validatedAcl.getParentAcl()!=null) {
                    validatedAcl=validatedAcl.getParentAcl();
                }
                uri= (InternalURIDefinition) validatedAcl.getObjectIdentity();
                if (!uri.getURI().equals(ROOT_FOlDER)) {
                    throw new NotFoundException("ACL should have parent !!!");
                }
            }
        }
    }
}
