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
package com.jaspersoft.jasperserver.dto.connection.query;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileConvert {
    private List<TextFileCastConversionRule> rules;

    public TextFileConvert(){}

    public TextFileConvert(TextFileConvert source){
        if(source.getRules() != null) {
            this.rules = new ArrayList<TextFileCastConversionRule>(source.getRules());
        }
    }

    public List<TextFileCastConversionRule> getRules() {
        return rules;
    }

    public TextFileConvert setRules(List<TextFileCastConversionRule> rules) {
        this.rules = rules;
        return this;
    }

    public TextFileConvert addRule(TextFileCastConversionRule rule){
        if(rules == null)rules = new ArrayList<TextFileCastConversionRule>();
        rules.add(rule);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileConvert)) return false;

        TextFileConvert that = (TextFileConvert) o;

        if (rules != null ? !rules.equals(that.rules) : that.rules != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rules != null ? rules.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TextFileConvert{" +
                "rules=" + rules +
                '}';
    }
}
