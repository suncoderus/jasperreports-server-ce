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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.BuiltInParameterProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Parameters for reports and queries from the user profile are injected.
 *
 * @author Sherman Wood (sgwood@users.sourceforge.net)
 * @version $Id$
 */
public class UserProfileBuiltInParameterProvider implements BuiltInParameterProvider, Serializable {
    /*
     * Automatically added by JasperServer when running a report
     *
     * $P{LoggedInUser}
     * $P{LoggedInUsername}
     *
     * This will be available if explicitly included in the query or report
     *
     * $P{LoggedInUserFullName}
     * $P{LoggedInUserEmailAddress}
     * $P{LoggedInUserEnabled}
     * $P{LoggedInUserExternallyDefined}
     * $P{LoggedInUserTenantId}
     * $P{LoggedInUserRoles} -> return collection of role names
     * $P{LoggedInUserAttributes} -> return Map of attributes
     * $P{LoggedInUserAttributeNames} -> return collection of attribute names
     * $P{LoggedInUserAttributeValues} -> return collection of attribute values
     * $P{LoggedInUserAttribute_attr1} -> return attribute value for matched attribute name
     *
     * For this parameters attribute is retrieved from the User's parent Tenant.
     *
     * $P{LoggedInTenantAttribute_attr1}
     *
     * Attribute is retrieved from the Server level
     *
     * $P{ServerAttribute_attr1}
     *
     * Parameters contain hierarchically resolved attribute: from User, Tenant or Server levels.
     *
     * $P{Attribute_attr1}
     */

    public static final String[] STANDARD_PARAMETERS = {
            Parameter.LOGGED_IN_USER.getName(),
            Parameter.LOGGED_IN_USER_NAME.getName()
    };

    public static final String ATTRIBUTE_DELIMITER = "_";

    private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<Class<?>, Object>() {{
        put(String.class, "");
        put(Collection.class, new ArrayList<Object>());
        put(Boolean.class, false);
    }};

    /**
     * Represents all available parameters that can be used in reports and queries
     */
    public static enum Parameter {
        // User parameters
        LOGGED_IN_USER("LoggedInUser"),
        LOGGED_IN_USER_NAME("LoggedInUsername"),
        LOGGED_IN_USER_FULL_NAME("LoggedInUserFullname"),
        LOGGED_IN_USER_EMAIL_ADDRESS("LoggedInUserEmailAddress"),
        LOGGED_IN_USER_ENABLED("LoggedInUserEnabled"),
        LOGGED_IN_USER_EXTERNALLY_DEFINED("LoggedInUserExternallyDefined"),
        LOGGED_IN_USER_TENANT_ID("LoggedInUserTenantId"),
        LOGGED_IN_USER_ROLES("LoggedInUserRoles"),
        LOGGED_IN_USER_ATTRIBUTES("LoggedInUserAttributes"),
        LOGGED_IN_USER_ATTRIBUTE_NAMES("LoggedInUserAttributeNames"),
        LOGGED_IN_USER_ATTRIBUTE_VALUES("LoggedInUserAttributeValues"),
        LOGGED_IN_USER_ATTRIBUTE("LoggedInUserAttribute_"),

        // Tenant parameters
        LOGGED_IN_TENANT_ATTRIBUTE("LoggedInTenantAttribute_"),

        // Server parameters
        SERVER_ATTRIBUTE("ServerAttribute_"),

        // General hierarchical parameters
        ATTRIBUTE("Attribute_");

        private String name;

