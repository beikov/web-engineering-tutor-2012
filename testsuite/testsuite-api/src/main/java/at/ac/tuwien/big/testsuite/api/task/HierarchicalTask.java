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

import at.ac.tuwien.big.testsuite.api.model.ProgressStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Christian
 */
public abstract class HierarchicalTask<T, X extends HierarchicalTask<T, X>> extends FutureTask<T> {

    private final ConcurrentMap<HierarchicalTask<?, ?>, Throwable> childExceptions = new ConcurrentHashMap<>();
    private final Collection<HierarchicalTask<?, ?>> tasks = new CopyOnWriteArrayList<>();
    private final AtomicInteger taskCount = new AtomicInteger(0);
    private final AtomicInteger tasksDone = new AtomicInteger(0);
    private final HierarchicalTask<?, ?> parent;
    private final HierarchicalTask<?, ?> root;
    private final Phaser phaser;

    public HierarchicalTask() {
        this(null);
    }

    public HierarchicalTask(HierarchicalTask<?, ?> parent) {
        this(new HierarchicalTaskCallable<T, X>(), parent);
    }

    private HierarchicalTask(HierarchicalTaskCallable<T, X> callable, HierarchicalTask<?, ?> parent) {
        super(callable);
        callable.task = this;
        this.parent = parent;

        if (parent == null) {
            this.root = null;
            this.phaser = new Phaser(1);
        } else {
            this.root = parent.getRoot();
            this.phaser = new Phaser(1);
        }
    }

    protected abstract T call() throws Exception;
    private final ReentrantReadWriteLock startLock = new ReentrantReadWriteLock();
    private boolean started = false;

    public X start(ExecutorService executorService) {
        try {
            startLock.writeLock().lock();

            if (!started) {
                this.started = true;
                setExecutorService(executorService);
                executorService.submit(this);
                startTasks();
            } else {
                throw new IllegalStateException("Tasks can only be started once");
            }
        } finally {
            startLock.writeLock().unlock();
        }

        return (X) this;
    }

    protected void startTasks() {
        for (HierarchicalTask<?, ?> task : tasks) {
            task.start(executorService);
        }
    }

    public X cancelAll() {
        cancel(true);

        for (HierarchicalTask<?, ?> task : tasks) {
            task.cancelAll();
        }

        return (X) this;
    }

    public final boolean cancel() {
        return cancel(true);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean flag = super.cancel(mayInterruptIfRunning);

        if (flag) {
            setState(State.CANCELLED);
        }

        // return the flag
        return flag;
    }

