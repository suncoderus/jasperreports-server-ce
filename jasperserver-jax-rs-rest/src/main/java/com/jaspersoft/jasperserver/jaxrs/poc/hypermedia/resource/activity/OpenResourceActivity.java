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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity;

import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.MediaTypes;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Permissions;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.Link;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.provider.RequestInfoProvider;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class OpenResourceActivity extends ReadResourceActivity {

    private List<String> canBeOpen = Arrays.asList(
            ResourceMediaType.FILE_CLIENT_TYPE,
            ResourceMediaType.DASHBOARD_CLIENT_TYPE,
            ResourceMediaType.DOMAIN_TOPIC_CLIENT_TYPE,
            ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE
    );


    @Resource
    private RequestInfoProvider requestInfoProvider;

    @Override
    public Relation getOwnRelation() {
        return Relation.open;
    }

    @Override
    public EmbeddedElement buildLink() {

        Link link = null;

        String resourceType = data.getResourceType();
        Integer permissionMask = data.getPermissionMask();

        Boolean isAllowed = (permissionMask & (Permissions.ADMINISTRATION.mask() | Permissions.READ.mask())) > 0;

        if (canBeOpen.contains(resourceType) && isAllowed){

            String resourceUri = data.getUri();

            String parentFolderUri = resourceUri.substring(0,resourceUri.lastIndexOf("/"));

            String url = MessageFormat.format("{0}flow.html?_flowId=", requestInfoProvider.getBaseUrl());

            if (ResourceMediaType.FILE_CLIENT_TYPE.equals(resourceType)){
                url = MessageFormat.format("{0}fileview/fileview{1}",
                        requestInfoProvider.getBaseUrl(), resourceUri);
            }else if (ResourceMediaType.DASHBOARD_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("dashboardDesignerFlow&ParentFolderUri={0}&resource={1}",
                        parentFolderUri, resourceUri);
            }else if (ResourceMediaType.DOMAIN_TOPIC_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("queryBuilderFlow&ParentFolderUri={0}&uri={1}",
                        parentFolderUri,resourceUri);
            }else if(ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("adhocFlow&resource={0}&ParentFolderUri={1}",
                        resourceUri, parentFolderUri);
            }

            link = new Link().setHref(url)
                    .setRelation(getOwnRelation())
                    .setType(MediaTypes.TEXT_HTML)
                    .setProfile("GET");

        }

        return link;

    }
}