        Parameter(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static interface ParameterCallback {
        public Object[] getParameter(String name);
    }

    // List of all available categories that can be use
    private List<ProfileAttributeCategory> profileAttributeCategories;
    private ProfileAttributeService profileAttributeService;
    private Map<String, ParameterCallback> availableParameters =
            new TreeMap<String, ParameterCallback>(String.CASE_INSENSITIVE_ORDER);

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    public void setProfileAttributeCategories(List<ProfileAttributeCategory> profileAttributeCategories) {
        this.profileAttributeCategories = profileAttributeCategories;
    }

    public void init() {
        // User callbacks
        availableParameters.put(Parameter.LOGGED_IN_USER.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                MetadataUserDetails returnUserDetails;

                try {
                    MetadataUserDetails userDetails = getUserDetails();
                    if (userDetails == null) {
                        return null;
                    }

                    // Write the object out to a byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos);
                    out.writeObject(userDetails);
                    out.flush();
                    out.close();

                    // Make an input stream from the byte array and read
                    // a copy of the object back in.
                    ObjectInputStream in = new ObjectInputStream(
                            new ByteArrayInputStream(bos.toByteArray()));
                    returnUserDetails = (MetadataUserDetails) in.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (ClassNotFoundException exception) {
                    exception.printStackTrace();
                    return null;
                }

                returnUserDetails.setPassword(null);
                return makeParameter(name, User.class, returnUserDetails);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_NAME.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                String username = (user != null) ? user.getUsername() : "";
                return makeParameter(name, String.class, username);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_FULL_NAME.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                String fullName = (user != null) ? user.getFullName() : "";
                return makeParameter(name, String.class, fullName);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_EMAIL_ADDRESS.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                String emailAddress = (user != null) ? user.getEmailAddress() : "";
                return makeParameter(name, String.class, emailAddress);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ENABLED.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                Boolean enabled = (user != null) && user.isEnabled();
                return makeParameter(name, Boolean.class, enabled);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_EXTERNALLY_DEFINED.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                Boolean externallyDefined = (user != null) && user.isExternallyDefined();
                return makeParameter(name, Boolean.class, externallyDefined);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_TENANT_ID.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                String tenantId = (user != null) ? user.getTenantId() : "";
                return makeParameter(name, String.class, tenantId);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ROLES.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                User user = getUserDetails();
                Set roles = (user != null) ? user.getRoles() : new HashSet();
                Collection<String> roleNames = new ArrayList<String>(roles.size());
                for (Object role : roles) {
                    roleNames.add(((Role) role).getRoleName());
                }
                return makeParameter(name, Collection.class, roleNames);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ATTRIBUTES.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> profileAttributes = getProfileAttributes(ProfileAttributeCategory.USER);
                return makeParameter(name, Collection.class, profileAttributes);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ATTRIBUTE_NAMES.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.USER);
                Collection<String> keys = getAttributeNames(attributes);
                return makeParameter(name, Collection.class, keys);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ATTRIBUTE_VALUES.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.USER);
                Collection<String> values = getAttributeValues(attributes);
                return makeParameter(name, Collection.class, values);
            }
        });
        availableParameters.put(Parameter.LOGGED_IN_USER_ATTRIBUTE.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.USER);
                String attrValue = getAttributeValue(attributes, name);
                return (attrValue != null) ? makeParameter(name, String.class, attrValue) : null;
            }
        });

        // Tenant callbacks
        availableParameters.put(Parameter.LOGGED_IN_TENANT_ATTRIBUTE.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.TENANT);
                String attrValue = getAttributeValue(attributes, name);
                return (attrValue != null) ? makeParameter(name, String.class, attrValue) : null;
            }
        });

        // Server callbacks
        availableParameters.put(Parameter.SERVER_ATTRIBUTE.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.SERVER);
                String attrValue = getAttributeValue(attributes, name);
                return (attrValue != null) ? makeParameter(name, String.class, attrValue) : null;
            }
        });

        // Callbacks for the hierarchically resolved attributes
        availableParameters.put(Parameter.ATTRIBUTE.getName(), new ParameterCallback() {
            @Override
            public Object[] getParameter(String name) {
                Collection<ProfileAttribute> attributes = getProfileAttributes(ProfileAttributeCategory.HIERARCHICAL);
                String attrValue = getAttributeValue(attributes, name);
                return (attrValue != null) ? makeParameter(name, String.class, attrValue) : null;
            }
        });
    }

    public UserProfileBuiltInParameterProvider() {
        init();
    }

    /**
     * Each element is a JRParameter, value
     *
     * @param context
     * @param jrParameters
     * @param parameters
     * @return List<Object[]> [JRParameter, value]
     */
    public List<Object[]> getParameters(ExecutionContext context, List jrParameters, Map parameters) {
        List<Object[]> userProfileParameters = new ArrayList<Object[]>();

        for (String parameterName : STANDARD_PARAMETERS) {
            Object[] result = getParameter(context, jrParameters, parameters, parameterName);
            if (result != null) {
                userProfileParameters.add(result);
            }
        }

        return userProfileParameters;
    }

    public Object[] getParameter(ExecutionContext context, List jrParameters, Map parameters, String name) {
        // Contains only parameter name, without attribute name (e.g. for parameter "LoggedInUserAttribute_attr1"
        // parameterName will be "LoggedInUserAttribute_")
        String parameterName;

        int indexOfDelimiter = name.indexOf(ATTRIBUTE_DELIMITER);
        if (indexOfDelimiter != -1) {
            // Remove attribute name but include delimiter
            parameterName = name.substring(0, indexOfDelimiter + 1);
        } else {
            parameterName = name;
        }

        Object[] result = null;
        ParameterCallback callback = availableParameters.get(parameterName);
        if (callback != null) {
            result = callback.getParameter(name);
        }

        return result;
    }

    protected MetadataUserDetails getUserDetails() {
        return (MetadataUserDetails) ProfileAttributeCategory.USER.getPrincipal(null);
    }

    protected Object[] makeParameter(String name, Class<?> type, Object value) {
        if (value == null && DEFAULT_VALUES.containsKey(type)) {
            value = DEFAULT_VALUES.get(type);
        }

        return new Object[]{JRQueryExecuterAdapter.makeParameter(name, type), value};
    }

    protected Collection<ProfileAttribute> getProfileAttributes(ProfileAttributeCategory category) {
        // Check if we can use this category, or retrieving attributes hierarchically
        if (category == ProfileAttributeCategory.HIERARCHICAL || profileAttributeCategories.contains(category)) {
            return profileAttributeService.
                    getCurrentUserProfileAttributes(ExecutionContextImpl.getRuntimeExecutionContext(), category);
        }

        return new ArrayList<ProfileAttribute>();
    }

    protected Collection<String> getAttributeNames(Collection<ProfileAttribute> attributes) {
        Collection<String> keys = new ArrayList<String>(attributes.size());
        for (ProfileAttribute attribute : attributes) {
            keys.add(attribute.getAttrName());
        }

        return keys;
    }

    protected Collection<String> getAttributeValues(Collection<ProfileAttribute> attributes) {
        Collection<String> values = new ArrayList<String>(attributes.size());
        for (ProfileAttribute attribute : attributes) {
            values.add(attribute.getAttrValue());
        }

        return values;
    }

    protected String getAttributeValue(Collection<ProfileAttribute> attributes, String name) {
        String attrName = name.split(ATTRIBUTE_DELIMITER, 2)[1];

        for (ProfileAttribute attribute : attributes) {
            if (attribute.getAttrName().equals(attrName)) {
                return attribute.getAttrValue();
            }
        }

        return null;
    }
}
