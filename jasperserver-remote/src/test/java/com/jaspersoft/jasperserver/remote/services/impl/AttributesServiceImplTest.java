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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeLevel;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.AttributesSearchResultImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributeEmbeddedContainer;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentity;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentityResolver;
import com.jaspersoft.jasperserver.remote.resources.converters.PermissionConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.UserAttributesConverter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * An unit-test for the AttributesServiceImpl class
 *
 * @author askorodumov
 * @version $Id$
 */
public class AttributesServiceImplTest {
    @Mock
    ProfileAttributeService profileAttributeService;
    @Mock
    RecipientIdentityResolver recipientIdentityResolver;
    @Mock
    UserAttributesConverter attributesConverter;
    @Mock
    AttributesPermissionServiceImpI attributesPermissionService;
    @Mock
    PermissionConverter permissionConverter;

    // Base for the all tests
    private static final String RECIPIENT_NAME = "anonymousUser";
    private static final String RECIPIENT_URI = "user:/anonymousUser";
    private static final String HOLDER_URI = "/users/anonymousUser";
    private static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    private static final String ROLE_ADMINISTRATOR_URI = "role:/ROLE_ADMINISTRATOR";

    private final User recipient = new UserImpl();
    private final RoleImpl administratorRole = new RoleImpl();
    private final RecipientIdentity recipientIdentity = new RecipientIdentity(User.class, RECIPIENT_NAME);
    private final RecipientIdentity roleAdministratorIdentity = new RecipientIdentity(Role.class, ROLE_ADMINISTRATOR);
    private final AttributesServiceImpl service = new AttributesServiceImpl();

    private final ProfileAttribute attributeA;
    private final ProfileAttribute attributeB;
    private final ProfileAttribute attributeC;
    private final ProfileAttribute attributeD;
    private final ClientAttribute clientAttributeA = new ClientAttribute();
    private final ClientAttribute clientAttributeB = new ClientAttribute();
    private final ClientAttribute clientAttributeC = new ClientAttribute();
    private final ClientAttribute clientAttributeD = new ClientAttribute();


    /**
     * The sole constructor
     */
    public AttributesServiceImplTest() {
        attributeA = new UnmodifiableProfileAttribute("attribute-A", "value for attribute A", HOLDER_URI);
        attributeB = new UnmodifiableProfileAttribute("attribute-B", "value for attribute B", HOLDER_URI);
        attributeC = new UnmodifiableProfileAttribute("attribute-C", "value for attribute C", HOLDER_URI);
        attributeD = new UnmodifiableProfileAttribute("attribute-D", "value for attribute D", HOLDER_URI);
    }

    @BeforeClass
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipient.setUsername(RECIPIENT_NAME);
        administratorRole.setRoleName(ROLE_ADMINISTRATOR);

        clientAttributeA.setName(attributeA.getAttrName());
        clientAttributeA.setValue(attributeA.getAttrValue());
        clientAttributeA.setHolder(RECIPIENT_URI);
        clientAttributeA.setPermissionMask(1);

        clientAttributeB.setName(attributeB.getAttrName());
        clientAttributeB.setValue(attributeB.getAttrValue());
        clientAttributeB.setHolder(RECIPIENT_URI);
        clientAttributeB.setPermissionMask(1);

        clientAttributeC.setName(attributeC.getAttrName());
        clientAttributeC.setValue(attributeC.getAttrValue());
        clientAttributeC.setHolder(RECIPIENT_URI);
        clientAttributeC.setPermissionMask(1);

        clientAttributeD.setName(attributeD.getAttrName());
        clientAttributeD.setValue(attributeD.getAttrValue());
        clientAttributeD.setHolder(RECIPIENT_URI);
        clientAttributeD.setPermissionMask(1);

        // set mockito beans to the service
        service.profileAttributeService = profileAttributeService;
        service.recipientIdentityResolver = recipientIdentityResolver;
        service.attributesPermissionService = attributesPermissionService;
        service.permissionConverter = permissionConverter;

