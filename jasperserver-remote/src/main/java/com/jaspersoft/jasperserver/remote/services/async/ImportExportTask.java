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

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.remote.exception.NoResultException;
import com.jaspersoft.jasperserver.remote.exception.NotReadyResultException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/*
*  @author inesterenko
*/
public class ImportExportTask<T> implements Task {

    protected final static Log log = LogFactory.getLog(Task.class);

    protected String uuid;
    protected final BaseImportExportTaskRunnable<T> taskRunner;
    //protected Thread thread;
    private Future<?> future;

    public ImportExportTask(BaseImportExportTaskRunnable<T> taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public String getUniqueId() {
        return uuid;
    }

    @Override
    public void setUniqueId(String uuid) {
       this.uuid = uuid;
    }

    @Override
    public State getState() {
        if (future != null && !isAlive()) {
            try {
                future.get(); // catching uncaught errors
            } catch (InterruptedException e) {
                State state = taskRunner.getState();
                state.setMessage(e.getMessage());
                state.setError(new ErrorDescriptor().setErrorCode("import.failed").addParameters(e.getMessage()));
                state.setPhase(FAILED);
                log.error("Import failed: ", e);
                return state;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                State state = taskRunner.getState();
                state.setMessage(cause.getMessage());
                state.setError(new ErrorDescriptor().setErrorCode("import.failed").addParameters(cause.getMessage()));
                state.setPhase(FAILED);
                log.error("Import failed: ", cause);
                return state;
            }
        }

        return taskRunner.getState();
    }

    @Override
    public T getResult() throws NotReadyResultException, NoResultException {
        return (T)taskRunner.getResult();
    }

    @Override
    public synchronized void start(ExecutorService executor) {
        taskRunner.prepare();
        if (isAlive()) {
            ErrorDescriptor errorDescriptor = new ErrorDescriptor();
            errorDescriptor.setMessage("Attempt to restart alive task");
            errorDescriptor.setErrorCode(ImportExportService.ERROR_CODE_RESTART_ALIVE_TASK);
            errorDescriptor.setParameters(getState().getId());

            throw new RemoteException(errorDescriptor);
        } else {
            final Locale locale = LocaleContextHolder.getLocale();
            final TimeZone timeZone = TimeZoneContextHolder.getTimeZone();
            final SecurityContext context = SecurityContextHolder.getContext();

            future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    LocaleContextHolder.setLocale(locale);
                    TimeZoneContextHolder.setTimeZone(timeZone);
                    SecurityContextHolder.setContext(context);
                    taskRunner.run();
                }
            });
        }
    }

    @Override
    public synchronized void stop() {
        if(isAlive()){
            future.cancel(true);
        }
        if (!taskRunner.getState().getPhase().equals(INPROGRESS)) {
            new Remover(taskRunner.getFile()).start();
        }
    }

    @Override
    public String getOrganizationId() {
        return taskRunner.getOrganizationId();
    }

    @Override
    public String getBrokenDependenciesStrategy() {
        return taskRunner.getBrokenDependenciesStrategy();
    }

    @Override
    public Map<String, Boolean> getParameters() {
        return taskRunner.getParameters();
    }

    @Override
    public Date getTaskCompletionDate() {
        return taskRunner.getTaskCompletionDate();
    }

    @Override
    public void updateTask(List parameters, String organizationId, String brokenDependenciesStrategy) {
        State state = getState();
        if (Task.PENDING.equals(state.getPhase())) {

            Map<String, Boolean> parametersMap = new HashMap<String, Boolean>();
            if (parameters != null) {
                for (Object obj : parameters) {
                    if (obj instanceof String) {
                        String name = (String) obj;
                        if (StringUtils.isNotBlank(name)) {
                            parametersMap.put(name, true);
                        }
                    }
                }
            }

            taskRunner.setParameters(parametersMap.isEmpty() ? null : parametersMap);
            taskRunner.setOrganizationId(organizationId);
            taskRunner.setBrokenDependenciesStrategy(brokenDependenciesStrategy);
        } else {
            ErrorDescriptor errorDescriptor = new ErrorDescriptor();
            errorDescriptor.setMessage("Attempt to update task in non-pending phase");
            errorDescriptor.setErrorCode(ImportExportService.ERROR_CODE_UPDATE_NOT_PENDING_PHASE);
            errorDescriptor.setParameters(getState().getId());

            throw new RemoteException(errorDescriptor);
        }
    }

    private boolean isAlive() {
        return future != null && !future.isCancelled() && !future.isDone();
    }

    protected class Remover extends Thread {
        private final File toRemove;
        private int counter = 0;

        public Remover(File toRemove) {
            this.toRemove = toRemove;
        }

        @Override
        public void run() {
            try {
                while (!toRemove.delete()) {
                    Thread.sleep(10000);
                    if (counter++ > 100) {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                // let's log exception if only toRemove file exist.
                if(toRemove.exists()) {
                    log.error("File " + toRemove.getAbsolutePath() + " was not deleted!");
                }
                // If toRemove doesn't exist, then OK, nothing to delete. Keep silent in this case
            }
        }
    }
}

