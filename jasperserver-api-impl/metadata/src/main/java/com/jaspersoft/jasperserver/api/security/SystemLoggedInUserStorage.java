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

import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

/**
 * System logged in user storage, that stores temporary user credentials to allow internal system do
 * successful authorization by userName and password. Originally it was created to allow PhantomJS driver, that is used
 * for dashboard job output generation, connect to the server with external userName and random generated password.
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class SystemLoggedInUserStorage {
    private ResourceFactory objectFactory;
    private Ehcache cache;
    private final String allowPasswordCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";

    /**
     * Stores user in temporary loggedIn user storage, to force security filters allow this credentials.
     *
     * @param userName user name. In tenant environment has pattern {userName}|{tenantId}.
     * @param password user password.
     *
     * @return user
     */
    public User storeUser(String userName, String password) {
        QueryCompositeKey key = new QueryCompositeKey(userName, password);

        User user = (User)objectFactory.newObject(User.class);
        user.setUsername(userName);
        user.setPassword(password);

        Element element = new Element(key, user);

        cache.put(element);

        return user;
    }

    public User storeUserWithRandomPassword(String userName) {
        return storeUser(userName, RandomStringUtils.random(15, allowPasswordCharacters));
    }

    public User loadUserByNameAndPassword(String userName, String password) {
        QueryCompositeKey key = new QueryCompositeKey(userName, password);
        if (cache.get(key) != null) {
            return (User)cache.get(key).getValue();
        }

        return null;
    }

    public void removeUser(String userName, String password) {
        QueryCompositeKey key = new QueryCompositeKey(userName, password);
        if (cache.get(key) != null) {
            cache.remove(key);
        }
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }

    public void setObjectFactory(ResourceFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    private class QueryCompositeKey implements Serializable {
        private String userName;
        private String password;

        public QueryCompositeKey(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QueryCompositeKey that = (QueryCompositeKey) o;

            if (password != null ? !password.equals(that.password) : that.password != null) return false;
            if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = userName != null ? userName.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "QueryCompositeKey{" +
                    "userName='" + userName + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

}
