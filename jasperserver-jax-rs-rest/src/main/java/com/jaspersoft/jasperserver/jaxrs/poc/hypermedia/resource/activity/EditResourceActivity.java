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
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.provider.RequestInfoProvider;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.Link;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class EditResourceActivity extends ReadResourceActivity {

    private List<String> canBeEditTypes = Arrays.asList(
//          TODO: what about  REPORT_OPTIONS
            ResourceMediaType.REPORT_UNIT_CLIENT_TYPE,
            ResourceMediaType.OLAP_UNIT_CLIENT_TYPE,
            ResourceMediaType.DASHBOARD_CLIENT_TYPE,
            ResourceMediaType.DOMAIN_TOPIC_CLIENT_TYPE,
            ResourceMediaType.JDBC_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.JNDI_JDBC_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.CUSTOM_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.BEAN_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.VIRTUAL_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.AWS_DATA_SOURCE_CLIENT_TYPE,
            ResourceMediaType.QUERY_CLIENT_TYPE,
            ResourceMediaType.INPUT_CONTROL_CLIENT_TYPE,
            ResourceMediaType.LIST_OF_VALUES_CLIENT_TYPE,
            ResourceMediaType.DATA_TYPE_CLIENT_TYPE,
            ResourceMediaType.MONDRIAN_CONNECTION_CLIENT_TYPE,
            ResourceMediaType.SECURE_MONDRIAN_CONNECTION_CLIENT_TYPE,
            ResourceMediaType.XMLA_CONNECTION_CLIENT_TYPE,
            ResourceMediaType.MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE,
            ResourceMediaType.FILE_CLIENT_TYPE,
            ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE
    );

    @Resource
    private RequestInfoProvider requestInfoProvider;

    @Override
    public Relation getOwnRelation() {
        return Relation.edit;
    }

    @Override
    public EmbeddedElement buildLink() {

        Link link = null;

        String resourceType = data.getResourceType();
        Integer permissionMask = data.getPermissionMask();

        Boolean isAllowed = (permissionMask & (Permissions.ADMINISTRATION.mask() | Permissions.WRITE.mask())) > 0;

        if (canBeEditTypes.contains(resourceType) && isAllowed){

            String resourceUri = data.getUri();

            String parentFolderUri = resourceUri.substring(0,resourceUri.lastIndexOf("/"));

            String url = MessageFormat.format("{0}flow.html?_flowId=", requestInfoProvider.getBaseUrl());

            if (ResourceMediaType.REPORT_UNIT_CLIENT_TYPE.equals(resourceType)
                    || ResourceMediaType.DOMAIN_TOPIC_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("reportUnitFlow&ParentFolderUri={0}&selectedResource={1}",
                        parentFolderUri, resourceUri);
            }else if (ResourceMediaType.OLAP_UNIT_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("olapUnitFlow&isEdit=edit&ParentFolderUri={0}&resourceModel={1}",
                        parentFolderUri, resourceUri);
            }else if (ResourceMediaType.DASHBOARD_CLIENT_TYPE.equals(resourceType)){
                //TODO: check is it edit ?
                url = requestInfoProvider.getBaseUrl().concat("/dashboard/designer.html#").concat(resourceUri);
            }else if("reportOptions".equals(resourceType)){
                //TODO: how to integrate ?
                url += MessageFormat.format("reportOptionsEditFlow&reportOptionsURI={0}&ParentFolderUri={1}",
                        resourceUri, parentFolderUri);
            }else if(ResourceMediaType.DATASOURCE_TYPES.contains(resourceType)){
                url += MessageFormat.format("addDataSourceFlow&resource={0}&ParentFolderUri={1}",
                        resourceUri, parentFolderUri);
            }else if(ResourceMediaType.QUERY_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("queryFlow&currentQuery={0}&ParentFolderUri={1}&isEdit=true",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.INPUT_CONTROL_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("addInputControlFlow&resource={0}&ParentFolderUri={1}&isEdit=true",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.LIST_OF_VALUES_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("addListOfValuesFlow&resource={0}&ParentFolderUri={1}&isEdit=edit",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.DATA_TYPE_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("dataTypeFlow&resource={0}&ParentFolderUri={1}&isEdit=edit",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.MONDRIAN_CONNECTION_CLIENT_TYPE.equals(resourceType)
                        || ResourceMediaType.SECURE_MONDRIAN_CONNECTION_CLIENT_TYPE.equals(resourceType)
                        || ResourceMediaType.XMLA_CONNECTION_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("olapClientConnectionFlow&selectedResource={0}&ParentFolderUri={1}&isEdit=edit",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.MONDRIAN_XMLA_DEFINITION_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("mondrianXmlaSourceFlow&selectedResource={0}&ParentFolderUri={1}&isEdit=edit",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.FILE_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("addFileResourceFlow&selectedResource={0}&ParentFolderUri={1}",
                        resourceUri, parentFolderUri);
            }else if (ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE.equals(resourceType)){
                url += MessageFormat.format("createSLDatasourceFlow&uri={0}&ParentFolderUri={1}",
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