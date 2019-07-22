package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * blaise-graphtheory3
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphNodeMetric;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Partial implementation of a node metric that gives a name to the metric and provides a default implementation for
 * mapping all values.
 * 
 * @param <T> metric result type
 *
 * @author Elisha Peterson
 */
public abstract class AbstractGraphNodeMetric<T> implements GraphNodeMetric<T> {
   
    protected final String name;

    public AbstractGraphNodeMetric(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public <N> Map<N, T> apply(Graph<N> graph) {
        return graph.nodes().stream().collect(toMap(n -> n, n -> apply(graph, n)));
    }
    
}
