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
package com.jaspersoft.jasperserver.jaxrs;

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.jaxrs.common.ErrorDescriptorContentTypeResponseFilter;
import com.jaspersoft.jasperserver.jaxrs.common.validation.ValidationExceptionMapper;
import com.jaspersoft.jasperserver.jaxrs.logging.RestLoggingSettings;
import com.jaspersoft.jasperserver.jaxrs.resources.DownloadResponseFilter;
import com.jaspersoft.jasperserver.remote.helpers.JacksonMapperProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.Path;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class RestSpringApplication extends ResourceConfig {
    public RestSpringApplication() {
        register(MultiPartFeature.class);
        register(HttpMethodOverrideFilter.class);
        register(DownloadResponseFilter.class);
        register(ErrorDescriptorContentTypeResponseFilter.class);
        register(JacksonMapperProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        final ApplicationContext applicationContext = StaticApplicationContext.getApplicationContext();
        if (applicationContext == null) {
            throw new IllegalStateException("Unable to initialize REST subsystem. " +
                    "Spring application context shouldn't be null");
        }
        final RestLoggingSettings restLoggingSettings = applicationContext.getBean(RestLoggingSettings.class);
        boolean isLoggingFeatureAvailable = true;
        try{
            Class.forName("org.glassfish.jersey.logging.LoggingFeature");
        } catch (ClassNotFoundException e) {
            isLoggingFeatureAvailable = false;
        }
        if(isLoggingFeatureAvailable) {
            register(new LoggingFeature(
                    Logger.getLogger(restLoggingSettings.getLoggerName()),
                    restLoggingSettings.getLevel(),
                    LoggingFeature.Verbosity.valueOf(restLoggingSettings.getVerbosity()),
                    restLoggingSettings.getMaxEntitySize()));
        }
        final Map<String, Object> providers = applicationContext.getBeansWithAnnotation(Provider.class);
        for (Object provider : providers.values()) {
            register(provider.getClass());
        }
        final Map<String, Object> services = applicationContext.getBeansWithAnnotation(Path.class);
        for (Object service : services.values()) {
            register(service.getClass());
        }
        // registration below is required for glassfish to override default validation exception mapper
        // see more information 
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(applicationContext.getBean(ValidationExceptionMapper.class)).to(ExceptionMapper.class);
            }
        });
    }
}
