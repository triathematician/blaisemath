/**
 * WheelGraphGenerator.java
 * Created 2012
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.List;

/**
 * Constructs wheel graph with n vertices.
 * All vertices are connected to a central hub, and all non-central
 * vertices connected in a cyclic fashion.
 * @author Elisha Peterson
 */
public final class WheelGraphGenerator implements GraphGenerator<DefaultGeneratorParameters,Integer> {

    @Override
    public String toString() {
        return "Wheel Graph";
    }

    @Override
    public DefaultGeneratorParameters createParameters() {
        return new DefaultGeneratorParameters();
    }

    @Override
    public Graph<Integer> apply(DefaultGeneratorParameters parm) {
        final int nodes = parm.getNodeCount();
        if (nodes == 0) {
            return GraphUtils.emptyGraph();
        }
        List<Integer[]> edges = Lists.newArrayList();
        for (int i = 1; i < nodes; i++) {
            edges.add(new Integer[]{0, i});
        }
        for (int i = 1; i < nodes - 1; i++) {
            edges.add(new Integer[]{i, i + 1});
            if (parm.isDirected()) {
                edges.add(new Integer[]{i + 1, i});
            }
        }
        edges.add(new Integer[]{nodes - 1, 1});
        if (parm.isDirected()) {
            edges.add(new Integer[]{1, nodes - 1});
        }
        return DefaultGeneratorParameters.createGraphWithEdges(parm, edges);
    }
    
}