    public X addTask(HierarchicalTask<?, ?> task) {
        if (task.parent != this) {
            throw new IllegalArgumentException("The given tasks parent is not this task");
        }

        try {
            stateLock.readLock().lock();

            if (state != State.READY && state != State.SCHEDULED && state != State.RUNNING) {
                throw new IllegalStateException("Task already completed");
            }

            try {
                startLock.readLock().lock();

                if (phaser.register() < 0) {
                    throw new IllegalStateException("Task already terminated");
                }

                incrementTaskCount();
                tasks.add(task);

                if (onTaskAddedEventHandler != null) {
                    onTaskAddedEventHandler.handle(task);
                }

                if (started) {
                    task.start(executorService);
                }
            } finally {
                startLock.readLock().unlock();
            }

            return (X) this;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public Collection<HierarchicalTask<?, ?>> getTasks() {
        return new ArrayList<>(tasks);
    }

    protected void incrementTaskCount() {
        addTaskCount(1);
    }

    protected void addTaskCount(int value) {
        taskCount.addAndGet(value);

        if (onProgressEventHandler != null) {
            onProgressEventHandler.handle(new ProgressStatus(taskCount.get(), tasksDone.get()));
        }
        if (parent != null) {
            parent.addTaskCount(value);
        }
    }

    protected void incrementTasksDone() {
        addTasksDone(1);
    }

    protected void addTasksDone(int value) {
        tasksDone.addAndGet(value);

        if (onProgressEventHandler != null) {
            onProgressEventHandler.handle(new ProgressStatus(taskCount.get(), tasksDone.get()));
        }
        if (parent != null) {
            parent.addTasksDone(value);
        }
    }

    protected void onSuccess() {
        int phase = phaser.arrive();

        while (true) {
            try {
                int nextPhase;

                while ((nextPhase = phaser.awaitAdvanceInterruptibly(phase)) > -1) {
                    try {
                        stateLock.writeLock().lock();

                        if (nextPhase == phase + 1) {
                            setState(State.SUCCEEDED);
                            break;
                        } else {
                            phase = phaser.arrive();
                        }
                    } finally {
                        stateLock.writeLock().unlock();
                    }
                }
            } catch (InterruptedException ex) {
                if (!getExecutorService().isShutdown()) {
                    continue;
                }

                phaser.forceTermination();
            }

            break;
        }
    }

    protected void onError(Throwable exception) {
        setState(State.FAILED);
        phaser.forceTermination();
    }

    protected void onChildSuccess(HierarchicalTask<?, ?> child, Object result) {
        incrementTasksDone();
        phaser.arrive();
    }

    protected void onChildError(HierarchicalTask<?, ?> child, Throwable exception) {
        incrementTasksDone();
        
        childExceptions.putAll(child.getChildException());
        
        if(exception != null) {
            childExceptions.put(child, exception);
        }
        
        onError(exception);
    }
    
    public Map<HierarchicalTask<?, ?>, Throwable> getChildException(){
        return new HashMap<>(childExceptions);
    }

    public AtomicInteger getTaskCount() {
        return taskCount;
    }

    public AtomicInteger getTasksDone() {
        return tasksDone;
    }

    public HierarchicalTask<?, ?> getRoot() {
        return root;
    }

    public HierarchicalTask<?, ?> getParent() {
        return parent;
    }
    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    private T value;

    public T getValue() {
        return value;
    }

    private void setValue(T value) {
        this.value = value;
    }
    private Throwable exception;

    public Throwable getException() {
        return exception;
    }

    private void _setException(Throwable exception) {
        this.exception = exception;
    }
    private EventHandler<ProgressStatus> onProgressEventHandler;

    public EventHandler<ProgressStatus> getOnProgress() {
        return onProgressEventHandler;
    }

    public X setOnProgress(EventHandler<ProgressStatus> onProgressEventHandler) {
        this.onProgressEventHandler = onProgressEventHandler;
        return (X) this;
    }
    private EventHandler<? super HierarchicalTask<?, ?>> onTaskAddedEventHandler;

    public EventHandler<? super HierarchicalTask<?, ?>> getOnTaskAdded() {
        return onTaskAddedEventHandler;
    }

    public X setOnTaskAdded(EventHandler<? super HierarchicalTask<?, ?>> onTaskAddedEventHandler) {
        this.onTaskAddedEventHandler = onTaskAddedEventHandler;
        return (X) this;
    }
    private EventHandler<? super X> onScheduledEventHandler;

    public EventHandler<? super X> getOnScheduled() {
        return onScheduledEventHandler;
    }

    public X setOnScheduled(EventHandler<? super X> value) {
        onScheduledEventHandler = value;
        return (X) this;
    }

    protected void scheduled() {
    }
    private EventHandler<? super X> onRunningEventHandler;

    public EventHandler<? super X> getOnRunning() {
        return onRunningEventHandler;
    }

    public X setOnRunning(EventHandler<? super X> value) {
        onRunningEventHandler = value;
        return (X) this;
    }

    protected void running() {
    }
    private EventHandler<? super X> onSucceededEventHandler;

    public EventHandler<? super X> getOnSucceeded() {
        return onSucceededEventHandler;
    }

    public X setOnSucceeded(EventHandler<? super X> value) {
        onSucceededEventHandler = value;
        return (X) this;
    }

    protected void succeeded() {
    }
    private EventHandler<? super X> onCancelledEventHandler;

    public EventHandler<? super X> getOnCancelled() {
        return onCancelledEventHandler;
    }

    public X setOnCancelled(EventHandler<? super X> value) {
        onCancelledEventHandler = value;
        return (X) this;
    }

    protected void cancelled() {
    }
    private EventHandler<? super X> onFailedEventHandler;

    public EventHandler<? super X> getOnFailed() {
        return onFailedEventHandler;
    }

    public X setOnFailed(EventHandler<? super X> value) {
        onFailedEventHandler = value;
        return (X) this;
    }

    protected void failed() {
    }
    private EventHandler<? super X> onCompleteEventHandler;

    public EventHandler<? super X> getOnComplete() {
        return onCompleteEventHandler;
    }

    public X setOnComplete(EventHandler<? super X> value) {
        onCompleteEventHandler = value;
        return (X) this;
    }

    protected void completed() {
    }
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public enum State {

        READY,
        SCHEDULED,
        RUNNING,
        SUCCEEDED,
        CANCELLED,
        FAILED
    }
    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();
    private State state = State.READY;

    public final State getState() {
        try {
            stateLock.readLock().lock();
            return state;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    final void setState(State value) {
        try {
            stateLock.writeLock().lock();

            if (state != State.CANCELLED) {
                this.state = value;
                setRunning(value == State.SCHEDULED || value == State.RUNNING);

                switch (state) {
                    case CANCELLED:
                        if (onCancelledEventHandler != null) {
                            onCancelledEventHandler.handle((X) this);
                        }
                        if (parent != null) {
                            parent.onChildError(this, exception);
                        }
                        cancelled();
                        break;
                    case FAILED:
                        if (onFailedEventHandler != null) {
                            onFailedEventHandler.handle((X) this);
                        }
                        if (parent != null) {
                            parent.onChildError(this, exception);
                        }
                        failed();
                        break;
                    case READY:
                        // This even can never meaningfully occur, because the
                        // Task begins life as ready and can never go back to it!
                        break;
                    case RUNNING:
                        if (onRunningEventHandler != null) {
                            onRunningEventHandler.handle((X) this);
                        }
                        running();
                        break;
                    case SCHEDULED:
                        if (onScheduledEventHandler != null) {
                            onScheduledEventHandler.handle((X) this);
                        }
                        scheduled();
                        break;
                    case SUCCEEDED:
                        if (onSucceededEventHandler != null) {
                            onSucceededEventHandler.handle((X) this);
                        }
                        if (parent != null) {
                            parent.onChildSuccess(this, getValue());
                        }
                        succeeded();
                        break;
                    default:
                        throw new AssertionError("Should be unreachable");
                }
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    protected static class HierarchicalTaskCallable<V, Y extends HierarchicalTask<V, Y>> implements Callable<V> {

        protected HierarchicalTask<V, Y> task;

        @Override
        public V call() throws Exception {
            try {
                task.setState(State.SCHEDULED);
                task.setState(State.RUNNING);
                final V result = task.call();
                if (!task.isCancelled()) {
                    task.setValue(result);
                    task.onSuccess();

                    return result;
                } else {
                    return task.getValue();
                }
            } catch (final Throwable th) {
                task._setException(th);
                task.onError(th);

                if (th instanceof Exception) {
                    throw (Exception) th;
                } else {
                    throw new Exception(th);
                }
            } finally {
                if (task.onCompleteEventHandler != null) {
                    task.onCompleteEventHandler.handle((Y) task);
                }
            }
        }
    }
}
