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
package com.jaspersoft.jasperserver.war.dto;

/**
 * @author vsabadosh
 */
public class AwsDBInstanceDTO {

    /**
     * Contains a user-supplied database identifier. This is the unique key
     * that identifies a DB Instance.
     */
    private String dBInstanceIdentifier;

    /**
     * The meaning of this parameter differs according to the database engine
     * you use. <p><b>MySQL</b> <p> Contains the name of the initial database
     * of this instance that was provided at create time, if one was
     * specified when the DB Instance was created. This same name is returned
     * for the life of the DB Instance. <p>Type: String <p><b>Oracle</b> <p>
     * Contains the Oracle System ID (SID) of the created DB Instance.
     */
    private String dBName;

    /**
     * Provides the name of the database engine to be used for this DB
     * Instance.
     */
    private String engine;

    /**
     * Indicates the database engine version.
     */
    private String engineVersion;

    /**
     * Specifies the DNS address of the DB Instance.
     */
    private String address;

    /**
     * Specifies the port that the database engine is listening on.
     */
    private Integer port;
    
    private String amazonDbService;

    private String jdbcTemplate;

    private String jdbcUrl;

    private String jdbcDriverClass;


    /**
     * Default constructor
     */
    public AwsDBInstanceDTO() {

    }

    public String getdBInstanceIdentifier() {
        return dBInstanceIdentifier;
    }

    public void setdBInstanceIdentifier(String dBInstanceIdentifier) {
        this.dBInstanceIdentifier = dBInstanceIdentifier;
    }

    public String getdBName() {
        return dBName;
    }

    public void setdBName(String dBName) {
        this.dBName = dBName;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setAmazonDbService(String amazonDbService) {
        this.amazonDbService = amazonDbService;
    }

    public String getAmazonDbService() {
        return amazonDbService;
    }

    public String getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(String jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcDriverClass(String jdbcDriverClass) {
        this.jdbcDriverClass = jdbcDriverClass;
    }

    public String getJdbcDriverClass() {
        return jdbcDriverClass;
    }
}
