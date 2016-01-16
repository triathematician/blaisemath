/**
 * StarGraphGenerator.java
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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.AbstractList;

/**
 * Constructs star graph with n vertices; all vertices are connected to a central hub.
 * @author Elisha Peterson
 */
public final class StarGraphGenerator implements GraphGenerator<DefaultGeneratorParameters,Integer> {

    @Override
    public String toString() {
        return "Star Graph";
    }

    @Override
    public DefaultGeneratorParameters createParameters() {
        return new DefaultGeneratorParameters();
    }

    @Override
    public Graph<Integer> apply(DefaultGeneratorParameters parm) {
        final int nodes = parm.getNodeCount();
        if (nodes == 0) {
            return GraphUtils.EMPTY_GRAPH;
        }
        return DefaultGeneratorParameters.createGraphWithEdges(parm, new AbstractList<Integer[]>() {
            @Override
            public Integer[] get(int index) {
                return new Integer[]{0, index + 1};
            }

            @Override
            public int size() {
                return nodes == 0 ? 0 : nodes - 1;
            }
        });
    }
    
}
