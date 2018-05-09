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

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateReportJobsUserDeleteListener implements HibernateBeforeDeleteListener {

	public void beforeDelete(DeleteEvent event) {
		Object o = event.getObject();
		if (o instanceof RepoUser) {
			deleteJobs((RepoUser) o, event.getSession());
		}
	}

	protected void deleteJobs(RepoUser user, EventSource session) {
		Criteria criteria = session.createCriteria(PersistentReportJob.class);
		criteria.add(Restrictions.eq("owner", user));
		List jobs = criteria.list();
		if (jobs != null && !jobs.isEmpty()) {
			for (Iterator it = jobs.iterator(); it.hasNext();) {
				PersistentReportJob job = (PersistentReportJob) it.next();
				session.delete(job);
			}
		}
	}

}
