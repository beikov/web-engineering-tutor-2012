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
package at.ac.tuwien.big.testsuite.api.model;

/**
 *
 * @author Christian
 */
public abstract class DelegatingProgressListener<T, E> implements ProgressListener<T, E> {

    private final ProgressListener<T, E> delegate;

    public DelegatingProgressListener(ProgressListener<T, E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onStart() {
        if (delegate != null) {
            delegate.onStart();
        }
    }

    @Override
    public void onProgress(ProgressStatus status) {
        if (delegate != null) {
            delegate.onProgress(status);
        }
    }

    @Override
    public void onError(E error) {
        if (delegate != null) {
            delegate.onError(error);
        }
    }

    @Override
    public void onSuccess(T event) {
        if (delegate != null) {
            delegate.onSuccess(event);
        }
    }
}
