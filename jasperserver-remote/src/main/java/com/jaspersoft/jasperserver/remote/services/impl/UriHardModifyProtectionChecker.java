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
package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>Use this checker to check if some URI is hard modify protected</p>
 * Such hard protection is used to protect technical folders, such as foot folder, /temp and others.
 * Inject set of regular expressions to specify protected resources.
 * Currently this class is called from remote module, but this check should be moved to core.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class UriHardModifyProtectionChecker {
    @javax.annotation.Resource
    protected Set<String> modifyProtected;

    @javax.annotation.Resource
    protected Set<String> foldersOnly;

    @javax.annotation.Resource
    ConfigurationBean configurationBean;

    public boolean isHardModifyProtected(String uri){
        return isUriInSet(uri, modifyProtected);
    }

    private boolean isUriInSet(String uri, Set<String> uris){
        boolean result = false;
        for(String currentPattern : uris){
            if(Pattern.matches(currentPattern, uri)){
                result = true;
                break;
            }
        }
        return result;
    }
}
