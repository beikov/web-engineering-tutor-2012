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

/**
 *
 * @author Christian
 */
public abstract class LeafTask<T> extends HierarchicalTask<T, LeafTask<T>> {

    public LeafTask() {
    }

    public LeafTask(HierarchicalTask<?, ?> parent) {
        super(parent);
    }

    public void addTotalWork(int value) {
        addTaskCount(value);
    }

    public void addDoneWork(int value) {
        addTasksDone(value);
    }
}
