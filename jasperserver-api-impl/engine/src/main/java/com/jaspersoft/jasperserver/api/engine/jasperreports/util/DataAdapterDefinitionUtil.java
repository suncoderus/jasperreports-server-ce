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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import net.sf.jasperreports.data.json.JsonExpressionLanguageEnum;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import org.apache.commons.lang3.LocaleUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 9/19/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataAdapterDefinitionUtil {

    private static String FILE_NAME_PROP = "fileName";

    /***  HELPER FUNCTIONS TO CAST OBJECT TO STRING (unfornately, CustomReportDataSource only supports STRING values)  ***/

    protected static Class findType(String name, PropertyDescriptor[] propertyDescriptors) {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (!propertyDescriptor.getName().equals(name)) continue;
            return propertyDescriptor.getPropertyType();
        }
        return null;
    }

    static public String toString(Object value, Class type) {
        if (value == null) return "";
        if (type == List.class) {
            String result = null;
            for (Object subValue : (List) value) {
                if (result == null) result = "\"" + (subValue != null? subValue.toString() : "") + "\"" ;
                else result = result + ", \"" + (subValue != null? subValue.toString() : "") + "\"";
            }
            return (result != null? result : "");
        } else if (type == TimeZone.class) {
            return ((TimeZone)value).getID();
        } else if (type == Locale.class) {
            return ((Locale)value).toString();
        }
        return value.toString();
    }

    static public Object toObject(String value, Class type) {
        if (type == null) return  value;
        if (type == List.class) {
            return toStringArray(value);
        } else if ((type == Boolean.class) || type.getName().equals("boolean")) {
            return (new Boolean(value)).booleanValue();
        } else if (type == TimeZone.class) {
                return TimeZone.getTimeZone(value);
        } else if (type == Locale.class) {
            return LocaleUtils.toLocale(value);
        } else if (type == JsonExpressionLanguageEnum.class) {
            return JsonExpressionLanguageEnum.getByName(value.toLowerCase());
        }
        return value;
    }

    static ArrayList<String> toStringArray(String strList) {
        if (strList == null) return null;
        ArrayList<String> arrayList = new ArrayList<String>();
        Scanner scanner = new Scanner(strList);
        Pattern pattern = Pattern.compile("\"[^\"]*\"" + "|'[^']*'" +"|[A-Za-z']+"); // double quoted, single quoted, everything else
        String token;
        while ((token = scanner.findInLine(pattern)) != null) {
            if ((token.length() > 1) && (token.startsWith("'") && token.endsWith("'")) ||
                    (token.startsWith("\"") && token.endsWith("\""))) {
                token = token.substring(1, token.length() - 1);
            }
            arrayList.add(token);
        }
        return arrayList;
    }

    /**
     * utility function to convert parameters to JRParameters
     */
    public static Map convertToFillParameters(Map params, Object[] builtInParams) throws Exception {
        JRFillObjectFactory fof = new JRFillObjectFactory((JRBaseFiller) null, null);
        Map<String, JRValueParameter>  fillParamMap = new LinkedHashMap<String, JRValueParameter>();
        JRDesignParameter dp = new JRDesignParameter();
        dp.setName(JRParameter.REPORT_PARAMETERS_MAP);
        dp.setValueClass(params.getClass());
        dp.setValueClassName(params.getClass().getName());
        JRFakeFillParameter ffp = new JRFakeFillParameter(dp, fof);
        ffp.setValue(params);
        fillParamMap.put(JRParameter.REPORT_PARAMETERS_MAP, ffp);

        Iterator pi = params.keySet().iterator();
        while (pi.hasNext()) {
            String name = (String) pi.next();
            Object value = params.get(name);
            dp = new JRDesignParameter();
            dp.setName(name);
            if (value != null) {
            	dp.setValueClass(value.getClass());
            	dp.setValueClassName(value.getClass().getName());
            }
            ffp = new JRFakeFillParameter(dp, fof);
            ffp.setValue(value);
            fillParamMap.put(name, ffp);
        }
        // i don't know why they didn't use a map for builtInParams...
        // this is a funky array which has alternate String and Class elements
        // make sure that you have a value param for anything that had null,
        // because the queryExecuter will blow up otherwise (a really useful behavior)
        if (builtInParams == null) return fillParamMap;
        for (int i = 0; i < builtInParams.length; i += 2) {
            String name = (String) builtInParams[i];
            if (! fillParamMap.containsKey(name)) {
                dp = new JRDesignParameter();
                dp.setName(name);
                try {
                    Class cl = (Class) Class.forName((String)builtInParams[i + 1]);
                    dp.setValueClass(cl);
                    dp.setValueClassName(cl.getName());
                } catch (Exception ex) {
                    // ignore
                }
                ffp = new JRFakeFillParameter(dp, fof);
                fillParamMap.put(name, ffp);
            }
        }
        return fillParamMap;
    }

    private static class JRFakeFillParameter extends JRFillParameter {

        protected JRFakeFillParameter(JRParameter param, JRFillObjectFactory fillfac) {
            super(param, fillfac);
        }

    }

    /**
     * obtain the tenant information from REPO path
     * for example:  original repo path:  repo:/reports/interactive/CsvData|organization_1
     * return tenant (organization) uri:  /organizations/organization_1/
     **/
    public static String getDataSourceUri(CustomReportDataSource customDataSource, TenantService tenantService) {
        String fileName = (String) customDataSource.getPropertyMap().get(FILE_NAME_PROP);
        if (fileName != null) {
            int sepIndex = fileName.lastIndexOf("|");
            if (fileName.startsWith("repo:/") && (sepIndex > 0) && (!fileName.endsWith("|"))) {

                final String tenantId = fileName.substring(sepIndex + 1);
                final Tenant tenant = tenantService.getTenant(null, tenantId);
                if (tenant != null) {
                    return tenant.getTenantUri();
                } else {
                    throw new IllegalStateException("Invalid tenant ID: " + tenantId);
                }
            }
        }

        return customDataSource.getURIString();
    }
}
