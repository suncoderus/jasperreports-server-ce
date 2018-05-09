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
package com.jaspersoft.jasperserver.war.xmla;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import mondrian.server.JsMondrianServerRegistry;
import mondrian.server.Repository;
import mondrian.spi.impl.IdentityCatalogLocator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.xmla.XmlaHandler;
import mondrian.xmla.impl.MondrianXmlaServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.jaspersoft.jasperserver.api.common.util.StaticCharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

import mondrian.spi.CatalogLocator;

/**
 * @author sbirney
 * @version $Id$
 */

public class XmlaServletImpl extends MondrianXmlaServlet {

    public static String SERVER_URL;
    private static final Log log = LogFactory.getLog(XmlaServletImpl.class);

    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();

        // TODO it is meaningless to generate URLs here, but the XMLServlet requires it
        // Looks like XML/A clients ignore the URL
        try {
            InetAddress local = InetAddress.getLocalHost();

            // We can override the default protocol and port with servlet init parameters

            String defaultProtocol = servletContext
                    .getInitParameter("defaultProtocol");
            if (defaultProtocol == null || defaultProtocol.trim().length() == 0) {
                defaultProtocol = "http";
            }

            String defaultPort = servletContext.getInitParameter("defaultPort");
            if (defaultPort == null || defaultPort.trim().length() == 0) {
                defaultPort = "-1";
            }
            int port = Integer.parseInt(defaultPort);

            URL root = servletContext.getResource("/");
            // Looks like the path will be /localhost/webapp

            int pastHost = root.getPath().indexOf("/", 1);
            String path = root.getPath().substring(pastHost,
                    root.getPath().length());

            SERVER_URL = (new URL(defaultProtocol,
                    local.getCanonicalHostName(), port, path)).toString()
                    + "xmla";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        super.init(config);
    }

    @Override
    protected XmlaHandler.ConnectionFactory createConnectionFactory(
            ServletConfig servletConfig)
            throws ServletException
    {
        if (server == null) {
            // A derived class can alter how the calalog locator object is
            // created.
            CatalogLocator catalogLocator = makeCatalogLocator(servletConfig);

            Properties springConfiguration = null;
            ApplicationContext ctx = StaticApplicationContext.getApplicationContext();

            springConfiguration = ((Properties) ctx.getBean("springConfiguration"));

            String xmlaRepository = "xmlaRepository";
            if (springConfiguration.containsKey("bean.xmlaRepository")) {
                xmlaRepository = springConfiguration.getProperty("bean.xmlaRepository");
            }

            final Repository repository = StaticApplicationContext.getApplicationContext().
                    getBean(xmlaRepository, Repository.class);

            if (catalogLocator == null) {
                catalogLocator = new IdentityCatalogLocator();
            }

            if (repository instanceof XmlaRepositoryImpl) {
                ((XmlaRepositoryImpl) repository).setLocator(catalogLocator);
            }

            server = JsMondrianServerRegistry.INSTANCE.createWithRepository(repository, catalogLocator);
        }
        return (XmlaHandler.ConnectionFactory) server;
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        try {
            // js:i18n set servlet character encoding
            this.charEncoding = ((StaticCharacterEncodingProvider) StaticApplicationContext.getApplicationContext().
                            getBean("encodingProvider")).getCharacterEncoding();
            super.doPost(request, response);
        } catch (Throwable t) {
            throw new ServletException(t);
        }
    }
}
