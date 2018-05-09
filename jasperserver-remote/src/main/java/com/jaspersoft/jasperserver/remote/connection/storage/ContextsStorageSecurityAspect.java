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
package com.jaspersoft.jasperserver.remote.connection.storage;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
@Aspect
public class ContextsStorageSecurityAspect {
    @Resource(name = "connectionsCache")
    private Cache cache;
    @Around("execution(* ConnectionDataStorage.save(ConnectionDataPair))")
    public Object saveOwnedContext(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object[] args = joinPoint.getArgs();
        args[0] = getOwnedDataPair((ConnectionDataPair) args[0]);
        return joinPoint.proceed(args);
    }
    @Around("execution(* ConnectionDataStorage.update(java.util.UUID, ConnectionDataPair))")
    public void updateOwnedContext(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object[] args = joinPoint.getArgs();
        args[1] = getOwnedDataPair((ConnectionDataPair) args[1]);
        joinPoint.proceed(args);
    }
    @Before("execution(* ConnectionDataStorage.update(java.util.UUID, ConnectionDataPair)) ||" +
            "execution(* ConnectionDataStorage.get(java.util.UUID)) ||" +
            "execution(* ConnectionDataStorage.delete(java.util.UUID))"
    )
    public void checkOwner(JoinPoint joinPoint){
        final UUID uuid = (UUID) joinPoint.getArgs()[0];
        final Element element = getElement(uuid);
        if(element != null){
            final Object pair = element.getObjectValue();
            if(pair != null){
                if(!(pair instanceof OwnedConnectionDataPair &&
                        getCurrentUserQualifiedName().equals(((OwnedConnectionDataPair) pair).getOwner()))){
                    // throw exception if pair isn't null and it has another owner, than current user or doesn't
                    // have owner at all
                    throw new AccessDeniedException(uuid.toString());
                }
            }
        }
    }

    /**
     * This method is created to avoid mocking of cache (which is mostly final)
     * @param uuid the context UUID
     * @return element with context or null if doesn't exist
     */
    protected Element getElement(UUID uuid){
        return cache.get(uuid);
    }

    protected OwnedConnectionDataPair getOwnedDataPair(ConnectionDataPair source){
        final String userQualifiedName = getCurrentUserQualifiedName();
        return new OwnedConnectionDataPair(source.getConnection(),
                source.getData(), userQualifiedName);
    }

    protected String getCurrentUserQualifiedName(){
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final String tenantId = user.getTenantId();
        return user.getUsername() + (tenantId != null ? "|" + tenantId : "");
    }

    protected static class OwnedConnectionDataPair extends ConnectionDataPair {
        private final String owner;

        public OwnedConnectionDataPair(Object connection, Map<String, Object> data, String owner) {
            super(connection, data);
            this.owner = owner;
        }

        public String getOwner() {
            return owner;
        }
    }

}
