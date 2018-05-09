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

package com.jaspersoft.jasperserver.api.metadata.tenant.service.impl;


import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.IlikeEscapeAwareExpression;
import com.jaspersoft.jasperserver.api.metadata.common.util.DatabaseCharactersEscapeResolver;
import com.jaspersoft.jasperserver.api.metadata.tenant.service.TenantPersistenceResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tenant service implementation.
 *
 * @author achan
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TenantServiceImpl extends HibernateDaoImpl
		implements TenantService, TenantPersistenceResolver, Diagnostic {

    Pattern patternTenantByURI = Pattern.compile("^(?:/" + TenantService.ORGANIZATIONS + "/([^/]+))*/?.*");

    protected static final Log log = LogFactory.getLog(TenantServiceImpl.class);
	private ResourceFactory objectMappingFactory;
	private ResourceFactory persistentClassFactory;
	private String userOrgIdDelimiter;
    private DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver;
    private ProfileAttributeService profileAttributeService;
    private boolean isTenantCaseSensitive;

    public void setDatabaseCharactersEscapeResolver(DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver) {
        this.databaseCharactersEscapeResolver = databaseCharactersEscapeResolver;
    }
	
	public ResourceFactory getPersistentClassFactory() {
		return persistentClassFactory;
	}
	
	public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
		this.persistentClassFactory = persistentClassFactory;
	}
	
	public ResourceFactory getObjectMappingFactory() {
		return objectMappingFactory;
	}

	public void setObjectMappingFactory(ResourceFactory objectFactory) {
		this.objectMappingFactory = objectFactory;
	}

    public boolean isTenantCaseSensitive() {
        return isTenantCaseSensitive;
    }

    public void setTenantCaseSensitive(boolean tenantCaseSensitive) {
        isTenantCaseSensitive = tenantCaseSensitive;
    }

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    protected RepoTenant getRepoTenant(String tenantId, boolean required) {
        // Search with sensitivity according to the configuration.
        List tenantList = getHibernateTemplate().findByCriteria(createTenantSearchCriteria(tenantId,
                isTenantCaseSensitive));

        // Before 5.0.1 we had case sensitive tenant ID. But there was a bug 24226 opened with at least 4 customer
        // cases that tenant ID should be case insensitive. Because of previous case sensitive logic existing customers
        // could have several tenants which tenant ID is the same when case insensitive logic is applied but different
        // when case sensitive logic is applied. So, we decided to implement adaptive logic which will allow both worlds
        // live together.
        // It means that by default we are trying to find the tenant using case insensitive logic (this is the matter of
        // configuration). If more then 1 tenant was found we do one more search with case sensitive logic applied.
        if (tenantList.size() > 1) {
            log.warn(tenantList.size() + " tenants were found during case insensitive search for \"" + tenantId +
                    "\". Retrying case sensitive search.");

            // Case sensitive search.
            tenantList = getHibernateTemplate().findByCriteria(createTenantSearchCriteria(tenantId, true));
        }


        return extractTenant(tenantId, required, tenantList);
	}

    private RepoTenant extractTenant(String tenantId, boolean required, List tenantList) {
        RepoTenant tenant = null;

        if (tenantList.isEmpty()) {
            if (required) {
                throw new JSException("Tenant not found with Tenant ID \"" + tenantId + "\"");//TODO i18n
            }

            log.debug("Tenant not found with Tenant ID \"" + tenantId + "\".");
        } else if (tenantList.size() == 1) {
            tenant = (RepoTenant) tenantList.get(0);
        }

        return tenant;
    }

    private DetachedCriteria createTenantSearchCriteria(String tenantId, boolean isCaseSensitive) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());

        criteria.add(isCaseSensitive ? Restrictions.eq("tenantId", tenantId) :
                Restrictions.ilike("tenantId", tenantId));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);

        return criteria;
    }

    protected RepoTenant getRepoTenantByAlias(String tenantAlias, boolean required) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());
        criteria.add(Restrictions.eq("tenantAlias", tenantAlias));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantList = getHibernateTemplate().findByCriteria(criteria);
        RepoTenant tenant;
        if (tenantList.isEmpty()) {
            if (required) {
                throw new JSException("Tenant not found with Tenant Alias \"" + tenantAlias + "\"");//TODO i18n
            }

            log.debug("Tenant not found with Tenant Alias \"" + tenantAlias + "\"");
            tenant = null;
        } else {
            tenant = (RepoTenant) tenantList.get(0);
        }
        return tenant;
    }

    protected List<RepoTenant> getRepoTenantListByIds(List<String> tenantIds) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());
        criteria.add(Restrictions.in("tenantId", tenantIds));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        @SuppressWarnings("unchecked")
        List<RepoTenant> tenantList = getHibernateTemplate().findByCriteria(criteria);
        if (tenantList.isEmpty()) {
            log.debug("Tenants not found with ids \"" + tenantIds + "\"");
        }
        return tenantList;
    }

	protected Class persistentTenantClass() {
		return getPersistentClassFactory().getImplementationClass(Tenant.class);
	}

	
	protected RepoTenant getRepoTenantBasedOnTenantUri(ExecutionContext context, String tenantUri) {
		DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());
		criteria.add(Restrictions.eq("tenantUri", tenantUri));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantList = getHibernateTemplate().findByCriteria(criteria);
		RepoTenant tenant = null;		
		if (tenantList.isEmpty()) {
			log.debug("Tenant not found with Tenant Name \"" + tenantUri + "\"");
		} else {
			tenant = (RepoTenant) tenantList.get(0);
		}
		return tenant;
	}	

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#putUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.User)
	 */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void putTenant(ExecutionContext context, Tenant aTenant) {
		RepoTenant existingTenant = getRepoTenant(aTenant.getId(), false);
		if (existingTenant == null) {
			existingTenant = (RepoTenant) getPersistentClassFactory().newObject(Tenant.class);
		}
		existingTenant.copyFromClient(aTenant, this);
		getHibernateTemplate().saveOrUpdate(existingTenant);
	}
	
	protected List getRepoSubTenants(ExecutionContext context, String parentTenantId) {
		RepoTenant parent = getRepoTenant(parentTenantId, false);
		if (parent == null || parent.getSubTenants() == null) {
			return new ArrayList(0);
		}
		return new ArrayList(parent.getSubTenants());
	}
	
	public List getSubTenantList(ExecutionContext context, Tenant parentTenant) {
		List persistentTenants = getRepoSubTenants(context, parentTenant.getId());
		return toClientTenantList(persistentTenants);
	}
	
	public List getSubTenantList(ExecutionContext context, String parnentTenantId) {
		List persistentTenants = getRepoSubTenants(context, parnentTenantId);
		return toClientTenantList(persistentTenants);
	}
	
	protected List<Tenant> toClientTenantList(List persistentTenants) {
		if (persistentTenants == null) {
			return null;
		}
		
		List<Tenant> tenants = new ArrayList<Tenant>(persistentTenants.size());
        for (Object persistentTenant1 : persistentTenants) {
            RepoTenant persistentTenant = (RepoTenant) persistentTenant1;
            Tenant tenant = toClientTenant(persistentTenant);
            tenants.add(tenant);
        }
		return tenants;
	}
	
	public Tenant getTenant(ExecutionContext context, String tenantId) {
		RepoTenant rTenant = getRepoTenant(tenantId, false);
		if (rTenant == null) {
			return null;
		} else {
            Tenant tenant = toClientTenant(rTenant);
            tenant.setAttributes(profileAttributeService.getProfileAttributesForPrincipal(null, tenant));
            return tenant;
        }
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteTenant(ExecutionContext context, String tenantId) {
		RepoTenant tenant = getRepoTenant(tenantId, false);
		if (tenant != null) {
		   getHibernateTemplate().delete(tenant);
           getHibernateTemplate().flush();
		} else {
			if (log.isInfoEnabled()) {
				log.info("Tenant " + tenantId + " not found for deletion");
			}
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateTenant(ExecutionContext context, Tenant aTenant) {
		putTenant(context, aTenant);
	}
	
    @Transactional(propagation = Propagation.REQUIRED)
    public List getAllSubTenantList(ExecutionContext context, final String parentTenantId) {
        return getAllSubTenantList(context, parentTenantId, null);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> getAllSubTenantIdList(ExecutionContext context, final String parentTenantId) {
        List<String> subTenantIdList = new ArrayList<String>();

        /* Retrieving parent tenant. */
        RepoTenant parent = getRepoTenant(parentTenantId, false);

        if (parent != null) {
            DetachedCriteria criteria = createSubTenantsCriteria(parent, null, -1, null);
            criteria.getExecutableCriteria(getSession()).setCacheable(true);
            criteria.setProjection(Projections.property("tenantId"));
            subTenantIdList.addAll(getHibernateTemplate().findByCriteria(criteria));
        }

        return subTenantIdList;
    }

    private DetachedCriteria createSubTenantsCriteria(RepoTenant parentTenant, String text, int depth, String sortBy) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());
        String value = "/%";

        if ("/".equals(parentTenant.getTenantUri())) {
            /* Excluding root parent from sub list. */
            criteria.add(Restrictions.ne("tenantId", parentTenant.getTenantId()));
        } else {
        	// escaping uri (but not special /%) to avoid bug #44445
            value = databaseCharactersEscapeResolver.getEscapedText(parentTenant.getTenantUri()) + value;
        }

        // EXACT is used because we have built-in % characters already. Using Escaped like to avoid DB2 bug #44445
        criteria.add(new IlikeEscapeAwareExpression("tenantUri", value, MatchMode.EXACT));
 
        if (sortBy != null){
            if (sortBy.equals("id")){
                criteria.addOrder(Order.asc("tenantId"));
            } else if (sortBy.equals("name")){
                criteria.addOrder(Order.asc("tenantName"));
            } else if (sortBy.equals("alias")) {
                criteria.addOrder(Order.asc("tenantAlias"));
            }
        }

        // value is already escaped so there is no need to escape it again.
        if (depth > 0){
            StringBuilder boundary = new StringBuilder(value);
            for (;depth > 0; depth--){
                boundary.append("/%");
            }
            // EXACT is used because we have built-in % characters already. Using Escaped like to avoid DB2 bug #44445
            criteria.add(Restrictions.not(new IlikeEscapeAwareExpression("tenantUri", boundary.toString(), MatchMode.EXACT)));
        }

        if (text != null) {
            text = databaseCharactersEscapeResolver.getEscapedText(text.trim());
            if(text.length() > 0){
                Disjunction disjunction = Restrictions.disjunction();

                disjunction.add(new IlikeEscapeAwareExpression("tenantId", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantAlias", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantName", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantDesc", text, MatchMode.ANYWHERE));

                criteria.add(disjunction);
            }
        }
        return criteria;
    }

    private void getAllSubTenantListPrivate(String parentTenantId, ArrayList allSubTenants) {
		List subTenant = getSubTenantList(null, parentTenantId);
		for (int i=0; i<subTenant.size(); i++) {
			allSubTenants.add(subTenant.get(i));
			getAllSubTenantListPrivate(((Tenant)subTenant.get(i)).getId(), allSubTenants);
		}
	}
	
	public int getNumberOfTenants(ExecutionContext context) {
		List allTenants = getAllSubTenantList(null, ORGANIZATIONS);
		return allTenants.size();
	}
	
	public Tenant getDefaultTenant(ExecutionContext context) {
		List allTenants = getAllSubTenantList(null, ORGANIZATIONS);	
		if (allTenants.size() == 1) {
			return (Tenant)allTenants.get(0);
		} else {
			return null;
		}
	}
	
	
	public Tenant getTenantBasedOnTenantUri(ExecutionContext context, String tenantUri) {
		RepoTenant rTenant = getRepoTenantBasedOnTenantUri(context, tenantUri);
		if (rTenant == null) {
			return null;
		}
		Tenant tenant = toClientTenant(rTenant);
		return tenant;		
	}

	protected Tenant toClientTenant(RepoTenant rTenant) {
		Tenant tenant = (Tenant) objectMappingFactory.newObject(Tenant.class);
		rTenant.copyToClient(tenant);
		return tenant;
	}

	public String getUserOrgIdDelimiter() {
		return userOrgIdDelimiter;
	}

	public void setUserOrgIdDelimiter(String userOrgIdDelimiter) {
		this.userOrgIdDelimiter = userOrgIdDelimiter;
	}

	public String getTenantIdBasedOnRepositoryUri(ExecutionContext context, String uri) {
        Matcher matcher = patternTenantByURI.matcher(uri);
        String possibleId = null;
        if (matcher.matches()) {
            possibleId = matcher.group(1);
            if (TenantService.ORG_TEMPLATE.equals(possibleId)){
                possibleId = getTenantIdBasedOnRepositoryUri(context, uri.substring(0, uri.lastIndexOf(TenantService.ORGANIZATIONS)));
            }
        }
        return possibleId;
	}

    public Tenant getTenantBasedOnRepositoryUri(ExecutionContext context, String uri) {
        String tenantId = getTenantIdBasedOnRepositoryUri(context, uri);
        if (tenantId != null) {
            return getTenant(context, tenantId);
        }
        return null;
    }

    public boolean isMultiTenantEnvironment(ExecutionContext context) {
        DetachedCriteria criteria = DetachedCriteria.forClass(
                persistentTenantClass());
        criteria.add(Restrictions.ne("tenantUri", "/"));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantList = getHibernateTemplate().findByCriteria(criteria);
        return tenantList.size() > 1;
	}

	public RepoTenant getPersistentTenant(String tenantId, boolean required) {
		if (tenantId == null || tenantId.length() == 0) {
			return null;
		}
		
		return getRepoTenant(tenantId, required);
	}

    public RepoTenant getPersistentTenantByAlias(String tenantAlias, boolean required) {
        if (tenantAlias == null || tenantAlias.length() == 0) {
            return null;
        }

        return getRepoTenantByAlias(tenantAlias, required);
    }

    @Override
    public List<RepoTenant> getPersistentTenants(List<String> tenantIds) {
        if (tenantIds == null || tenantIds.size() == 0) {
            return null;
        }

        return getRepoTenantListByIds(tenantIds);
    }

    public String getUniqueTenantId(String proposedTenantId) {
        List similarTenantIdList;
        DetachedCriteria criteria = DetachedCriteria.forClass(
                persistentTenantClass());
        criteria.add(Restrictions.like("tenantId", proposedTenantId + "%").ignoreCase());
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantList = getHibernateTemplate().findByCriteria(criteria);
        if (tenantList.isEmpty()) {
            similarTenantIdList = Collections.emptyList();
        } else {
            similarTenantIdList = new ArrayList(tenantList.size());
            for (int i = 0; i < tenantList.size(); i++) {
                similarTenantIdList.add(((RepoTenant) tenantList.get(i)).getTenantId());
            }
        }

        return getUniqueItem(proposedTenantId, similarTenantIdList);
    }

    private String getUniqueItem(String proposedItem, List similarItemsList) {
        if (similarItemsList.size() == 0) {
            return proposedItem;
        }

        for (int i = 0; i < similarItemsList.size(); i++) {
            String orgId = (String) similarItemsList.get(i);
            if (orgId.equalsIgnoreCase(proposedItem)) {
                break;
            }
            if (i == (similarItemsList.size() - 1)) {
                return proposedItem;
            }
        }

        int index = 1;
        boolean ready = false;
        while (!ready) {
            for (int i = 0; i < similarItemsList.size(); i++) {
                String orgId = (String) similarItemsList.get(i);
                if ((proposedItem + index).equalsIgnoreCase(orgId)) {
                    index++;
                    break;
                }
                if (i == (similarItemsList.size() - 1)) {
                    ready = true;
                    proposedItem = proposedItem + index;
                }
            }
        }

        return proposedItem;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getUniqueTenantAlias(String proposedAlias) {
        List similarAliasesAndTenantIdList;
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());
        criteria.add(Restrictions.like("tenantAlias", proposedAlias + "%").ignoreCase());
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantAliasesList = getHibernateTemplate().findByCriteria(criteria);
        if (tenantAliasesList.isEmpty()) {
            similarAliasesAndTenantIdList = Collections.emptyList();
        } else {
            similarAliasesAndTenantIdList = new ArrayList(tenantAliasesList.size());
            for (int i = 0; i < tenantAliasesList.size(); i++) {
                similarAliasesAndTenantIdList.add(((RepoTenant) tenantAliasesList.get(i)).getTenantAlias());
            }
        }

        criteria = DetachedCriteria.forClass(persistentTenantClass());
        criteria.add(Restrictions.like("tenantId", proposedAlias + "%"));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List tenantIdsList = getHibernateTemplate().findByCriteria(criteria);
        if (!tenantIdsList.isEmpty()) {
            if (similarAliasesAndTenantIdList.isEmpty()) {
                similarAliasesAndTenantIdList = new ArrayList(tenantIdsList.size());
            }
            for (int i = 0; i < tenantIdsList.size(); i++) {
                similarAliasesAndTenantIdList.add(((RepoTenant) tenantIdsList.get(i)).getTenantId());
            }
        }

        // Alias should be unique among all tenant IDs and tenant aliases.
        return getUniqueItem(proposedAlias, similarAliasesAndTenantIdList);
    }

    public int getNumberOfUsers(ExecutionContext context, String tenantId) {
        return createNumberOfUsersOrRolesCriteria(tenantId,
                getPersistentClassFactory().getImplementationClass(User.class));
    }

    public int getNumberOfRoles(ExecutionContext context, String tenantId) {
        return createNumberOfUsersOrRolesCriteria(tenantId,
                getPersistentClassFactory().getImplementationClass(Role.class));
    }

    private int createNumberOfUsersOrRolesCriteria(String tenantId, Class aClass) {
        Integer rowCount = 0;

        RepoTenant tenant = getRepoTenant(tenantId, false);
        if (tenant != null) {
            DetachedCriteria criteria = DetachedCriteria.forClass(aClass);
            criteria.createAlias("tenant", "t");

            criteria.add(Restrictions.or(
                    Restrictions.eq("t.tenantUri", tenant.getTenantUri()),
                    Restrictions.like("t.tenantUri",
                            tenant.getTenantUri().equals("/") ? "/%" : tenant.getTenantUri() + "/%")
            ));

            criteria.setProjection(Projections.count("id"));
            criteria.getExecutableCriteria(getSession()).setCacheable(true);

            List results = getHibernateTemplate().findByCriteria(criteria);
            if (results != null && !results.isEmpty()) {

                rowCount = (Integer) results.get(0);
            }
        }

        return rowCount;
    }

    public int getSubTenantsCount(ExecutionContext context, String parentTenantId, String text) {
        DetachedCriteria criteria = createSearchTenantsCriteria(parentTenantId, text);

        criteria.setProjection(Projections.rowCount());
        criteria.getExecutableCriteria(getSession()).setCacheable(true);

        List results = getHibernateTemplate().findByCriteria(criteria);

        if (results != null && !results.isEmpty()) {
            return (Integer) results.get(0);
        }

        return 0;
    }

    /**
     * The number of all tenants
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected int getTotalTenantsCount(ExecutionContext context) {
        Integer rowCount = (Integer) getHibernateTemplate().execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(persistentTenantClass());
                criteria.add(Restrictions.not(Restrictions.eq("tenantId", ORGANIZATIONS)));
                criteria.setProjection(Projections.rowCount());
                return criteria.uniqueResult();
            }
        });

        return rowCount;
    }

    public List<Tenant> getSubTenants(ExecutionContext context, String parentTenantId, String text, int firstResult,
            int maxResults) {
        DetachedCriteria criteria = createSearchTenantsCriteria(parentTenantId, text);
        criteria.getExecutableCriteria(getSession()).setCacheable(true);

        List results = getHibernateTemplate().findByCriteria(criteria, firstResult, maxResults);

        return toClientTenantList(results);
    }

    private DetachedCriteria createSearchTenantsCriteria(String parentTenantId, String text) {
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());

        criteria.createAlias("parent", "p");
        criteria.add(Restrictions.naturalId().set("p.tenantId", parentTenantId));

        if (text != null) {
            text = databaseCharactersEscapeResolver.getEscapedText(text.trim());
            if(text.length() > 0){
                Disjunction disjunction = Restrictions.disjunction();

//                disjunction.add(Restrictions.ilike("tenantId", "%" + text + "%"));
//                disjunction.add(Restrictions.ilike("tenantAlias", "%" + text + "%"));
//                disjunction.add(Restrictions.ilike("tenantName", "%" + text + "%"));
//                disjunction.add(Restrictions.ilike("tenantDesc", "%" + text + "%"));

                disjunction.add(new IlikeEscapeAwareExpression("tenantId", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantAlias", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantName", text, MatchMode.ANYWHERE));
                disjunction.add(new IlikeEscapeAwareExpression("tenantDesc", text, MatchMode.ANYWHERE));

                criteria.add(disjunction);
            }
        }
        
        return criteria;
    }

    public Map<String, Integer> getSubTenantsCountMap(List<String> tenantIds) {
        if (tenantIds == null || tenantIds.size() == 0) {
            return Collections.emptyMap();
        }
        
        DetachedCriteria criteria = DetachedCriteria.forClass(persistentTenantClass());

        criteria.createAlias("parent", "p");
        criteria.add(Restrictions.in("p.tenantId", tenantIds));

        criteria.setProjection(Projections.projectionList()
        .add(Projections.rowCount())
        .add(Projections.groupProperty("p.tenantId")));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);

        List results = getHibernateTemplate().findByCriteria(criteria);

        Map<String, Integer> subTenantCounts = new HashMap<String, Integer>(tenantIds.size(), 1);
        if (results != null && results.size() > 0) {
            for (Object result: results) {
                String tenantId = (String)((Object[])result)[1];
                Integer count = (Integer)((Object[])result)[0];

                subTenantCounts.put(tenantId, count);
            }
        }

        for (String tenantId: tenantIds) {
            if (!subTenantCounts.containsKey(tenantId)) {
                subTenantCounts.put(tenantId, 0);
            }
        }

        return subTenantCounts;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setTenantActiveTheme(ExecutionContext context, String tenantId, String themeName) {
        if (themeName == null) {
            throw new JSException("Theme name should not be null!");//TODO i18n
        }
        Tenant tenant = getTenant(context, tenantId);
        if (tenant == null) {
            throw new JSException("Tenant not found : \"" + tenantId + "\"");//TODO i18n
        }
        tenant.setTheme(themeName);
        putTenant(context, tenant);
    }


    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_ORGANIZATIONS_COUNT, new DiagnosticCallback<Integer>() {
                @Override
                public Integer getDiagnosticAttributeValue() {
                    return getTotalTenantsCount(null);
                }
            }).build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text) {
        return getAllSubTenantList(context, parentTenantId, text, -1);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth) {
        return  getAllSubTenantList(context, parentTenantId, text, depth, null);
    }

    @Override
    public List<Tenant> getAllSubTenantList(ExecutionContext context, String parentTenantId, String text, int depth, String sortBy) {
        List subTenantList = new ArrayList();

        /* Retrieving parent tenant. */
        RepoTenant parent = getRepoTenant(parentTenantId, false);

        if (parent != null) {
            DetachedCriteria criteria = createSubTenantsCriteria(parent, text, depth, sortBy);
            criteria.getExecutableCriteria(getSession()).setCacheable(true); // .setCacheMode(CacheMode.REFRESH);
            subTenantList.addAll(getHibernateTemplate().findByCriteria(criteria));
        }

        return toClientTenantList(subTenantList);
    }
}
