package com.googlecode.blaisemath.graph.mod.metrics;

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
 * Global metric describing the average degree of the graph.
 *
 * @author Elisha Peterson
 */
public class AverageDegree extends AbstractGraphMetric<Double> {

    public AverageDegree() {
        super("Average degree", "Average degree of vertices in the graph. Uses average indegree/outdegree for a directed graph.", true);
    }

    @Override
    public Double apply(Graph graph) {
        return graph.isDirected() ? graph.edges().size() / (double) graph.nodes().size()
                : 2.0 * graph.edges().size() / (double) graph.nodes().size();
    }
}
