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
package com.jaspersoft.jasperserver.dto.resources;


import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class FilesMapXmlAdapterTest {
    private  FilesMapXmlAdapter adapter = new FilesMapXmlAdapter();

    @Test
    public void marshal_nullProducesNull() throws Exception {
        assertNull(adapter.marshal(null));
    }

    @Test
    public void marshal_emtpyProducesEmpty() throws Exception {
        final ClientReportUnitResourceListWrapper wrapper = adapter.marshal(new HashMap<String, ClientReferenceableFile>());
        assertNotNull(wrapper);
        assertNotNull(wrapper.getFiles());
        assertTrue(wrapper.getFiles().isEmpty());
    }

    @Test
    public void marshal() throws Exception{
        final HashMap<String, ClientReferenceableFile> files = new HashMap<String, ClientReferenceableFile>();
        files.put("file1", new ClientReference("/some/uri1"));
        files.put("file2", new ClientFile());
        files.put("file3", new ClientReference("/some/uri2"));
        files.put("file4", new ClientFile());
        final ClientReportUnitResourceListWrapper wrapper = adapter.marshal(files);
        assertNotNull(wrapper);
        final List<ClientReportUnitResource> filesList = wrapper.getFiles();
        assertNotNull(filesList);
        assertEquals(filesList.size(), files.size());
        for(ClientReportUnitResource currentResource : filesList){
            assertSame(files.get(currentResource.getName()), currentResource.getFile());
        }
    }

    @Test
    public void unmarshal_nullProducesNull() throws Exception {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    public void unmarshal_emtpyProducesEmpty() throws Exception {
        final Map<String, ClientReferenceableFile> files =
                adapter.unmarshal(new ClientReportUnitResourceListWrapper(new ArrayList<ClientReportUnitResource>()));
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    public void unmarshal() throws Exception{
        final ArrayList<ClientReportUnitResource> filesList = new ArrayList<ClientReportUnitResource>();
        filesList.add(new ClientReportUnitResource("file1", new ClientFile()));
        filesList.add(new ClientReportUnitResource("file2", new ClientReference()));
        filesList.add(new ClientReportUnitResource("file3", new ClientFile()));
        filesList.add(new ClientReportUnitResource("file4", new ClientReference()));
        final Map<String, ClientReferenceableFile> filesMap = adapter.unmarshal(new ClientReportUnitResourceListWrapper(filesList));
        assertNotNull(filesMap);
        assertEquals(filesMap.size(), filesList.size());
        for(ClientReportUnitResource currentResource : filesList){
            assertSame(filesMap.get(currentResource.getName()), currentResource.getFile());
        }
    }

}
