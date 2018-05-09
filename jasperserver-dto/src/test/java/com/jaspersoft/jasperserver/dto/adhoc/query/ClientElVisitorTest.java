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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

/**
 * <p>
 * <p/>
 * </p>
 *
 * @author Stas Chubar (schubar@tibco.com)
 * @version $Id$
 */
public class ClientElVisitorTest {

    @Test
    public void ensureVisitedAllPartsOfComparison() throws Exception {
        ClientExpression q = variable("v1").eq(1);

        q.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                assertThat(expression.getName(), is("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                super.visit(expression);
            }
        });
    }

    @Test
    public void ensureVisitedAllPartsOfLogical() throws Exception {
        ClientExpression e1 = variable("v1").eq(1);
        ClientExpression e2 = variable("v2").startsWith("A");

        final List<String> visitedVariables = new ArrayList<String>(2);
        final List<String> visitedLiterals = new ArrayList<String>(2);

        final AtomicInteger totalVisited = new AtomicInteger(0);
        and(e1, e2).accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientFunction expression) {
                totalVisited.incrementAndGet();
                assertThat(expression.getFunctionName(), is("startsWith"));
            }

            @Override
            public void visit(ClientEquals expression) {
                totalVisited.incrementAndGet();
                assertThat(expression.getOperator(), is("equals"));
            }

            @Override
            public void visit(ClientAnd expression) {
                totalVisited.incrementAndGet();
                assertThat(expression.getOperator(), is("and"));
            }

            @Override
            public void visit(ClientVariable expression) {
                totalVisited.incrementAndGet();
                visitedVariables.add(expression.getName());
            }

            @Override
            public void visit(ClientLiteral expression) {
                totalVisited.incrementAndGet();
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedVariables, contains("v1", "v2"));
        assertThat(visitedLiterals, contains("1", "A"));

        assertThat(totalVisited.get(), is(7));

    }

    @Test
    public void ensureVisitedAllPartsOfAddition() throws Exception {
        ClientExpression e1 = new ClientAdd(asList(literal(1), literal(2)));

        final List<String> visitedLiterals = new ArrayList<String>(2);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientAdd expression) {
                assertThat(expression, is(instanceOf(ClientAdd.class)));
                assertThat(visitedLiterals, contains("1", "2"));
            }
            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1", "2"));

    }

    @Test
    public void ensureVisitedAllPartsOfFunction() throws Exception {
        ClientExpression e1 = function("do", literal(1), variable("v1"));

        final List<String> visitedVariables = new ArrayList<String>(1);
        final List<String> visitedLiterals = new ArrayList<String>(1);

        e1.accept(new ClientELVisitorAdapter() {
            @Override
            public void visit(ClientVariable expression) {
                visitedVariables.add(expression.getName());
            }

            @Override
            public void visit(ClientFunction expression) {
                assertThat(expression, is(notNullValue()));
                assertThat(expression.getFunctionName(), is("do"));

                assertThat(visitedLiterals, contains("1"));
                assertThat(visitedVariables, contains("v1"));
            }

            @Override
            public void visit(ClientLiteral expression) {
                visitedLiterals.add(expression.getValue().toString());
            }
        });

        assertThat(visitedLiterals, contains("1"));
        assertThat(visitedVariables, contains("v1"));

    }
}