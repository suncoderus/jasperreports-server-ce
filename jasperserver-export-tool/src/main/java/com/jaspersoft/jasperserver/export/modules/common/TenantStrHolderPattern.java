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

package com.jaspersoft.jasperserver.export.modules.common;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.metadata.common.domain.Folder.SEPARATOR;
import static com.jaspersoft.jasperserver.api.metadata.user.service.TenantService.ORGANIZATIONS;

/**
 * @author Vlad Zavadskii
 * @author askorodumov
 * @version $Id$
 */
public enum TenantStrHolderPattern {

    // ([^\/]+)
    TENANT_URI("([^\\" + SEPARATOR + "]+)", 1),

    // \/organizations\/([^\/]+)
    TENANT_FOLDER_URI_ELEMENT("\\" + SEPARATOR + ORGANIZATIONS + "\\" + SEPARATOR + "([^\\" + SEPARATOR + "]+)", 1),

    // ^(\/organizations\/([^\/]+))+
    TENANT_FOLDER_URI("^(" + TENANT_FOLDER_URI_ELEMENT.pattern.pattern() + ")+", 0) {
        @Override
        public String replaceWithNewTenantIds(Map<String, String> oldToNewTenantIds, String strToReplace) {
            if (strToReplace == null) {
                return null;
            }

            if (oldToNewTenantIds.isEmpty()) return strToReplace;

            Matcher matcher = pattern.matcher(strToReplace);
            if (matcher.find()) {
                String tenantPart = matcher.group(group);
                String resourcePart = strToReplace.substring(matcher.end(group), strToReplace.length());

                tenantPart = TENANT_FOLDER_URI_ELEMENT.replaceWithNewTenantIds(oldToNewTenantIds, tenantPart);
                return tenantPart.concat(resourcePart);
            }

            return strToReplace;
        }
    },

    TENANT_ID("^(.+)$", 1),

    TENANT_QUALIFIED_NAME("^[^|]+|([^|]+)$", 1);

    TenantStrHolderPattern(String pattern, int group) {
        this.pattern = Pattern.compile(pattern);
        this.group = group;
    }

    public static String replaceWithNewTenantIds(
            Map<String, String> oldToNewTenantIds, String strToReplace, TenantStrHolderPattern pattern) {
        return pattern.replaceWithNewTenantIds(oldToNewTenantIds, strToReplace);
    }

    public String replaceWithNewTenantIds(Map<String, String> oldToNewTenantIds, String strToReplace) {
        if (strToReplace == null) {
            return null;
        }

        if (oldToNewTenantIds.isEmpty()) return strToReplace;
        Matcher matcher = pattern.matcher(strToReplace);
        StringBuilder sb = new StringBuilder(strToReplace);

        // Used to compensate length difference between old and new tenant ids when doing replacement part
        int groupOffset = 0;

        while (matcher.find()) {
            String oldTenantId = matcher.group(group);

            if (oldToNewTenantIds.containsKey(oldTenantId)) {
                String newTenantId = oldToNewTenantIds.get(oldTenantId);

                // Calculate "start" and "end" indexes where to do replacement.
                // Append offset to them affected by previous replacements
                int start = matcher.start(group) + groupOffset;
                int end = matcher.end(group) + groupOffset;

                // Calculate new offset
                groupOffset += newTenantId.length() - (end - start);

                sb.replace(start, end, newTenantId);
            }
        }

        return sb.toString();
    }

    public Pattern getPattern() {
        return pattern;
    }

    protected Pattern pattern;
    protected int group;
}
