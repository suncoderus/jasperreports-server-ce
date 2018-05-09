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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link com.jaspersoft.jasperserver.api.engine.jasperreports.util.InputControlLabelResolver}
 *
 * @author Sergey Prilukin
 * @version $Id: MessageSourceLoaderTest.java 23845 2012-05-22 09:13:54Z afomin $
 */
public class MessageSourceLoaderTest extends UnitilsJUnit4 {

    @Before
    public void before() throws Exception {
        RepositoryUtil.clearThreadRepositoryContext();
    }

    @Test
    public void ensureBundleUseLocalizedMessageIfPresent() throws Exception {

        final FileResource resource1 = createResource(FileResource.class.getName(), "bundle.properties", "prop", "prop1=prop1.base");
        final FileResource resource2 = createResource(FileResource.class.getName(), "bundle_yy.properties", "prop", "prop1=prop1.yy\r\nprop2=prop2.yy");
        final FileResource resource3 = createResource(FileResource.class.getName(), "bundle_xx.properties", "prop", "prop1=prop1.xx\r\nprop3=prop3.xx");
        final ResourceContainer resourceContainer = createResourceContainer(resource1, resource2, resource3);
        final RepositoryService repositoryService = createRepositoryService(resource1, resource2, resource3);

        MessageSource messageSource = MessageSourceLoader.loadMessageSource(null, resourceContainer, repositoryService);
        assertEquals("prop1.base", messageSource.getMessage("prop1", null, new Locale("zz")));
        assertEquals("prop1.yy", messageSource.getMessage("prop1", null, new Locale("yy")));
        assertEquals("prop2.yy", messageSource.getMessage("prop2", null, new Locale("yy")));
        assertEquals("prop1.xx", messageSource.getMessage("prop1", null, new Locale("xx")));
        assertEquals("prop3.xx", messageSource.getMessage("prop3", null, new Locale("xx")));
    }

    @Test
    public void ensureExceptionIsThrownIfNoBundles() throws Exception {

        final ResourceContainer resourceContainer = createResourceContainer();
        final RepositoryService repositoryService = createRepositoryService();

        MessageSource messageSource = MessageSourceLoader.loadMessageSource(null, resourceContainer, repositoryService);
        assertNull(messageSource);
    }

    private ResourceContainer createResourceContainer(FileResource... references) {
        Mock<ResourceContainer> resourceContainerMock = MockUnitils.createMock(ResourceContainer.class);
        resourceContainerMock.returns(createRefs(references)).getResources();
        return resourceContainerMock.getMock();
    }

    private FileResource createResource(String resourceType, String name, String fileType, String propertyStrings) {
        Mock<FileResource> fileResourceMock = MockUnitils.createMock(FileResource.class);
        fileResourceMock.returns(resourceType).getResourceType();
        fileResourceMock.returns(name).getName();
        fileResourceMock.returns(fileType).getFileType();
        fileResourceMock.returns("/" + name).getURIString();
        fileResourceMock.returns(new ByteArrayInputStream(propertyStrings.getBytes())).getDataStream();

        return fileResourceMock.getMock();
    }

    private List<ResourceReference> createRefs(FileResource... fileResources) {
        List<ResourceReference> list = new ArrayList<ResourceReference>(fileResources.length);
        for (FileResource res: fileResources) {
            list.add(new ResourceReference(res));
        }
        return list;
    }

    private RepositoryService createRepositoryService(final FileResource... resources) {
        final Map<String, FileResource> resourcesMap = new HashMap<String, FileResource>();
        for (FileResource resource: resources) {
            resourcesMap.put(resource.getURIString(), resource);
        }

        Mock<RepositoryService> repositoryServiceMock = MockUnitils.createMock(RepositoryService.class);
        repositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return resourcesMap.get(proxyInvocation.getArguments().get(1));
            }
        }).getResource(null, null, FileResource.class);

        repositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return resourcesMap.get(proxyInvocation.getArguments().get(1));
            }
        }).getResource(null, null);

        repositoryServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                Mock<FileResourceData> fileResourceDataMock = MockUnitils.createMock(FileResourceData.class);
                FileResource fileResource = resourcesMap.get(proxyInvocation.getArguments().get(1));

                final InputStream dataStream = fileResource.getDataStream();
                fileResourceDataMock.returns(dataStream).getDataStream();

                return fileResourceDataMock.getMock();
            }
        }).getResourceData(null, null);

        return repositoryServiceMock.getMock();
    }
}
