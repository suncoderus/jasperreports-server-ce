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

package com.jaspersoft.jasperserver.api.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.common.util.JdbcDriverShim;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.sql.Driver;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class JdbcDriverServiceImplTest {

    @InjectMocks
    private JdbcDriverServiceImpl jdbcDriverService = Mockito.spy(new JdbcDriverServiceImpl());

    @Mock
    private PropertiesManagementService propertiesManagementService;

    @Mock
    private RepositoryService repositoryService;

    private String TEST_DRIVER_CLASS_NAME = "com.jaspersoft.jasperserver.api.common.service.impl.TestJdbcDriver";
    private String TEST_MULTI_DRIVER_CLASS_NAME = "com.jaspersoft.jasperserver.api.common.service.impl.TestMultipleJarsJdbcDriver";

    final private Map<String, String> propertiesManagementServiceProps = new HashMap<String, String>();
    final private Set<Driver> driversSet = new LinkedHashSet<Driver>();

    @Before
    public void setUp() throws Exception {
        jdbcDriverService.setJdbcDriversFolder("/jdbc");
        jdbcDriverService.setSystemClassLoaderFirst(false);

        //Properties management service
        propertiesManagementServiceProps.clear();
        Mockito.when(propertiesManagementService.getProperty(Matchers.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String key = (String) invocation.getArguments()[0];
                return propertiesManagementServiceProps.get(key);
            }
        });
        Mockito.when(propertiesManagementService.entrySet()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return propertiesManagementServiceProps.entrySet();
            }
        });

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String key = (String) invocation.getArguments()[0];
                String value = (String) invocation.getArguments()[1];
                propertiesManagementServiceProps.put(key, value);
                return null;
            }
        }).when(propertiesManagementService).setProperty(Matchers.anyString(), Matchers.anyString());

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String key = (String) invocation.getArguments()[0];
                propertiesManagementServiceProps.remove(key);
                return null;
            }
        }).when(propertiesManagementService).remove(Matchers.anyString());

        //DriverManager mocks
        driversSet.clear();
        Mockito.when(jdbcDriverService.getDriversRegisteredInJVM()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Iterator<Driver> i = driversSet.iterator();
                return new Enumeration() {
                    @Override
                    public boolean hasMoreElements() {
                        return i.hasNext();
                    }

                    @Override
                    public Object nextElement() {
                        return i.next();
                    }
                };
            }
        });

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                driversSet.add((Driver) invocation.getArguments()[0]);
                return null;
            }
        }).when(jdbcDriverService).registerDriverInJVM(Matchers.any(Driver.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                driversSet.remove(invocation.getArguments()[0]);
                return null;
            }
        }).when(jdbcDriverService).unRegisterDriverFromJVM(Matchers.any(Driver.class));
    }

    private void afterPropertiesSet() throws Exception {
        for (Map.Entry<String, String> entry: propertiesManagementServiceProps.entrySet()) {
            if (entry.getKey().startsWith(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_PREFIX)) {
                try {
                    jdbcDriverService.setDriverMapping(entry.getKey().substring(
                            JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_PREFIX.length()), entry.getValue());
                } catch (Exception e) {
                    /* do nothing */
                }
            }
        }
    }

    /**
     * When jdbc driver is available in classpath (and no other settings set)
     * it should be loaded and global properties list should be updated
     * to reflect that driver was registered from system classloader.
     *
     * @throws Exception
     */
    @Test
    public void ifJdbcDriverAvailableInClassPathItShouldBeLoaded() throws Exception {
        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TestJdbcDriver.class, driversSet.iterator().next().getClass());
        assertEquals(0, propertiesManagementServiceProps.size());
    }

    /**
     * If driver class isn't present in classpath NoClassDefFoundError should be thrown
     * @throws ClassNotFoundException
     */
    @Test(expected = ClassNotFoundException.class)
    public void ifJdbcDriverNotAvailableInClassPathExceptionShouldBeThrown() throws Exception {
        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME + "1");
    }

    /**
     * If driver class isn't present neither in classpath nor in repo NoClassDefFoundError should be thrown
     * @throws ClassNotFoundException
     */
    @Test(expected = ClassNotFoundException.class)
    public void ifJdbcDriverNotAvailableNeitherInClassPathNorInRepoExceptionShouldBeThrown() throws Exception {

        //Set up
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME + "1"), "testjdbcdriver.jar");
        afterPropertiesSet();

        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME + "1");
    }

    /**
     * If there is a mapping in global properties list for driver and
     * this driver present in both classpath and repository by this mapping -
     * repository version should take priority.
     * @throws Exception
     */
    @Test
    public void ifJdbcDriverMappingSetItShouldHavePriorityOverSystemClassPath() throws Exception {

        //Set up
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");

        afterPropertiesSet();

        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof JdbcDriverShim);

        Class<? extends Driver> registeredDriverClass = ((JdbcDriverShim)driver).getDriver().getClass();
        assertEquals(TEST_DRIVER_CLASS_NAME, registeredDriverClass.getName());
        assertFalse(TestJdbcDriver.class.equals(registeredDriverClass));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertEquals(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME),
                propertiesManagementServiceProps.keySet().iterator().next());
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.values().iterator().next());
    }

    /**
     * If there is a mapping in global properties list for driver and
     * this driver present in both classpath and repository by this mapping
     * and JdbcDriverService is configured that system classpath has priority -
     * driver from classpath should always be loaded first
     * @throws Exception
     */
    @Test
    public void testWhenSystemClasLoaderHasPriority() throws Exception {

        jdbcDriverService.setSystemClassLoaderFirst(true);

        //Set up
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");

        afterPropertiesSet();

        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TestJdbcDriver.class, driversSet.iterator().next().getClass());

        assertEquals(1, propertiesManagementServiceProps.size());
        assertNull(propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        TEST_DRIVER_CLASS_NAME)));
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        TEST_DRIVER_CLASS_NAME) + JdbcDriverServiceImpl.SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX));
    }

    /**
     * If there is a mapping in global properties list for driver and
     * this driver present only in classpath it should be loaded from classpath.
     * Also global properties list should be modified to reflect this.
     * @throws Exception
     */
    @Test
    public void ifJdbcDriverMappingSetButDoesNotExistsItShouldBeLoadedFromSystemClassPath() throws Exception {

        //Set up
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver.jar");

        afterPropertiesSet();

        //Test method call
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Checck expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof TestJdbcDriver);
        assertTrue(TestJdbcDriver.class.equals(driver.getClass()));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertNull(propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME)));
        assertEquals("testjdbcdriver.jar",
                propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        TEST_DRIVER_CLASS_NAME) + JdbcDriverServiceImpl.SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX));
    }

    /**
     * If driver was already registered but then mapping was changed.
     * Original driver should be unregistered. And with next attempt to register driver
     * one from new mapping should be used.
     *
     * @throws Exception
     */
    @Test
    public void ifJdbcDriverMappingHasChangedItShouldBeReflectedInService() throws Exception {

        //Driver from classpath will be registered
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);


        //Add new mapping
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");

        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");

        //Test method call
        jdbcDriverService.setDriverMapping(TEST_DRIVER_CLASS_NAME, "testjdbcdriver");
        //Assert that previously loaded driver was unregistered
        assertEquals(0, driversSet.size());

        //try to register driver again
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof JdbcDriverShim);

        Class<? extends Driver> registeredDriverClass = ((JdbcDriverShim)driver).getDriver().getClass();
        assertEquals(TEST_DRIVER_CLASS_NAME, registeredDriverClass.getName());
        assertFalse(TestJdbcDriver.class.equals(registeredDriverClass));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertEquals(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME),
                propertiesManagementServiceProps.keySet().iterator().next());
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.values().iterator().next());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void allDriversWhichAreNotPresentInUpdatedMappingShouldBeUnloaded() throws Exception {


        //Set up
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
        afterPropertiesSet();

        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        propertiesManagementServiceProps.remove(TEST_DRIVER_CLASS_NAME);
        jdbcDriverService.setDriverMappings(new HashMap<String, String>());
        //Assert that previously loaded driver was unregistered
        assertEquals(0, driversSet.size());

        //Driver from classpath should be registered
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TestJdbcDriver.class, driversSet.iterator().next().getClass());
        assertEquals(0, propertiesManagementServiceProps.size());
    }

    @Test
    public void subSequentMappingChangesShouldNotCauseErrors() throws Exception {

        //Driver from classpath will be registered
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);


        //Add new mapping
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");

        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");

        //First mapping change
        jdbcDriverService.setDriverMapping(TEST_DRIVER_CLASS_NAME, "testjdbcdriver");
        //Assert that previously loaded driver was unregistered
        assertEquals(0, driversSet.size());

        //Next mapping change
        propertiesManagementServiceProps.remove(TEST_DRIVER_CLASS_NAME);
        jdbcDriverService.setDriverMappings(new HashMap<String, String>());

        //try to register driver again
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TestJdbcDriver.class, driversSet.iterator().next().getClass());
        assertEquals(0, propertiesManagementServiceProps.size());
    }

    @Test
    public void testMultipleJarsJdbcDriver() throws Exception {

        //Add new mapping
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_MULTI_DRIVER_CLASS_NAME), "multiplejars");

        mockJars("/jdbc/multiplejars", "testmultiplejarsjdbcdriver.jar", "testjdbcdriver.jar");
        afterPropertiesSet();

        jdbcDriverService.register(TEST_MULTI_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof JdbcDriverShim);

        Class<? extends Driver> registeredDriverClass = ((JdbcDriverShim)driver).getDriver().getClass();
        assertEquals(TEST_MULTI_DRIVER_CLASS_NAME, registeredDriverClass.getName());
        assertFalse(TestMultipleJarsJdbcDriver.class.equals(registeredDriverClass));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertEquals(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_MULTI_DRIVER_CLASS_NAME),
                propertiesManagementServiceProps.keySet().iterator().next());
        assertEquals("multiplejars",
                propertiesManagementServiceProps.values().iterator().next());
    }

    /**
     * If there is a mapping in global properties list for driver and
     * this driver is not present in url by this mapping and there are other mapping for other driver
     * which also contains this driver that driver should not be loaded, only one from system classpath could replace
     * absent driver by specific mapping.
     *
     * @throws Exception
     */
    @Test
    public void ifDriverClassNotAvailableByMappingOnlyClasspathDriverCouldBeUsedInstead() throws Exception {

        //Set up
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_MULTI_DRIVER_CLASS_NAME), "multiplejars");
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "fakeurl");
        mockJars("/jdbc/multiplejars", "testmultiplejarsjdbcdriver.jar", "testjdbcdriver.jar");
        afterPropertiesSet();

        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TestJdbcDriver.class, driversSet.iterator().next().getClass());

        assertEquals(2, propertiesManagementServiceProps.size());
        assertNull(propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME)));
        assertEquals("fakeurl",
                propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        TEST_DRIVER_CLASS_NAME) + JdbcDriverServiceImpl.SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX));
    }


    /**
     * If there are no mapping for driver class in global properties list
     * but this driver present in class by other mapping - it also could be used for this driver
     * and it has priority over system classpath.
     *
     * @throws Exception
     */
    @Test
    public void ifThereAreNoMappingForClassButOtherClassLoadersContainsThisDriverItShouldNotBeUsed() throws Exception {

        //Set up
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_MULTI_DRIVER_CLASS_NAME), "multiplejars");
        mockJars("/jdbc/multiplejars", "testmultiplejarsjdbcdriver.jar", "testjdbcdriver.jar");
        afterPropertiesSet();

        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof TestJdbcDriver);

        assertEquals(1, propertiesManagementServiceProps.size());

        assertNull(propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME)));
    }

    @Test
    public void ifDriverAlsreadyRegisteredItShouldNotBeDoubleRegistered() throws Exception {
        //Test method call
        boolean registered = jdbcDriverService.isRegistered(TEST_DRIVER_CLASS_NAME);
        assertTrue(registered);

        registered = jdbcDriverService.isRegistered(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertTrue(registered);

        Mockito.verify(propertiesManagementService, Mockito.times(1)).getProperty(
                Matchers.eq(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT, TEST_DRIVER_CLASS_NAME)));
    }

    @Test
    public void isRegisteredShouldNotThrowExceptions() throws Exception {
        //Set up
        //Test method call
        boolean registered = jdbcDriverService.isRegistered(TEST_DRIVER_CLASS_NAME + 1);
        assertFalse(registered);
    }

    @Test
    public void removeMappingShouldUpdateMappingsAndUnregisterDriver() throws Exception {

        //Add new mapping
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");

        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
        afterPropertiesSet();

        //jdbcDriverService.setDriverMapping(TEST_DRIVER_CLASS_NAME, "testjdbcdriver");
        jdbcDriverService.register(TEST_DRIVER_CLASS_NAME);

        //Test method call
        jdbcDriverService.removeDriverMapping(TEST_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(0, driversSet.size());
    }

    @Test
    public void removeMappingShouldUnregisterSpecifiedSystemDriver() throws Exception {
        assertTrue(jdbcDriverService.
                isRegistered(TEST_DRIVER_CLASS_NAME));
        assertTrue(jdbcDriverService.
                isRegistered(TEST_MULTI_DRIVER_CLASS_NAME));

        //Test method call
        jdbcDriverService.removeDriverMapping(TEST_MULTI_DRIVER_CLASS_NAME);

        //Check expectations
        assertEquals(1, driversSet.size());
        assertEquals(TEST_DRIVER_CLASS_NAME,
                driversSet.iterator().next().getClass().getName());
    }

    @Test
    public void setDriverMappingAndRegisterShouldRegisterDriverAndSetMappingAndUpdateGlobalProperties() throws Exception {

        //Set up
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");

        //Test method call
        jdbcDriverService.setDriverMappingAndRegister(TEST_DRIVER_CLASS_NAME, "testjdbcdriver");

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof JdbcDriverShim);

        Class<? extends Driver> registeredDriverClass = ((JdbcDriverShim)driver).getDriver().getClass();
        assertEquals(TEST_DRIVER_CLASS_NAME, registeredDriverClass.getName());
        assertFalse(TestJdbcDriver.class.equals(registeredDriverClass));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertEquals(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME),
                propertiesManagementServiceProps.keySet().iterator().next());
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.values().iterator().next());
    }

    @Test
    public void setDriverMappingAndRegisterShouldReuploadSystemDriver() throws Exception {

        //Set up
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
//        mockJars("/jdbc/testmultiplejarsjdbcdriver", "testmultiplejarsjdbcdriver.jar");

        assertTrue(jdbcDriverService.isRegistered(TEST_DRIVER_CLASS_NAME));
        assertTrue(jdbcDriverService.
                isRegistered(TEST_MULTI_DRIVER_CLASS_NAME));

        //Test method call
        jdbcDriverService.setDriverMappingAndRegister(TEST_DRIVER_CLASS_NAME, "testjdbcdriver");

        //Check expectations
        assertEquals(2, driversSet.size());

        Iterator<Driver> driverIterator = driversSet.iterator();
        assertTrue(driverIterator.next() instanceof TestMultipleJarsJdbcDriver);
        assertTrue(driverIterator.next() instanceof JdbcDriverShim);

        assertEquals(1, propertiesManagementServiceProps.size());

        Iterator<String> keySetIterator = propertiesManagementServiceProps.keySet().iterator();
        Iterator<String> valuesIterator = propertiesManagementServiceProps.values().iterator();
        String f = JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT;
        assertEquals(String.format(f, TEST_DRIVER_CLASS_NAME),
                keySetIterator.next());
        assertEquals("testjdbcdriver", valuesIterator.next());
    }

