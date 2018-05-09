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
package com.jaspersoft.jasperserver.dto.adhoc.query.field;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientAggregate;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientField;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientFieldReference;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientIdentifiable;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.*;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckExpressionContainer;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckExpressionContainerValidator.AGGREGATION_EXPRESSION_NOT_VALID;


/**
 * @author Andriy Godovanets
 */
@XmlRootElement
public class ClientQueryAggregatedField implements ClientField, ClientAggregate, ClientFieldReference, ClientIdentifiable<String>, ClientQueryExpression {
    private String id;
    private String aggregateFunction;
    private String aggregateFirstLevelFunction;

    @CheckExpressionContainer(message = AGGREGATION_EXPRESSION_NOT_VALID,
            value = {
                    ClientAdd.class,
                    ClientAnd.class,
                    ClientBoolean.class,
                    ClientDate.class,
                    ClientByte.class,
                    ClientShort.class,
                    ClientInteger.class,
                    ClientLong.class,
                    ClientBigInteger.class,
                    ClientFloat.class,
                    ClientDouble.class,
                    ClientBigDecimal.class,
                    ClientDivide.class,
                    ClientEquals.class,
                    ClientFunction.class,
                    ClientGreater.class,
                    ClientGreaterOrEqual.class,
                    ClientIn.class,
                    ClientInteger.class,
                    ClientLess.class,
                    ClientLessOrEqual.class,
                    ClientMultiply.class,
                    ClientNot.class,
                    ClientNotEqual.class,
                    ClientNull.class,
                    ClientOr.class,
                    ClientRange.class,
                    ClientString.class,
                    ClientSubtract.class,
                    ClientTime.class,
                    ClientTimestamp.class,
                    ClientVariable.class
            })

    private ClientExpressionContainer expressionContainer;

    private String aggregateType;

    //TODO-Andriy Remove
    private String aggregateArg;

    @NotNull
    private String fieldRef;

    public ClientQueryAggregatedField() {
    }

    public ClientQueryAggregatedField(ClientQueryAggregatedField field) {
        if (field != null) {
            this
                    .setId(field.getId())
                    .setFieldReference(field.getFieldReference())
                    .setAggregateFunction(field.getAggregateFunction())
                    .setAggregateFirstLevelFunction(field.getAggregateFirstLevelFunction())
                    .setAggregateType(field.getAggregateType())
                    .setExpressionContainer(field.getExpressionContainer())
                    .setAggregateArg(field.getAggregateArg());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public ClientQueryAggregatedField setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    @XmlTransient
    public String getName() {
        return fieldRef;
    }

    @Override
    @XmlElement(name = "type")
    public String getType() {
        return getAggregateType();
    }

    public ClientQueryAggregatedField setType(String type) {
        setAggregateType(type);
        return this;
    }

    @Override
    @XmlElement(name = "fieldRef")
    public String getFieldReference() {
        return fieldRef;
    }

    public ClientQueryAggregatedField setFieldReference(String fieldRef) {
        this.fieldRef = fieldRef;
        return this;
    }

    @Override
    @XmlElement(name = "functionName")
    public String getAggregateFunction() {
        return aggregateFunction;
    }

    public ClientQueryAggregatedField setAggregateFunction(String aggregateFunction) {
        this.aggregateFunction = aggregateFunction;
        return this;
    }

    @Override
    @XmlElement(name = "timeBalanceFunctionName")
    public String getAggregateFirstLevelFunction() {
        return aggregateFirstLevelFunction;
    }

    public ClientQueryAggregatedField setAggregateFirstLevelFunction(String aggregateFirstLevelFunction) {
        this.aggregateFirstLevelFunction = aggregateFirstLevelFunction;
        return this;
    }

    @Override
    @XmlTransient
    public String getAggregateExpression() {
        if (expressionContainer == null) return null;
        return expressionContainer.getString();
    }

    @XmlElement(name = "expression")
    public ClientExpressionContainer getExpressionContainer() {
        return expressionContainer;
    }

    public ClientQueryAggregatedField setExpressionContainer(ClientExpressionContainer expressionContainer) {
        this.expressionContainer = expressionContainer;
        return this;
    }

    /**
     * {@link #setAggregateArg(String)}
     *
     * @return
     */
    @Deprecated
    @Override
    @XmlElement(name = "arg")
    public String getAggregateArg() {
        return aggregateArg;
    }

    /**
     * TODO-Andriy Bob suggested to remove it.
     * TODO-Andriy Because this field is used in case of function is WeightedAverage. We propose to make WeightedAverage available only if it's was defined in metadata as default function.
     * That means we cant specify it in the aggregate function but we will be able to use it as default or in custom expression
     *
     * @return
     */
    @Deprecated
    public ClientQueryAggregatedField setAggregateArg(String aggregateArg) {
        this.aggregateArg = aggregateArg;
        return this;
    }

    @Override
    @XmlTransient
    public String getAggregateType() {
        return aggregateType;
    }

    public ClientQueryAggregatedField setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
        return this;
    }

    public ClientQueryAggregatedField setDataSourceField(ClientDataSourceField field) {
        setFieldReference(field.getName());
        return this;
    }

    /**
     * {@link #setAggregateArg(String)}
     *
     * @return
     */
    @Deprecated
    public String getSecondAggregateField() {
        return getAggregateArg();
    }

    @XmlTransient
    @AssertTrue(message = "query.aggregate.definition.error")
    public boolean isValidAggregate() {
        boolean isDefault = (getAggregateFunction() == null && getAggregateExpression() == null);
        boolean isBuiltin = (getAggregateFunction() != null && !getAggregateFunction().isEmpty());
        boolean isCustom = (getAggregateExpression() != null && !getAggregateExpression().isEmpty())
                || (getExpressionContainer() != null);


        return isDefault || (isBuiltin && !isCustom) || (isCustom && !isBuiltin);
    }

    @Override
    public void accept(ClientQueryVisitor visitor) {
        if (this.expressionContainer != null) {
            this.expressionContainer.getObject().accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientQueryAggregatedField that = (ClientQueryAggregatedField) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (aggregateFunction != null ? !aggregateFunction.equals(that.aggregateFunction) : that.aggregateFunction != null)
            return false;
        if (expressionContainer != null ? !expressionContainer.equals(that.expressionContainer) : that.expressionContainer != null)
            return false;
        if (aggregateType != null ? !aggregateType.equals(that.aggregateType) : that.aggregateType != null)
            return false;
        if (aggregateArg != null ? !aggregateArg.equals(that.aggregateArg) : that.aggregateArg != null) return false;
        return !(fieldRef != null ? !fieldRef.equals(that.fieldRef) : that.fieldRef != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (aggregateFunction != null ? aggregateFunction.hashCode() : 0);
        result = 31 * result + (expressionContainer != null ? expressionContainer.hashCode() : 0);
        result = 31 * result + (aggregateType != null ? aggregateType.hashCode() : 0);
        result = 31 * result + (aggregateArg != null ? aggregateArg.hashCode() : 0);
        result = 31 * result + (fieldRef != null ? fieldRef.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientQueryAggregatedField{" +
                "id='" + id + '\'' +
                ", aggregateFunction='" + aggregateFunction + '\'' +
                ", expression=" + expressionContainer +
                ", aggregateType='" + aggregateType + '\'' +
                ", aggregateArg='" + aggregateArg + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
