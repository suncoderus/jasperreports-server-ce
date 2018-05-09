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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.security.EhCacheBasedJasperServerAclCache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoObjectPermission;

import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;

/**
 * this interceptor is put in the chain for the repo service
 * it should be between the security interceptor and the actual repo service, 
 * so that the return value of getSubFolders() gets processed in this order:
 * - base repo service returns list of folders
 * - this guy gets called, prepopulates acl cache
 * - security interceptor gets called
 * - obj permission service gets called to get acl's 
 * - lo and behold, iterms are found in cache
 * @author bob
 *
 */

public class PermissionsPrefetcher extends HibernateDaoImpl implements MethodInterceptor {
	private static final Log log = LogFactory.getLog(PermissionsPrefetcher.class);
	private int minimumPrefetch = 10;
	private AclService repositoryAclService;
    private EhCacheBasedJasperServerAclCache aclCache;
    private JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy;
	private ResourceFactory objectMappingFactory;
	private ResourceFactory persistentClassFactory;

	private interface Interceptor{
		Object process(final MethodInvocation call) throws Throwable;
	}
	
	final Map<String, Interceptor> wiredCalls = new HashMap<String, Interceptor>(){
		private static final long serialVersionUID = 1L;
		{
			put("getSubFolders", new Interceptor() {
				@Override
				public Object process(MethodInvocation call) throws Throwable {
					List<?> subFolderList = (List<?>) call.proceed();
					String folderURI = (String) call.getArguments()[1];
					preloadSubFolderPerms(folderURI, subFolderList, true);
					return subFolderList;
				}
			});
			
			put("folderExists", new Interceptor() {
				@Override
				public Object process(MethodInvocation call) throws Throwable {
					Object result = call.proceed();
					if(Boolean.class.isAssignableFrom(result.getClass())){
						String folderURI = call.getArguments()[1].toString();
						if (!folderURI.equals("/") && ((Boolean)result).booleanValue()){
							List<?> subfolders = getSubfolders(folderURI);
							if(!subfolders.isEmpty()){
								preloadSubFolderPerms(folderURI, subfolders, true);
							}
						}
					}
					return result;
				}
			});
		}
	};
	
	public Object invoke(MethodInvocation call) throws Throwable {
		Interceptor callback = wiredCalls.get(call.getMethod().getName());
		if(callback!=null){
			// log.error("****** PREFETCHER running for ********: " + AopUtils.getTargetClass(call.getThis()).getName() + ":" + call.getMethod().getName() + "");
			return callback.process(call);
		} else {
//			log.error("****** PREFETCHER skipped for ********: " + AopUtils.getTargetClass(call.getThis()).getName() + ":" + call.getMethod().getName() + "");
//			int i = 0;
//			for(Object o: call.getArguments()){
//				log.error("****** PREFETCHERARGS - args["+i+"]:" + (o==null?"null":o.toString()));
//				i++;
//			}
			return call.proceed();
		}
	}
	
	private List<?> getSubfolders(String folderURI){
		final String folderClassName = getPersistentClassFactory().getImplementationClassName(Folder.class);		
		final String queryString = "select f.subFolders from " + folderClassName + " f " + 
			" where f.URI = :uri ";
		
		HibernateTemplate template = getHibernateTemplate();
		template.setCacheQueries(true);
		@SuppressWarnings("unchecked")
		List<RepoFolder> sqlList = (List<RepoFolder>)template.findByNamedParam(queryString, "uri", folderURI);
		if(sqlList!=null && !sqlList.isEmpty()){
			List<Folder> result = new ArrayList<Folder>(sqlList.size());
			for(RepoFolder f: sqlList){
				if(f!=null){
					result.add(f.toClient());
				}
			}
			return result;
		}
		return Collections.EMPTY_LIST;
	}
	
	@SuppressWarnings("rawtypes")
	private void preloadSubFolderPerms(String folderURI, List<?> subFolderList, boolean missingOnly) {
		// don't prefetch root
		if (folderURI.startsWith(PermissionUriProtocol.DOBLE_FOLDER_SEPARATOR)) {
			log.warn("PermissionPrefetcher: incorrect URI detected !!!");
			Thread.dumpStack();
		}
		if (folderURI.equals("/")) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("Prefetching permissions for folder:".concat(folderURI));
		}
		if (ImportRunMonitor.isImportRun()) {
			// if import is running and permission prefetcher was called - this can un-sync permission cache
			log.error("PermissionPrefetcher should not be called during Import operation !!!");
			Thread.dumpStack();
			return;
		}

		Acl parentAcl = aclCache.getFromCache(new InternalURIDefinition(folderURI));
		if (parentAcl == null) {
			parentAcl = repositoryAclService.readAclById(new InternalURIDefinition(folderURI));
		}

