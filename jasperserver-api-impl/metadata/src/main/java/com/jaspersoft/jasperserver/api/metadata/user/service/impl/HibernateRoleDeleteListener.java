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

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateBeforeDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoTenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Cascade deletion of roles changed to listener because of bug #18501:
 * it seems that cascade works not properly.
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class HibernateRoleDeleteListener implements HibernateBeforeDeleteListener {

    public void beforeDelete(DeleteEvent event) {
        Object o = event.getObject();
        if (o instanceof RepoTenant) {
            deleteRolesByTenant((RepoTenant) o, event.getSession());
        }
    }

    private void deleteRoleFromUsers(RepoRole role) {
		// Get all users that have this role and remove the role from them
		Set userList = role.getUsers();
		for (Iterator it = userList.iterator(); it.hasNext(); ) {
			RepoUser u = (RepoUser) it.next();
			u.removeRole(role);
		}
    }

    protected void deleteRolesByTenant(RepoTenant tenant, EventSource session) {
        Criteria criteria = session.createCriteria(RepoRole.class);
        criteria.add(Restrictions.eq("tenant", tenant));
        List roles = criteria.list();
        if (roles != null && !roles.isEmpty()) {
            for (Iterator it = roles.iterator(); it.hasNext();) {
                RepoRole role = (RepoRole) it.next();
                deleteRoleFromUsers(role);
                session.delete(role);
            }
        }
    }

}
