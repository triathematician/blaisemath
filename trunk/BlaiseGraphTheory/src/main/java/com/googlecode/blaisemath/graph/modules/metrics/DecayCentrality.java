/*
 * DecayCentrality.java
 * Created May 12, 2010
 */
package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.graph.GraphNodeMetric;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * <p> Provides a metric describing the decay centrality of a vertex in a graph.
 * This computation can be slow for large graphs since it uses all vertices in a
 * component. </p> <p> The computation depends on a single <i>decay
 * parameter</i>... a node at distance 1 contributes this parameter, at distance
 * 2 the parameter squared, and so on. As the parameter approaches 1, the
 * metric's value approaches the size of the node's component. As the parameter
 * approaches 0, the metric's value also approaches the parameter times the size
 * of the node's neighborhood. </p>
 *
 * @author Elisha Peterson
 */
public class DecayCentrality implements GraphNodeMetric<Double> {

    /** Decay parameter */
    protected double parameter = 0.5;

    /** Construct with default decay parameter of 0.5 */
    public DecayCentrality() {
        this(0.5);
    }

    /**
     * Construct with specified parameter.
     * @param parameter value of decay parameter
     * @throws IllegalArgumentException if value is outside of the range [0,1]
     */
    public DecayCentrality(double parameter) {
        setParameter(parameter);
    }

    @Override
    public String toString() {
        return "Decay Centrality (" + parameter + ")";
    }

    /**
     * @return value of decay parameter
     */
    public double getParameter() {
        return parameter;
    }

    /**
     * Set new parameter.
     * @param newValue new value of decay parameter
     */
    public final void setParameter(double newValue) {
        if (!(newValue <= 1 && newValue >= 0)) {
            throw new IllegalArgumentException("Parameter for DecayCentrality must be between 0 and 1: " + newValue);
        }
        parameter = newValue;
    }

    public <V> Double apply(Graph<V> graph, V vertex) {
        Map<V, Integer> nvg = GraphUtils.geodesicTree(graph, vertex);
        double total = 0.0;
        for (V v : nvg.keySet()) {
            total += Math.pow(parameter, nvg.get(v));
        }
        return total;
    }
}
