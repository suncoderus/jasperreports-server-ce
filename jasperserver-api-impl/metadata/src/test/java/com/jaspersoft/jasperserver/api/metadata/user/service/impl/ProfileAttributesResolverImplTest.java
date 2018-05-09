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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ProfileAttributesResolverImplTest {
    @InjectMocks
    private ProfileAttributesResolverImpl profileAttributesResolverImpl = new ProfileAttributesResolverImpl();
    @Mock
    private ProfileAttributeService profileAttributeService;
    @Mock
    private MessageSource messageSource;
    @Mock
    Set<String> excludedResourcesFromAttrResolving;

    private JdbcReportDataSourceImpl parametrizedJdbcReportDataSource;
    private JdbcReportDataSourceImpl notParametrizedJdbcReportDataSource;

    private static String jdbcDriverClass = "4";
    private static String jdbcUserName = "joeUser";
    private static String jdbcUserPassword = "securePassword";
    private static String jdbcConnectionUrl = "jdbc:postgresql://localhost:5432/foodmart";

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void refresh() throws SQLException {
        reset(profileAttributeService, messageSource, excludedResourcesFromAttrResolving);
        List<ProfileAttributeCategory> profileAttributeCategories = Arrays.asList(ProfileAttributeCategory.USER,
                ProfileAttributeCategory.TENANT, ProfileAttributeCategory.SERVER);

        profileAttributesResolverImpl.setProfileAttributeCategories(profileAttributeCategories);
        profileAttributesResolverImpl.setEnabledResolving(true);

        parametrizedJdbcReportDataSource = new JdbcReportDataSourceImpl();
        parametrizedJdbcReportDataSource.setName("{attribute('name')}");
        parametrizedJdbcReportDataSource.setDescription("{attribute('description')}");
        parametrizedJdbcReportDataSource.setPassword("{attribute('password')}");
        parametrizedJdbcReportDataSource.setUsername("{attribute('userName', 'User')}");
        parametrizedJdbcReportDataSource.setConnectionUrl("{attribute('connectionUrl', 'Tenant')}");
        parametrizedJdbcReportDataSource.setDriverClass("{attribute('driverClass', 'Server')}");

        notParametrizedJdbcReportDataSource = new JdbcReportDataSourceImpl();
        notParametrizedJdbcReportDataSource.setName("Some name");
        notParametrizedJdbcReportDataSource.setDescription("Some description");
        notParametrizedJdbcReportDataSource.setPassword("Some password");
        notParametrizedJdbcReportDataSource.setUsername("Some userName" );
        notParametrizedJdbcReportDataSource.setConnectionUrl("Some connectionUrl");
        notParametrizedJdbcReportDataSource.setDriverClass("Some driverClass");

        when(messageSource.getMessage(any(String.class), any(Object[].class), any(Locale.class))).thenReturn("Some message");

        String functionPattern = "\\s*attribute\\s*\\(\\s*'([^\\\\/']+)'\\s*(,\\s*'([^\\\\/']+)'\\s*)?\\)\\s*";
        String pattern = "\\{"+ functionPattern+"\\}";

        List<ProfileAttribute> allProfileAttributesList = new ArrayList<ProfileAttribute>();
        List<ProfileAttribute> userProfileAttributesList = new ArrayList<ProfileAttribute>();
        List<ProfileAttribute> tenantProfileAttributesList = new ArrayList<ProfileAttribute>();
        List<ProfileAttribute> serverProfileAttributesList = new ArrayList<ProfileAttribute>();

        final String custom = "custom";

        ProfileAttribute profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("userName");
        profileAttribute.setAttrValue(jdbcUserName);
        profileAttribute.setGroup(custom);
        userProfileAttributesList.add(profileAttribute);

        profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("password");
        profileAttribute.setAttrValue(jdbcUserPassword);
        profileAttribute.setGroup(custom);
        userProfileAttributesList.add(profileAttribute);

        profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("name");
        profileAttribute.setAttrValue("name");
        profileAttribute.setGroup(custom);
        tenantProfileAttributesList.add(profileAttribute);

        profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("description");
        profileAttribute.setAttrValue("description");
        profileAttribute.setGroup(custom);
        tenantProfileAttributesList.add(profileAttribute);

        profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("connectionUrl");
        profileAttribute.setAttrValue(jdbcConnectionUrl);
        profileAttribute.setGroup(custom);
        tenantProfileAttributesList.add(profileAttribute);

        profileAttribute = new ProfileAttributeImpl();
        profileAttribute.setAttrName("driverClass");
        profileAttribute.setAttrValue(jdbcDriverClass);
        profileAttribute.setGroup(custom);
        serverProfileAttributesList.add(profileAttribute);

        allProfileAttributesList.addAll(userProfileAttributesList);
        allProfileAttributesList.addAll(tenantProfileAttributesList);
        allProfileAttributesList.addAll(serverProfileAttributesList);

        when(profileAttributeService.getCurrentUserProfileAttributes(any(ExecutionContext.class), eq(ProfileAttributeCategory.USER))).thenReturn(userProfileAttributesList);
        when(profileAttributeService.getCurrentUserProfileAttributes(any(ExecutionContext.class), eq(ProfileAttributeCategory.TENANT))).thenReturn(tenantProfileAttributesList);
        when(profileAttributeService.getCurrentUserProfileAttributes(any(ExecutionContext.class), eq(ProfileAttributeCategory.SERVER))).thenReturn(serverProfileAttributesList);
        when(profileAttributeService.getCurrentUserProfileAttributes(any(ExecutionContext.class), eq(ProfileAttributeCategory.HIERARCHICAL))).thenReturn(allProfileAttributesList);

        profileAttributesResolverImpl.setAttributePlaceholderPattern(pattern);
        profileAttributesResolverImpl.setAttributeFunctionPattern(functionPattern);
    }

    @Test
    public void merge_allAttributesIsValidAndDefined_passed() {

        Logger log = Logger.getLogger("com.jaspersoft.jasperserver.api.metadata.user.service.impl.ProfileAttributesResolverImpl");
        log.setLevel(Level.DEBUG);

        JdbcReportDataSourceImpl result = profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);
        assertEquals(result.getDriverClass(), jdbcDriverClass);
        assertEquals(result.getUsername(), jdbcUserName);
        assertEquals(result.getPassword(), jdbcUserPassword);
        assertEquals(result.getConnectionUrl(), jdbcConnectionUrl);
        verify(messageSource, times(4)).getMessage(eq("profile.attribute.debug.substitution.success"), any(Object[].class), any(Locale.class));
        log.setLevel(null);
    }

    @Test
    public void merge_nameAndDescriptionAreSkiped_passed() {
        JdbcReportDataSourceImpl result = profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);

        assertEquals(result.getName(), "{attribute('name')}");
        assertEquals(result.getDescription(), "{attribute('description')}");
    }

    @Test
    public void merge_ResourcesIsExcludedFromResolving_passed() {
        when(excludedResourcesFromAttrResolving.contains(parametrizedJdbcReportDataSource.getClass().getCanonicalName())).thenReturn(true);
        JdbcReportDataSourceImpl result = profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);

        assertEquals(result.getDriverClass(), parametrizedJdbcReportDataSource.getDriverClass());
        assertEquals(result.getUsername(), parametrizedJdbcReportDataSource.getUsername());
        assertEquals(result.getPassword(), parametrizedJdbcReportDataSource.getPassword());
        assertEquals(result.getConnectionUrl(), parametrizedJdbcReportDataSource.getConnectionUrl());
    }

    @Test
    public void merge_ResolvingIsDisabled_passed() {
        profileAttributesResolverImpl.setEnabledResolving(false);
        JdbcReportDataSourceImpl result = profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);

        assertEquals(result.getDriverClass(), parametrizedJdbcReportDataSource.getDriverClass());
        assertEquals(result.getUsername(), parametrizedJdbcReportDataSource.getUsername());
        assertEquals(result.getPassword(), parametrizedJdbcReportDataSource.getPassword());
        assertEquals(result.getConnectionUrl(), parametrizedJdbcReportDataSource.getConnectionUrl());
    }

    @Test(expectedExceptions = JSException.class)
    public void merge_attributeCategoryIsNotSupported_exception() throws Exception {
        parametrizedJdbcReportDataSource.setUsername("{attribute('userName', 'UnknownCategory')}");
        profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);
    }

    @Test(expectedExceptions = JSException.class)
    public void merge_someCategoricalAttributeIsNotDefined_exception() throws Exception {
        parametrizedJdbcReportDataSource.setUsername("{attribute('unknownAttribute', 'User')}");
        profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);
    }

    @Test(expectedExceptions = JSException.class)
    public void merge_someHierarchicalAttributeIsNotDefined_exception() throws Exception {
        parametrizedJdbcReportDataSource.setUsername("{attribute('unknownAttribute')}");
        profileAttributesResolverImpl.mergeResource(parametrizedJdbcReportDataSource);
    }

    @Test
    public void isParametrizedResource_RestrictedCategoriesIsNotSpecified_passed() throws Exception {
        assertTrue(profileAttributesResolverImpl.isParametrizedResource(parametrizedJdbcReportDataSource));
        assertFalse(profileAttributesResolverImpl.isParametrizedResource(notParametrizedJdbcReportDataSource));
    }

    @Test
    public void isParametrizedResource_parametrizedOnlyWithServerAttributes_passed() throws Exception {
        JdbcReportDataSourceImpl parametrizedOnlyWithServerAttr = new JdbcReportDataSourceImpl();
        parametrizedOnlyWithServerAttr.setName("Some name");
        parametrizedOnlyWithServerAttr.setDescription("Some description");
        parametrizedOnlyWithServerAttr.setPassword("Some password");
        parametrizedOnlyWithServerAttr.setUsername("{attribute('userName', 'Server')}");
        parametrizedOnlyWithServerAttr.setConnectionUrl("{attribute('connectionUrl', 'Server')}");
        parametrizedOnlyWithServerAttr.setDriverClass("{attribute('driverClass', 'Server')}");

        assertTrue(profileAttributesResolverImpl.isParametrizedResource(parametrizedOnlyWithServerAttr,
                ProfileAttributeCategory.SERVER));
        assertFalse(profileAttributesResolverImpl.isParametrizedResource(parametrizedJdbcReportDataSource,
                ProfileAttributeCategory.SERVER));
        assertFalse(profileAttributesResolverImpl.isParametrizedResource(notParametrizedJdbcReportDataSource,
                ProfileAttributeCategory.SERVER));
    }

}
