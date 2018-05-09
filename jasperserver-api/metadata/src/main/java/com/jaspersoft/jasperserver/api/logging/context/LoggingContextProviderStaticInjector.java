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
package com.jaspersoft.jasperserver.api.logging.context;

import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>This class i used to inject implementation of {@link LoggingContextProvider}
 * to other classes if we can not use spring injection way,
 * for example if instance of particular class is created dynamically in code which we can't control.</p>
 *
 * <p>Injection is done in following way:<p/>
 * <p>implementation of {@link LoggingContextProvider} injected here via spring context,
 * and then it is injected using <b>static</b> setter to all classes which are passed to this bean</p>
 *
 * <p>The only requirement for such classes is to have static setter for {@link LoggingContextProvider}<p/>
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public final class LoggingContextProviderStaticInjector implements InitializingBean {

    private static final String SETTER_METHOD_NAME = "setLoggingContextProvider";

    private LoggingContextProvider loggingContextProvider;
    private List<String> loggingContextProviderAwareClasses;

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    public void setLoggingContextProviderAwareClasses(List<String> loggingContextProviderAwareClasses) {
        this.loggingContextProviderAwareClasses = loggingContextProviderAwareClasses;
    }

    public void afterPropertiesSet() throws Exception {
        if (loggingContextProviderAwareClasses != null && !loggingContextProviderAwareClasses.isEmpty()) {
            for (String loggingContextProviderAwareClassName: loggingContextProviderAwareClasses) {
                Class loggingContextProviderAwareClass = Class.forName(loggingContextProviderAwareClassName);
                Method method =
                        loggingContextProviderAwareClass.getMethod(SETTER_METHOD_NAME, LoggingContextProvider.class);
                method.invoke(null, loggingContextProvider);
            }
        }
    }
}
