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

package com.jaspersoft.jasperserver.api.common.util.spring;

import com.jaspersoft.jasperserver.api.JSException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author bob
 *
 */
public class GenericBeanUpdater extends AbstractBeanPropertyProcessor {

    public static final String TYPE_STRING = "string";
    public static final String TYPE_SECURITY_METADATA_SOURCE = "securityMetadataSource";
    public static final String TYPE_FILTER_CHAIN_MAP = "filterChainMap";
    public static final String TYPE_REF = "ref";
    public static final String TYPE_IDREF = "idRef";
    public static final String TYPE_STRING_LIST = "stringList";
    public static final String TYPE_REF_LIST = "refList";
    public static final String TYPE_IDREF_LIST = "idRefList";
    public static final String TYPE_IDREF_MAP = "idRefMap";
    public static final String TYPE_STRING_SET = "stringSet";
    public static final String TYPE_STRING_MAP = "stringMap";
    public static final String TYPE_REF_MAP = "refMap";
    public static final String TYPE_STRING_PROPERTIES = "stringProperties";

	private GenericBeanUpdaterDefinition definition;
	private String key;
	private Object value;
    private String valueType;
	private String before;
	private String after;
	private int order = 0;
	private boolean orderSet = false;
	private String oldValue = null;
	private String replacement = null;
    private String securityMetadataSourceDefinition;
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        super.postProcessBeanFactory(beanFactory);
    }

    public String getSecurityMetadataSourceDefinition() {
        return securityMetadataSourceDefinition;
    }

    public void setSecurityMetadataSourceDefinition(String securityMetadataSourceDefinition) {
        this.securityMetadataSourceDefinition = securityMetadataSourceDefinition;
    }

    /* (non-Javadoc)
             * @see com.jaspersoft.jasperserver.api.common.util.spring.AbstractBeanPropertyProcessor#getProcessedPropertyValue(java.lang.Object)
             */
	protected Object getProcessedPropertyValue(Object originalValue) {
		try {
			if (definition.getOperation().equals(GenericBeanUpdaterDefinition.APPEND)) {
				return append(originalValue);
			} else if (definition.getOperation().equals(GenericBeanUpdaterDefinition.SET)) {
				return set(originalValue);
			} else if (definition.getOperation().equals(GenericBeanUpdaterDefinition.INSERT)) {
				return insert(originalValue);
			} else if (definition.getOperation().equals(GenericBeanUpdaterDefinition.REPLACE)) {
				return replace(originalValue);
			} else {
				throw new JSException("jsexception.unknown.updater.operation", new Object[] { getBeanName(), definition.getOperation()});
			}
		} catch (Throwable ex) {
			throw new RuntimeException("Bean updater failure. Operation: " + definition.getOperation() + " on bean: " + getBeanName() +
					" for property: " + getPropertyName() + " with value: " + value + ", value type: " + valueType, ex);
		}
	}

	/**
	 * @param originalValue
	 * @return
	 */
	private Object append(Object originalValue) {

		// just set value if not set to anything now
		if (originalValue == null) {
			return getSinglePropertyValue();
		}
		if (TYPE_STRING_MAP.equals(valueType)) {
			Map newValue = new ManagedMap();
            newValue.putAll((Map) originalValue);
			if (key != null) {
				newValue.put(new TypedStringValue(key), new TypedStringValue((String)value));
			} else if (value instanceof Map) {
				newValue.putAll((Map) convertToMetaData(value, TYPE_STRING_MAP));
			} else {
				throw new JSException("jsexception.cant.append.to.map", new Object[] {getPropertyName(), getBeanName(), value});
			}
			return newValue;
		} else if (TYPE_STRING_PROPERTIES.equals(valueType)) {
			Map newValue = new ManagedProperties();
            newValue.putAll((Properties) originalValue);
			if (key != null) {
				newValue.put(new TypedStringValue(key), new TypedStringValue((String)value));
			} else if (value instanceof Properties) {
				newValue.putAll((Properties) convertToMetaData(value, TYPE_STRING_PROPERTIES));
			} else {
				throw new JSException("jsexception.cant.append.to.properties", new Object[] {getPropertyName(), getBeanName(), value});
			}
			return newValue;
		} else if (TYPE_STRING_LIST.equals(valueType)) {
			List newValue = new ManagedList();
            newValue.addAll((List) originalValue);
			if (value instanceof List) {
				newValue.addAll((List) convertToMetaData(value, TYPE_STRING_LIST));
            } else if (value instanceof String) {
                newValue.add(convertToMetaData(value, TYPE_STRING));
			} else {
                throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
			}
			return newValue;
        } else if (TYPE_REF_LIST.equals(valueType)) {
            List newValue = new ManagedList();
            newValue.addAll((List) originalValue);
            if (value instanceof List) {
                newValue.addAll((List) convertToMetaData(value, TYPE_REF_LIST));
            } else if (value instanceof String) {
                newValue.add(convertToMetaData(value, TYPE_REF));
            } else {
                throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
            }
            return newValue;
        } else if (TYPE_IDREF_LIST.equals(valueType)) {
            List newValue = new ManagedList();
            newValue.addAll((List) originalValue);
            if (value instanceof List) {
                newValue.addAll((List) convertToMetaData(value, TYPE_IDREF_LIST));
            } else if (value instanceof String) {
                newValue.add(convertToMetaData(value, TYPE_IDREF));
            } else {
                throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
            }
            return newValue;
        } else if (TYPE_IDREF_MAP.equals(valueType)) {
            Map newValue = new ManagedMap();
            newValue.putAll((Map) originalValue);
            if (key != null) {
				newValue.put(new TypedStringValue(key), convertToMetaData(value, TYPE_REF));
			} else if (value instanceof Map) {
                newValue.putAll((Map) convertToMetaData(value, TYPE_IDREF_MAP));
            } else {
                throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
            }
            return newValue;
        } else if (TYPE_REF_MAP.equals(valueType)) {
            Map newValue = new ManagedMap();
            newValue.putAll((Map) originalValue);
            if (key != null) {
				newValue.put(new TypedStringValue(key), convertToMetaData(value, TYPE_REF));
			} else if (value instanceof Map) {
                newValue.putAll((Map) convertToMetaData(value, TYPE_REF_MAP));
            } else {
                throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
            }
            return newValue;
        } else if(TYPE_SECURITY_METADATA_SOURCE.equals(valueType)){
            if(securityMetadataSourceDefinition == null){
                throw new IllegalStateException("securityMetadataSourceDefinition can't be null");
            }
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(securityMetadataSourceDefinition);
            if(beanDefinition == null){
                throw new IllegalStateException("bean '" + securityMetadataSourceDefinition + "' is not defined");
            } else if(!"org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource".equals(beanDefinition.getBeanClassName())){
                try{
                    final Object bean = beanFactory.getBean(securityMetadataSourceDefinition);
                    if(bean instanceof BeanDefinition){
                        beanDefinition = (BeanDefinition) bean;
                    }
                }catch (Exception e){
                    // do nothing. Unknown unsupported bean.
                }
            }
            // getting of originalSecurityMetadataSourceDefinition and metadataSourceDefinitionToAppend is tricky and found while debugging
            // I don't see any other option to extend securityMetadataSource. So, let's hope it will work for future Spring releases...
            final ManagedMap<BeanDefinition, BeanDefinition> originalSecurityMetadataSourceDefinition = (ManagedMap<BeanDefinition, BeanDefinition>) ((BeanDefinitionHolder) originalValue).getBeanDefinition().getConstructorArgumentValues().getIndexedArgumentValues().get(0).getValue();
            final ManagedMap<BeanDefinition, BeanDefinition> metadataSourceDefinitionToAppend = (ManagedMap<BeanDefinition, BeanDefinition>) beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().get(0).getValue();
            originalSecurityMetadataSourceDefinition.putAll(metadataSourceDefinitionToAppend);
            return originalValue;
        } else if (originalValue instanceof TypedStringValue) {
			return new TypedStringValue(((TypedStringValue) originalValue).getValue() + value);
		} else {
			throw new JSException("jsexception.cant.append", new Object[] {getPropertyName(), getBeanName(), value});
		}
	}

	/**
	 * just set it
	 * @param originalValue
	 * @return
	 */
	private Object set(Object originalValue) {
		return getSinglePropertyValue();
	}

	/**
	 * @param originalValue
	 * @return TypedStringValue
	 */
	private Object insert(Object originalValue) {
        if(TYPE_FILTER_CHAIN_MAP.equals(valueType) && originalValue instanceof Map && value instanceof String){
            Map<BeanDefinition, List<BeanReference>> filterChainMap = (Map<BeanDefinition, List<BeanReference>>) originalValue;
            if(before != null && !before.isEmpty()){
                for(List<BeanReference> filters : filterChainMap.values()){
                    for(int i = 0; i < filters.size(); i++){
                        if(before.equals(filters.get(i).getBeanName())){
                            final String[] namesToInsert = ((String) value).split(",");
                            for(int j = namesToInsert.length - 1; j >= 0; j--){
                                if(namesToInsert[j] != null && !namesToInsert[j].isEmpty()){
                                    filters.add(i, new RuntimeBeanReference(namesToInsert[j]));
                                }
                            }
                            break;
                        }
                    }
                }
            } else if(after != null && !after.isEmpty()){
                for(BeanDefinition urlPatternDefinition: filterChainMap.keySet()){
                    final String pattern = (String) urlPatternDefinition.getConstructorArgumentValues().getIndexedArgumentValues().get(0).getValue();
                    if(after.equals(pattern)){
                        List<BeanReference> filters = filterChainMap.get(urlPatternDefinition);
                        final String[] namesToInsert = ((String) value).split(",");
                        for(int j = namesToInsert.length - 1; j >= 0; j--){
                            if(namesToInsert[j] != null && !namesToInsert[j].isEmpty()){
                                filters.add(0, new RuntimeBeanReference(namesToInsert[j]));
                            }
                        }
                        break;
                    }
                }
            }
            return originalValue;
        } else if (originalValue instanceof TypedStringValue) {
			StringBuffer newValue = new StringBuffer(((TypedStringValue) originalValue).getValue());
			int index = 0;
			if (before != null) {
				/* look for "before" string */
                index = newValue.indexOf(before);
                /* check if original value does not contain the "before" string */
			    if (index == -1) {
        		    throw new JSException("jsexception.cant.find.before.string", new Object[] {getPropertyName(), getBeanName(), value});
			    }
                /* Inserting values */
                do {
                    newValue.insert(index, value);
                    /* A small hack to get the next index (with the shift),
                       because it's inconvenient to get size of the "value" object here */
				    index = newValue.indexOf(before, newValue.indexOf(before, index) + 1);
                } while (index > 0);
			} else if(after != null && !"".equals(after)){
                /* look for "after" string */
                index = newValue.indexOf(after);
                /* check if original value does not contain the "after" string */
                if (index == -1) {
                    throw new JSException("jsexception.cant.find.after.string", new Object[] {getPropertyName(), getBeanName(), value});
                }
                /* Inserting values */
                do {
                    index += after.length();
                    newValue.insert(index, value);
                    index = newValue.indexOf(after, index);
                } while (index > 0);


            }else {
			    newValue.insert(index, value);
            }
			return new TypedStringValue(newValue.toString());
		} else {
			throw new JSException("jsexception.cant.insert", new Object[] {getPropertyName(), getBeanName(), value});
		}
	}

	/**
	 * @param originalValue
	 * @return
	 */
	private Object replace(Object originalValue) {
		if (TYPE_STRING.equals(valueType)) {
			return new TypedStringValue(((TypedStringValue)originalValue).getValue().replaceAll(oldValue, replacement));
		} else {
			throw new JSException("jsexception.cant.insert", new Object[] {getPropertyName(), getBeanName(), value});
		}
	}

	public String getBeanName() {
		return definition.getBeanName();
	}

	protected Object getSinglePropertyValue() {
		Object propValue;
		if (value != null) {
			propValue = convertToMetaData(value, valueType);
		} else {
			propValue = null;
		}
		return propValue;
	}

	public int getOrder() {
		return orderSet ? order : definition.getOrder();
	}

	// you can set order here to override the one in the def
	public void setOrder(int order) {
		orderSet = true;
		this.order = order;
	}

	public String getPropertyName() {
		return definition.getPropertyName();
	}

	public GenericBeanUpdaterDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(GenericBeanUpdaterDefinition definition) {
		this.definition = definition;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    /**
	 * @return Returns the oldValue.
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @param val The oldValue to set.
	 */
	public void setOldValue(String val) {
		this.oldValue = val;
	}

	/**
	 * @return Returns the replacement.
	 */
	public String getReplacement() {
		return replacement;
	}

	/**
	 * @param replacement The replacement to set.
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	protected static Object convertToMetaData(Object value, String valueType) {
        if (value != null && valueType != null) {
            if (TYPE_STRING.equals(valueType)) {
                return new TypedStringValue((String) value);
            }
            if (TYPE_STRING_LIST.equals(valueType)) {
                List list = new ManagedList();
                for (Iterator iter = ((List) value).iterator(); iter.hasNext(); ) {
                    list.add(convertToMetaData(iter.next(), TYPE_STRING));
                }
                return list;
            }
            if (TYPE_STRING_SET.equals(valueType)) {
                Set newSet = new ManagedSet();
                for (Iterator iter = ((Set) value).iterator(); iter.hasNext(); ) {
                    newSet.add(convertToMetaData(iter.next(), TYPE_STRING));
                }
                return newSet;
            }
            if (TYPE_STRING_PROPERTIES.equals(valueType)) {
                Properties newProperties = new ManagedProperties();
                for (Iterator iter = ((Map) value).entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    newProperties.put( convertToMetaData(entry.getKey(), TYPE_STRING),
                            convertToMetaData(entry.getValue(), TYPE_STRING) );
                }
                return newProperties;
            }
            if (TYPE_STRING_MAP.equals(valueType)) {
                Map newMap = new ManagedMap();
                for (Iterator iter = ((Map) value).entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    newMap.put( convertToMetaData(entry.getKey(), TYPE_STRING),
                            convertToMetaData(entry.getValue(), TYPE_STRING) );
                }
                return newMap;
            }
            if (TYPE_IDREF.equals(valueType)) {
                return new RuntimeBeanReference((String) value);
            }
            if (TYPE_IDREF_LIST.equals(valueType)) {
                List list = new ManagedList();
                for (Iterator iter = ((List) value).iterator(); iter.hasNext(); ) {
                    list.add(convertToMetaData(iter.next(), TYPE_IDREF));
                }
                return list;
            }
            if (TYPE_IDREF_MAP.equals(valueType)) {
                Map newMap = new ManagedMap();
                for (Iterator iter = ((Map) value).entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    newMap.put( convertToMetaData(entry.getKey(), TYPE_STRING),
                            convertToMetaData(entry.getValue(), TYPE_IDREF) );
                }
                return newMap;
            }
            if (TYPE_REF_MAP.equals(valueType)) {
                Map newMap = new ManagedMap();
                for (Iterator iter = ((Map) value).entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    newMap.put( convertToMetaData(entry.getKey(), TYPE_STRING),
                            convertToMetaData(entry.getValue(), TYPE_REF) );
                }
                return newMap;
            }
        }
        return value;
    }
}
