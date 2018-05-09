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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * JRS Client for accessing the Amazon EC2 Instance Metadata Service.
 *
 * @author vsabadosh
 */
public class AwsEc2MetadataClient {

    /** System property for overriding the Amazon EC2 Instance Metadata Service endpoint. */
    public static final String EC2_METADATA_SERVICE_OVERRIDE = "com.amazonaws.sdk.ec2MetadataServiceEndpointOverride";

    /** Default endpoint for the Amazon EC2 Instance Metadata Service. */
    private static final String EC2_METADATA_SERVICE_URL = "http://169.254.169.254";

    /** Default resource paths for Metadata items in the Amazon EC2 Instance Metadata Service. */
    public static final String INSTANCE_ID_RESOURCE = "/latest/meta-data/instance-id/";
    public static final String REGION_RESOURCE = "/latest/meta-data/placement/availability-zone/";
    public static final String MACS_RESOURCE = "/latest/meta-data/network/interfaces/macs/";
    public static final String PRIVATE_IP_RESOURCE = "/latest/meta-data/local-ipv4/";
    public static final String PUBLIC_IP_RESOURCE = "/latest/meta-data/public-ipv4/";
    public static final String PRODUCT_CODES_RESOURCE = "/latest/meta-data/product-codes";
    public static final String INSTANCE_TYPE_RESOURCE = "/latest/meta-data/instance-type";
    public static final String SECURITY_CREDENTIALS_RESOURCE = "/latest/meta-data/iam/security-credentials/";
    public static final String DOCUMENT_RESOURCE = "/latest/dynamic/instance-identity/document";
    public static final String ACCOUNT_ID = "accountId";
    public static final String AWS_DOMAIN = ".amazonaws.com";
    public static final String USER_DATA = "/latest/user-data/";
    
    public static int SECOND = 1000;

    public static final String VPC_ID = "vpc-id";


    private static final Log log = LogFactory.getLog(AwsEc2MetadataClient.class);

    private static Map<String, String> metadataValuesCache = new HashMap<String, String>();

    private List<String> awsRegions;
    
    public AwsEc2MetadataClient() {

    }

    public void setAwsRegions(List<String> awsRegions) {
        this.awsRegions = awsRegions;
    }

    /**
     *
     * @param resource the resource path for target EC2 Instance Metadata item(ec. instance-id, instance-type, etc).
     * @return value of  EC2 Instance Metadata item
     */
    public String getEc2InstanceMetadataItem(String resource) {
        if (!metadataValuesCache.containsKey(resource)) {
            String value = readResource(resource);
            metadataValuesCache.put(resource, value);
            return value;
        } else {
            return metadataValuesCache.get(resource);
        }
    }

