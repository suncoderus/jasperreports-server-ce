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

import com.jaspersoft.jasperserver.api.common.util.UpgradeRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceCopiedEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ObjectPermissionsRepositoryListener extends RepositoryEventListenerSupport implements RepositoryListener {

	private ObjectPermissionServiceInternal permissionsService;
	private List<Pattern> patternsToSkip;

	public void onResourceDelete(Class resourceItf, String resourceURI) {
        // We do not remove permissions when upgrading because the new resources will be created with the same URI and
        // they will have the same permissions.
        if (!UpgradeRunMonitor.isUpgradeRun() && !skipPermissionProcessing(resourceURI)) {
            getPermissionsService().deleteObjectPermissionForRepositoryPath(null, resourceURI);
        }
	}

	public void onFolderDelete(String folderURI) {
        if (!skipPermissionProcessing(folderURI)) {
            getPermissionsService().deleteObjectPermissionForRepositoryPath(null, folderURI);
        }
	}

	public ObjectPermissionServiceInternal getPermissionsService() {
		return permissionsService;
	}

	public void setPermissionsService(ObjectPermissionServiceInternal permissionsService) {
		this.permissionsService = permissionsService;
	}

	public void folderMoved(FolderMoveEvent folderMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				folderMove.getOldFolderURI(), folderMove.getNewFolderURI());
	}

	public void resourceMoved(ResourceMoveEvent resourceMove) {
		getPermissionsService().updateObjectPermissionRepositoryPath(
				resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
	}

	public void resourceCopied(ResourceCopiedEvent event) {
		// NOP
	}

    public void setPatternsToSkip(List<String> patternsToSkip) {
        if (patternsToSkip != null) {
            List<Pattern> patterns = new ArrayList<Pattern>();
            for (String pattern : patternsToSkip) {
                patterns.add(Pattern.compile(pattern));
            }
            this.patternsToSkip = Collections.unmodifiableList(patterns);
        }
    }

    private boolean skipPermissionProcessing(String uri) {
        if (uri == null || patternsToSkip == null || patternsToSkip.isEmpty()) {
            return false;
        }
        for (Pattern pattern : patternsToSkip) {
            if (pattern.matcher(uri).matches()) {
                return true;
            }
        }
        return false;
    }
}
