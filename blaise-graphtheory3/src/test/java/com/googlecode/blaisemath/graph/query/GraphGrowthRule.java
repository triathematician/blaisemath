/*
 * GraphGrowthRule.java
 * Created on Jun 8, 2012
 */
package com.googlecode.blaisemath.graph.query;

/*
 * #%L
 * BlaiseGraphTheory
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

import java.util.Set;
import com.googlecode.blaisemath.graph.Graph;

/**
 * A "growth rule" to make a (small) subgraph larger.
 *
 * @author petereb1
 */
public interface GraphGrowthRule {

    /**
     * Name of rule for display
     * @return name
     */
    String getName();

    /**
     * Grows a subset of a graph based on some rule. While the resulting set is
     * generally expected to be a superset of the input set, this is not enforced.
     * @param graph the entire graph
     * @param seed the seed input set, a subset of nodes of the graph
     * @return larger subset of the graph
     */
    Set grow(Graph graph, Set seed);

}