    public Boolean hasAwsProductCode(String jsProductCode) {
        if (!isNotEmpty(jsProductCode)) {
            return false;
        }
        List<String> ec2InstanceProductCodes = parseValueAsList(getEc2InstanceMetadataItem(PRODUCT_CODES_RESOURCE));
        if (ec2InstanceProductCodes !=null && ec2InstanceProductCodes.size() > 0) {
            for (String productCode : ec2InstanceProductCodes) {
                if (productCode.equals(jsProductCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean hasAwsAccountId(String jsAmazonAccountId) {
        if (!isNotEmpty(jsAmazonAccountId)) {
            return false;
        }
        String accountId = parsePropertyValueFromJson(getEc2InstanceMetadataItem(DOCUMENT_RESOURCE), ACCOUNT_ID);
        return (accountId != null && accountId.equals(jsAmazonAccountId));
    }

    public Boolean hasSecurityCredentials() {
        String securityCredentialsList = getEc2InstanceMetadataItem(SECURITY_CREDENTIALS_RESOURCE);
        return securityCredentialsList !=null && securityCredentialsList.trim().length() > 0;
    }

    public Boolean isEc2InstanceInTargetVpc(String vpcId) {
        if (!isNotEmpty(vpcId) && !isEc2InstanceInVpc()) {
            return true;
        }
        if (isNotEmpty(vpcId)) {
            List<String> ec2MacAddresses = parseValueAsList(getEc2InstanceMetadataItem(MACS_RESOURCE));
            if (ec2MacAddresses != null) {
                for (String ec2MacAddress : ec2MacAddresses) {
                    String ec2VpcId = getEc2InstanceMetadataItem(MACS_RESOURCE + ec2MacAddress + VPC_ID);
                    if (ec2VpcId != null && ec2VpcId.trim().equals(vpcId.trim())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isEc2InstanceInVpc() {
        List<String> ec2MacAddresses = parseValueAsList(getEc2InstanceMetadataItem(MACS_RESOURCE));
        if (ec2MacAddresses != null && ec2MacAddresses.size() > 0) {
            for (String ec2MacAddress : ec2MacAddresses) {
                List<String> networkInformation = parseValueAsList(getEc2InstanceMetadataItem(MACS_RESOURCE + ec2MacAddress));
                if (networkInformation != null && networkInformation.size() > 0) {
                    if (networkInformation.contains(VPC_ID)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isEc2Instance() {
        return getAwsAccountId() != null;
    }

    public String getEc2InstanceId() {
        return getEc2InstanceMetadataItem(AwsEc2MetadataClient.INSTANCE_ID_RESOURCE);
    }
    public String getAwsAccountId() {
        return parsePropertyValueFromJson(getEc2InstanceMetadataItem(DOCUMENT_RESOURCE), ACCOUNT_ID);
    }
    public String getEc2InstanceRegion() {
        return parseRegionFromSubRegion(getEc2InstanceMetadataItem(AwsEc2MetadataClient.REGION_RESOURCE));
    }

    public String getEc2InstanceFullRegion() {
        return getEc2InstanceMetadataItem(AwsEc2MetadataClient.REGION_RESOURCE) + AWS_DOMAIN;
    }

    private String parseRegionFromSubRegion(String region) {
        if (region != null) {
            for (String awsRegion : awsRegions) {
                String[] parse = awsRegion.split(AWS_DOMAIN);
                if (region.contains(parse[0])) {
                    return awsRegion;
                }
            }
        }
        return null;
    }

    private List<String> parseValueAsList(String value) {
        if (value != null && value.trim().length() > 0) {
            String[] values = value.trim().split("\n");
            List<String> valueList = new ArrayList<String>();
            for (String val : values) {
                valueList.add(val.trim());
            }
            return valueList;
        }
        return null;
    }

    public String parsePropertyValueFromJson(String jsonText, String property) {
        if (isEmpty(jsonText) || isEmpty(property)) {
            return null;
        }

        Matcher matcher = Pattern.compile("(\"" + property + "\")\\s*:\\s*\"([^\"]+)").matcher(jsonText);
        if (matcher.find()) {
            return matcher.group(2);
        }   else {
            return null;
        }
    }

    private URL getEc2MetadataServiceUrlForResource(String resourcePath) throws Exception{
        String endpoint = EC2_METADATA_SERVICE_URL;
        if (System.getProperty(EC2_METADATA_SERVICE_OVERRIDE) != null) {
            endpoint = System.getProperty(EC2_METADATA_SERVICE_OVERRIDE);
        }

        return new URL(endpoint + resourcePath);
    }

    private  String readResource(String resourcePath)  {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = getEc2MetadataServiceUrlForResource(resourcePath);
            log.debug("Connecting to EC2 instance metadata service at URL: " + url.toString());
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(SECOND * 2);
            connection.setReadTimeout(SECOND * 7);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            if (connection.getResponseCode() >= HttpURLConnection.HTTP_OK && connection.getResponseCode() <
                    HttpURLConnection.HTTP_BAD_REQUEST) {
                return readResponse(connection);
            }  else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();

        try {
            StringBuilder buffer = new StringBuilder();
            while (true) {
                int c = inputStream.read();
                if (c == -1) break;
                buffer.append((char)c);
            }

            return buffer.toString();
        } finally {
            inputStream.close();
        }
    }

    public List<String> getEc2UserDataAsList() {
        return parseValueAsList(readResource(USER_DATA));
    }

    public String getEc2UserData() {
        return readResource(USER_DATA);
    }

    public String getEc2UserDataProperty(String propertyName,String defaultValue) {
        Properties prop = new Properties();
        try {
            if (getEc2UserData()!=null) {
                prop.load(new StringReader(getEc2UserData()));
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            return defaultValue;
        }
        return prop.getProperty(propertyName,null);
    }

}
