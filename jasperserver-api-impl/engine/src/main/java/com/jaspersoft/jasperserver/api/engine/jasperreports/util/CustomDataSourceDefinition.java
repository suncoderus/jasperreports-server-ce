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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import org.springframework.beans.*;

/**
 * @author bob
 * This class is meant to be instantiated as a spring bean that registers a custom data source with the system.
 *
 */
public class CustomDataSourceDefinition implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8081959598490729657L;
    public static final String PARAM_NAME = "name";
    public static final String PARAM_LABEL = "label";
    public static final String PARAM_DEFAULT = "default";
    // this is a param that doesn't get edited, such as a bean; in that case, you need a default
    public static final String PARAM_HIDDEN = "hidden";

    // master factory
    private transient CustomReportDataSourceServiceFactory factory;
    // custom factory (optional)
    private CustomDelegatedDataSourceServiceFactory customFactory;
    private String name;
    private String serviceClassName;
    private CustomDataSourceValidator validator;
    private List<Map<String, Object>> propertyDefinitions;
    private Map<String, String> queryExecuterMap;
    public static final String PROPERTY_MAP = "propertyMap";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This is always set to the CustomReportDataSourceServiceFactory which is instantiated with the other RDSSF's
     * in applicationContext.xml.
     * We then need to register ourselves with that factory so it knows about us.
     *
     * @param factory
     */
    public void setFactory(CustomReportDataSourceServiceFactory factory) {
        this.factory = factory;
        factory.addDefinition(this);
    }

    public CustomReportDataSourceServiceFactory getFactory() {
        return factory;
    }

    /**
     * The name of the custom impl of ReportDataSourceService.
     * @param serviceClassName
     */
    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    /**
     * (optional) the name of a validator that's used to check the params
     * @param validator
     */
    public void setValidator(CustomDataSourceValidator validator) {
        this.validator = validator;
    }

    public CustomDataSourceValidator getValidator() {
        return validator;
    }

    /**
     * a list of parameter defs.
     * Each param is a map of props. Here are current valid props:
     *  name: name used to refer to the prop--in particular, used as the key in the persisted custom data source prop map
     *  label (auto generated): name of a label for this parameter to get out of a message catalog
     *  type:
     *  mandatory:
	 * @param propertyDefinitions
	 */
	public void setPropertyDefinitions(List<Map<String, Object>> propertyDefinitions) {
        if (this.propertyDefinitions  == null) this.propertyDefinitions = new ArrayList<Map<String, Object>>();
		// auto generate labels
		for (Map<String, Object> pd : propertyDefinitions) {
			Map<String, Object> newPd = new HashMap<String, Object>(pd);
			// create label name
			newPd.put(PARAM_LABEL, getParameterLabelName((String) newPd.get(PARAM_NAME)));
			addOrReplacePropertyDefinition(newPd);
		}
	}

    public void addOrReplacePropertyDefinition(Map<String, Object> newPd) {
        for (Map<String, ?> pd : propertyDefinitions) {
            if (pd.get(PARAM_NAME).equals(newPd.get(PARAM_NAME))) {
                propertyDefinitions.remove(pd);
                break;
            }
        }
        propertyDefinitions.add(newPd);
    }

    public List<Map<String, Object>> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    /**
     * message name used as label
     */
    public String getLabelName() {
        return name + ".name";
    }

    /**
     * message names for params
     * @param paramName
     * @return
     */
    public String getParameterLabelName(String paramName) {
        return name + ".properties." + paramName;
    }

    /**
     * utility function for the jsp--return just the editable param defs
     * @return
     */
    public List<Map<String, Object>> getEditablePropertyDefinitions() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(propertyDefinitions);
        Iterator<Map<String, Object>> pdi = list.iterator();
        while (pdi.hasNext()) {
            Map<String, Object> pd = pdi.next();
            // if hidden, delete from list
            String hidden = (String) pd.get(PARAM_HIDDEN);
            if (Boolean.parseBoolean(hidden)) {
                pdi.remove();
            }
        }
        return list;
    }

    /**
     * Map with query languages (used language attribute of JRXML queryString element)
     * as keys, and JRQueryExecuterFactory class names as values
     * @return
     */
    public Map<String, String> getQueryExecuterMap() {
        return queryExecuterMap;
    }

    /**
     * register query executer factories with JR
     * @param queryExecuterMap
     */
    public void setQueryExecuterMap(Map<String, String> queryExecuterMap) {
        this.queryExecuterMap = queryExecuterMap;
        if (queryExecuterMap == null) {
            return;
        }
        JRPropertiesUtil propertiesUtil = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
        Iterator<String> qei = queryExecuterMap.keySet().iterator();
        while (qei.hasNext()) {
            String lang = qei.next();
            String qefClassName = queryExecuterMap.get(lang);
            // set property that will allow jr to look up qe factory
			propertiesUtil.setProperty(QueryExecuterFactory.QUERY_EXECUTER_FACTORY_PREFIX + lang, qefClassName);
        }
    }

    /**
     * Initialize a CustomReportDataSource instance with the defaults specified
     * @param cds
     * @param b
     */
    public void setDefaultValues(CustomReportDataSource cds) {
        if (cds.getPropertyMap() == null) {
            cds.setPropertyMap(new HashMap<String, Object>());
        }
        for (Map<String, Object> pd : propertyDefinitions) {
            String name = (String) pd.get(PARAM_NAME);
            if (isHiddenProperty(pd)) continue;
            Object def = pd.get(PARAM_DEFAULT);
            String value = (String) cds.getPropertyMap().get(name);
            if (value == null) {
                if (def == null) {
                    def = "";
                }
                // assume that defaults for non-hidden props are strings
                cds.getPropertyMap().put(name, def.toString());
            }
        }
    }

    // return whether it is hidden property
    public boolean isHiddenProperty(Map<String, ?> dsProperty) {
        String hidden = (String) dsProperty.get(CustomDataSourceDefinition.PARAM_HIDDEN);
        return (Boolean.parseBoolean(hidden));
    }

    /**
     * Initialize a ReportDataSourceService by using introspection to map
     * properties on the CustomReportDataSource to setters.
     * If the property is not set on the CRDS, use the defaults from the prop definitions.
     *
     * @param customDataSource
     * @param service
     */
    public void setDataSourceServiceProperties(
            CustomReportDataSource customDataSource,
            ReportDataSourceService service) {
        // use spring for introspection help
        BeanWrapperImpl bw = new BeanWrapperImpl(service);
        // use "propertyMap" for passing params if you want that...
        Map<String, Object> propMap = new HashMap<String, Object>();
        // set params
        for (Map<String, Object> pd : getPropertyDefinitions()) {
            String name = (String) pd.get(PARAM_NAME);
            Object deflt = pd.get(PARAM_DEFAULT);
            Object value = customDataSource.getPropertyMap().get(name);
            if (value == null && deflt != null) {
                value = deflt;
            }
            // set prop if it's writeable
            if (value != null) {
                if (bw.isWritableProperty(name)) {
                    bw.setPropertyValue(name, value);
                }
                propMap.put(name, value);
            }
        }
        // pass all prop values as map if available
        if (bw.isWritableProperty(PROPERTY_MAP)) {
            bw.setPropertyValue(PROPERTY_MAP, propMap);
        }
    }


    public void setCustomFactory(CustomDelegatedDataSourceServiceFactory customFactory) {
        this.customFactory = customFactory;
        customFactory.setCustomDataSourceDefinition(this);
    }

    public CustomDelegatedDataSourceServiceFactory getCustomFactory() {
        return customFactory;
    }
}
