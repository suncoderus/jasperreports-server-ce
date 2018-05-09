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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/*
*  @author inesterenko
*/
public interface Task<T> {

    String INPROGRESS = "inprogress";
    String FINISHED = "finished";
    String FAILED = "failed";
    String PENDING = "pending";

    void start(ExecutorService executor);

    void stop();

    String getUniqueId();

    void setUniqueId(String uuid);

    State getState();

    T getResult() throws NotReadyResultException, NoResultException;

    String getOrganizationId();

    String getBrokenDependenciesStrategy();

    Map<String, Boolean> getParameters();

    void updateTask(List<String> parameters, String organizationId, String brokenDependenciesStrategy);

    /**
     * Returns task completion date
     *
     * @return task completion date
     */
    Date getTaskCompletionDate();
}
