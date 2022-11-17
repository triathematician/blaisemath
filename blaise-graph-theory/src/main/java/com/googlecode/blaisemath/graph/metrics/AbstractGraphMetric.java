package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
 * --
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
 * #L%
 */


import com.googlecode.blaisemath.graph.GraphMetric;

/**
 * Partial implementation of global metric.
 *
 * @param <T> computed value type
 * @author Elisha Peterson
 */
public abstract class AbstractGraphMetric<T> implements GraphMetric<T> {

    private final String name;
    private final String description;
    private final boolean supportsDirected;

    public AbstractGraphMetric(String name, String description, boolean supportsDirected) {
        this.name = name;
        this.description = description;
        this.supportsDirected = supportsDirected;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean supportsGraph(boolean directed) {
        return !directed || supportsDirected;
    }

}
