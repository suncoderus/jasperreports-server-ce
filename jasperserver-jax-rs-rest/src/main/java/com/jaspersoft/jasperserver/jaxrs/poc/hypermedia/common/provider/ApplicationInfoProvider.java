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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.provider;

import com.jaspersoft.jasperserver.api.engine.common.service.impl.NavigationActionModelSupport;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
@Component
public class ApplicationInfoProvider {

    @Resource(name="concreteNavigationActionModelSupport")
    private NavigationActionModelSupport navigationActionModelSupport;

    public boolean isProVersion() {
        return navigationActionModelSupport.isProVersion();
    }

    public boolean isCEVersion() {
        return navigationActionModelSupport.isCEVersion();
    }

    public boolean isAvailableProFeature(String id) {
        return navigationActionModelSupport.isAvailableProFeature(id);
    }

    public boolean isMainFeaturesDisabled() {
        return navigationActionModelSupport.isMainFeaturesDisabled();
    }

    public boolean banUserRole() {
        return navigationActionModelSupport.banUserRole();
    }
}
