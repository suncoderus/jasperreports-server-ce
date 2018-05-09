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

package com.jaspersoft.jasperserver.api.metadata.security;

import org.springframework.security.acls.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg.Gavavka
 */

public class JasperServerPermissionGrantingStrategy implements PermissionGrantingStrategy {

    protected Boolean checkPermissionsForSid(List<AccessControlEntry> aces, Sid sid,List<Permission> permissions) {
        for (AccessControlEntry ace : aces ) {
            if (ace.getSid().equals(sid)) {
                //we found matching SID in ACE - so this permission will override all parents permissions
                int acePermissionMask = ace.getPermission().getMask();
                for (Permission requiredPermission : permissions) {
                    // Compare permissions by using bit operations because ACE entry can have set of permissions
                    // this comparision was used by default in SpringSecurity 2.0.7
                    if ((acePermissionMask & requiredPermission.getMask())== requiredPermission.getMask())  {
                        // Found a matching Permission, so its authorization decision will prevail
                        // Success
                        return true;
                    }
                }
                //We found some permission for SID but permissions don`t match
                // Fail for this sid
                return false;
            }
        }
        // We can`t find any ACE entry for this SID
        return null;
    }

    @Override
    public boolean isGranted(Acl acl, List<Permission> permissions, List<Sid> sidList, boolean administrativeMode) {
        final List<AccessControlEntry> aces = acl.getEntries();

        List<Sid> unknownSidStatus = new ArrayList<Sid>();
        Boolean sidPermitted;
        for (Sid sid: sidList) {
            sidPermitted = checkPermissionsForSid(aces,sid,permissions);
            if (sidPermitted!=null) {
                if (sidPermitted) {
                    // Success
                    return true;
                }
            } else {
                //Sid was not found in this ACL, add it for parent scan
                unknownSidStatus.add(sid);
            }
        }

        // No matches have been found so far
        if (acl.isEntriesInheriting() && (acl.getParentAcl() != null) && (!unknownSidStatus.isEmpty())) {
            // We have a parent, so let them try to find a matching ACE
            return acl.getParentAcl().isGranted(permissions, unknownSidStatus, false);
        } else {
            if (unknownSidStatus.isEmpty()) {
                // we don`t have any parent and we resolve all Sids - so we deny
                return false;
            }
//            We either have no parent, or we're the uppermost parent - by default we
            //throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
            return false;
        }
    }
}
