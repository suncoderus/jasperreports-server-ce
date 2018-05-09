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

package com.jaspersoft.jasperserver.war.themes;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ComparableBlob;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateSaveUpdateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class listens delete and saveOrUpdate hibernate events.
 * It detects updates in application themes and flushes the theme cache.
 *
 * @author asokolnikov
 */
public class ThemeHibernateListener implements HibernateSaveUpdateDeleteListener,
        BeanFactoryAware {

    private static Log log = LogFactory.getLog(ThemeHibernateListener.class);

    protected BeanFactory beanFactory;
    protected String themeCacheBeanName;
    protected ThemeCache themeCache;

    protected Log getLog() {
        return log;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public ThemeCache getThemeCache() {
        if (themeCache == null) {
            themeCache = (ThemeCache) beanFactory.getBean(themeCacheBeanName);
        }
        return themeCache;
    }

    protected void preProcessDelete(RepoResource res) {
    }

    protected void preProcessUpdate(RepoResource res) {
    }

    @Override
    public void beforeSave(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterSave(Object entity, HibernateTemplate hibernateTemplate) {
        afterSaveOrUpdate(entity, hibernateTemplate);
    }

    @Override
    public void beforeUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void beforeSaveOrUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterSaveOrUpdate(Object entity, HibernateTemplate hibernateTemplate) {
        if (entity instanceof RepoResource) {
            RepoResource res = (RepoResource) entity;
            String uri = res.getResourceURI();
            if (getThemeCache().isThemeResource(uri)) {
            	if(log.isTraceEnabled()){
            		log.trace("onSaveOrUpdate: Resource :" + uri);
            	}
            	preProcessUpdate(res);
                getThemeCache().onThemeResourceChanged(uri);
            }
        }
    }

    @Override
    public void beforeDelete(Object entity, HibernateTemplate hibernateTemplate) {
        if (entity instanceof RepoResource) {
            RepoResource res = (RepoResource) entity;
            String uri = res.getResourceURI();
            if (getThemeCache().isThemeResource(uri)) {
            	if(log.isTraceEnabled()){
            		log.trace("onDelete: Resource :" + uri);
            	}
            	preProcessDelete(res);
                getThemeCache().onThemeResourceChanged(uri);
            }
        }
        if (entity instanceof RepoFolder) {
            RepoFolder folder = (RepoFolder) entity;
            String uri = folder.getResourceURI();
            if (getThemeCache().isThemeResource(uri+"/")) {
                // ok we are deleteing Theme folder
                // as this folder can contain resource files which are used in propagated resources as reference
                // we should unreference all resources which are using resources in this folder
                for( Object resource: folder.getChildren()) {
                    if (resource instanceof RepoFileResource) {
                        RepoFileResource fileResource = (RepoFileResource) resource;
                        unreferenceFileResource(fileResource,hibernateTemplate);
                        getThemeCache().onThemeResourceChanged(fileResource.getResourceURI());
                    }
                }

                // Iterate to all subFolders
                Set subFolders = folder.getSubFolders();
                for(Iterator<RepoFolder> iterator = subFolders.iterator(); iterator.hasNext();) {
                    beforeDelete(iterator.next(),hibernateTemplate);
                }

            }
        }
    }

    private void unreferenceFileResource(RepoFileResource resource, HibernateTemplate hibernateTemplate) {
        DetachedCriteria criteria = DetachedCriteria.forClass(RepoFileResource.class);
        criteria.add(Restrictions.eq("reference", resource));
        List<RepoFileResource> referencedResources = hibernateTemplate.findByCriteria(criteria);
        for(RepoFileResource fileResource: referencedResources) {
            // if current resource is reference then just change reference in dependant resource
            // if not - then set data in dependant resource and remove reference
            if (resource.isFileReference()) {
                fileResource.setReference(resource.getReference());
            } else {
                fileResource.setData((ComparableBlob) resource.getData());
                fileResource.setReference(null);
            }
        }
    }

    @Override
    public void afterDelete(Object entity, HibernateTemplate hibernateTemplate) {
    }

    public String getThemeCacheBeanName() {
        return themeCacheBeanName;
    }

    public void setThemeCacheBeanName(String themeCacheBeanName) {
        this.themeCacheBeanName = themeCacheBeanName;
    }
}
