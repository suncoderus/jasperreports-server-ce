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
package com.jaspersoft.jasperserver.war.model.test;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.war.model.TreeHelper;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import com.jaspersoft.jasperserver.war.model.impl.RepositoryTreeDataProviderImpl;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Ignore // This test is excluded in pom.xml, so ignoring it here.
public class TreeTest extends TestCase {
    private Properties jdbcProps;
    private RepositoryService repo;
    //private UserAuthorityService userAuthService;
    //private ObjectPermissionService objectPermissionService;

    public TreeTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        //loadJdbcProps();
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] {"hibernateConfig.xml", "viewService.xml", 
                        /*"userAuthorityService.xml", "repoService-Security.xml", 
                        "methodAndObjectSecurity.xml"*/
                        });

        //userAuthService = (UserAuthorityService) appContext.getBean("userAuthorityService");
        //objectPermissionService = (ObjectPermissionService) appContext.getBean("objectPermissionService");
        repo = (HibernateRepositoryService) appContext.getBean("repoService");

        // clobber adhoc folder if it's there
        /*Folder adhocFolder = repo.getFolder(null, "/adhoc");
        if (adhocFolder != null) {
            repo.deleteFolder(null, "/adhoc");
        }*/
    }

    protected Properties loadJdbcProps() throws IOException, FileNotFoundException {
        jdbcProps = new Properties();
        String jdbcPropFile = System.getProperty("test.hibernate.jdbc.properties");
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(jdbcPropFile));
        jdbcProps.load(is);
        is.close();
        return jdbcProps;
    }
    
    public void testRepositoryTreeDataProvider() {
        
        RepositoryTreeDataProviderImpl repDP = new RepositoryTreeDataProviderImpl();
        repDP.setRepositoryService(repo);
        TreeNode node = repDP.getNode(null, "/", 1);
        //repDP.getNode("/datasources/JServerJdbcDS", 0);
        if (node != null) {
            System.out.println(node.toJSONString());
        }
        
    }
    
    public void testTreeHelper() {
        
        RepositoryTreeDataProviderImpl repDP = new RepositoryTreeDataProviderImpl();
        repDP.setRepositoryService(repo);
        List list = new ArrayList();
        list.add("/reports/samples");
        TreeNode node = TreeHelper.getSubtree(null, repDP, "/", list, 0);
        if (node != null) {
            System.out.println(node.toJSONString());
        }
        
    }
}
