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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id$
 * @date 3/4/16 2:34 PM
 */
public class CopyFactory {
    
    public static ClientExpression copy(ClientExpression e) {
        CopyVisitor copyVisitor = new CopyVisitor();
        e.accept(copyVisitor);
        return copyVisitor.getCopy();
    }

    public static List<ClientExpression> copy(List<? extends ClientExpression> expressions) {
        List<ClientExpression> resultList = new ArrayList<ClientExpression>();
        for (ClientExpression expr : expressions) {
            resultList.add((expr == null) ? null : copy(expr));
        }
        return resultList;
    }

}
