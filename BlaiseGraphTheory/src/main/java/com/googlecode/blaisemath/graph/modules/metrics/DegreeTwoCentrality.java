/*
 * DegreeCentrality.java
 * Created Nov 4, 2011
 */
package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;


/**
 * Computes the second-order degree of a vertex in a graph, i.e. how many vertices are within two hops.
 * Does not include the vertex itself.
 * 
 * @author elisha
 */
public class DegreeTwoCentrality implements GraphNodeMetric<Integer> {
    
    public <V> Integer apply(Graph<V> graph, V vertex) { 
        return GraphUtils.neighborhood(graph, vertex, 2).size() - 1;
    }
    
}
