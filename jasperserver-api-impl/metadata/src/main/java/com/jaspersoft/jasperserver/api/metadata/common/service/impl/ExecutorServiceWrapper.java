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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Yakiv Tymoshenko
 * @version $Id$
 * @since 25.06.2015
 */
public abstract class ExecutorServiceWrapper implements ExecutorService {

    private ExecutorService wrappedExecutorService;

    public ExecutorServiceWrapper(ExecutorService wrappedExecutorService) {
        this.wrappedExecutorService = wrappedExecutorService;
    }

    /**
    Must be overridden to pass a thread-local context from the main thread into a thread-pool-thread.
     */
    @Override
    public abstract void execute(Runnable command);

    /**
     Must be overridden to pass a thread-local context from the main thread into a thread-pool-thread.
     */
    @Override
    public abstract <T> Future<T> submit(Callable<T> task);

    /**
     Must be overridden to pass a thread-local context from the main thread into a thread-pool-thread.
     */
    @Override
    public abstract <T> Future<T> submit(Runnable task, T result);

    /**
     Must be overridden to pass a thread-local context from the main thread into a thread-pool-thread.
     */
    @Override
    public abstract Future<?> submit(Runnable task);

    public ExecutorService getWrappedExecutorService() {
        return wrappedExecutorService;
    }

    @Override
    public void shutdown() {
        wrappedExecutorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return wrappedExecutorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return wrappedExecutorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return wrappedExecutorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return wrappedExecutorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }
}
