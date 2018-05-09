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
package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "variable")
public class ClientVariable extends VariableOperations implements ClientExpression<ClientVariable> {
    public static final String EXPRESSION_TYPE_VARIABLE = "variable";

    private String name;
    protected Boolean paren = null;

    protected ClientVariable() {
    }

    public ClientVariable(String name) {
        this.name = name;
    }

    public ClientVariable(ClientVariable variable) {
        this(variable.getName());
        this.paren = variable.paren;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    @XmlTransient
    public String getType() { return EXPRESSION_TYPE_VARIABLE; }

    public ClientVariable setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientVariable)) return false;

        ClientVariable that = (ClientVariable) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return paren != null ? paren.equals(that.paren) : that.paren == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (paren != null ? paren.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected ClientVariable getMe() {
        return this;
    }

    /**
     * This method is used for serialization purposes only
     *
     * @return boolean
     */
    @XmlElement(name = "paren")
    public Boolean isParen() {
        return (paren == null) ? null : paren;
    }

    @Override
    public Boolean hasParen() {
        return isParen() != null && paren;
    }

    @Override
    public ClientVariable deepClone() {
        return new ClientVariable(this);
    }
}
