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
package com.jaspersoft.jasperserver.api.common.service;

import java.util.Map;
import java.util.Set;

/**
 * Interface for a service which should be used for registering jdbc drivers
 * and to check wether jdbc driver was already registered.
 *
 * @author Sergey Prilukin (sprilukin@jaspersoft.com)
 * @version $Id$
 */
public interface JdbcDriverService {

    /**
     * Trying to register driver with given classname in java.sql.DriverManager
     * Using driver mappings from global properties list.
     *
     * @param driverClass name of the driver class
     * @throws Exception
     */
    public void register(String driverClass) throws Exception;

    /**
     * Same as {@link #register(String)} but does not throws an exception.
     *
     * @param driverClass name of the driver class
     * @return true if driver was registered succesfully and false otherwise
     */
    public boolean isRegistered(String driverClass);

    /**
     * Should be only called by Properties Management Service infrastructure
     * Method does synchronization of mappings in global properties and in
     * JdbcDriverService implementation
     *
     * @param driverMappings key is className value is path to jar
     * @throws Exception
     */
    public void setDriverMappings(Map<String, String> driverMappings) throws Exception;

    /**
     * Should be only called by Properties Management Service infrastructure
     * Method does synchronization of mappings in global properties and in
     * JdbcDriverService implementation
     *
     * @param driverClass driver class name
     * @param path path to jar
     * @throws Exception
     */
    public void setDriverMapping(String driverClass, String path) throws Exception;

    /**
     * Remove mapping for passed driver class name and unregister driver if exists
     *
     * @param driverClass driver class name
     * @throws Exception
     */
    public void removeDriverMapping(String driverClass) throws Exception;

    /**
     * Returns current mappings
     *
     * @return current mappings
     */
    public Map<String, String> getDriverMappings();

    /**
     * Returns collection of driver names which are already registered in the system
     * (But only using existing mappings)
     *
     * @return set of drivers from existing mapping which are registered in the system
     */
    public Set<String> getRegisteredDriverClassNames();

    public String getJdbcDriversFolder();

    /**
     * Trying to load driver class from repository using
     * given path. If driver can not be loaded from given location - trows exception,
     * If it is loaded successfully it will be registered and mapping saved
     *
     * @param driverClass driver class name
     * @param path relative path to jar in repository
     * @throws Exception if driver could not be loaded from given path
     */
    public void setDriverMappingAndRegister(String driverClass, String path) throws Exception;

    /**
     * Validate driver, save it, backup old driver if exists.
     * @param driverClassName driver class name
     * @param driverFiles a map where the key is original file name in user`s file system
     *                    and value is byte array which is file data.
     */
    public void setDriver(String driverClassName, Map<String, byte[]> driverFiles) throws Exception;

}
