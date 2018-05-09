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

package com.jaspersoft.jasperserver.remote.services.async;

import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.remote.exception.NoResultException;
import com.jaspersoft.jasperserver.remote.exception.NotReadyResultException;

import java.util.Map;

/**
 *
 * @author  inesterenko
 */

public interface TaskRunnable<T> extends Runnable{

    State getState();

    T getResult() throws NotReadyResultException, NoResultException;

    String getOrganizationId();

    void setOrganizationId(String organizationId);

    String getBrokenDependenciesStrategy();

    void setBrokenDependenciesStrategy(String value);

    Map<String, Boolean> getParameters();

    void setParameters(Map<String, Boolean> parameters);
}
