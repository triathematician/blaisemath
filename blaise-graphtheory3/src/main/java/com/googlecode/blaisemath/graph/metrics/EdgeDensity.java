package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

/**
 * Global metric describing the density of the graph (# edges divided by # possible).
 *
 * @author Elisha Peterson
 */
public class EdgeDensity extends AbstractGraphMetric<Double> {

    public EdgeDensity() {
        super("Edge density", "Number of edges in the graph divided by the total number possible.", true);
    }

    @Override
    public Double apply(Graph graph) {
        int n = graph.nodes().size();
        return graph.isDirected() ? graph.edges().size() / (n * (n - 1))
                : graph.edges().size() / (n * (n - 1) / 2.0);
    }
}