		Set<String> missingEntries = new HashSet<String>(subFolderList.isEmpty()?1:subFolderList.size());
		if(missingOnly){
			for (Object subFolderObj : subFolderList) {
				Folder subFolder = (Folder) subFolderObj;
				String uri = subFolder.getURI();
				if (aclCache.getFromCache(new InternalURIDefinition(uri)) == null) {
					missingEntries.add(uri);
				}
			}
				
			if (missingEntries.size() < minimumPrefetch) {
				// log.error("****** PREFETCHER minimum not met: ********: " + missingEntries.size() +"<" + minimumPrefetch);
				return;
			}
		}
		
		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);

		final String fixedFolderURI = folderURI.endsWith("/")? folderURI: folderURI + "/";
		final String queryString = "from " + objPermissionClassName + 
			" where URI like :parent " + 
            " and URI not like :level";

		List<?> permList = getHibernateTemplate().execute(new HibernateCallback<List>() {
            @SuppressWarnings("unchecked")
			public List doInHibernate(Session session) throws HibernateException {
                List<ObjectPermission> perms = session.createQuery(queryString)
                		.setParameter("parent",  "repo:" + fixedFolderURI + "%")
                		.setParameter("level", "repo:" + fixedFolderURI + "%/%" )
                		.setCacheable(true)
                		.list();
                for (ObjectPermission perm : perms){
                    IdedObject recepient = (IdedObject)perm.getPermissionRecipient();
                    recepient.toClient(objectMappingFactory); // initialize all fields while it persistent
                }
                Collections.sort(perms, new Comparator<ObjectPermission>() {
					@Override
					public int compare(ObjectPermission o1, ObjectPermission o2) {
						return o1.getURI().compareTo(o2.getURI());
					}
				} );
                return perms;
            }
        });
		
		String currentURI = null;
		List<AccessControlEntry> permsForURI = new ArrayList<AccessControlEntry>();
        Acl tempAcl=new JasperServerAclImpl(new InternalURIDefinition(""),null);
		for (Object permObj : permList) {
			RepoObjectPermission perm = (RepoObjectPermission) permObj;
			// only handle perms that match one of the returned folders
			// which is not cached yet
			if (missingOnly && ! missingEntries.contains(perm.getURI())) {
				continue;
			}
			// we are sorting by uri; if we've hit a new uri, then we need to 
			// put the previously gathered up perms for the last uri into the cache
			if (! perm.getURI().equals(currentURI)) {
                Acl acl = new JasperServerAclImpl(new InternalURIDefinition(currentURI),permsForURI,parentAcl);
                aclCache.putInCache(acl);
				if (log.isDebugEnabled()) {
					log.debug("adding acl to cache:".concat(acl.toString()));
				}
				permsForURI.clear();
				// remove old version from missingEntries
				if(missingOnly){
					missingEntries.remove(currentURI);
				}
				// set currentURI to new value
				currentURI = perm.getURI();
			}
			// create aclEntry from the repo perm object, and save it in list
			ObjectPermission clientPermission = (ObjectPermission) perm.toClient(objectMappingFactory);
			AccessControlEntry aclEntry = new AccessControlEntryImpl(null,tempAcl,sidRetrievalStrategy.getSid(clientPermission.getPermissionRecipient()), new JasperServerPermission(clientPermission.getPermissionMask()),true,false,false);
			permsForURI.add(aclEntry);
		}
		// put any perms that haven't been put yet 
		if (permsForURI.size() > 0) {
            Acl acl = new JasperServerAclImpl(new InternalURIDefinition(currentURI),permsForURI,parentAcl);
            aclCache.putInCache(acl);
			if (log.isDebugEnabled()) {
				log.debug("adding last acl to cache:".concat(acl.toString()));
			}
			if(missingOnly){
				missingEntries.remove(currentURI);
			}
		}
		// put in empty perms entry for folders in returned list that don't have any perms records
		for (String uriWithNoPerms : missingEntries) {
            Acl acl = new JasperServerAclImpl(new InternalURIDefinition(uriWithNoPerms),new ArrayList<AccessControlEntry>(),parentAcl);
            aclCache.putInCache(acl);
			if (log.isDebugEnabled()) {
				log.debug("adding empty acl to cache:".concat(acl.toString()));
			}
		}
	}

	public void setMinimumPrefetch(int minimumPrefetch) {
		this.minimumPrefetch = minimumPrefetch;
	}

	public int getMinimumPrefetch() {
		return minimumPrefetch;
	}

	public void setObjectMappingFactory(ResourceFactory objectFactory) {
		this.objectMappingFactory = objectFactory;
	}

	public ResourceFactory getObjectMappingFactory() {
		return objectMappingFactory;
	}

	public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
		this.persistentClassFactory = persistentClassFactory;
	}

	public ResourceFactory getPersistentClassFactory() {
		return persistentClassFactory;
	}

    public void setAclCache(EhCacheBasedJasperServerAclCache aclCache) {
        this.aclCache = aclCache;
    }

    public void setSidRetrievalStrategy(JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }

	public void setRepositoryAclService(AclService repositoryAclService) {
		this.repositoryAclService = repositoryAclService;
	}
}
