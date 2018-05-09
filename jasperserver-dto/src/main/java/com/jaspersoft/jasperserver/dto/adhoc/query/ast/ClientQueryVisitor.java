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
package com.jaspersoft.jasperserver.dto.adhoc.query.ast;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.filter.ClientFilter;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;

/**
 * <p>
 * Helper class
 * </p>
 *
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
public interface ClientQueryVisitor extends ClientELVisitor {

    void visit(ClientSelect clientSelect);

    void visit(ClientQueryField aggregatedField);

    void visit(ClientQueryAggregatedField aggregatedField);

    void visit(ClientWhere clientWhere);

    void visit(ClientFilter clientFilter);

}
