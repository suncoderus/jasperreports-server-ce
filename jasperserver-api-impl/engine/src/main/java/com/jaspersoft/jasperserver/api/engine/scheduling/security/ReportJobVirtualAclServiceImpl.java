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
package com.jaspersoft.jasperserver.api.engine.scheduling.security;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportJobsInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.JasperServerSidRetrievalStrategyImpl;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oleg Gavavka
 *         02.10.2014.
 */
public class ReportJobVirtualAclServiceImpl implements AclService {
    private PermissionGrantingStrategy permissionGrantingStrategy;
    private String administratorRole;
    private ReportJobsInternalService reportJobsInternalService;
    private JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy;

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        return null;
    }

    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        throw new NotFoundException("This method is not supported in ReportJobVirtualAclServiceImpl class");
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        if (!(object instanceof ReportJobObjectIdentity)) {
            throw new NotFoundException("ReportJobVirtualAclServiceImpl support only ReportJobObjectIdentity");
        }
        JasperServerAclImpl acl = new JasperServerAclImpl(object, permissionGrantingStrategy, null);
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        String owner;
        owner = getReportJobsInternalService().getJobOwner((Long) object.getIdentifier());

        // Generate permissions for job owner
        aces.add(new AccessControlEntryImpl(null, acl, sidRetrievalStrategy.getSid(owner), JasperServerPermission.READ_WRITE_DELETE, true, false, false));
        // Generate permission for Admin Role
        if (getAdministratorRole() != null) {
            aces.add(new AccessControlEntryImpl(null, acl, sidRetrievalStrategy.getSid(new SimpleGrantedAuthority(getAdministratorRole())), JasperServerPermission.ADMINISTRATION, true, false, false));
        }

        return new JasperServerAclImpl(object, getPermissionGrantingStrategy(), aces);
    }


    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        throw new NotFoundException("This method is not supported in ReportJobVirtualAclServiceImpl class");
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>();
        for (ObjectIdentity oid : objects) {
            result.put(oid, readAclById(oid, sids));
        }

        return result;
    }

    public void setPermissionGrantingStrategy(PermissionGrantingStrategy permissionGrantingStrategy) {
        this.permissionGrantingStrategy = permissionGrantingStrategy;
    }

    public void setReportJobsInternalService(ReportJobsInternalService reportJobsInternalService) {
        this.reportJobsInternalService = reportJobsInternalService;
    }

    public PermissionGrantingStrategy getPermissionGrantingStrategy() {
        return permissionGrantingStrategy;
    }

    public ReportJobsInternalService getReportJobsInternalService() {
        return reportJobsInternalService;
    }

    public void setSidRetrievalStrategy(JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }

    public String getAdministratorRole() {
        return administratorRole;
    }

    public void setAdministratorRole(String administratorRole) {
        this.administratorRole = administratorRole;
    }

}

