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
package com.jaspersoft.jasperserver.dto.domain;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomElExpressionCollectionContext extends BaseDomElContext<DomElExpressionCollectionContext> {
    @NotNull
    private List<DomElExpressionContext> expressionContexts;
    public DomElExpressionCollectionContext(){}

    public DomElExpressionCollectionContext(DomElExpressionCollectionContext source){
        final List<DomElExpressionContext> sourceExpressionContexts = source.getExpressionContexts();
        if(sourceExpressionContexts != null){
            expressionContexts = new ArrayList<DomElExpressionContext>();
            for (DomElExpressionContext expressionContext : sourceExpressionContexts) {
                expressionContexts.add(expressionContext.deepClone());
            }
        }
    }

    @XmlElementWrapper(name = "expressionContexts")
    @XmlElement(name = "expressionContext")
    public List<DomElExpressionContext> getExpressionContexts() {
        return expressionContexts;
    }

    public DomElExpressionCollectionContext setExpressionContexts(List<DomElExpressionContext> expressionContexts) {
        this.expressionContexts = expressionContexts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomElExpressionCollectionContext)) return false;

        DomElExpressionCollectionContext that = (DomElExpressionCollectionContext) o;

        return expressionContexts != null ? expressionContexts.equals(that.expressionContexts) : that.expressionContexts == null;
    }

    @Override
    public int hashCode() {
        return expressionContexts != null ? expressionContexts.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DomElExpressionContextCollection{" +
                "expressionContexts=" + expressionContexts +
                '}';
    }

    @Override
    public DomElExpressionCollectionContext deepClone() {
        return new DomElExpressionCollectionContext(this);
    }
}
