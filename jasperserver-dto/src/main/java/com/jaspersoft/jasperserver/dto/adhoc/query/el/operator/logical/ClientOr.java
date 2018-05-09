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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = ClientOr.OPERATOR_ID)
public class ClientOr extends ClientLogical<ClientOr> {

    public static final String OPERATOR_ID = "or";

    public ClientOr() {
        super(ClientOperation.OR, new ArrayList<ClientExpression>());
    }

    public ClientOr(List<ClientExpression> operands) {
        super(ClientOperation.OR, operands);
    }

    public ClientOr(ClientOr logical) {
        super(logical);
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        super.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public ClientOr deepClone() {
        return new ClientOr(this);
    }
}
