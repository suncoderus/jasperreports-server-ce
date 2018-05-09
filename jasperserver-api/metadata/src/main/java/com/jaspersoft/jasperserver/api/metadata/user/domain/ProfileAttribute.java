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
package com.jaspersoft.jasperserver.api.metadata.user.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeLevel;

/**
 * ProfileAttribute interface manages attaching of extra information to users and roles. For example, you may assign
 * �State� attribute and �CA� value for it to users, and then use it as a default state for logged user in reports or
 * views.
 *
 * @author sbirney
 * @version $Id$
 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl
 * @since 2.0.0
 */
@JasperServerAPI
public interface ProfileAttribute extends InternalURI {

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute.
     */
    public String getAttrName();

    /**
     * Sets the name of the attribute.
     *
     * @param s the name of the attribute.
     */
    public void setAttrName(String s);

    /**
     * Returns the value of the attribute.
     *
     * @return the value of the attribute.
     */
    public String getAttrValue();

    /**
     * Sets the value of the attribute.
     *
     * @param s the value of the attribute.
     */
    public void setAttrValue(String s);

    /**
     * Returns the principal object (see {@link #setPrincipal(Object)}).
     *
     * @return the principal object.
     */
    public Object getPrincipal();

    /**
     * Sets the principal object ({@link User} or {@link Role}) to which this profile attribute is attached.
     *
     * @param o the principal object
     */
    public void setPrincipal(Object o);

    /**
     * Shows whether attribute is secure.
     *
     * @return <code>true</code> if the attribute is secure, <code>false</code> otherwise.
     */
    public boolean isSecure();

    /**
     * Sets the value of secure field of the attribute. If the value is set to <code>true</code>, then attribute
     * is secure. If it is set to <code>false</code>, then attribute is not secure.
     */
    public void setSecure(boolean secure);

    /**
     * Returns the group of the attribute.
     *
     * @return the group of the attribute.
     */
    public String getGroup();

    /**
     * Sets group for the attribute.
     *
     * @param group the group of the attribute.
     */
    public void setGroup(String group);

    /**
     * Returns the description of the attribute
     *
     * @return the description of the attribute
     */
    public String getDescription();

    /**
     * Sets description for the attribute
     *
     * @param description the description of the attribute
     */
    public void setDescription(String description);

    /**
     * Shows profile attribute level relative to target level.
     *
     * @return <code>ProfileAttributeLevel</code> enum.
     */
    public ProfileAttributeLevel getLevel();

    /**
     * Sets profile attribute level relative to target level.
     */
    public void setLevel(ProfileAttributeLevel level);

    /**
     * Returns uri
     *
     * @return  uri
     */
    public String getUri();

    /**
     * Sets generated uri based on attribute holder uri and attribute name
     *
     * @param holderUri the internal attribute holder uri
     * @param holderUri the internal attribute holder uri
     */
    public void setUri(String attrName, String holderUri);
}
