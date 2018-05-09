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

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.RoleManagerService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PaginationHelper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation for {@link RoleManagerService}.
 *
 * @author Stas Chubar
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleManagerServiceImpl extends HibernateDaoImpl implements RoleManagerService {

    protected static final Log log = LogFactory.getLog(RoleManagerServiceImpl.class);

    private AbstractPlatformTransactionManager transactionManager;
    private UserAuthorityService userService;

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserService(UserAuthorityService userService) {
        this.userService = userService;
    }

    public PaginatedOperationResult getUsersWithoutRole(final ExecutionContext context, final String roleName, final String userName,
                                    Set assignedUserNameSet, Set unassignedUserNameSet,
                                    int firstResult, int maxResults) {

        PaginatedOperationResult result;
        
        TransactionStatus transaction = null;

        try {

            // start transaction
            transaction = transactionManager.getTransaction(
                    new DefaultTransactionDefinition(
                            TransactionDefinition.PROPAGATION_REQUIRES_NEW));

            userService.assignUsers(context, roleName, assignedUserNameSet);
            userService.unassignUsers(context, roleName, unassignedUserNameSet);

            getHibernateTemplate().flush();

            result = PaginationHelper.paginatedGetOperationResult(firstResult, maxResults, new PaginationHelper.JasperService() {
                public List getResultList(int firstResult, int maxResults) {
                    return userService.getUsersWithoutRole(context, roleName, userName, firstResult, maxResults);
                }

                public int getResultCount() {
                    return userService.getUsersCountWithoutRole(context, roleName, userName);
                }
            });

        } finally {
            if (transaction != null && !transaction.isCompleted()) {
                try {
                    // rollback
                    transactionManager.rollback(transaction);
                } catch (Exception e) {
                    // suppress exception
                }
            }
        }


        return result;
    }

    public PaginatedOperationResult getUsersWithRole(final ExecutionContext context, final String roleName, final String userName,
                                 Set assignedUserNameSet, Set unassignedUserNameSet,
                                 int firstResult, int maxResults) {

        PaginatedOperationResult result;

        TransactionStatus transaction = null;
        try {

            // start transaction
            transaction = transactionManager.getTransaction(
                    new DefaultTransactionDefinition(
                            TransactionDefinition.PROPAGATION_REQUIRES_NEW));

            userService.assignUsers(context, roleName, assignedUserNameSet);
            userService.unassignUsers(context, roleName, unassignedUserNameSet);

            getHibernateTemplate().flush();

            result = PaginationHelper.paginatedGetOperationResult(firstResult, maxResults, new PaginationHelper.JasperService() {
                public List getResultList(int firstResult, int maxResults) {
                    return userService.getUsersWithRole(context, roleName, userName, firstResult, maxResults);
                }

                public int getResultCount() {
                    return userService.getUsersCountWithRole(context, roleName, userName);
                }
            });

        } finally {
            if (transaction != null && !transaction.isCompleted()) {
                try {
                    // rollback
                    transactionManager.rollback(transaction);
                } catch (Exception e) {
                    // suppress exception
                }
            }
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateRole(ExecutionContext context, String roleName, Role roleDetails, Set<User> assignedUserSet,
            Set<User> unassignedUserSet) {
        userService.assignUsers(context, roleName, toNameSet(assignedUserSet));
        userService.unassignUsers(context, roleName, toNameSet(unassignedUserSet));

        userService.updateRole(null, roleName, roleDetails);
    }

    protected Set<String> toNameSet(Set<User> users) {
        Set<String> names = new HashSet<String>();

        for(User user : users) {
            names.add(user.getUsername());
        }

        return names;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteAll(ExecutionContext context, List<String> roles) {
        if (roles != null) {
            for(String roleName : roles) {
                if (roleName != null && roleName.length() > 0) {
                    userService.deleteRole(context, roleName);
                }
            }
        }
    }
}

