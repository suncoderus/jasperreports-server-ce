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

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ServerSettingsInitialization implements ApplicationListener<ContextRefreshedEvent> {
    private ProfileAttributeService profileAttributeService;
    private boolean initialized = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (!initialized) {
            profileAttributeService.applyProfileAttributes();
            initialized = true;
        }
    }

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }
}