        // Use reflection for setting private field "attributesConverter"
        Field fieldAttributesConverter = service.getClass().getDeclaredField("attributesConverter");
        fieldAttributesConverter.setAccessible(true);
        fieldAttributesConverter.set(service, attributesConverter);
    }

    @BeforeMethod
    public void initBeforeMethod() {
        reset(profileAttributeService, recipientIdentityResolver,
                attributesConverter, attributesPermissionService, permissionConverter);

        // Base mockito stubs
        when(recipientIdentityResolver.resolveRecipientObject(recipientIdentity))
                .thenReturn(recipient);
        when(recipientIdentityResolver.toRecipientUri(recipientIdentity))
                .thenReturn(RECIPIENT_URI);
        when(recipientIdentityResolver.resolveRecipientObject(RECIPIENT_URI))
                .thenReturn(recipient);

        when(attributesConverter.toServer(clientAttributeA, null))
                .thenReturn(attributeA);
        when(attributesConverter.toServer(clientAttributeB, null))
                .thenReturn(attributeB);
        when(attributesConverter.toServer(clientAttributeC, null))
                .thenReturn(attributeC);
        when(attributesConverter.toServer(clientAttributeD, null))
                .thenReturn(attributeD);
        when(attributesConverter.toClient(attributeA, null))
                .thenReturn(clientAttributeA);
        when(attributesConverter.toClient(attributeB, null))
                .thenReturn(clientAttributeB);
        when(attributesConverter.toClient(attributeC, null))
                .thenReturn(clientAttributeC);
        when(attributesConverter.toClient(attributeD, null))
                .thenReturn(clientAttributeD);
    }

    @Test
    public void getAttributes_recipientDoesNotHaveAttributes_successEmptyResult() throws Exception {
        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class),
                eq(recipient),
                eq(new AttributesSearchCriteria.Builder().setNames(Collections.singleton(attributeB.getAttrName())).build())
        )).thenReturn(new AttributesSearchResultImpl<ProfileAttribute>());

        List<ClientAttribute> list = service.getAttributes(
                recipientIdentity, Collections.singleton(attributeB.getAttrName()), false);
        assertNotNull("Result should not be null", list);
        assertTrue("Size of the result should be zero", list.size() == 0);
    }

    @Test
    public void getAttributes_recipientHasTwoAttributesAndIncludePermissionsIsEnabled_foundTheTwoAttributes()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);
        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class),
                eq(recipient),
                eq(new AttributesSearchCriteria.Builder().build())
        )).thenReturn(searchResult);

        List<ClientAttribute> result
                = service.getAttributes(recipientIdentity, null, true);
        assertNotNull("Result should not be null", result);
        assertTrue("Size of the result should be two", result.size() == 2);

        assertTrue("The result must contain certain attributes, but it does not!",
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result,
                        strings(attributeA.getAttrName(), attributeB.getAttrName()),
                        strings(attributeA.getAttrValue(), attributeB.getAttrValue())
                ));
    }

    @Test
    public void getAttributes_usedSearchCriteriaWithHolderAndRecipientHasTwoAttributes_foundTheTwoAttributes()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);
        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class),
                eq(recipient),
                eq(new AttributesSearchCriteria.Builder().setHolder(RECIPIENT_URI).build())
        )).thenReturn(searchResult);

        AttributesSearchResult<ClientAttribute> result = service.getAttributes(
                new AttributesSearchCriteria.Builder().setHolder(RECIPIENT_URI).build(),
                false);
        assertNotNull("Result should not be null", result);
        assertNotNull("The list in the result should not be null", result.getList());
        assertTrue("Size of the result should be two", result.getList().size() == 2);

        assertTrue("The result must contain certain attributes, but it does not!",
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result.getList(),
                        strings(attributeA.getAttrName(), attributeB.getAttrName()),
                        strings(attributeA.getAttrValue(), attributeB.getAttrValue())
                ));
    }

    @Test
    public void putAttributes_listAndNamesContainsTheSameAttributeAndRecipientHasNoAttributes_attributeWasInserted()
            throws Exception {
        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class),
                eq(recipient),
                eq(new AttributesSearchCriteria.Builder().setNames(Collections.singleton(attributeB.getAttrName())).build())
        )).thenReturn(new AttributesSearchResultImpl<ProfileAttribute>());

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Collections.singletonList(clientAttributeB),
                Collections.singleton(attributeB.getAttrName()),
                false);
        assertNotNull("Result should not be null", result);
        assertTrue("Size of the result should be one", result.size() == 1);
        assertEquals("Attribute name does not match the original", result.get(0).getName(), clientAttributeB.getName());

        assertTrue("The result must contain certain attribute, but it does not!",
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result,
                        attributeB.getAttrName(),
                        attributeB.getAttrValue()
                ));
    }

    @Test
    public void deleteAttribute_deleteExistingAttribute_success() throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        ProfileAttribute profileAttribute = new UnmodifiableProfileAttribute(
                attributeB.getAttrName(), attributeB.getAttrValue(), HOLDER_URI);
        searchResult.setList(Collections.singletonList(profileAttribute));
        searchResult.setTotalCount(1);

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setNames(Collections.singleton(attributeB.getAttrName())).setSkipServerSettings(true).build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                (ExecutionContext) isNull(), eq(recipient), eq(searchCriteria))).thenReturn(searchResult);

        service.deleteAttributes(recipientIdentity, Collections.singleton(attributeB.getAttrName()));

        verify(profileAttributeService, times(1)).deleteProfileAttribute(null, profileAttribute);
    }

    /**
     * The test checks, whether the order is saved for added attributes (bug 42207).
     *
     * @throws Exception
     */
    @Test
    public void putAttributes_insert15NewAttributesWhenRecipientHasNoAttributes_attributesWereInsertedInTheSameOrder()
            throws Exception {
        final int ATTRIBUTES_COUNT = 15;
        final List<ProfileAttribute> attributes = new ArrayList<ProfileAttribute>(ATTRIBUTES_COUNT);
        final List<ClientAttribute> clientAttributes = new ArrayList<ClientAttribute>(ATTRIBUTES_COUNT);

        for (int i = 0; i < ATTRIBUTES_COUNT; i++) {
            String name = Integer.toString(i);
            ProfileAttribute attribute = new UnmodifiableProfileAttribute(name, name, HOLDER_URI);
            attributes.add(attribute);

            ClientAttribute clientAttribute = new ClientAttribute();
            clientAttribute.setName(attribute.getAttrName());
            clientAttribute.setValue(attribute.getAttrValue());
            clientAttributes.add(clientAttribute);
        }

        // Mockito stubbing
        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class),
                eq(recipient),
                eq(new AttributesSearchCriteria.Builder().build())
        )).thenReturn(new AttributesSearchResultImpl<ProfileAttribute>());

        for (int i = 0; i < ATTRIBUTES_COUNT; i++) {
            when(attributesConverter.toServer(clientAttributes.get(i), null))
                    .thenReturn(attributes.get(i));
            when(attributesConverter.toClient(attributes.get(i), null))
                    .thenReturn(clientAttributes.get(i));
        }

        ArgumentCaptor<ProfileAttribute> captor = ArgumentCaptor.forClass(ProfileAttribute.class);
        List<ClientAttribute> immutableList = Collections.unmodifiableList(clientAttributes);
        List<ClientAttribute> list = service.putAttributes(
                recipientIdentity,
                immutableList,
                null,
                false);
        assertNotNull("Result should not be null", list);
        assertTrue("Size of the result should be one", list.size() == ATTRIBUTES_COUNT);
        for (int i = 0; i < ATTRIBUTES_COUNT; i++) {
            assertEquals("Attributes order does not match the original",
                    list.get(i).getName(), clientAttributes.get(i).getName());
        }
        verify(profileAttributeService, times(0))
                .deleteProfileAttribute(any(ExecutionContext.class), any(ProfileAttribute.class));
        verify(profileAttributeService, times(ATTRIBUTES_COUNT))
                .putProfileAttribute(any(ExecutionContext.class), captor.capture());
        List<ProfileAttribute> capturedList = captor.getAllValues();
        assertNotNull("Captured list should not be null", capturedList);
        for (int i = 0; i < ATTRIBUTES_COUNT; i++) {
            assertEquals("Attributes order does not match the order",
                    list.get(i).getName(), attributes.get(i).getAttrName());
        }
    }

    /**
     * The test put search criteria with wrong holder-URI to the method getAttributes(AttributesSearchCriteria, boolean).
     * The test expects that inner invocation of recipientIdentityResolver.resolveRecipientObject(String)
     * will cause an exception, and as a result the tested method will return empty result.
     */
    @Test
    public void
    getAttributes_usedSearchCriteriaWithHolderAndRecipientIdentityResolverThrowsAnException_successEmptyResult()
            throws Exception {
        String wrongHolderUri = "wrong-holder-uri";
        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setHolder(wrongHolderUri).build();

        when(recipientIdentityResolver.resolveRecipientObject(wrongHolderUri))
                .thenThrow(new RuntimeException("wrong holder URI"));

        AttributesSearchResult<ClientAttribute> searchResult = service.getAttributes(searchCriteria, false);
        assertNotNull(searchResult.getList());
        assertTrue("", searchResult.getList().isEmpty());
    }

    /**
     * Tests getAttributes(AttributesSearchCriteria, boolean) method
     * where the includeEffectivePermissionsInResult parameter is true
     */
    @Test
    public void
    getAttributes_usedSearchCriteriaWithHolderAndNamesAndIncludeEffectivePermissionsIsEnabled_recipientHasTwoAttributes_foundRequiredAttributes()
            throws Exception {
        AttributesSearchCriteria searchCriteria = new  AttributesSearchCriteria.Builder()
                .setHolder(RECIPIENT_URI)
                .setNames(Collections.unmodifiableSet(
                        new HashSet<String>(Arrays.asList(attributeA.getAttrName(), attributeB.getAttrName()))))
                .build();

        AttributesSearchResult<ProfileAttribute> serverSearchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        serverSearchResult.setList(Arrays.asList(attributeA, attributeB));
        serverSearchResult.setTotalCount(2);

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(serverSearchResult);

        AttributesSearchResult<ClientAttribute> result = service.getAttributes(searchCriteria, true);
        assertEquals("Result must contain 2 attributes", result.getList().size(), 2);

        LinkedList<ClientAttribute> cleanupList = new LinkedList<ClientAttribute>(result.getList());
        HypermediaAttribute hypermediaAttribute;
        while (!cleanupList.isEmpty() && (cleanupList.get(0) instanceof HypermediaAttribute)
                && (attributeA.getAttrName().equals((hypermediaAttribute = (HypermediaAttribute) cleanupList.get(0)).getName())
                || attributeB.getAttrName().equals(hypermediaAttribute.getName()))) {
            assertNotNull("In the HypermediaAttribute the \"embedded\" field must be not null",
                    hypermediaAttribute.getEmbedded());
            cleanupList.remove(0);
        }

        assertTrue("Result must contain only HypermediaAttribute attributes with the names represented"
                .concat(" variables \'name1\' and \'name2\'"), cleanupList.isEmpty());

        assertTrue("The result must contain certain attributes, but it does not!",
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result.getList(),
                        strings(attributeA.getAttrName(), attributeB.getAttrName()),
                        strings(attributeA.getAttrValue(), attributeB.getAttrValue())
                ));
    }

    /**
     * Update attributes
     * 1) Replaces existing collection to new one(in body request) if there are no any name parameters.
     * With includeEffectivePermissionsInResult = false
     */
    @Test
    public void
    putAttributes_listTakesTwoAttributesAndRecipientHasAnotherAttributes_resultTakesTheInsertedAttributes()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder().build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Arrays.asList(clientAttributeC, clientAttributeD),
                null,
                false
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must contains two attributes", result.size(), 2);

        assertTrue("The result must contains attributes ".concat(attributeC.getAttrName())
                        .concat(" and ").concat(attributeD.getAttrName()),
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result,
                        strings(attributeC.getAttrName(), attributeD.getAttrName()),
                        strings(attributeC.getAttrValue(), attributeD.getAttrValue())
                ));

        ArgumentCaptor<ProfileAttribute> putCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .putProfileAttribute(any(ExecutionContext.class), putCaptor.capture());
        ArgumentCaptor<ProfileAttribute> delCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .deleteProfileAttribute(any(ExecutionContext.class), delCaptor.capture());

        List<ProfileAttribute> putCapturedList = putCaptor.getAllValues();
        assertNotNull("Captured list for putProfileAttribute() method must not be null", putCapturedList);
        assertEquals("Captured list for putProfileAttribute() method must contains two elements",
                putCapturedList.size(), 2);

        assertTrue("Captured list for putProfileAttribute() method must contains attributes "
                        .concat(attributeC.getAttrName()).concat(" and").concat(attributeD.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        putCapturedList,
                        strings(attributeC.getAttrName(), attributeD.getAttrName()),
                        strings(attributeC.getAttrValue(), attributeD.getAttrValue())
                ));

        List<ProfileAttribute> delCapturedList = delCaptor.getAllValues();
        assertNotNull("Captured list for deleteProfileAttribute() method must not be null", delCapturedList);
        assertEquals("Captured list for deleteProfileAttribute() method must contains two elements",
                delCapturedList.size(), 2);

        assertTrue("Captured list for deleteProfileAttribute() method must contains attributes "
                        .concat(attributeA.getAttrName()).concat(" and").concat(attributeB.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        delCapturedList,
                        strings(attributeA.getAttrName(), attributeB.getAttrName()),
                        strings(attributeA.getAttrValue(), attributeB.getAttrValue())
                ));
    }

    /**
     * Update attributes
     * 1) Replaces existing collection to new one(in body request) if there are no any name parameters.
     * With null instead of "attributes" argument
     * With includeEffectivePermissionsInResult = false
     */
    @Test
    public void putAttributes_usedEmptyListAndEmptySetAndRecipientHasTwoAttributes_theTwoAttributesWereDeleted()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder().build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                null,
                null,
                false
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must not contains any attributes", result.size(), 0);

        verify(profileAttributeService, times(0))
                .putProfileAttribute(any(ExecutionContext.class), any(ProfileAttribute.class));
        ArgumentCaptor<ProfileAttribute> delCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .deleteProfileAttribute(any(ExecutionContext.class), delCaptor.capture());

        List<ProfileAttribute> delCapturedList = delCaptor.getAllValues();
        assertNotNull("Captured list for deleteProfileAttribute() method must not be null", delCapturedList);
        assertEquals("Captured list for deleteProfileAttribute() method must contains two elements",
                delCapturedList.size(), 2);

        assertTrue("Captured list for deleteProfileAttribute() method must contains attributes "
                        .concat(attributeA.getAttrName()).concat(" and").concat(attributeB.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        delCapturedList,
                        strings(attributeA.getAttrName(), attributeB.getAttrName()),
                        strings(attributeA.getAttrValue(), attributeB.getAttrValue())
                ));
    }

    /**
     * Update attributes
     * 1) Replaces existing collection to new one(in body request) if there are no any name parameters.
     * With includeEffectivePermissionsInResult = true
     */
    @Test
    public void
    putAttributes_listHasTwoAttributesAndIncludeEffectivePermissionsIsEnabledAndRecipientHasAnotherTwoAttributes_twoAttributesWereDeletedAndTwoAttributesWereInserted()
            throws Exception {
        ProfileAttribute attribute1 = new UnmodifiableProfileAttribute(
                "attribute-1", "value for attribute 1", HOLDER_URI);
        ProfileAttribute attribute2 = new UnmodifiableProfileAttribute(
                "attribute-2", "value for attribute 2", HOLDER_URI);
        ProfileAttribute attribute3 = new UnmodifiableProfileAttribute(
                "attribute-3", "value for attribute 3", HOLDER_URI);
        ProfileAttribute attribute4 = new UnmodifiableProfileAttribute(
                "attribute-4", "value for attribute 4", HOLDER_URI);

        ClientAttribute clientAttribute1 = new ClientAttribute();
        ClientAttribute clientAttribute2 = new ClientAttribute();
        ClientAttribute clientAttribute3 = new ClientAttribute();
        ClientAttribute clientAttribute4 = new ClientAttribute();

        clientAttribute1.setName(attribute1.getAttrName());
        clientAttribute1.setValue(attribute1.getAttrValue());
        clientAttribute1.setHolder(RECIPIENT_URI);
        clientAttribute1.setPermissionMask(1);

        clientAttribute2.setName(attribute2.getAttrName());
        clientAttribute2.setValue(attribute2.getAttrValue());
        clientAttribute2.setHolder(RECIPIENT_URI);
        clientAttribute2.setPermissionMask(1);

        clientAttribute3.setName(attribute3.getAttrName());
        clientAttribute3.setValue(attribute3.getAttrValue());
        clientAttribute3.setHolder(RECIPIENT_URI);
        clientAttribute3.setPermissionMask(1);

        clientAttribute4.setName(attribute4.getAttrName());
        clientAttribute4.setValue(attribute4.getAttrValue());
        clientAttribute4.setHolder(RECIPIENT_URI);
        clientAttribute4.setPermissionMask(1);

        when(attributesConverter.toServer(clientAttribute1, null))
                .thenReturn(attribute1);
        when(attributesConverter.toServer(clientAttribute2, null))
                .thenReturn(attribute2);
        when(attributesConverter.toServer(clientAttribute3, null))
                .thenReturn(attribute3);
        when(attributesConverter.toServer(clientAttribute4, null))
                .thenReturn(attribute4);
        when(attributesConverter.toClient(attribute1, null))
                .thenReturn(clientAttribute1);
        when(attributesConverter.toClient(attribute2, null))
                .thenReturn(clientAttribute2);
        when(attributesConverter.toClient(attribute3, null))
                .thenReturn(clientAttribute3);
        when(attributesConverter.toClient(attribute4, null))
                .thenReturn(clientAttribute4);

        RepositoryPermission repositoryPermission1 = new RepositoryPermission(attribute1.getURI(), ROLE_ADMINISTRATOR_URI, 1);
        RepositoryPermission repositoryPermission2 = new RepositoryPermission(attribute2.getURI(), ROLE_ADMINISTRATOR_URI, 1);
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = new HypermediaAttributeEmbeddedContainer();
        embeddedContainer1.setRepositoryPermissions(
                Collections.singletonList(repositoryPermission1));
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer();
        embeddedContainer2.setRepositoryPermissions(
                Collections.singletonList(repositoryPermission2));

        HypermediaAttribute hypermediaAttribute1 = new HypermediaAttribute(clientAttribute1);
        hypermediaAttribute1.setEmbedded(embeddedContainer1);
        HypermediaAttribute hypermediaAttribute2 = new HypermediaAttribute(clientAttribute2);
        hypermediaAttribute2.setEmbedded(embeddedContainer2);

        when(attributesConverter.toServer(hypermediaAttribute1, null))
                .thenReturn(attribute1);
        when(attributesConverter.toServer(hypermediaAttribute2, null))
                .thenReturn(attribute2);

        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attribute3, attribute4));
        searchResult.setTotalCount(2);

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder().build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        when(permissionConverter.toServer(repositoryPermission1, null))
                .thenReturn(createObjectPermission(attribute1.getURI(), roleAdministratorIdentity, 1));
        when(permissionConverter.toServer(repositoryPermission2, null))
                .thenReturn(createObjectPermission(attribute2.getURI(), roleAdministratorIdentity, 1));

        ObjectPermission objectPermission = createObjectPermission(attribute1.getURI(), administratorRole, 1);
        when(attributesPermissionService.getPermissions(attribute1.getURI(), null, null, true, false))
                .thenReturn(Collections.singletonList(objectPermission));
        when(permissionConverter.toClient(objectPermission, null))
                .thenReturn(repositoryPermission1);
        objectPermission = createObjectPermission(attribute2.getURI(), administratorRole, 1);
        when(attributesPermissionService.getPermissions(attribute2.getURI(), null, null, true, false))
                .thenReturn(Collections.singletonList(objectPermission));
        when(permissionConverter.toClient(objectPermission, null))
                .thenReturn(repositoryPermission2);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Arrays.asList(hypermediaAttribute1, hypermediaAttribute2),
                null,
                true
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must contains two attributes", result.size(), 2);

        assertTrue("Result must contains attributes ".concat(attribute1.getAttrName())
                        .concat(" and ").concat(attribute2.getAttrName()),
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result,
                        strings(attribute1.getAttrName(), attribute2.getAttrName()),
                        strings(attribute1.getAttrValue(), attribute2.getAttrValue())
                ));

        ArgumentCaptor<ProfileAttribute> putCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .putProfileAttribute(any(ExecutionContext.class), putCaptor.capture());
        ArgumentCaptor<ProfileAttribute> delCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .deleteProfileAttribute(any(ExecutionContext.class), delCaptor.capture());
        ArgumentCaptor<ProfileAttribute> putPermAttrCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        ArgumentCaptor<List> putPermObjCaptor = ArgumentCaptor.forClass(List.class);
        verify(attributesPermissionService, times(2))
                .putPermissions(putPermAttrCaptor.capture(), putPermObjCaptor.capture());

        // -------- check putCaptor ---------------
        List<ProfileAttribute> putCapturedList = putCaptor.getAllValues();
        assertNotNull("Captured list for putProfileAttribute() method must not be null", putCapturedList);
        assertEquals("Captured list for putProfileAttribute() method must contains two elements",
                putCapturedList.size(), 2);

        assertTrue("Captured list for putProfileAttribute() method must contains attributes "
                        .concat(attribute1.getAttrName()).concat(" and").concat(attribute2.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        putCapturedList,
                        strings(attribute1.getAttrName(), attribute2.getAttrName()),
                        strings(attribute1.getAttrValue(), attribute2.getAttrValue())
                ));

        // -------- check delCaptor ---------------
        List<ProfileAttribute> delCapturedList = delCaptor.getAllValues();
        assertNotNull("Captured list for deleteProfileAttribute() method must not be null", delCapturedList);
        assertEquals("Captured list for deleteProfileAttribute() method must contains two elements",
                delCapturedList.size(), 2);

        assertTrue("Captured list for deleteProfileAttribute() method must contains attributes "
                        .concat(attribute3.getAttrName()).concat(" and").concat(attribute4.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        delCapturedList,
                        strings(attribute3.getAttrName(), attribute4.getAttrName()),
                        strings(attribute3.getAttrValue(), attribute4.getAttrValue())
                ));

        // -------- check putPermAttrCaptor ---------------
        List<ProfileAttribute> putPermAttrList = putPermAttrCaptor.getAllValues();
        assertNotNull("Captured list for the 1-st arg of the putPermissions() method must not be null", putPermAttrList);
        assertEquals("Captured list for the 1-st arg of the putPermissions() method must contains two elements",
                putPermAttrList.size(), 2);

        assertTrue("Captured list for the 1-st arg of the putPermissions() method must contains attributes "
                .concat(attribute1.getAttrName()).concat(" and").concat(attribute2.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        putPermAttrList,
                        strings(attribute1.getAttrName(), attribute2.getAttrName()),
                        strings(attribute1.getAttrValue(), attribute2.getAttrValue())
                ));

        // -------- check putPermObjCaptor ---------------
        List<List> putPermObjList =  putPermObjCaptor.getAllValues();
        assertNotNull("Captured list for the 2-nd arg of the putPermissions() method must not be null", putPermObjList);
        assertEquals("Captured list for the 2-nd arg of the putPermissions() method must contains two elements",
                putPermObjList.size(), 2);

        int check = 3;
        for (List list : putPermObjList) {
            assertNotNull("Passed objectPermissions list must not be null", list);
            assertEquals("Passed objectPermissions list must contains one ObjectPermission", list.size(), 1);
            assertTrue("Passed objectPermissions list must contains one ObjectPermission",
                    list.get(0) instanceof ObjectPermission);
            ObjectPermission permission = (ObjectPermission) list.get(0);
            if (attribute1.getURI().equals(permission.getURI())) {
                check -= 1;
            } else if (attribute2.getURI().equals(permission.getURI())) {
                check -= 2;
            } else {
                break;
            }
        }
        assertEquals("Captured list for the 2-nd arg of the putPermissions() method must contains URIs "
                        .concat(attribute1.getURI()).concat(" and").concat(attribute2.getURI()), check, 0);
    }

    /**
     * 2) If there are name parameters, then attributes should be updated in following way:
     * a) If requested attribute name parameter value match with attribute name located in body,
     *    then we will create or update the attribute on server side;
     */
    @Test
    public void
    putAttributes_listAndNamesHaveTheSameTwoAttributesAndRecipientHasAnotherTwoAttributes_twoAttributesWereInserted()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        Set<String> namesSet = new HashSet<String>(Arrays.asList(attributeC.getAttrName(), attributeD.getAttrName()));

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setNames(namesSet).build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Arrays.asList(clientAttributeC, clientAttributeD),
                namesSet,
                false
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must contains two attributes", result.size(), 2);

        assertTrue("Result must contains attributes ".concat(attributeC.getAttrName())
                        .concat(" and ").concat(attributeD.getAttrName()),
                isListClientAttributesIdenticalInCompositionOfNamesValues(
                        result,
                        strings(attributeC.getAttrName(), attributeD.getAttrName()),
                        strings(attributeC.getAttrValue(), attributeD.getAttrValue())
                ));

        ArgumentCaptor<ProfileAttribute> putCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(2))
                .putProfileAttribute(any(ExecutionContext.class), putCaptor.capture());
        verify(profileAttributeService, times(0))
                .deleteProfileAttribute(any(ExecutionContext.class), any(ProfileAttribute.class));

        List<ProfileAttribute> putCapturedList = putCaptor.getAllValues();
        assertNotNull("Captured list for putProfileAttribute() method must not be null", putCapturedList);
        assertEquals("Captured list for putProfileAttribute() method must contains two elements",
                putCapturedList.size(), 2);

        assertTrue("Captured list for putProfileAttribute() method must contains attributes "
                        .concat(attributeC.getAttrName()).concat(" and").concat(attributeD.getAttrName()),
                isListAttributesIdenticalInCompositionOfNamesValues(
                        putCapturedList,
                        strings(attributeC.getAttrName(), attributeD.getAttrName()),
                        strings(attributeC.getAttrValue(), attributeD.getAttrValue())
                ));
    }

    /**
     * 2) If there are name parameters, then attributes should be updated in following way:
     * b) If requested attribute name parameter value is not exist in body, then we will delete attribute on the server.
     */
    @Test
    public void
    putAttributes_listHasTwoAttributesAndNamesHasAnotherAttribute_recipientHasAttributeFromNames_OneAttributeWereDeletedNoOneWereInserted()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        Set<String> names = Collections.singleton(attributeA.getAttrName());

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder().setNames(names).build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Arrays.asList(clientAttributeC, clientAttributeD),
                names,
                false
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must contains two attributes", result.size(), 0);

        verify(profileAttributeService, times(0))
                .putProfileAttribute(any(ExecutionContext.class), any(ProfileAttribute.class));
        ArgumentCaptor<ProfileAttribute> delCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(1))
                .deleteProfileAttribute(any(ExecutionContext.class), delCaptor.capture());


        List<ProfileAttribute> delCapturedList = delCaptor.getAllValues();
        assertNotNull("Captured list for deleteProfileAttribute() method must not be null", delCapturedList);
        assertEquals("Captured list for deleteProfileAttribute() method must contains two elements",
                delCapturedList.size(), 1);
        assertEquals("Captured list for deleteProfileAttribute() method must contains attribute "
                        .concat(attributeA.getAttrName()),
                delCapturedList.get(0).getAttrName(), attributeA.getAttrName());
    }

    /**
     * 2) If there are name parameters, then attributes should be updated in following way:
     *  c) If attribute in body doesn't match with name parameter, then we will ignore it.
     */
    @Test
    public void
    putAttributes_ListHasTwoAttributesAndNamesHasOneFromListAndRecipientHasAnotherAttributes_onlyAttributeFromNamesWereInserted()
            throws Exception {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        Set<String> namesSet = Collections.singleton(attributeC.getAttrName());

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
                .setNames(namesSet).build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        List<ClientAttribute> result = service.putAttributes(
                recipientIdentity,
                Arrays.asList(clientAttributeC, clientAttributeD),
                namesSet,
                false
        );

        assertNotNull("Result must be not null", result);
        assertEquals("Result must contains two attributes", result.size(), 1);
        assertEquals("Result must contains attribute ".concat(attributeC.getAttrName()),
                result.get(0).getName(), attributeC.getAttrName());

        ArgumentCaptor<ProfileAttribute> putCaptor = ArgumentCaptor.forClass(ProfileAttribute.class);
        verify(profileAttributeService, times(1))
                .putProfileAttribute(any(ExecutionContext.class), putCaptor.capture());
        verify(profileAttributeService, times(0))
                .deleteProfileAttribute(any(ExecutionContext.class), any(ProfileAttribute.class));

        List<ProfileAttribute> putCapturedList = putCaptor.getAllValues();
        assertNotNull("Captured list for putProfileAttribute() method must not be null", putCapturedList);
        assertEquals("Captured list for putProfileAttribute() method must contains one element",
                putCapturedList.size(), 1);
        assertEquals("Captured list for putProfileAttribute() method must contains attribute "
                        .concat(attributeC.getAttrName()),
                putCapturedList.get(0).getAttrName(), attributeC.getAttrName());
    }






    private static class UnmodifiableProfileAttribute implements ProfileAttribute {
        private final ProfileAttributeImpl instance;

        public UnmodifiableProfileAttribute(String name, String value, String holderUri) {
            instance = new ProfileAttributeImpl();
            instance.setAttrName(name);
            instance.setAttrValue(value);
            instance.setUri(name, holderUri);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(107, 701)
                    .append(getAttrName())
                    .append(getAttrValue())
                    .append(getURI())
                    .toHashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UnmodifiableProfileAttribute)) return false;

            UnmodifiableProfileAttribute that = (UnmodifiableProfileAttribute) o;

            return new EqualsBuilder()
                    .append(this.getAttrName(), that.getAttrName())
                    .append(this.getAttrValue(), that.getAttrValue())
                    .append(this.getURI(), that.getURI())
                    .isEquals();
        }

        @Override
        public String toString() {
            return instance.toString();
        }

        @Override
        public String getAttrName() {
            return instance.getAttrName();
        }

        @Override
        public void setAttrName(String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAttrValue() {
            return instance.getAttrValue();
        }

        @Override
        public void setAttrValue(String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getPrincipal() {
            return instance.getPrincipal();
        }

        @Override
        public void setPrincipal(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSecure() {
            return instance.isSecure();
        }

        @Override
        public void setSecure(boolean secure) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getGroup() {
            return instance.getGroup();
        }

        @Override
        public void setGroup(String group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDescription() {
            return instance.getDescription();
        }

        @Override
        public void setDescription(String description) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProfileAttributeLevel getLevel() {
            return instance.getLevel();
        }

        @Override
        public void setLevel(ProfileAttributeLevel level) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getUri() {
            return instance.getUri();
        }

        @Override
        public void setUri(String attrName, String holderUri) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getURI() {
            return instance.getURI();
        }

        @Override
        public String getPath() {
            return instance.getPath();
        }

        @Override
        public String getProtocol() {
            return instance.getProtocol();
        }

        @Override
        public String getParentURI() {
            return instance.getParentURI();
        }

        @Override
        public String getParentPath() {
            return instance.getParentPath();
        }

        @Override
        public Serializable getIdentifier() {
            return instance.getIdentifier();
        }

        @Override
        public String getType() {
            return instance.getType();
        }
    }

    @Test(expectedExceptions = {RemoteException.class})
    public void putAttributes_listTakesTwoAttributesWithSameName_exception() {
        AttributesSearchResult<ProfileAttribute> searchResult = new AttributesSearchResultImpl<ProfileAttribute>();
        searchResult.setList(Arrays.asList(attributeA, attributeB));
        searchResult.setTotalCount(2);

        AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder().build();

        when(profileAttributeService.getProfileAttributesForPrincipal(
                any(ExecutionContext.class), eq(recipient), eq(searchCriteria)))
                .thenReturn(searchResult);

        ClientAttribute attribute1 = new ClientAttribute(clientAttributeC);
        ClientAttribute attribute2 = new ClientAttribute(clientAttributeC);

        service.putAttributes(
                recipientIdentity,
                Arrays.asList(attribute1, attribute2),
                null,
                false);
    }

    private static ObjectPermission createObjectPermission(String URI, Object recipient, int mask) {
        ObjectPermission result = new ObjectPermissionImpl();
        result.setURI(URI);
        result.setPermissionRecipient(recipient);
        result.setPermissionMask(mask);
        return result;
    }

    /*
     * Return true if the list of attributes matches the arrays of names and values
     *
     * @param list is a list of ClientAttribute instances
     * @param keys is an array of attributes names. In the order corresponding to the "values" array
     * @param values is an array of attributes values. In the order corresponding to the "keys" array.
     *     Must contains null for an attribute if you do not want check the value of the attribute.
     * @return true if the list matches the arrays of names and values
     */
    private static boolean isListClientAttributesIdenticalInCompositionOfNamesValues(
            List<ClientAttribute> list, String[] names, String[] values) {

        if (list == null) {
            throw new IllegalArgumentException();
        }

        Map<String, String> map = prepareMap(names, values);

        if (list.size() != map.size()) {
            return false;
        }

        Set<String> keySet = map.keySet();

        for (ClientAttribute clientAttribute : list) {
            String name = clientAttribute.getName();
            if (!keySet.contains(name)) {
                return false;
            }

            String value = map.get(name);
            if (value != null && !value.equals(clientAttribute.getValue())) {
                return false;
            }
        }

        return true;
    }

    /*
     * Overloaded version.
     * see isListClientAttributesIdenticalInCompositionOfNamesValues(List, String[], String[])
     */
    private static boolean isListClientAttributesIdenticalInCompositionOfNamesValues(
            List<ClientAttribute> list, String name, String value) {
        return isListClientAttributesIdenticalInCompositionOfNamesValues(list, strings(name), strings(value));
    }

    /*
     * Return true if the list of attributes matches the arrays of names and values
     *
     * @param list is a list of ProfileAttribute instances
     * @param keys is an array of attributes names. In the order corresponding to the "values" array
     * @param values is an array of attributes values. In the order corresponding to the "keys" array.
     *     Must contains null for an attribute if you do not want check the value of the attribute.
     * @return true if the list matches the arrays of names and values
     */
    private static boolean isListAttributesIdenticalInCompositionOfNamesValues(
            List<ProfileAttribute> list, String[] names, String[] values) {

        if (list == null) {
            throw new IllegalArgumentException();
        }

        Map<String, String> map = prepareMap(names, values);

        if (list.size() != map.size()) {
            return false;
        }

        Set<String> keySet = map.keySet();

        for (ProfileAttribute attribute : list) {
            if (attribute instanceof ProfileAttributeImpl) {
                throw new IllegalArgumentException("ProfileAttributeImpl is not supported by this method, because it"
                        +" doesn't implement equals() and hashCode() methods!");
            }
            String name = attribute.getAttrName();
            if (!keySet.contains(name)) {
                return false;
            }

            String value = map.get(name);
            if (value != null && !value.equals(attribute.getAttrValue())) {
                return false;
            }
        }

        return true;
    }

    /*
     * Prepares a map based on the arrays of keys and values and returns it.
     */
    private static <T> Map<T, T> prepareMap(T[] keys, T[] values) {
        if (keys == null || values == null || keys.length != values.length) {
            throw new IllegalArgumentException();
        }

        Map<T, T> result = new HashMap<T, T>();

        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }

        return result;
    }

    private static String[] strings(String... args) {
        return args;
    }
}
