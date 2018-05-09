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

package com.jaspersoft.jasperserver.jaxrs.common.validation;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jaspersoft.jasperserver.api.metadata.common.util.ConstraintValidatorContextDecorator;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.ValidationErrorDescriptorBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException.MANDATORY_PARAMETER_ERROR;

/**
 * {@link ExceptionMapper} for {@link ValidationException}.
 * <p/>
 * instances is sent in {@link Response} as well (in addition to HTTP 400/500 status code). Supported media types are:
 * {@code application/json}/{@code application/xml} (in appropriate provider is registered on server) or
 *
 * @author Michal Gajdos (michal.gajdos at oracle.com)
 */
@javax.ws.rs.ext.Provider
@Component
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Resource
    private MessageSource messageSource;
    private static final Logger LOGGER = Logger.getLogger(ValidationExceptionMapper.class.getName());
    public static final String BUNDLE_PREFIX = "exception.remote.";
    public static final String METHOD_MESSAGE = "message";

    @Override
    public Response toResponse(final ValidationException exception) {
        if (exception instanceof ConstraintViolationException) {
            LOGGER.log(Level.FINER, "constraint.violations.encountered", exception);

            final ConstraintViolationException cve = (ConstraintViolationException) exception;
            final Response.ResponseBuilder response = Response.status(Response.Status.BAD_REQUEST);

            response.entity(toErrorDescriptors(cve));

            return response.build();
        } else {
            LOGGER.log(Level.WARNING, "validation.exception.raised", exception);

            return Response.serverError().entity(exception.getMessage()).build();
        }
    }

    /**
     * Extract {@link ConstraintViolation constraint violations} from given exception and transform them into a list of
     * {@link com.jaspersoft.jasperserver.dto.common.ErrorDescriptor validation errors}.
     *
     * @param violation exception containing constraint violations.
     * @return list of validation errors (not {@code null}).
     */
    public List<ErrorDescriptor> toErrorDescriptors(final ConstraintViolationException violation) {
        return Lists.transform(Lists.newArrayList(violation.getConstraintViolations()),
                new Function<ConstraintViolation, ErrorDescriptor>() {
                    @Override
                    public ErrorDescriptor apply(final ConstraintViolation violation) {
                        final ConstraintDescriptor constraintDescriptor = violation.getConstraintDescriptor();
                        final List<Class<ConstraintValidator>> constraintValidatorClasses =
                                constraintDescriptor.getConstraintValidatorClasses();
                        if (constraintValidatorClasses != null) {
                            for (Class<ConstraintValidator> constraintValidatorClass : constraintValidatorClasses) {
                                if (ValidationErrorDescriptorBuilder.class.isAssignableFrom(constraintValidatorClass)) {
                                    try {
                                        final ConstraintValidator validator = constraintValidatorClass.newInstance();
                                        validator.initialize(constraintDescriptor.getAnnotation());
                                        final ErrorDescriptor errorDescriptor = ((ValidationErrorDescriptorBuilder) validator)
                                                .build(violation);
                                        if (errorDescriptor != null) {
                                            if (errorDescriptor.getMessage() == null) {
                                                errorDescriptor.setMessage(getDefaultMessage(errorDescriptor.getErrorCode(),
                                                        errorDescriptor.getParameters()));
                                            }
                                            return errorDescriptor;
                                        }
                                    } catch (Exception e) {
                                        throw new IllegalStateException("Failed to instantiate validator", e);
                                    }
                                }
                            }
                        }
                        Object[] args;
                        String errorCode = getErrorCode(violation);
                        if (ConstraintValidatorContextDecorator.getArguments(violation) != null) {
                            args = ConstraintValidatorContextDecorator.getArguments(violation).toArray();
                        } else if (MANDATORY_PARAMETER_ERROR.equals(errorCode) && violation.getInvalidValue() == null) {
                            args = new Object[]{
                                    getViolationPath(violation)
                            };
                        } else {
                            args = new Object[]{
                                    getViolationPath(violation),
                                    getViolationInvalidValue(violation.getInvalidValue())
                            };
                        }

                        return new ErrorDescriptor()
                                .setErrorCode(errorCode)
                                .addParameters(args)
                                .setMessage(getDefaultMessage(errorCode, args));
                    }
                });
    }

    /**
     * Get a path to a field causing constraint violations.
     *
     * @param violation constraint violation.
     * @return path to a property that caused constraint violations.
     */
    private String getViolationPath(final ConstraintViolation violation) {
        return violation.getPropertyPath().toString();
    }

    private String getDefaultMessage(String errorCode, Object[] args) {
        try {
            String message = null;
            if (messageSource != null) {
                message = messageSource.getMessage(BUNDLE_PREFIX.concat(errorCode), args, Locale.ENGLISH);
                if (message == null || message.equals(BUNDLE_PREFIX.concat(errorCode))) {
                    message = messageSource.getMessage(errorCode, args, Locale.ENGLISH);
                }
            }

            return message;
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    /**
     * Provide a string value of (invalid) value that caused the exception.
     *
     * @param invalidValue invalid value causing BV exception.
     * @return string value of given object or {@code null}.
     */
    private static String getViolationInvalidValue(final Object invalidValue) {
        if (invalidValue == null) {
            return null;
        }

        if (invalidValue.getClass().isArray()) {
            if (invalidValue instanceof Object[]) {
                return Arrays.toString((Object[]) invalidValue);
            } else if (invalidValue instanceof boolean[]) {
                return Arrays.toString((boolean[]) invalidValue);
            } else if (invalidValue instanceof byte[]) {
                return Arrays.toString((byte[]) invalidValue);
            } else if (invalidValue instanceof char[]) {
                return Arrays.toString((char[]) invalidValue);
            } else if (invalidValue instanceof double[]) {
                return Arrays.toString((double[]) invalidValue);
            } else if (invalidValue instanceof float[]) {
                return Arrays.toString((float[]) invalidValue);
            } else if (invalidValue instanceof int[]) {
                return Arrays.toString((int[]) invalidValue);
            } else if (invalidValue instanceof long[]) {
                return Arrays.toString((long[]) invalidValue);
            } else if (invalidValue instanceof short[]) {
                return Arrays.toString((short[]) invalidValue);
            }
        }

        return invalidValue.toString();
    }

    private static String getErrorCode(final ConstraintViolation violation) {
        for (DefaultConstraintAnnotation annotationEnum : DefaultConstraintAnnotation.values()) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType() == annotationEnum.clazz &&
                    violation.getMessageTemplate().equals(annotationEnum.defaultErrorCode)) {
                return annotationEnum.overrideErrorCode;
            }
        }

        return violation.getMessageTemplate();
    }

    enum DefaultConstraintAnnotation {
        NotNull(javax.validation.constraints.NotNull.class, MANDATORY_PARAMETER_ERROR);

        Class clazz;
        String overrideErrorCode;
        String defaultErrorCode;

        DefaultConstraintAnnotation(Class clazz, String overrideErrorCode) {
            this.clazz = clazz;
            this.overrideErrorCode = overrideErrorCode;
            this.defaultErrorCode = getDefaultValue(clazz, METHOD_MESSAGE);
        }
    }

    public static <T> T getDefaultValue(Class<?> type, String method) {
        try {
            return (T) type.getMethod(method).getDefaultValue();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Please verify that method \"" + method + "\" for class \"" + type + "\" exists", e);
        }
    }

}

