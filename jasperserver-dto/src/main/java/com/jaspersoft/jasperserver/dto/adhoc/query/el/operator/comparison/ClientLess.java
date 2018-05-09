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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = ClientLess.OPERATOR_ID)
public class ClientLess extends ClientComparison<ClientLess> {

    public static final String OPERATOR_ID = "less";

    public ClientLess() {
        super(ClientOperation.LESS);
    }

    public ClientLess(List<ClientExpression> operands) {
        super(ClientOperation.LESS, operands);
    }

    public ClientLess(ClientLess source){
        this(source.getOperands());
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        super.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public ClientLess deepClone() {
        return new ClientLess(this);
    }
}
