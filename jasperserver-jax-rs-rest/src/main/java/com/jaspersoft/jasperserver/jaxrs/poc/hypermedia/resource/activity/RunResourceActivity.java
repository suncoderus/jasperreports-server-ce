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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class RunResourceActivity extends ReadResourceActivity {

    private List<String> canBeRunTypes = Arrays.asList(
            ResourceMediaType.REPORT_UNIT_CLIENT_TYPE,
            ResourceMediaType.OLAP_UNIT_CLIENT_TYPE,
            ResourceMediaType.DASHBOARD_CLIENT_TYPE,
            ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE,
            ResourceMediaType.LEGACY_DASHBOARD_CLIENT_TYPE
    );

    @Resource
    private RequestInfoProvider requestInfoProvider;



    @Override
    public Relation getOwnRelation() {
        return Relation.run;
    }

    @Override
    public EmbeddedElement buildLink() {

        Link link = null;


        String resourceType = data.getResourceType();
        Integer permissionMask = data.getPermissionMask();

        Boolean isAllowed = (permissionMask & (Permissions.ADMINISTRATION.mask() | Permissions.READ.mask())) > 0;

        if (canBeRunTypes.contains(resourceType) && isAllowed){

            String resourceUri = data.getUri();

            String parentFolderUri = resourceUri.substring(0,resourceUri.lastIndexOf("/"));

            try {
                resourceUri = URLEncoder.encode(resourceUri, "UTF-8");
                parentFolderUri = URLEncoder.encode(parentFolderUri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println(e);
            }

            String url = MessageFormat.format("{0}flow.html?_flowId=", requestInfoProvider.getBaseUrl());

            if (ResourceMediaType.REPORT_UNIT_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("viewReportFlow&standAlone=true&ParentFolderUri={0}&reportUnit={1}",
                        parentFolderUri, resourceUri);
            }else if (ResourceMediaType.OLAP_UNIT_CLIENT_TYPE.equals(resourceType)){
                url = MessageFormat.format("{0}olap/viewOlap.html?", requestInfoProvider.getBaseUrl());
                url += MessageFormat.format("new=true&parentFlow=searchFlow&ParentFolderUri={0}&name={1}",
                        parentFolderUri, resourceUri);
            }else if (ResourceMediaType.DASHBOARD_CLIENT_TYPE.equals(resourceType)){
                url = requestInfoProvider.getBaseUrl().concat("dashboard/viewer.html#").concat(resourceUri);
            }else if (ResourceMediaType.LEGACY_DASHBOARD_CLIENT_TYPE.equals(resourceType)) {
                url += MessageFormat.format("dashboardRuntimeFlow&dashboardResource={0}", resourceUri);
            }else if (ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("adhocFlow&resource={0}&ParentFolderUri={1}", resourceUri, parentFolderUri);
            }

            link = new Link().setHref(url)
                .setRelation(getOwnRelation())
                .setType(MediaTypes.TEXT_HTML)
                .setProfile("GET");

        }

        return link;

    }
}