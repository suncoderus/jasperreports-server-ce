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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
@XmlType(propOrder = {"name", "expression"})
public class ClientParameterExpression {
    private String name;
    private ClientExpressionContainer expression;

    public ClientParameterExpression() {

    }

    public ClientParameterExpression(String name, ClientExpressionContainer expression) {
        this.name = name;
        this.expression = expression;
    }

    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClientExpressionContainer getExpression() {
        return expression;
    }

    public void setExpression(ClientExpressionContainer expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientParameterExpression)) return false;

        ClientParameterExpression that = (ClientParameterExpression) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return expression != null ? expression.equals(that.expression) : that.expression == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        return result;
    }
}
