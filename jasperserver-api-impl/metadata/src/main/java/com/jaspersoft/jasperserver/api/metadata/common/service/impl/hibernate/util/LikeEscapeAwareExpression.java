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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import org.apache.commons.collections.SetUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.TypedValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.CharUtils.isAsciiPrintable;

/**
 * A criterion representing a "like" expression
 * that explicitly specifies escape character.
 *
 * @author askorodumov
 * @version $Id$
 */
public class LikeEscapeAwareExpression extends LikeExpression {

    /**
     * Default escape character is '!'.
     */
    public static final Character ESCAPE_CHAR = '!';

    @SuppressWarnings("unchecked")
    private static final Set<Character> ESCAPING_CHARS = SetUtils.unmodifiableSet(new HashSet<Character>(Arrays.asList('_', '%', '[')));

    private final String propertyName;
    private final Object value;

    public LikeEscapeAwareExpression(String propertyName, String value, MatchMode matchMode) {
        super(propertyName, value, matchMode, ESCAPE_CHAR, false);
        this.propertyName = propertyName;
        this.value = matchMode.toMatchString(value);
    }

    public LikeEscapeAwareExpression(String propertyName, String value, MatchMode matchMode, Character escapeChar) {
        super(propertyName, value, matchMode, escapeChar, false);
        this.propertyName = propertyName;
        this.value = matchMode.toMatchString(value);

        checkEscapeChar(escapeChar);
    }

    public LikeEscapeAwareExpression(String propertyName, String value, Character escapeChar) {
        super(propertyName, value, escapeChar, false);
        this.propertyName = propertyName;
        this.value = value;

        checkEscapeChar(escapeChar);
    }

    @Override
    public TypedValue[] getTypedValues(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] {
                criteriaQuery.getTypedValue( criteria, propertyName, value.toString() )
        };
    }

    public static void checkEscapeChar(char escapeChar) {
        if (!isAsciiPrintable(escapeChar) || ESCAPING_CHARS.contains(escapeChar)) {
            throw new IllegalArgumentException("Illegal escape character");
        }
    }

    public static String escape(String expr, char escapeChar) {
        checkEscapeChar(escapeChar);

        expr = expr.replace(new String(new char[] {escapeChar}), new String(new char[] {escapeChar, escapeChar}));
        for (char escapingChar : ESCAPING_CHARS) {
            expr = expr.replace(new String(new char[] {escapingChar}), new String(new char[] {escapeChar, escapingChar}));
        }

        return expr;
    }
}
