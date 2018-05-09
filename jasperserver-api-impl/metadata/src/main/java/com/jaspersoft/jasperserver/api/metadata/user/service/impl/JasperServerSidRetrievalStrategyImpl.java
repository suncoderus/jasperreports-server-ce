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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.TenantAwareGrantedAuthority;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

/**
 * @author Oleg.Gavavka
 */

public class JasperServerSidRetrievalStrategyImpl implements SidRetrievalStrategy {

    public Sid getSid(Object obj) {
        if (obj instanceof User) {
            String tenant = ((User) obj).getTenantId();
            String user = ((User) obj).getUsername();
            return new PrincipalSid(tenant==null ? user : user.concat("|").concat(tenant));
        }
        if (obj instanceof TenantAwareGrantedAuthority) {
            return new GrantedAuthoritySid(((TenantAwareGrantedAuthority) obj).getAuthority());
        }
        if (obj instanceof GrantedAuthority) {
            return new GrantedAuthoritySid(((GrantedAuthority) obj).getAuthority());
        }
        if (obj instanceof Role) {
            return new GrantedAuthoritySid(new TenantAwareGrantedAuthority((Role) obj).getAuthority());
        }

        if (obj instanceof String) {
            return new PrincipalSid((String) obj);
        }
        return null;
    }

    @Override
    public List<Sid> getSids(Authentication authentication) {
        if (authentication==null) {
            return new ArrayList<Sid>();
        }
        HashSet<Sid> sids = new HashSet<Sid>();
        Object principal = authentication.getPrincipal();
        if (principal!=null) {
            if (principal instanceof User) {
                //Adding User
                sids.add(getSid(principal));
                Set roles = ((User) principal).getRoles();
                for (Object o : roles) {
                    if (o instanceof Role) {
                        sids.add(getSid(o));
                    }
                }
            }
        } else {
            sids.add(getSid(principal));
        }
        if (authentication.getAuthorities()!=null) {
            for(GrantedAuthority authority: authentication.getAuthorities()) {
                sids.add(getSid(authority));
            }
        }
        return new ArrayList<Sid>(sids);
    }
}
