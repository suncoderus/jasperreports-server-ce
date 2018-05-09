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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ComparableBlob;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReportThumbnailServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoReportThumbnail;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.RepoReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ReportThumbnailServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    PartialMock<ReportThumbnailServiceImpl> thumbnailService;

    Mock<HibernateRepositoryServiceImpl> repositoryService;
    Mock<SessionFactory> sessionFactory;
    Mock<UserAuthorityServiceImpl> userAuthorityService;
    Mock<HibernateTemplate> hibernateTemplate;

    User jasperadminUser = new UserImpl();
    ReportUnit reportUnit = new ReportUnitImpl();

    RepoUser repoJasperAdmin = new RepoUser();
    RepoReportUnit repoReportUnit = new RepoReportUnit();

    RepoReportThumbnail repoThumbnail = new RepoReportThumbnail();

    byte[] thumbnailBytes = new byte[] { (byte) 0xDE,
                                         (byte) 0xAD,
                                         (byte) 0xBE,
                                         (byte) 0xEF  };

    ByteArrayOutputStream thumbnail = new ByteArrayOutputStream();

    @Before
    public void before() throws IOException {
        IOUtils.write(thumbnailBytes, thumbnail);

        // Configure UserAuthorityService
        userAuthorityService.returns(repoJasperAdmin).getPersistentObject(jasperadminUser);

        // Configure Hibernate search
        hibernateTemplate.returns(Arrays.asList(new RepoReportThumbnail[]{repoThumbnail})).findByCriteria(null);

        // Configure ThumbnailService
        thumbnailService.getMock().setRepositoryService(repositoryService.getMock());
        thumbnailService.getMock().setUserAuthorityService(userAuthorityService.getMock());
        thumbnailService.getMock().setSessionFactory(sessionFactory.getMock());
        thumbnailService.getMock().setHibernateTemplate(hibernateTemplate.getMock());
        thumbnailService.returns(repoJasperAdmin).getPersistentObject(jasperadminUser);

        // Configure RepositoryService
        repositoryService.returns(repoReportUnit).getRepoResource(reportUnit);

        repoThumbnail.setResource(repoReportUnit);
        repoThumbnail.setThumbnail(new ComparableBlob(thumbnailBytes));
        repoThumbnail.setUser(repoJasperAdmin);
    }

    @Test(expected = JSException.class)
    public void saveReportThumbnail_NullResource_Test() {
        thumbnailService.getMock().saveReportThumbnail(thumbnail, jasperadminUser, null);
        thumbnailService.assertInvoked().getPersistentObject(jasperadminUser);
        repositoryService.assertInvoked().getRepoResource(null);

    }

    @Test
    public void getReportThumbnail_ValidData_Test() {
        thumbnailService.getMock().getReportThumbnail(jasperadminUser, reportUnit);
        thumbnailService.assertInvoked().getPersistentObject(reportUnit);
        userAuthorityService.assertInvoked().getPersistentObject(jasperadminUser);
        hibernateTemplate.assertInvoked().findByCriteria(null);
    }

    @Test
    public void saveReportThumbnail_ValidData_Test() {
        thumbnailService.getMock().saveReportThumbnail(thumbnail, jasperadminUser, reportUnit);
        thumbnailService.assertInvoked().getPersistentObject(jasperadminUser);
        repositoryService.assertInvoked().getRepoResource(reportUnit);

        thumbnailService.getMock().getHibernateTemplate().saveOrUpdate(repoThumbnail);

        hibernateTemplate.assertInvoked().saveOrUpdate(repoThumbnail);

    }


}
