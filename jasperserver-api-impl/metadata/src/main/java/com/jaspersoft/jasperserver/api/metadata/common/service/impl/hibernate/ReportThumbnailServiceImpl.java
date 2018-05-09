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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ComparableBlob;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoReportThumbnail;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;

@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ReportThumbnailServiceImpl extends HibernateDaoImpl implements ReportThumbnailService, PersistentObjectResolver {

    private UserAuthorityService userAuthorityService;
    private HibernateRepositoryService repositoryService;
    private SessionFactory sessionFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    private String defaultThumbnailPath;

    private ByteArrayInputStream defaultThumbnail = null;

    public ReportThumbnailServiceImpl() {}

    public Object getPersistentObject(Object clientObject) {
        if (clientObject == null) {
            return null;
        } else if (clientObject instanceof Resource) {
            return repositoryService.getRepoResource((Resource)clientObject);
        } else if (clientObject instanceof User) {
            return ((PersistentObjectResolver) userAuthorityService).getPersistentObject(clientObject);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveReportThumbnail(ByteArrayOutputStream thumbnailStream, User user, Resource resource)
            throws JSException
    {
        byte[] thumbnail = thumbnailStream.toByteArray();

        RepoUser repoUser = (RepoUser) getPersistentObject(user);
        RepoResource repoResource = repositoryService.getRepoResource(resource);
        RepoReportThumbnail repoThumbnail = null;

        if (repoResource == null) {
            throw new JSException("jsexception.unable.to.resolve.report");
        }

        boolean update = true;

        try {
            repoThumbnail = retrieveThumbnail(repoUser, repoResource);
        } catch (JSResourceNotFoundException e) {
            // No existing resource found, we will create new.
            update = false;
            repoThumbnail = new RepoReportThumbnail();
        }

        repoThumbnail.setThumbnail(new ComparableBlob(thumbnail));

        if (update) {
            getHibernateTemplate().update(repoThumbnail);
        } else {
            repoThumbnail.setResource(repoResource);
            repoThumbnail.setUser(repoUser);
            getHibernateTemplate().save(repoThumbnail);
        }

    }

    /**
     * Obtain a RepoUser object given a User object, used to obtain UserID from database to add to the record
     *
     * @param user
     * @return RepoUser
     */
    private RepoUser resolveUser(User user) {
        return (RepoUser) ((PersistentObjectResolver) userAuthorityService).getPersistentObject(user);
    }

    /**
     * Obtain the thumbnail from the database related to the User and Report provided
     *
     * @param repoUser
     * @param repoResource
     * @return ByteArrayInputStream
     */
    @Transactional(propagation = Propagation.NESTED, readOnly = true)
    protected RepoReportThumbnail retrieveThumbnail(RepoUser repoUser, RepoResource repoResource) {
        DetachedCriteria criteria = DetachedCriteria.forClass(RepoReportThumbnail.class)
                .add(Restrictions.eq("user", repoUser))
                .add(Restrictions.eq("resource", repoResource));

        List<RepoReportThumbnail> results = getHibernateTemplate().findByCriteria(criteria);
        if (results.size() > 0) {
            ByteArrayInputStream resultStream;
            RepoReportThumbnail result = results.get(0); // given unique constrain on user, resource, we can expect this to only have a 0 element
                return result;
        } else if (results.size() == 0) {
            throw new JSResourceNotFoundException("ReportThumbnailService: No thumbnail found, warning only");
        } else {
            throw new JSException("ReportThumbnailService: Invalid results for thumbnail search");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ByteArrayInputStream getReportThumbnail(User user, Resource resource)
    {
        RepoUser repoUser = resolveUser(user);
        RepoResource repoResource = (RepoResource) getPersistentObject(resource);

        if (repoResource == null || repoUser == null)
            throw new JSException("ReportThumbnailServiceImpl: invalid.resource.or.user");

        RepoReportThumbnail result;
        ByteArrayInputStream resultStream = null;
        try {
           result = retrieveThumbnail(repoUser, repoResource);
           resultStream = (ByteArrayInputStream) result.getThumbnail().getBinaryStream();
        } catch (JSResourceNotFoundException e) {
            return null;
        } catch (JSException e) {
            return null;
        } catch (SQLException e) {
            logger.error(e, e);
            return null;
        }

        return resultStream;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ByteArrayInputStream getReportThumbnail(User user, String reportUri)
    {
        RepoUser repoUser = resolveUser(user);
        RepoResource repoResource = (RepoResource) getPersistentObject(repositoryService.getResource(null, reportUri));

        if (repoResource == null)
            throw new JSException("ReportThumbnailServiceImpl: invalid.resource");
        else if (repoUser == null)
            throw new JSException("ReportThumbnailServiceImpl: invalid.user");

        RepoReportThumbnail result;
        ByteArrayInputStream resultStream;

        try {
            result = retrieveThumbnail(repoUser, repoResource);
            resultStream = (ByteArrayInputStream) result.getThumbnail().getBinaryStream();
        } catch (JSResourceNotFoundException e) {
            return null;
        } catch (JSException e) {
            return null;
        } catch (SQLException e) {
            logger.error(e, e);
            return null;
        }
        return resultStream;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ByteArrayInputStream getDefaultThumbnail()
    {
        if (defaultThumbnail == null) {
            try {
                defaultThumbnail = (ByteArrayInputStream) resourceLoader.getResource(defaultThumbnailPath).getInputStream();

            } catch (Exception e) {
                logger.error(e, e);
                return null;
            }

        } else {
            defaultThumbnail.reset();
        }
        return defaultThumbnail;
    }

    public HibernateRepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(HibernateRepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public UserAuthorityService getUserAuthorityService() {
        return userAuthorityService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    public String getDefaultThumbnailPath() {
        return defaultThumbnailPath;
    }

    public void setDefaultThumbnailPath(String defaultThumbnailPath) {
        this.defaultThumbnailPath = defaultThumbnailPath;
    }
}
