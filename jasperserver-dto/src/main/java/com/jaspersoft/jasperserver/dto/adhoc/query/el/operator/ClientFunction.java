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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = ClientFunction.FUNCTION_ID)
public class ClientFunction extends ClientOperator<ClientFunction> {

    public static final String FUNCTION_ID = "function";
    protected String functionName;

    public ClientFunction() {
        super(ClientOperation.FUNCTION.getName(), new ArrayList<ClientExpression>());
    }

    public ClientFunction(String functionName) {
        this();
        this.setFunctionName(functionName);
    }

    public ClientFunction(String functionName, List<ClientExpression> operands) {
        super(ClientOperation.FUNCTION.getName(), operands);
        this.functionName = functionName;
    }

    public ClientFunction(ClientFunction source){
        this(source.getFunctionName(), source.getOperands());
    }

    public String getFunctionName() {
        return functionName;
    }

    public ClientFunction setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public ClientFunction addArgument(ClientExpression expr) {
        this.operands.add(expr);
        return this;
    }

    @Override
    public String toString() {
        String args = "";
        int len = (getOperands() != null) ? getOperands().size() : 0;
        if (len > 0) {
            StringBuilder sb = new StringBuilder(getOperands().get(0).toString());
            for (int i = 1; i < len; i++) {
                if (getOperands().get(i) != null) {
                    sb.append(", ");
                    sb.append(getOperands().get(i).toString());
                }
            }
            args = sb.toString();
        }
        final String functionNameString = (functionName != null) ? functionName : ClientExpressions.MISSING_REPRESENTATION;
        return functionNameString + "(" + args + ")";
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        super.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientFunction)) return false;
        if (!super.equals(o)) return false;

        ClientFunction that = (ClientFunction) o;

        return functionName != null ? functionName.equals(that.functionName) : that.functionName == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (functionName != null ? functionName.hashCode() : 0);
        return result;
    }

    @Override
    public ClientFunction deepClone() {
        return new ClientFunction(this);
    }
}
