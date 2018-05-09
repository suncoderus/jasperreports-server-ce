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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

/**
 * Helper Class for determining VM product types.
 *
 * @author ogavavka
 */
public class InstanceProductTypeResolver {
    protected boolean isEC2;
    protected AwsEc2MetadataClient awsEc2MetadataClient = new AwsEc2MetadataClient();
    protected boolean hasMarketplaceCodes = false;
    protected boolean overriding = false;
    protected String productTypeName="";
    protected static final String OVERRIDE_PRODUCT_TYPE_NONE = "" ;
    public static String MARKETPLACE_PREFIX = "MP_";
    public static String NOT_EC2 = "NOT_EC2";
    public static String EC2 = "EC2";
    public static String CONFIG_FILE_NAME="product_type.properties";
    public static String PROPERTY_NAME_OVERRIDE="overrideProductType";


    private static InstanceProductTypeResolver instance;
    /** property to mimic particular product type (for QA and Dev) **/
    private String overrideProductType = OVERRIDE_PRODUCT_TYPE_NONE;
    /** Marketplace product code and type which we use to determine instance type for Amazon AWS **/
    public static String MarketplaceCeProductType = "CE";
    private static String MarketplaceCeProductCode = "9xda1pooiqrx1xrl6127ygztq";
    protected static Map<String,String> mpProductCodes = new HashMap<String, String>(){
        {
            put(MarketplaceCeProductType, MarketplaceCeProductCode);
        };
    };

    public static synchronized InstanceProductTypeResolver getInstance() {
        if (instance == null) {
            instance = new InstanceProductTypeResolver();
        }
        return instance;
    }

    protected InstanceProductTypeResolver(){
        init();
    }
    /** Init method which will check if override is set if not use automatic detection **/
    protected void init(){
        if (!readAndSetOverride()) {
            isEC2 = awsEc2MetadataClient.isEc2Instance();
            if (isEC2) {
                productTypeName=EC2;
                for (String product: mpProductCodes.keySet()) {
                    if (awsEc2MetadataClient.hasAwsProductCode(mpProductCodes.get(product))) {
                        hasMarketplaceCodes =true;
                        productTypeName=product;
                        break;
                    }
                }
            }
        }
    }
    /** Method return true if this instance works under AWS environment **/
    public boolean isEC2() {
        return isEC2;
    }
    // TODO: Add support for NonMarketplace AMIs
    /** Method to determine if this instance was created from our Marketplace AMIs **/
    public boolean isJrsAmi(){
        return isMpAmi();
    }
    public boolean isMpAmi() {
        return hasMarketplaceCodes;
    }
    public String getProductTypeName() {
        return productTypeName;
    }
    public String getProductPrefix(){
        return MARKETPLACE_PREFIX;
    }
    public boolean isOverriding() {
        return overriding;
    }

    public String getAwsProductNameForHeartBeat(){
        if (isEC2) {
            if (isMpAmi()) {
                return getProductPrefix() + getProductTypeName();
            } else {
                return EC2;
            }
        } else {
            return NOT_EC2;
        }
    }

    protected void setOverrideProductType(String overrideProductType) {
        this.overrideProductType = overrideProductType;
        overriding=true;
        if ( !overrideProductType.equals(OVERRIDE_PRODUCT_TYPE_NONE)) {
            hasMarketplaceCodes =mpProductCodes.containsKey(overrideProductType);
            if (hasMarketplaceCodes)
                isEC2=true;
            else
                isEC2=overrideProductType.equals(EC2);
            productTypeName=overrideProductType;
        }
    }

    /** Method try to find override property file and read it for setting override feature
     *  by default it will look for file in WEB-INF/classes folder **/
    protected boolean readAndSetOverride() {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(CONFIG_FILE_NAME);
        if (stream==null)
            return false;
        try {

            prop.load(stream);
            String overrideProperty = prop.getProperty(PROPERTY_NAME_OVERRIDE);
            if (overrideProperty==null)
                return false;
            setOverrideProductType(overrideProperty);
        } catch (IOException ignored) {
            return false;

        }
        return true;
    }

}
