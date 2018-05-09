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

import org.hibernate.criterion.*;
import org.hibernate.type.Type;

/**
 * Utilities for resource criterion creation.
 *
 * @author Yuriy Plakosh
 */
public class ResourceCriterionUtils {

    /**
     * Creates text criterion which allows to search occurrence of each word of the text in some of the possible
     * targeted properties of the resource. Example:
     * let say we have "Hello World" text to search resources by. In this case each word can be either in label or in
     * description. The SQL for this criterion should be:
     * (lower(label) like "hello" or lower(description) like "hello") &&
     * (lower(label) like "world" or lower(description) like "world")
     *
     * @param text the text to search.
     * @return text criterion.
     */
    public static Criterion getTextCriterion(String text) {
        String[] words = text.trim().split("\\s+");

        Conjunction wordsCriterion = null;

        for (String word : words) {
        	if(word.trim().isEmpty()){
        		continue;
        	}
            Disjunction wordCriterion = Restrictions.disjunction();

            // Each word should be in label or in description.
            wordCriterion.add(new IlikeEscapeAwareExpression("label", word, MatchMode.ANYWHERE));
            wordCriterion.add(new IlikeEscapeAwareExpression("description", word, MatchMode.ANYWHERE));

            if(wordsCriterion == null){
            	wordsCriterion = Restrictions.conjunction();
            }
            // Resource should contain all the words.
            wordsCriterion.add(wordCriterion);
        }

        return wordsCriterion;
    }

    public static Criterion getSQLCriterion(String sql){
        return new ConcatenationAwareSqlCriterion(sql);
    }

    public static Criterion getSQLCriterion(String sql, Object[] values, Type[] types){
        return new ConcatenationAwareSqlCriterion(sql, values, types);
    }
}
