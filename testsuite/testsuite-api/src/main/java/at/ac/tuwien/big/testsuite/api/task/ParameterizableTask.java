/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.big.testsuite.api.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author Christian
 */
public abstract class ParameterizableTask<T, I> extends HierarchicalTask<T, ParameterizableTask<T, I>> {

    private Future<I> parameter;
    private EventHandler<? super ParameterizableTask<T, I>> onScheduledEventHandler;

    public ParameterizableTask() {
        this(null);
    }

    public ParameterizableTask(HierarchicalTask<?, ?> parent) {
        super(parent);
        super.setOnScheduled(new EventHandler<ParameterizableTask<T, I>>() {
            @Override
            public void handle(ParameterizableTask<T, I> event) {
                if (parameter != null) {
                    Throwable exception = null;

                    do {
                        try {
                            exception = null;
                            parameter.get();
                        } catch (InterruptedException ex) {
                            if (getExecutorService().isShutdown()) {
                                break;
                            }

                            exception = ex;
                        } catch (ExecutionException ex) {
                            exception = ex.getCause();
                        }
                    } while (exception != null && exception instanceof InterruptedException);

                    if (exception != null) {
                        if (exception instanceof RuntimeException) {
                            throw (RuntimeException) exception;
                        } else {
                            throw new RuntimeException(exception);
                        }
                    }
                }

                if (onScheduledEventHandler != null) {
                    onScheduledEventHandler.handle(event);
                }
            }
        });
    }

    public ParameterizableTask<T, I> setParameter(Future<I> parameter) {
        this.parameter = parameter;
        return this;
    }

    public Future<I> getParameter() {
        return this.parameter;
    }

    @Override
    public EventHandler<? super ParameterizableTask<T, I>> getOnScheduled() {
        return onScheduledEventHandler;
    }

    @Override
    public ParameterizableTask<T, I> setOnScheduled(EventHandler<? super ParameterizableTask<T, I>> value) {
        this.onScheduledEventHandler = value;
        return this;
    }

    @Override
    protected T call() throws Exception {
        while (true) {
            try {
                return call(parameter.get());
            } catch (InterruptedException ex) {
            }
        }
    }

    protected abstract T call(I value) throws Exception;
}
