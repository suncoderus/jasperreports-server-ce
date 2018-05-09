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
package com.jaspersoft.jasperserver.api.logging.util;

import com.jaspersoft.jasperserver.api.logging.audit.context.RequestType;
import com.jaspersoft.jasperserver.api.logging.audit.context.RequestTypeListener;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ExecutorServiceWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A special {@link Executor} service that asynchronously execute the tasks in which logging events are setup.
 *
 * @author vsabadosh
 * @version $Id$
 */
public class LoggableExecutorService extends ExecutorServiceWrapper {
    private LoggingContextProvider loggingContextProvider;
    private RequestTypeListener requestTypeListener;

    public LoggableExecutorService() {
        super(Executors.newCachedThreadPool());
    }

    @Override
    public void execute(final Runnable command) {
        //As requestType is thread local then we have to move it from main thread to the new thread.
        final RequestType requestType = requestTypeListener.getRequestType();

        getWrappedExecutorService().execute(wrapRunnable(command, requestType));
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        //As requestType is thread local then we have to move it from main thread to the new thread.
        final RequestType requestType = requestTypeListener.getRequestType();

        return getWrappedExecutorService().submit(wrapCallable(task, requestType));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        //As requestType is thread local then we have to move it from main thread to the new thread.
        final RequestType requestType = requestTypeListener.getRequestType();

        return getWrappedExecutorService().submit(wrapRunnable(task, requestType), result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        //As requestType is thread local then we have to move it from main thread to the new thread.
        final RequestType requestType = requestTypeListener.getRequestType();

        return getWrappedExecutorService().submit(wrapRunnable(task, requestType));
    }

    private Runnable wrapRunnable(final Runnable command, final RequestType requestType) {
        return new Runnable() {
            @Override
            public void run() {
                requestTypeListener.setRequestType(requestType);
                command.run();
                //flushing logging context after task is executed.
                loggingContextProvider.flushContext();
            }
        };
    }

    private <T> Callable<T> wrapCallable(final Callable<T> task, final RequestType requestType) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                requestTypeListener.setRequestType(requestType);

                T result = task.call();

                //flushing logging context after task is executed.
                loggingContextProvider.flushContext();

                return result;
            }
        };
    }

    public void setLoggingContextProvider(LoggingContextProvider loggingContextProvider) {
        this.loggingContextProvider = loggingContextProvider;
    }

    public void setRequestTypeListener(RequestTypeListener requestTypeListener) {
        this.requestTypeListener = requestTypeListener;
    }
}
