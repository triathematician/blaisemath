package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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


import com.googlecode.blaisemath.graph.Graph;

/**
 * Global metric describing the density of the graph (# edges divided by #
 * @author elisha
 */
public class GraphDensity extends AbstractGraphMetric<Double> {

    public GraphDensity() {
        super("Link density", "Number of edges in the graph divided by the total number possible.", true);
    }

    @Override
    public Double apply(Graph graph) {
        int n = graph.nodeCount();
        return graph.isDirected()
                ? graph.edgeCount() / (n * (n - 1))
                : graph.edgeCount() / (n * (n - 1) / 2.0);
    }
}
