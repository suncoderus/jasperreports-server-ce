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

package com.jaspersoft.jasperserver.war.model.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 */
public class TestTreeDataProviderImpl extends RepositoryExplorerTreeDataProviderImpl {

    private boolean isInvalid = true;

    @Override
    protected TreeNode createNode(Resource resource, boolean isFolder) {
        Calendar cal = new GregorianCalendar(LocaleContextHolder.getLocale());
        cal.setTime(new Date());

        int seconds = cal.get(Calendar.SECOND);

        TreeNode node = super.createNode(resource, isFolder);

        isInvalid = !isInvalid;
        if (isInvalid) {
//            node.setCssClass("invalid");
        }

        return node;
    }
}
