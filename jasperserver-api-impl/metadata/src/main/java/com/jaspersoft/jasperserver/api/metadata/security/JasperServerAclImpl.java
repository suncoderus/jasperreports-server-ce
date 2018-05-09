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

import com.jaspersoft.jasperserver.api.metadata.user.service.impl.InternalURIDefinition;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.model.*;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg.Gavavka
 */

public class JasperServerAclImpl implements Acl {
    private ObjectIdentity objectIdentity;
    private transient Acl parentAcl;
    private ObjectIdentity parentOid=null;
    private Boolean entriesInheriting=false;
    private transient Boolean cacheMarker=false;
    private final List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();


    private transient PermissionGrantingStrategy permissionGrantingStrategy;

    public JasperServerAclImpl(ObjectIdentity objectIdentity,List<AccessControlEntry> aces) {
        this(objectIdentity, (new JasperServerPermissionGrantingStrategy()), aces);
    }
    public JasperServerAclImpl(ObjectIdentity objectIdentity,List<AccessControlEntry> aces, Boolean cacheMarker) {
        this(objectIdentity, (new JasperServerPermissionGrantingStrategy()), aces);
        this.cacheMarker=cacheMarker;
    }

    public JasperServerAclImpl(ObjectIdentity objectIdentity,List<AccessControlEntry> aces,Acl parentAcl) {
        this(objectIdentity, (new JasperServerPermissionGrantingStrategy()), aces);
        if (parentAcl!=null) {
            this.parentAcl=parentAcl;
            this.parentOid=parentAcl.getObjectIdentity();
            this.entriesInheriting=true;

        }
    }



    public JasperServerAclImpl(ObjectIdentity objectIdentity,PermissionGrantingStrategy permissionGrantingStrategy,List<AccessControlEntry> aces) {
        Assert.notNull(objectIdentity, "Object Identity required");
        //Assert.notNull(id, "Id required");

        if (permissionGrantingStrategy==null) {
            this.permissionGrantingStrategy = new JasperServerPermissionGrantingStrategy();
        } else {
            this.permissionGrantingStrategy=permissionGrantingStrategy;
        }
        this.objectIdentity=objectIdentity instanceof InternalURIDefinition ? objectIdentity : new InternalURIDefinition(objectIdentity.getIdentifier().toString());
        //this.id=id;
        if (aces!=null) {
            // Recreating ACE and binding them to this ACL
            for(AccessControlEntry ace: aces) {
                AccessControlEntry entry = new AccessControlEntryImpl(ace.getId(),this,ace.getSid(),ace.getPermission(),ace.isGranting(),false,false);
                this.aces.add(entry);
            }
        }
    }

    @Override
    public List<AccessControlEntry> getEntries() {
        return aces;  
    }

    @Override
    public ObjectIdentity getObjectIdentity() {
        return objectIdentity;  
    }

    @Override
    public Sid getOwner() {
        return null;  
    }

    @Override
    public Acl getParentAcl() {
        return parentAcl;  
    }

    @Override
    public boolean isEntriesInheriting() {
        return entriesInheriting;  
    }

    @Override
    public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode) throws NotFoundException, UnloadedSidException {
        Assert.notEmpty(permission, "Permissions required");
        Assert.notEmpty(sids, "SIDs required");

/*
        if (!this.isSidLoaded(sids)) {
            throw new UnloadedSidException("ACL was not loaded for one or more SID");
        }
*/

        return permissionGrantingStrategy.isGranted(this, permission, sids, administrativeMode);
    }

    @Override
    public boolean isSidLoaded(List<Sid> sids) {
        List<Sid> loadedSids = new ArrayList<Sid>();
        for(AccessControlEntry ace: getEntries()) {
            if (!loadedSids.contains(ace.getSid())) {
                loadedSids.add(ace.getSid());
            }

        }

        return loadedSids.containsAll(sids);  
    }

    public ObjectIdentity getParentOid() {
        return parentOid;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JasperServerAclImpl[");
//        sb.append("id: ").append(this.id).append("; ");
        sb.append("objectIdentity: ").append(this.objectIdentity).append("; ");
//        sb.append("owner: ").append(this.owner).append("; ");

        int count = 0;

        for (AccessControlEntry ace : aces) {
            count++;

            if (count == 1) {
                sb.append("\n");
            }

            sb.append(ace).append("\n");
        }

        if (count == 0) {
            sb.append("no ACEs; ");
        }

        sb.append("inheriting: ").append(this.entriesInheriting).append("; ");
        sb.append("parent: ").append((this.parentAcl == null) ? "Null" : this.parentAcl.getObjectIdentity().toString());
        sb.append("; ");
        sb.append("permissionGrantingStrategy: ").append(this.permissionGrantingStrategy.getClass().getName());
        sb.append("cached: ").append(this.cacheMarker);
        sb.append("]");

        return sb.toString();
    }

    public Boolean getCacheMarker() {
        return cacheMarker;
    }
}
