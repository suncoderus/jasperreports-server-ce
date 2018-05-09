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

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.properties.Log4jPropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.PropertyChanger;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.tenant.service.TenantPersistenceResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.*;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Manages attributes for principals - Users, Roles.
 *
 * @author sbirney
 * @version $Id$
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ProfileAttributeServiceImpl extends HibernateDaoImpl
        implements ProfileAttributeService, PersistentObjectResolver, ApplicationContextAware, ProfileAttributeServiceInternal {

    protected static final Log log = LogFactory.getLog(ProfileAttributeServiceImpl.class);
    private HibernateRepositoryService repoService;
    private UserAuthorityService userService;
    private TenantPersistenceResolver tenantPersistenceResolver;
    private List<ProfileAttributeCategory> profileAttributeCategories;
    private Map<String, String> changers;
    private Map<String, PropertyChanger> changerObjects;
    private ResourceFactory objectFactory;
    private ResourceFactory persistentClassFactory;
    private ApplicationContext applicationContext;
    private List<ProfileAttributeEventListener> listeners;

    public ProfileAttributeCache getProfileAttributeCache() {

        return profileAttributeCache;
    }

    public void setProfileAttributeCache(ProfileAttributeCache profileAttributeCache) {
        this.profileAttributeCache = profileAttributeCache;
    }

    private ProfileAttributeCache profileAttributeCache;

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

    /**
     * @return The tenant persistence resolver service
     */
    public TenantPersistenceResolver getTenantPersistenceResolver() {
        return tenantPersistenceResolver;
    }

    /**
     * @param tenantPersistenceResolver The tenant persistence resolver service
     */
    public void setTenantPersistenceResolver(TenantPersistenceResolver tenantPersistenceResolver) {
        this.tenantPersistenceResolver = tenantPersistenceResolver;
    }

    /**
     * @param profileAttributeCategories An ordered List of profile attribute categories to set
     */
    public void setProfileAttributeCategories(List<ProfileAttributeCategory> profileAttributeCategories) {
        this.profileAttributeCategories = profileAttributeCategories;
    }

    /**
     * @param changers A Map of property changers
     */
    public void setChangers(Map<String, String> changers) {
        this.changers = changers;
    }

    public void setListeners(List<ProfileAttributeEventListener> listeners) {
        this.listeners = listeners;
    }

    public Map<String, PropertyChanger> getChangerObjects() {
        // This code looks up spring context for changer beans based on this name
        // We are wiring the service with changer names and not changer beans to eliminate circular references.
        if (changerObjects == null) {
            log.debug("Collecting all changers from Spring context");
            changerObjects = new HashMap<String, PropertyChanger>();
            for (Map.Entry<String, String> e : changers.entrySet()) {
                changerObjects.put(e.getKey(), (PropertyChanger) applicationContext.getBean(e.getValue()));
            }
        }

        return changerObjects;
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProfileAttribute newProfileAttribute(ExecutionContext context) {
        return (ProfileAttribute) getObjectMappingFactory().newObject(ProfileAttribute.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void putProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        RepoProfileAttribute existingAttr = getRepoProfileAttribute(attr);
        final boolean update = existingAttr != null;

        //Disallow to override server settings at lower relatively server level.
        if (!isServerPrincipal(attr.getPrincipal())) {
            List<ProfileAttribute> attributes =
                    this.getDefaultServerProfileAttributes(context,
                            new AttributesSearchCriteria.Builder()
                                    .setNames(Collections.singleton(attr.getAttrName())).build(), false);
            if (attributes.size() > 0) {
                throw new JSDuplicateResourceException("The attribute name is reserved for use at the server level." +
                        " Choose a different name.");
            }
        }

        if (existingAttr == null) {
            existingAttr = (RepoProfileAttribute) getPersistentClassFactory().newObject(ProfileAttribute.class);
        }

        final String existingValue = existingAttr.getAttrValue();

        existingAttr.copyFromClient(attr, this);
        getHibernateTemplate().saveOrUpdate(existingAttr);
        getProfileAttributeCache().clearAll();

        if (isServerPrincipal(attr.getPrincipal())) {
            applyServerSetting(attr);
        }

        if (listeners != null) {
            for (ProfileAttributeEventListener listener : listeners) {
                if (update) {
                    listener.onUpdate(attr, existingValue);
                } else {
                    listener.onCreate(attr);
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        RepoProfileAttribute existingAttr = getRepoProfileAttribute(attr);
        if (existingAttr != null) {
            getHibernateTemplate().delete(existingAttr);
        }
        if (isServerPrincipal(attr.getPrincipal())) {
            removeServerAttribute(attr);
        }
        getProfileAttributeCache().clearAll();

        if (listeners != null && existingAttr != null) {
            for (ProfileAttributeEventListener listener : listeners) {
                listener.onDelete(attr);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttributes(Collection<ProfileAttribute> attributesToDelete) {
        Map<String, ProfileAttribute> paMapToDel = new HashMap<String, ProfileAttribute>(attributesToDelete.size());
        for (ProfileAttribute profileAttribute : attributesToDelete) {
            paMapToDel.put(profileAttribute.getAttrName(), profileAttribute);
        }
        deleteProfileAttributes(paMapToDel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttributes(Map<String, ProfileAttribute> attributeMapToDelete) {
        List<RepoProfileAttribute> paListToDelete = new ArrayList<RepoProfileAttribute>();
        List<RepoProfileAttribute> paList = getRepoProfileAttributes(getCurrentUserDetails());
        for (RepoProfileAttribute profileAttribute : paList) {
            ProfileAttribute attributeToDelete = attributeMapToDelete.get(profileAttribute.getAttrName());
            if (attributeToDelete != null) {
                paListToDelete.add(profileAttribute);

                if (listeners != null) {
                    for (ProfileAttributeEventListener listener : listeners) {
                        listener.onDelete(attributeToDelete);
                    }
                }
            }
        }
        if (!paListToDelete.isEmpty()) {
            getHibernateTemplate().deleteAll(paListToDelete);
            getProfileAttributeCache().clearAll();
            for (RepoProfileAttribute profileAttribute : paListToDelete) {
                if (isServerPrincipal(profileAttribute.getPrincipal())) {
                    removeServerAttribute(attributeMapToDelete.get(profileAttribute.getAttrName()));
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttributesForRecipient(ExecutionContext context, ObjectRecipientIdentity recipientIdentity) {
        if (log.isDebugEnabled()) {
            log.debug("Deleting object permissions for recipient " + recipientIdentity);
        }

        List attributes = getRepoProfileAttributes(recipientIdentity.getRecipientClass(),
                Collections.singleton(recipientIdentity.getId()), null);
        getHibernateTemplate().deleteAll(attributes);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProfileAttribute getProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        // Given the object and the recipient, find the permission
        RepoProfileAttribute existingPerm = getRepoProfileAttribute(attr);
        if (existingPerm != null) {
            ProfileAttribute profileAttribute = (ProfileAttribute) existingPerm.toClient(getObjectMappingFactory());
            profileAttribute.setGroup(getChangerName(attr.getAttrName()));
            return profileAttribute;
        } else if (isServerPrincipal(attr.getPrincipal())) {
            return getServerAttribute(context, attr);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ProfileAttribute> getCurrentUserProfileAttributes(ExecutionContext executionContext, ProfileAttributeCategory category) {
        Object principal;
        boolean effectiveAttributes = false;

        ProfileAttributeCacheKey key = new ProfileAttributeCacheKey();
        key.setProfileAttributeCategory(category);
        key.setCurrentLoggedInUser(getCurrentUser());

        List<ProfileAttribute> cachedAttr = (List<ProfileAttribute>) profileAttributeCache.getItem(key);
        if (cachedAttr != null) {
            return cachedAttr;
        }

        // Return an empty profile attributes for Anonymous user for any category
        if (ProfileAttributeCategory.USER.getPrincipal(getObjectMappingFactory()) == null) {
            return new ArrayList<ProfileAttribute>();
        }

        if (category != null && category != ProfileAttributeCategory.HIERARCHICAL) {
            if (!profileAttributeCategories.contains(category)) {
                throw new IllegalArgumentException("Provided \"" + category + "\" category is not supported");
            }
            principal = category.getPrincipal(getObjectMappingFactory());
        } else {
            principal = ProfileAttributeCategory.USER.getPrincipal(getObjectMappingFactory());
            effectiveAttributes = true;
        }

        if (principal != null) {
            AttributesSearchCriteria searchCriteria =
                    new AttributesSearchCriteria.Builder().setEffective(effectiveAttributes).build();
            List<ProfileAttribute> result =  getProfileAttributesForPrincipal(executionContext,
                    principal, searchCriteria).getList();
            profileAttributeCache.addItem(key, result);

            return result;
        }

        return new ArrayList<ProfileAttribute>();
    }

    private RepoProfileAttribute getRepoProfileAttribute(ProfileAttribute attr) {
        List objList = getRepoProfileAttributes(attr.getPrincipal(), new String[]{attr.getAttrName()});
        return (objList.size() == 0) ? null : (RepoProfileAttribute) objList.get(0);
    }

    private Class<?> getObjectClass(Class<?> classObj) {
        if (classObj.toString().indexOf("_$$_") > 0) return classObj.getSuperclass();
        return classObj;
    }

    private List<RepoProfileAttribute> getRepoProfileAttributes(final Class<?> principalClass,
                                                                final Set<Long> principalIds,
                                                                final String[] attrNames) {
        final String idKey = "principalId";
        final String classKey = "principalClass";
        final String attrNameKey = "principalAttrName";
        final String queryName = (attrNames == null) ? "JIProfileAttributeFindByClassAndIds" : "JIProfileAttributeFindByClassAndIdsAndNames";



        List objList = getHibernateTemplate().executeFind(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    Query query = session.getNamedQuery(queryName);
                    query.setParameterList(idKey, principalIds, Hibernate.LONG);
                    query.setParameter(classKey, getObjectClass(principalClass), Hibernate.CLASS);

                    if (attrNames != null) {
                        query.setParameterList(attrNameKey, attrNames, Hibernate.STRING);
                    }

                    return query.list();
                }
            });

        return objList;
    }

    /**
     * Fetches from database and returns the list of repo profile attributes entities.
     *
     * @param principal The principalObject object used to search attributes by its "class" and "id".
     * @param attrNames The attributes names.
     * @return The list of repo profile attributes.
     */
    private List<RepoProfileAttribute> getRepoProfileAttributes(Object principal, String[] attrNames) {
        final IdedObject principalObject = (IdedObject) getPersistentObject(principal);

        if (principalObject == null) {
            throw new JSException("jsexception.no.principal");
        }

        Set<Long> principalId = new HashSet<Long>() {{
            add(principalObject.getId());
        }};
        Class<?> principalClass = principalObject.getClass();
        return getRepoProfileAttributes(principalClass, principalId, attrNames);
    }

    /**
     * Fetches from database and returns the list of repo profile attributes entities.
     *
     * @param principal The principalObject object used to search attributes by its "class" and "id"
     * @return The list of repo profile attributes.
     */
    protected List<RepoProfileAttribute> getRepoProfileAttributes(Object principal) {
        return getRepoProfileAttributes(principal, null);
    }

    protected AttributesSearchResult<ProfileAttribute> getClientProfileAttributes(ExecutionContext context,
                                                                                  Object principal,
                                                                                  AttributesSearchCriteria searchCriteria) {
        List<ProfileAttribute> profileAttributes = new ArrayList<ProfileAttribute>();
        if (principal == null) {
            return new AttributesSearchResultImpl<ProfileAttribute>();
        }

        // Given the principalObject to find the attributes
        final IdedObject principalObject = (IdedObject) getPersistentObject(principal);
        principal = principalObject.toClient(getObjectMappingFactory());

        Set<Long> parentsIds;
        if (searchCriteria.isEffective()) {
            // Get all parent RepoTenant database ids
            parentsIds = getAllParentIds(principalObject);
        } else {
            parentsIds = Collections.emptySet();
        }
        List<RepoProfileAttribute> repoProfileAttributes = getAttributesBySearchCriteria(principalObject, searchCriteria, parentsIds);

        // Convert Repo to Client profile attributes
        profileAttributes.addAll(makeProfileAttributeClientList(
                context, repoProfileAttributes, principal, searchCriteria));

        // Remove all profile attributes that have the same name, but locates on higher level.
        // E.g. if User have attribute with the name "attr1", and his parent Tenant also have attribute with "attr1" name
        // then tenant's attribute will be removed
        profileAttributes = overrideProfileAttributesHierarchically(profileAttributes);

        // apply filters from searchCriteria
        return applyFilter(profileAttributes, searchCriteria);
    }

    private List<ProfileAttribute> overrideProfileAttributesHierarchically(
            List<ProfileAttribute> orderedProfileAttributes) {
        List<ProfileAttribute> result = new LinkedList<ProfileAttribute>();
        Map<String, ProfileAttribute> profileAttributeMap = new LinkedHashMap<String, ProfileAttribute>();

        for (ProfileAttribute profileAttribute : orderedProfileAttributes) {
            if (profileAttribute.getLevel() == ProfileAttributeLevel.CHILD) {
                result.add(profileAttribute);
            } else {
                if (!profileAttributeMap.containsKey(profileAttribute.getAttrName())) {
                    profileAttributeMap.put(profileAttribute.getAttrName(), profileAttribute);
                }
            }
        }

        result.addAll(profileAttributeMap.values());
        return result;
    }

    @SuppressWarnings("unchecked")
    protected List<RepoProfileAttribute> getAttributesBySearchCriteria(final IdedObject principalObject,
                                                                       final AttributesSearchCriteria searchCriteria,
                                                                       final Set<Long> parentsIds) {
        if (!(principalObject instanceof RepoTenant || principalObject instanceof RepoUser)) {
            List<RepoProfileAttribute> repoProfileAttributes = getRepoProfileAttributes(principalObject);

            if (searchCriteria.isEffective()) {
                repoProfileAttributes.addAll(getInheritedRepoProfileAttributes(principalObject));
            }

            return repoProfileAttributes;
        }

        final String tenantUri = getPrincipalUri(principalObject);
        final boolean effective = searchCriteria.isEffective() && !parentsIds.isEmpty();
        final boolean recursive = searchCriteria.isRecursive() && principalObject instanceof RepoTenant;
        final boolean useFilterByNames = searchCriteria.getNames() != null && !searchCriteria.getNames().isEmpty();

        List<RepoProfileAttribute> result = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                // TODO optimize query: make joins instead of IN
                StringBuilder qb = new StringBuilder(1000);
                qb.append("from RepoProfileAttribute as pa\n");
                qb.append("where\n");
                qb.append("(");
                if (recursive) {
                    qb.append("pa.principal.class = :userClass and pa.principal.id in\n");
                    qb.append("(select users.id from RepoUser as users where users.tenant is not null and users.tenant.tenantFolderUri like :uriPref)\n");
                    qb.append("or\n");
                }
                if (effective || recursive) {
                    qb.append("pa.principal.class = :tenantClass and pa.principal.id in\n");
                    qb.append("(select tenants.id from RepoTenant as tenants where ");
                    if (recursive) {
                        qb.append("tenants.tenantFolderUri like :uriPref\n");
                        if (effective) {
                            qb.append(" or ");
                        }
                    }
                    if (effective) {
                        qb.append("tenants.id in (:parentIds)");
                    }
                    qb.append(")\n");
                    qb.append("or\n");
                }
                qb.append("pa.principal.class = :curClass and pa.principal.id = :curId)\n");
                if (useFilterByNames) {
                    qb.append("and\n");
                    qb.append("pa.attrName in (:names)");
                }

                Query query = session.createQuery(qb.toString());
                query.setParameter("curClass", getObjectClass(principalObject.getClass()), Hibernate.CLASS);
                query.setParameter("curId", Long.valueOf(principalObject.getId()), Hibernate.LONG);
                if (recursive) {
                    query.setParameter("userClass", RepoUser.class, Hibernate.CLASS);
                    query.setParameter("tenantClass", RepoTenant.class, Hibernate.CLASS);
                    query.setParameter("uriPref", tenantUri.concat("%"));
                }
                if (effective) {
                    query.setParameterList("parentIds", parentsIds, Hibernate.LONG);
                    query.setParameter("tenantClass", RepoTenant.class, Hibernate.CLASS);
                }
                if (useFilterByNames) {
                    query.setParameterList("names", searchCriteria.getNames());
                }


                return query.list();
            }
        });

        sortRepoProfileAttributes(result);

        return result;
    }

    private String getPrincipalUri(Object principalObject) {
        if (principalObject instanceof RepoUser) {
            RepoUser user = (RepoUser) principalObject;
            String tenantFolderUri = user.getTenant().getTenantFolderUri();
            if (tenantFolderUri == null) {
                return null;
            } else {
                return tenantFolderUri.concat("/users/").concat(user.getUsername());
            }
        } else if (principalObject instanceof RepoTenant) {
            return ((RepoTenant) principalObject).getTenantFolderUri();
        }

        return null;
    }

    private AttributesSearchResult<ProfileAttribute> applyFilter(List<ProfileAttribute> attributes, AttributesSearchCriteria searchCriteria) {
        AttributesSearchResult<ProfileAttribute> result = new AttributesSearchResultImpl<ProfileAttribute>();

        int totalCount = attributes.size();
        int startIndex = searchCriteria.getStartIndex();
        int maxRecords = searchCriteria.getMaxRecords();

        result.setTotalCount(totalCount);

        if (totalCount < startIndex) {
            attributes.clear();
        } else {
            if (maxRecords != 0) {
                if (startIndex + maxRecords > totalCount) {
                    attributes = attributes.subList(startIndex, totalCount);
                } else {
                    attributes = attributes.subList(startIndex, startIndex + maxRecords);
                }
            } else {
                if (startIndex > 0) {
                    attributes = attributes.subList(startIndex, totalCount);
                }
            }
        }

        result.setList(attributes);

        return result;
    }

    private List<RepoProfileAttribute> getInheritedRepoProfileAttributes(IdedObject principalObject) {
        List<RepoProfileAttribute> inheritedAttributes = new ArrayList<RepoProfileAttribute>();
        // Get all parent RepoTenant database ids
        Set<Long> parentsIds = getAllParentIds(principalObject);

        if (parentsIds != null && parentsIds.size() > 0) {
            inheritedAttributes = getRepoProfileAttributes(RepoTenant.class, parentsIds, null);
            // Sort profile attributes by tenantUri
            sortRepoProfileAttributes(inheritedAttributes);
        }

        return inheritedAttributes;
    }

    protected List<ProfileAttribute> getDefaultServerProfileAttributes(ExecutionContext context,
                                                                       AttributesSearchCriteria searchCriteria,
                                                                       boolean isServerPrincipal) {
        List<ProfileAttribute> resultList = new ArrayList<ProfileAttribute>();

        for (Map.Entry<String, PropertyChanger> changerEntry : getChangerObjects().entrySet()) {
            PropertyChanger propertyChanger = changerEntry.getValue();
            String group = changerEntry.getKey();
            if (searchCriteria.getGroups() == null
                    || searchCriteria.getGroups().isEmpty() || searchCriteria.getGroups().contains(group)) {

                for (Map.Entry<String, String> entry : propertyChanger.getProperties().entrySet()) {
                    String name = entry.getKey();
                    if (searchCriteria.getNames() == null
                            || searchCriteria.getNames().isEmpty() || searchCriteria.getNames().contains(name)) {

                        ProfileAttribute profileAttribute = newProfileAttribute(context);
                        profileAttribute.setAttrName(entry.getKey());
                        String value = entry.getValue();
                        if (PasswordCipherer.getInstance().isEncrypted(value)) {
                            value = PasswordCipherer.getInstance().decryptSecureAttribute(value);
                            profileAttribute.setSecure(true);
                        }
                        profileAttribute.setAttrValue(value);
                        profileAttribute.setGroup(changerEntry.getKey());
                        profileAttribute.setPrincipal(ProfileAttributeCategory.SERVER.getPrincipal(getObjectMappingFactory()));
                        if (isServerPrincipal) {
                            profileAttribute.setLevel(ProfileAttributeLevel.TARGET_ASSIGNED);
                        } else {
                            profileAttribute.setLevel(ProfileAttributeLevel.PARENT);
                        }

                        resultList.add(profileAttribute);
                    }
                }
            }
        }

        return resultList;
    }

    private void applyServerSetting(ProfileAttribute profileAttribute) {
        String key = profileAttribute.getAttrName();
        String value = profileAttribute.getAttrValue();
        PropertyChanger changer = null;

        log.debug("Applying configuration property " + profileAttribute.getAttrName() +
                " = " + profileAttribute.getAttrValue());

        try {
            changer = getChanger(key);
            if (changer != null) {
                if (PasswordCipherer.getInstance().isEncrypted(value)) {
                    value = PasswordCipherer.getInstance().decryptSecureAttribute(value);
                }

                changer.setProperty(key, value);
            }
        } catch (Exception e) {
            log.error("PropertyChanger " + changer +
                    " failed to apply configuration property " + key + " = " + value, e);
        }
    }

    private void removeServerAttribute(ProfileAttribute profileAttribute) {
        PropertyChanger changer = getChanger(profileAttribute.getAttrName());
        if (changer != null) {
            changer.removeProperty(profileAttribute.getAttrName(), profileAttribute.getAttrValue());
        }
    }

    private ProfileAttribute getServerAttribute(ExecutionContext context,
                                                ProfileAttribute profileAttribute) {
        try {
            PropertyChanger changer = getChanger(profileAttribute.getAttrName());
            if (changer == null) return null;

            String value = changer.getProperty(profileAttribute.getAttrName());
            log.debug("Read configuration property " + profileAttribute.getAttrName() + " = " + value);

            ProfileAttribute newProfileAttribute = newProfileAttribute(context);
            newProfileAttribute.setAttrName(profileAttribute.getAttrName());
            if (PasswordCipherer.getInstance().isEncrypted(value)) {
                value = PasswordCipherer.getInstance().decryptSecureAttribute(value);
                newProfileAttribute.setSecure(true);
            }
            newProfileAttribute.setAttrValue(value);
            newProfileAttribute.setGroup(profileAttribute.getGroup());
            newProfileAttribute.setPrincipal(profileAttribute.getPrincipal());

            return newProfileAttribute;
        } catch (Exception e) {
            log.error("PropertyChanger " + getChanger(profileAttribute.getAttrName()) +
                    "failed to read configuration property " + profileAttribute.getAttrName());
        }

        return null;
    }

    private PropertyChanger getChanger(String key) {
        if (key.isEmpty()) return null;

        int semicolonPos = key.indexOf(":") > 0 ? key.indexOf(":") : Integer.MAX_VALUE;
        int pointPos = key.indexOf(".") > 0 ? key.indexOf(".") : Integer.MAX_VALUE;
        int changerKeyLength = Math.min(key.length(), Math.min(semicolonPos, pointPos));

        String changerKey = key.substring(0, changerKeyLength);
        PropertyChanger propertyChanger = getChangerObjects().get(changerKey);

        if (propertyChanger == null) {
            log.debug("PropertyChanger not found for property " + key);
        }

        return propertyChanger;
    }

    // TODO: remove this method
    @Override
    public String getChangerName(String propertyName) {
        for (Map.Entry<String, PropertyChanger> entry : getChangerObjects().entrySet()) {
            if (entry.getValue().getProperties().containsKey(propertyName)) {
                return entry.getKey();
            }
        }

        if (propertyName.startsWith(Log4jPropertyChanger.PROPERTY_PREFIX)) {
            return ProfileAttributeGroup.LOG4j.toString();
        } else {
            return ProfileAttributeGroup.CUSTOM.toString();
        }
    }

    @Override
    public void applyProfileAttributes() {
        log.info("Applying all configuration properties");

        RepoTenant rootTenant = tenantPersistenceResolver.getPersistentTenant(TenantService.ORGANIZATIONS, false);

        // Apply attributes only if "root" tenant exists
        if (rootTenant != null) {
            List<ProfileAttribute> profileAttributes = getClientProfileAttributes(null, rootTenant,
                    new AttributesSearchCriteria.Builder()
                            .setEffective(false)
                            .setSkipServerSettings(false)
                            .build()).getList();

            for (ProfileAttribute profileAttribute : profileAttributes) {
                applyServerSetting(profileAttribute);
            }
        }
    }

    private List<String> getAllParentTenantIds(Object principalObject) {
        if (principalObject instanceof RepoUser || principalObject instanceof RepoTenant) {
            RepoTenant repoTenant;
            boolean isPrincipalInstanceOfRepoTenant = false;

            if (principalObject instanceof RepoUser) {
                repoTenant = ((RepoUser) principalObject).getTenant();
            } else {
                isPrincipalInstanceOfRepoTenant = true;
                repoTenant = (RepoTenant) principalObject;
            }

            // Add root tenant id
            String tenantUri = TenantService.ORGANIZATIONS + repoTenant.getTenantUri();

            if (isPrincipalInstanceOfRepoTenant) {
                // Remove this principal
                int indexOfTenantId = tenantUri.lastIndexOf("/" + repoTenant.getTenantId());
                if (indexOfTenantId != -1) {
                    tenantUri = tenantUri.substring(0, indexOfTenantId);
                }
            }

            return Arrays.asList(tenantUri.split("/"));
        }

        return null;
    }

    private boolean isServerPrincipal(Object principal) {
        if (principal instanceof Tenant) {
            String id = ((Tenant) principal).getId();
            return (id == null || id.equals(TenantService.ORGANIZATIONS));
        }

        return false;
    }

    private Set<Long> getAllParentIds(IdedObject principalObject) {
        // Get all parent Tenant ids (e.g. org_1, org_2, ...)
        List<String> parentTenantIds = getAllParentTenantIds(principalObject);
        List<RepoTenant> tenants = getTenantPersistenceResolver().getPersistentTenants(parentTenantIds);
        Set<Long> parentIds = new HashSet<Long>();

        if (tenants != null) {
            for (RepoTenant tenant : tenants) {
                parentIds.add(tenant.getId());
            }
        }

        return parentIds;
    }

    private void sortRepoProfileAttributes(List<RepoProfileAttribute> profileAttributes) {
        Collections.sort(profileAttributes, new Comparator<RepoProfileAttribute>() {
            @Override
            public int compare(RepoProfileAttribute attr1, RepoProfileAttribute attr2) {
                String uri1 = getPrincipalUri(attr1.getPrincipal());
                String uri2 = getPrincipalUri(attr2.getPrincipal());
                int length1 = uri1 == null ? 0 : uri1.length();
                int length2 = uri2 == null ? 0 : uri2.length();
                // Sort by Uri length. First should go child because parent
                // will also have lesser Uri.
                // First, sort by Uri-length in descending order
                // Next, for equals Uri-lengths, sort by Id in ascending order
                if (length1 < length2) {
                    return 1;
                } else if (length1 > length2) {
                    return -1;
                } else if (attr1.getId() < attr2.getId()) {
                    return -1;
                } else if (attr1.getId() > attr2.getId()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List getProfileAttributesForPrincipal(ExecutionContext context, Object principal) {
        AttributesSearchCriteria searchCriteria =
                new AttributesSearchCriteria.Builder().setSkipServerSettings(true).build();
        return getClientProfileAttributes(context, principal, searchCriteria).getList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AttributesSearchResult getProfileAttributesForPrincipal(ExecutionContext context, Object principal, AttributesSearchCriteria searchCriteria) {
        return getClientProfileAttributes(context, principal, searchCriteria);
    }

    public List getProfileAttributesForPrincipal(ExecutionContext context) {
        User user = getCurrentUserDetails();
        return getProfileAttributesForPrincipal(context, user);
    }

    public List getProfileAttributesForPrincipal() {
        return getProfileAttributesForPrincipal(null);
    }

    protected List<ProfileAttribute> makeProfileAttributeClientList(ExecutionContext context,
                                                                    List<RepoProfileAttribute> profileAttributes,
                                                                    Object curPrincipal,
                                                                    AttributesSearchCriteria searchCriteria) {
        List<ProfileAttribute> clientProfileAttributes = new LinkedList<ProfileAttribute>();

        for (RepoProfileAttribute profileAttribute : profileAttributes) {
            String attrGroup = getChangerName(profileAttribute.getAttrName());
            if (searchCriteria.getGroups() == null
                    || searchCriteria.getGroups().isEmpty()
                    || searchCriteria.getGroups().contains(attrGroup)
                    || !attrGroup.equals(ProfileAttributeGroup.CUSTOM.toString())
                    && searchCriteria.getGroups().contains(ProfileAttributeGroup.CUSTOM_SERVER_SETTINGS.toString())) {

                if (searchCriteria.isSkipServerSettings() &&
                        !attrGroup.equals(ProfileAttributeGroup.CUSTOM.toString())) {
                    continue;
                }

                //skip system JDBC attributes
                if (searchCriteria.getGroups() != null
                        && searchCriteria.getGroups().contains(ProfileAttributeGroup.CUSTOM_SERVER_SETTINGS.toString())
                        && attrGroup.equals(ProfileAttributeGroup.JDBC.toString())
                        && profileAttribute.getAttrValue().equals("[SYSTEM]")) {
                    continue;
                }

                ProfileAttribute clientProfileAttribute = (ProfileAttribute) profileAttribute.toClient(getObjectMappingFactory());
                clientProfileAttribute.setGroup(attrGroup);

                // determination of the attribute level
                Object principal = clientProfileAttribute.getPrincipal();
                if (principal instanceof Tenant) {
                    if (curPrincipal instanceof Tenant) {
                        String curUri = ((Tenant) curPrincipal).getTenantFolderUri();
                        String uri = ((Tenant) principal).getTenantFolderUri();
                        int curUriLength = curUri == null ? 0 : curUri.length();
                        int uriLength = uri == null ? 0 : uri.length();
                        if (curUriLength < uriLength) {
                            clientProfileAttribute.setLevel(ProfileAttributeLevel.CHILD);
                        } else if (curUriLength > uriLength) {
                            clientProfileAttribute.setLevel(ProfileAttributeLevel.PARENT);
                        } else {
                            clientProfileAttribute.setLevel(ProfileAttributeLevel.TARGET_ASSIGNED);
                        }
                    } else {
                        clientProfileAttribute.setLevel(ProfileAttributeLevel.PARENT);
                    }
                } else {
                    if (curPrincipal instanceof Tenant) {
                        clientProfileAttribute.setLevel(ProfileAttributeLevel.CHILD);
                    } else {
                        clientProfileAttribute.setLevel(ProfileAttributeLevel.TARGET_ASSIGNED);
                    }
                }

                clientProfileAttributes.add(clientProfileAttribute);
            }
        }

        //Add default server profile attributes
        boolean isServerPrincipal = isServerPrincipal(curPrincipal);
        if ((isServerPrincipal || searchCriteria.isEffective()) && !searchCriteria.isSkipServerSettings()) {
            clientProfileAttributes.addAll(getDefaultServerProfileAttributes(context, searchCriteria, isServerPrincipal));
        }

        return clientProfileAttributes;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object getPersistentObject(Object clientObject) {
        // If already persisted, just return it
        if (clientObject == null) {
            return null;
        } else if (clientObject instanceof IdedObject) {
            return clientObject;
        } else if (clientObject instanceof Role || clientObject instanceof User || clientObject instanceof Tenant) {
            return ((PersistentObjectResolver) userService).getPersistentObject(clientObject);
        } else if (clientObject instanceof Resource) {
            //TODO Hack! Make it an interface!
            String uri = ((Resource) clientObject).getPath();
            return repoService.findByURI(RepoResource.class, uri, false);
        } else if (clientObject instanceof ProfileAttribute) {
            return getRepoProfileAttribute((ProfileAttribute) clientObject);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getCurrentUserPreferenceValue(String attrName) {
        String attrValue = null;
        User user = getCurrentUserDetails();
        ProfileAttribute attr = newProfileAttribute(null);
        attr.setPrincipal(user);
        attr.setAttrName(attrName);
        ProfileAttribute savedAttr = getProfileAttribute(null, attr);
        if (savedAttr != null) {
            attrValue = savedAttr.getAttrValue();
        }
        return attrValue;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setCurrentUserPreferenceValue(String attrName, String attrValue) {
        User user = getCurrentUserDetails();
        ProfileAttribute attr = newProfileAttribute(null);
        attr.setPrincipal(user);
        attr.setAttrName(attrName);
        ProfileAttribute savedAttr = getProfileAttribute(null, attr);
        if (savedAttr == null) {
            savedAttr = attr;
        }
        if (!equalsWithNull(savedAttr.getAttrValue(), attrValue)) {
            savedAttr.setAttrValue(attrValue);
            putProfileAttribute(null, savedAttr);
            updateUserAttribute(user, savedAttr);
        }
    }

    private static void updateUserAttribute(User user, final ProfileAttribute attributeToUpdate) {
        @SuppressWarnings("unchecked")
        // user.getAttributes() can be null or List<ProfileAttribute>
        List<ProfileAttribute> userAttributes = defaultIfNull(user.getAttributes(), EMPTY_LIST);

        ProfileAttribute existingAttribute = (ProfileAttribute) find(userAttributes, new Predicate() {
            public boolean evaluate(Object object) {
                return object instanceof ProfileAttribute
                        && equalsWithNull(attributeToUpdate.getAttrName(), ((ProfileAttribute) object).getAttrName());
            }
        });
        if (existingAttribute == null) {
            List<ProfileAttribute> newAttributesList = new LinkedList<ProfileAttribute>(userAttributes);
            newAttributesList.add(attributeToUpdate);
            user.setAttributes(newAttributesList);
        } else {
            existingAttribute.setAttrValue(attributeToUpdate.getAttrValue());
        }
    }

    public String generateAttributeHolderUri(Object holder) {
        StringBuilder builder = new StringBuilder();
        String tenantFolderUri = "";
        if (holder instanceof User) {
            builder.append("/users/").append(((User) holder).getUsername());
            User user = (User) holder;
            String tenantId = user.getTenantId() == null ? TenantService.ORGANIZATIONS : user.getTenantId();
            RepoTenant tenant = tenantPersistenceResolver.getPersistentTenant(tenantId, true);
            tenantFolderUri = tenant.getTenantFolderUri();
        } else if (holder instanceof Tenant) {
            Tenant tenant = (Tenant) holder;
            RepoTenant repoTenant = tenantPersistenceResolver.getPersistentTenant(tenant.getId(), true);
            tenantFolderUri = repoTenant.getTenantFolderUri();
        }
        String parentPath = tenantFolderUri.equals("/") ? "" : tenantFolderUri;
        builder.insert(0, parentPath);

        return builder.toString();
    }


    public static boolean equalsWithNull(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
    }

    public MetadataUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (MetadataUserDetails) auth.getPrincipal();
        }
        return null;
    }

    protected String getCurrentUser() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();

        String name = authenticationToken.getName();

        if (authenticationToken.getPrincipal() instanceof TenantQualified) {
            TenantQualified tenantQualified = (TenantQualified) authenticationToken.getPrincipal();
            String tenantId = tenantQualified.getTenantId();
            return (tenantId != null) ? name + "|" + tenantId : name;
        }

        return name;
    }
}
