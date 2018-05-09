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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ExecutorServiceWrapper;
import org.apache.log4j.MDC;

import java.util.Hashtable;
import java.util.concurrent.*;

/**
 * This class propagates log4j's logging context (MDC) from the invoker thread into thread-pool-thread.
 * Log4j's MDC is a thread-local map which keeps info about UserId or ResourceUri for using in log messages.
 *
 * @author Yakiv Tymoshenko
 * @version $Id$
 * @since 13.11.14
 */
class DiagnosticLoggingContextCompatibleExecutorService extends ExecutorServiceWrapper {

    public DiagnosticLoggingContextCompatibleExecutorService(ExecutorService wrappedExecutorService) {
        super(wrappedExecutorService);
    }

    @Override
    // Suppressing type cast warning because log4j MDC.getContext() returns legacy ungenerified  Hashtable.
    public void execute(final Runnable command) {
        final Hashtable<String, Object> mdcContextCopy = getMdcContextCopy();
        if (mdcContextCopy != null) {
            getWrappedExecutorService().execute(wrapRunnable(command, mdcContextCopy));
        } else {
            getWrappedExecutorService().execute(command);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        final Hashtable<String, Object> mdcContextCopy = getMdcContextCopy();
        if (mdcContextCopy != null) {
            return getWrappedExecutorService().submit(wrapCallable(task, mdcContextCopy));
        } else {
            return getWrappedExecutorService().submit(task);
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        final Hashtable<String, Object> mdcContextCopy = getMdcContextCopy();
        if (mdcContextCopy != null) {
            return getWrappedExecutorService().submit(wrapRunnable(task, mdcContextCopy), result);
        } else {
            return getWrappedExecutorService().submit(task, result);
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        final Hashtable<String, Object> mdcContextCopy = getMdcContextCopy();
        if (mdcContextCopy != null) {
            return getWrappedExecutorService().submit(wrapRunnable(task, mdcContextCopy));
        } else {
            return getWrappedExecutorService().submit(task);
        }
    }

    @SuppressWarnings("unchecked")
    private Runnable wrapRunnable(final Runnable command, final Hashtable<String, Object> mdcContextCopy) {
        return new Runnable() {
            @Override
            public void run() {
                /*
                    MDC.getContext() may be null if MDC.put(String, String) was never called.
                    See bug http://bugzilla.jaspersoft.com/show_bug.cgi?id=42523
                */
                (MDC.getContext()).putAll(mdcContextCopy);

                command.run();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> Callable<T> wrapCallable(final Callable<T> task, final Hashtable<String, Object> mdcContextCopy) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                /*
                    MDC.getContext() may be null if MDC.put(String, String) was never called.
                    See bug http://bugzilla.jaspersoft.com/show_bug.cgi?id=42523
                */
                (MDC.getContext()).putAll(mdcContextCopy);

                return task.call();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Hashtable<String, Object> getMdcContextCopy() {
        if (MDC.getContext() != null) {
            Hashtable<String, Object> mdcContextCopy = new Hashtable<String, Object>();
            mdcContextCopy.putAll(MDC.getContext());
            return mdcContextCopy;
        }

        return null;
    }
}
