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
public abstract class DelegatingHierarchicalProgressListener<T, E, G extends ProgressListener> extends DelegatingProgressListener<T, E> implements HierarchicalProgressListener<T, E, G> {

    private final HierarchicalProgressListener<T, E, G> delegate;

    public DelegatingHierarchicalProgressListener(HierarchicalProgressListener<T, E, G> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public G getChildListener(Object child) {
        if (delegate != null) {
            return delegate.getChildListener(child);
        }

        return null;
    }
}
