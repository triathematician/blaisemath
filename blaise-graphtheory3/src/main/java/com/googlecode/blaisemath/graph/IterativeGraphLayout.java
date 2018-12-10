package com.googlecode.blaisemath.graph;

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
 * Performs an iterative 2D layout on a graph, using a given set of parameters. Implementations should use a state
 * object to track their state and make any changes from step to step.
 *
 * @param <P> object describing layout parameters
 * @param <S> object describing layout state
 * 
 * @author Elisha Peterson
 */
public interface IterativeGraphLayout<P,S extends IterativeGraphLayoutState> extends ParameterSupplier<P> {
    
    /**
     * Create a new state object for the layout.
     * @return new state object
     */
    S createState();

    /**
     * Iterate the energy layout algorithm. The data structure provided to this method should not be changed during
     * iteration. However, the graph's nodes may not be exactly the same as for previous calls to iterate (i.e. some may
     * have been added or removed). If nodes are present for the first time, the algorithm should add in support for
     * those nodes. If nodes have been removed since the last iteration, the algorithm should simply ignore those nodes.
     * <p>
     * If a request has been placed for new locations, the algorithm should adjust the positions of the requested nodes.
     *
     * @param <C> graph node type
     * @param graph the graph
     * @param layoutState state object for the layout
     * @param layoutParams parameters object for the layout
     * @return energy computation, to provide a measure of algorithm convergence 
     */
    <C> double iterate(Graph<C> graph, S layoutState, P layoutParams);

}
