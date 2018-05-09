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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.MediaTypes;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.Activity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.AbstractActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.Link;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.data.ContentReferenceStorage;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.dto.ContentReference;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.representation.ContentReferenceCollectionRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.provider.RequestInfoProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class ReadContentReferenceCollectionActivity extends AbstractActivity<ContentReferenceCollectionRepresentation, List<ContentReference>> {

    protected String relativeUriPattern = "rest_v2/hypermedia/contentReferences";

    @Resource
    private RequestInfoProvider requestInfoProvider;

    @Resource
    private ContentReferenceStorage contentStorage;

    @Override
    public ContentReferenceCollectionRepresentation buildRepresentation() {
        return new ContentReferenceCollectionRepresentation(data);
    }

    public ReadContentReferenceCollectionActivity() {
        super();
    }

    public ReadContentReferenceCollectionActivity(Map<Relation, Activity> relations, List<Relation> linkRelations) {
        super(relations, linkRelations);
    }

    @Override
    public Relation getOwnRelation() {
        return Relation.contentReferences;
    }

    @Override
    public List<ContentReference> findData(GenericRequest request){
        String group = (String) request.getParams().get("group");
        List<ContentReference> contents = new ArrayList<ContentReference>();
        if (group == null || group.equals("null")){
            contents.addAll(contentStorage.findAll());
        } else {
            contents = contentStorage.findAllByGroup(group);
        }
        return  contents;
    }

    @Override
    public EmbeddedElement buildLink() {

        String url = requestInfoProvider.getBaseUrl()+ relativeUriPattern;

        if (genericRequest != null && genericRequest.getParams().size() > 0){
            String group = (String)genericRequest.getParams().get("group");
            if (group != null){
                url += "?group=" + group;
            }
        }

        return new Link()
                .setHref(url)
                .setRelation(getOwnRelation())
                .setType(MediaTypes.APPLICATION_HAL_JSON)
                .setProfile("GET");
    }
}
