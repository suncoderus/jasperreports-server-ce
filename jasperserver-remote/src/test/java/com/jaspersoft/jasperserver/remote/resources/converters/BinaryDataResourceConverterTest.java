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

package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class BinaryDataResourceConverterTest {
    @InjectMocks
    private BinaryDataResourceConverter converter = new BinaryDataResourceConverter();

    @Mock private ContentResourceConverter contentResourceConverter;
    @Mock private FileResourceConverter fileResourceConverter;
    @Mock private Set<String> fileResourceTypes;

    private final ClientFile file = new ClientFile();
    private final ObjectPermission repositoryPermission = new ObjectPermissionImpl();

    @BeforeClass
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void fillSampleObjects() throws Exception{
        file.setLabel("test");
        reset(contentResourceConverter, fileResourceConverter, fileResourceTypes);
        when(fileResourceConverter.toServer(any(ClientFile.class), any(FileResource.class), any(ToServerConversionOptions.class))).thenReturn(new FileResourceImpl());
        when(contentResourceConverter.toServer(any(ClientFile.class), any(ContentResource.class), any(ToServerConversionOptions.class))).thenReturn(new ContentResourceImpl());
    }

    @Test
    public void testToServer_file() throws Exception {
        file.setType(ClientFile.FileType.prop);

        converter.toServer(file, new FileResourceImpl(), null);

        verify(fileResourceConverter).toServer(eq(file), any(FileResource.class), any(ToServerConversionOptions.class));
    }

    @Test
    public void testToServer_file_no_update() throws Exception {
        file.setType(ClientFile.FileType.prop);
        when(fileResourceTypes.contains(any())).thenReturn(true);

        converter.toServer(file,null);

        verify(fileResourceConverter).toServer(eq(file), any(FileResource.class), any(ToServerConversionOptions.class));
    }

    @Test
    public void testToServer_content() throws Exception {
        file.setType(ClientFile.FileType.css);

        converter.toServer(file, new ContentResourceImpl(), null);

        verify(contentResourceConverter).toServer(eq(file), any(ContentResource.class), any(ToServerConversionOptions.class));
    }

    @Test
    public void testToServer_content_no_update() throws Exception {
        file.setType(ClientFile.FileType.css);
        when(fileResourceTypes.contains(any())).thenReturn(false);

        converter.toServer(file, null);

        verify(contentResourceConverter).toServer(eq(file), any(ContentResource.class), any(ToServerConversionOptions.class));
    }
}
