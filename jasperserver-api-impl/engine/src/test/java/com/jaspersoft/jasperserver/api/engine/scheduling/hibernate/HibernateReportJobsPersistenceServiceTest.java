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

package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.BaseUnitTest;
import com.jaspersoft.jasperserver.api.engine.common.user.UserPersistenceHandler;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Taras Matyashovsky
 */
public class HibernateReportJobsPersistenceServiceTest extends BaseUnitTest {

    @TestedObject
    private HibernateReportJobsPersistenceService hibernateReportJobsPersistenceService;

    @InjectInto(property = "userHandler")
    private Mock<UserPersistenceHandler> userHandler;

    @InjectInto(property = "hibernateTemplate")
    private Mock<HibernateTemplate> hibernateTemplate;

    @Test
    public void listJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Creating stub for output persistent report jobs. */
        List<PersistentReportJob> persistentReportJobs = new ArrayList<PersistentReportJob>();
        PersistentReportJob persistentReportJob = new PersistentReportJob();
        persistentReportJob.setSource(new PersistentReportJobSource());
        persistentReportJobs.add(persistentReportJob);

        /* Hibernate template should once return persistent report jobs. */
        hibernateTemplate.onceReturns(persistentReportJobs).loadAll(PersistentReportJob.class);

        /* Target method invocation. */
        List jobs = hibernateReportJobsPersistenceService.listJobs(executionContext);

        /* Assertions. */
        assertNotNull("Jobs should not be null", jobs);
        assertEquals(1, jobs.size());

        /* Hibernate template have been invoked from tested object with request to load all persistent report jobs. */
        hibernateTemplate.assertInvoked().loadAll(PersistentReportJob.class);
    }
	
}
