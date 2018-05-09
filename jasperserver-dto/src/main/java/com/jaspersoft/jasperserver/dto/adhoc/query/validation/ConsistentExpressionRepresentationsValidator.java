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
package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
import com.jaspersoft.jasperserver.dto.bridge.BridgeRegistry;
import com.jaspersoft.jasperserver.dto.bridge.ExpressionParsingBridge;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.ValidationErrorDescriptorBuilder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ConsistentExpressionRepresentationsValidator implements ConstraintValidator<ConsistentExpressionRepresentations, ClientExpressionContainer>, ValidationErrorDescriptorBuilder {
    @Override
    public void initialize(ConsistentExpressionRepresentations constraintAnnotation) {
        // nothing to initialize
    }

    @Override
    public boolean isValid(ClientExpressionContainer value, ConstraintValidatorContext context) {
        ClientExpression expressionObject = null;
        String expressionString = null;
        if(value != null){
            expressionObject = value.getObject();
            expressionString = value.getString();
        }
        final boolean nothing = expressionObject == null && expressionString == null;
        final boolean objectOnly = expressionObject != null && expressionString == null;
        final boolean stringOnly = expressionObject == null && expressionString != null;
        boolean bothConsistent = expressionObject != null && expressionString != null
                && expressionObject.toString().equals(expressionString);
        if(expressionObject != null && expressionString != null && !bothConsistent){
            final ExpressionParsingBridge expressionParsingBridge = BridgeRegistry.getBridge(ExpressionParsingBridge.class);
            if(expressionParsingBridge == null){
                // string representations don't match and parsing bridge isn't registered. We can't parse expression
                // string and check if object model is equal. As far as same object model could have multiple string
                // representations we can't say for sure, that expression object and expression string are not
                // consistent by string representation only. So, le's return true to avoid possible validation failure
                bothConsistent = true;
            } else {
                ClientExpression anotherExpressionObject = null;
                final Set<String> variables = new HashSet<String>();
                // collect variables names
                expressionObject.accept(new ClientELVisitorAdapter() {
                    @Override
                    public void visit(ClientVariable expression) {
                        variables.add(expression.getName());
                    }
                });
                try {
                    // string representations don't match and expression parsing bridge is available. Now we can get another
                    // expression object out of given expression string.
                    // Let's parse string to object model and check if object.
                    anotherExpressionObject = expressionParsingBridge.parseExpression(expressionString, variables);
                } catch (Exception e){
                    // unable to parse expression string. Let anotherExpressionObject be null then.
                }
                bothConsistent = expressionObject.equals(anotherExpressionObject);
            }

        }
        return  nothing || objectOnly || stringOnly || bothConsistent;
    }

    @Override
    public ErrorDescriptor build(ConstraintViolation violation) {
        final ClientExpressionContainer invalidValue = (ClientExpressionContainer) violation.getInvalidValue();
        final Path propertyPath = violation.getPropertyPath();
        return buildErrorDescriptor(invalidValue, propertyPath.toString());
    }

    public static ErrorDescriptor buildErrorDescriptor(ClientExpressionContainer invalidValue, String propertyPath){
        final String expressionObject = invalidValue.getObject() != null ? invalidValue.getObject().toString() : null;
        final String expressionString = invalidValue.getString();
        return new ErrorDescriptor().setErrorCode(ConsistentExpressionRepresentations.errorCode).setParameters(
                propertyPath,
                "string",
                expressionString,
                "object",
                expressionObject
        ).setMessage("Expression representations do not match. Expression path: " + propertyPath + ", string: "
                + expressionString + ", object: " + expressionObject);

    }
}
