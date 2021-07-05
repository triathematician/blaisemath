package com.googlecode.blaisemath.graph.mod.ordering;

/*
 * #%L
 * blaise-graphtheory3
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import java.util.List;

/**
 * Provides an ordering of nodes in a graph.
 * @author Elisha Peterson
 */
public interface NodeOrdering<C> {

    /**
     * Compute node order in a graph.
     * @param graph the graph
     * @return ordered list of (all) nodes in the graph
     */
    List<C> order(Graph<C> graph);
    
}