/*    @Test
    public void setDriverShouldRegisterDriverAndSetMappingAndUpdateGlobalProperties() throws Exception {

        Map<String, byte[]> sampleData = getSampleFilesData();

        //Test method call
        jdbcDriverService.setDriver(TEST_DRIVER_CLASS_NAME, sampleData);

        //Check expectations
        assertEquals(1, driversSet.size());

        Driver driver = driversSet.iterator().next();
        assertTrue(driver instanceof JdbcDriverShim);

        Class<? extends Driver> registeredDriverClass = ((JdbcDriverShim)driver).getDriver().getClass();
        assertEquals(TEST_DRIVER_CLASS_NAME, registeredDriverClass.getName());
        assertFalse(TestJdbcDriver.class.equals(registeredDriverClass));

        assertEquals(1, propertiesManagementServiceProps.size());
        assertEquals(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME),
                propertiesManagementServiceProps.keySet().iterator().next());
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.values().iterator().next());
    }

    private Map<String, byte[]> getSampleFilesData() {
        Map<String, byte[]> filesData = new HashMap<String, byte[]>();
        filesData.put("testjdbcdriver.jar", new byte[] {1});
        filesData.put("testjdbcdriver1.jar", new byte[] {2});
        return filesData;
    }*/

    @Test
    public void setDriverMappingAndRegisterShouldNotSetMappingAndUpdateGlobalPropertiesIfExceptionOccurs() throws Exception {

        try {
            //Test method call
            jdbcDriverService.setDriverMappingAndRegister(TEST_DRIVER_CLASS_NAME + "1", "testjdbcdriver");
            fail("Exception was not thrown");
        } catch (Exception e) {
            //Check expectations
            assertEquals(0, driversSet.size());
            assertEquals(0, propertiesManagementServiceProps.size());
        }
    }

    @Test
    public void ttt() throws Exception {

        //Set up
        mockJars("/jdbc/testjdbcdriver", "testjdbcdriver.jar");
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                TEST_DRIVER_CLASS_NAME), "testjdbcdriver");
        propertiesManagementServiceProps.put(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                "not.existing.Driver"), "notExistingFolder");

        afterPropertiesSet();

        //Test method call
        Set<String> registeredDrivers = jdbcDriverService.getRegisteredDriverClassNames();
        assertEquals(1, registeredDrivers.size());
        assertEquals(TEST_DRIVER_CLASS_NAME, registeredDrivers.iterator().next());

        //Check expectations
        assertEquals(1, driversSet.size());

        assertEquals(2, propertiesManagementServiceProps.size());
        assertEquals("testjdbcdriver",
                propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        TEST_DRIVER_CLASS_NAME)));
        assertEquals("notExistingFolder",
                propertiesManagementServiceProps.get(String.format(JdbcDriverServiceImpl.SYSTEM_PROPERTIES_LIST_KEY_FORMAT,
                        "not.existing.Driver")));
    }

    private void mockJars(String url, String... pathToJar) throws Exception {

        Folder folderMock = Mockito.mock(Folder.class);
        Mockito.when(repositoryService.getFolder(Matchers.any(ExecutionContext.class), Matchers.eq(url))).thenReturn(folderMock);

        List<ResourceLookup> resources = new LinkedList<ResourceLookup>();
        for (String path: pathToJar) {
            ResourceLookup lookup = Mockito.mock(ResourceLookup.class);
            Mockito.when(lookup.getURIString()).thenReturn(url + "/" + path);
            resources.add(lookup);

            byte[] jar = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
            FileResource fileResourceMock = Mockito.mock(FileResource.class);
            Mockito.when(fileResourceMock.getFileType()).thenReturn(FileResource.TYPE_JAR);
            Mockito.when(fileResourceMock.hasData()).thenReturn(true);
            Mockito.when(fileResourceMock.getData()).thenReturn(jar);

            Mockito.when(repositoryService.getResource(Matchers.any(ExecutionContext.class), Matchers.eq(url + "/" + path))).thenReturn(fileResourceMock);
        }

        Mockito.when(repositoryService.loadResourcesList(Matchers.any(ExecutionContext.class), Matchers.any(FilterCriteria.class))).thenReturn(resources);
    }
}
