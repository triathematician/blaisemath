/*
 * IterativeGraphLayout.java
 * Created Jul 9, 2010
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 * <p>
 * Provides methods for a layout scheme that has several iterations. The layout
 * class may have internal parameters that are used to accomplish the layout, and
 * an internal state which changes over the course of the layout. The methods here
 * allow the user to reset the layout scheme and to iterate the layout scheme.
 * </p>
 * <p>
 * Implementations are intended to be accessed from a single thread.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface IterativeGraphLayout {

    /**
     * Get iteration #
     * @return index of current iteration (should reset to 0 whenever the reset method is called)
     */
    int getIteration();

    /**
     * Retrieve current value of cooling parameter.
     * @return current value of a "cooling parameter"
     */
    double getCoolingParameter();

    /**
     * Update current value of cooling parameter.
     * @param val new value
     */
    void setCoolingParameter(double val);

    /**
     * @return current "energy" or some double representing the convergence status
     *   of the layout
     */
    double getEnergyStatus();
    
    /**
     * Return the set of nodes that are currently "pinned" in place.
     * @return pinned nodes
     */
    SetSelectionModel<?> getPinnedNodes();

    /**
     * Returns the current list of point locations.
     * @return current list of positions
     */
    Map<?,Point2D.Double> getPositions();

    /**
     * Request an adjustment to the current positions of the nodes in the graph during the next iteration.
     * @param positions map specifying new positions for certain nodes, which should take effect
     *   during the next call to iterate()
     * @param resetNodes if true, this should adjust the graph's set of nodes to include only those in the map
     */
    void requestPositions(Map<?, Point2D.Double> positions, boolean resetNodes);

    /**
     * <p>
     * Iterate the energy layout algorithm. The graph's nodes may not be exactly
     * the same as for previous calls to iterate (i.e. some may have been added or removed).
     * If nodes are present for the first time, the algorithm should add in support for
     * those nodes. If nodes have been removed since the last iteration, the algorithm
     * should simply ignore those nodes.
     * </p>
     * <p>
     * If a request has been placed for new locations, the algorithm should adjust
     * the positions of the requested nodes.
     * </p>
     *
     * @param <V> node type of graph
     * @param g the graph to layout
     */
    <V> void iterate(Graph<V> g);

}
