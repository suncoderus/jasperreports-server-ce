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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.properties.PropertyChangerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 * @author agodovanets@jaspersoft.com
 * @version $Id$
 */
public class AwsPropertyChanger extends PropertyChangerAdapter {
    final public static String PROPERTY_PREFIX = "aws.";

    protected static final Log log = LogFactory.getLog(AwsPropertyChanger.class);

    private AwsProperties awsProperties;
    
    @Override
    public void setProperty(String key, String val) {
        assert (key.startsWith(PROPERTY_PREFIX));
        log.debug("setting AWS property: " + key + " - " + val);
        AwsProperties.Property property = AwsProperties.Property.fromString(key);

        if (AwsProperties.Property.DB_SECURITY_GROUP_CHANGES_ENABLED.equals(property)) {
            awsProperties.setSecurityGroupChangesEnabled(Boolean.valueOf(val));
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_NAME.equals(property)) {
            awsProperties.setSecurityGroupName(val);
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_DESCRIPTION.equals(property)) {
            awsProperties.setSecurityGroupDescription(val);
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_INGRESS_PUBLIC_IP.equals(property)) {
            awsProperties.setSecurityGroupIngressPublicIp(val);
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS.equals(property)) {
            awsProperties.setSuppressEc2CredentialsWarnings(Boolean.valueOf(val));
        } else {
            throw new RuntimeException("Unknown AWS config property: "+key);
        }
    }

    @Override
    public String getProperty(String key) {
        assert (key.startsWith(PROPERTY_PREFIX));
        log.debug("getting AWS property: " + key);
        AwsProperties.Property property = AwsProperties.Property.fromString(key);

        if (AwsProperties.Property.DB_SECURITY_GROUP_CHANGES_ENABLED.equals(property)) {
            return Boolean.toString(awsProperties.isSecurityGroupChangesEnabled());
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_NAME.equals(property)) {
            return awsProperties.getSecurityGroupName();
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_DESCRIPTION.equals(property)) {
            return awsProperties.getSecurityGroupDescription();
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_INGRESS_PUBLIC_IP.equals(property)) {
            return awsProperties.getSecurityGroupIngressPublicIp();
        } else if (AwsProperties.Property.DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS.equals(property)) {
            return Boolean.toString(awsProperties.isSuppressEc2CredentialsWarnings());
        } else {
            throw new RuntimeException("Unknown AWS config property: "+key);
        }
    }

    public Map<String, String> getProperties() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (AwsProperties.Property key : AwsProperties.Property.values()) {
            map.put(key.toString(), getProperty(key.toString()));
        }
        return map;
    }

    public void setAwsProperties(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

}
