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

package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.service.UpdatableXMLAContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author vsabadosh
 */
public class MondrianXMLADefinitionListener extends RepositoryEventListenerSupport implements RepositoryListener  {
    @Autowired(required = false)
    @Qualifier("concreteXmlaRepository")
    private UpdatableXMLAContainer updatableXMLAContainer;

    @Override
    public void folderMoved(FolderMoveEvent folderMove) {
        //Empty body
    }

    @Override
    public void resourceMoved(ResourceMoveEvent resourceMove) {
        if (MondrianXMLADefinition.class.isAssignableFrom(resourceMove.getResourceType()) && updatableXMLAContainer != null) {
            updatableXMLAContainer.clearCache();
        }
    }

    @Override
    public void resourceCopied(ResourceCopiedEvent event) {
        if (event.getResource() instanceof MondrianXMLADefinition && updatableXMLAContainer != null) {
            updatableXMLAContainer.clearCache();
        }
    }

    @Override
    public void onResourceDelete(Class resourceItf, String resourceURI) {
        if (MondrianXMLADefinition.class.isAssignableFrom(resourceItf) && updatableXMLAContainer != null) {
            updatableXMLAContainer.clearCache();
        }
    }

}
