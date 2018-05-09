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
package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Oleg.Gavavka
 */

public class JasperServerAclHelper {
    // this method will scan Acl for AccessControlEntry (ACE) for supplied Sid
    // if this Acl don`t have ACE for sid - will try to lookup in parent
    public static AccessControlEntry locateAceForSid(Acl acl,Sid sid) {
        for(AccessControlEntry ace:acl.getEntries()) {
            if (ace.getSid().equals(sid))
                return ace;
        }
        if (acl.isEntriesInheriting()&&(acl.getParentAcl()!=null)) {
            return locateAceForSid(acl.getParentAcl(),sid);
        }
        return null;
    }

    public static Permission getPermissionForSid(Acl acl,Sid sid) {
        AccessControlEntry ace = locateAceForSid(acl,sid);
        return ace!=null ? ace.getPermission() : JasperServerPermission.NOTHING;
    }

    public static Set<Sid> locateSids(Acl acl,Boolean lookInParent) {
        Set<Sid> result = new HashSet<Sid>();
        for(AccessControlEntry ace: acl.getEntries()) {
            result.add(ace.getSid());
        }
        if (lookInParent&&acl.isEntriesInheriting()&&(acl.getParentAcl()!=null)) {
            result.addAll(locateSids(acl.getParentAcl(),lookInParent));
        }
        return result;


    }
}
