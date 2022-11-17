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

import com.google.common.graph.Graph;

/**
 * Global metric describing the clustering coefficient of the graph; in the
 * directed case, measures "transitivity", i.e. when a-%gt;b,b-%gt;c implies
 * a-%gt;c
 *
 * @author Elisha Peterson
 */
public class ClusteringCoefficientByPath extends AbstractGraphMetric<Double> {

    public ClusteringCoefficientByPath() {
        super("Clustering coefficient (by path)", "Computes the clustering coefficient:"
                + " Out of all length-3 paths, how many are enclosed by a triangle?", true);
    }

    @Override
    public Double apply(Graph graph) {
        int[] tri = ClusteringCoefficient.triples(graph);
        int triangles = tri[0], triples = tri[1];
        if (!graph.isDirected()) {
            triangles /= 3;
        }
        return triangles / (double) triples;
    }
}
