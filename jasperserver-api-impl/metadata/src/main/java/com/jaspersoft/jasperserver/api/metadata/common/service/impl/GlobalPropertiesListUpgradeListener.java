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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Transfer all properties from GlobalPropertiesList resource to JIProfileAttribute table.
 * After GlobalPropertiesList resource will be deleted
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
public class GlobalPropertiesListUpgradeListener implements ApplicationListener<ContextRefreshedEvent> {
    private GlobalPropertiesListUpgradeExecutor globalPropertiesListUpgradeExecutor;

    private boolean upgraded = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!upgraded) {
            globalPropertiesListUpgradeExecutor.upgrade();
            upgraded = true;
        }
    }

    public void setGlobalPropertiesListUpgradeExecutor(GlobalPropertiesListUpgradeExecutor
                                                               globalPropertiesListUpgradeExecutor) {
        this.globalPropertiesListUpgradeExecutor = globalPropertiesListUpgradeExecutor;
    }
}
