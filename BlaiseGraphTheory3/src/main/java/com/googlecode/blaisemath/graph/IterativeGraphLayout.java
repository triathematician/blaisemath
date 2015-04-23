/*
 * IterativeGraphLayout.java
 * Created Jul 9, 2010
 */

package com.googlecode.blaisemath.graph;

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

import com.googlecode.blaisemath.graph.Graph;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>
 *   Provides methods for a layout scheme that has several iterations. The layout
 *   class may have internal parameters that are used to accomplish the layout, and
 *   an internal state which changes over the course of the layout. The methods here
 *   allow the user to reset the layout scheme and to iterate the layout scheme.
 * </p>
 * <p>
 * Implementations should be thread-safe, as the API methods will be called from
 * both the layout thread and UI threads.
 * </p>
 *
 * @param <C> node type
 * @author Elisha Peterson
 */
@ThreadSafe
public interface IterativeGraphLayout<C> {

    /**
     * Get the current iteration #
     * @return index of current iteration (should reset to 0 whenever the reset method is called)
     */
    int getIteration();

    /**
     * @return current "energy" or some double representing the convergence status
     *   of the layout
     */
    double getEnergyStatus();

    /**
     * Returns copy of the current list of positions.
     * @return current list of positions
     */
    Map<C,Point2D.Double> getPositionsCopy();

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
     * Return the set of nodes that are currently "pinned" or "locked" in place.
     * @return pinned nodes
     */
    Set<C> getLockedNodes();
    
    /**
     * Update the collection of nodes that are currently "pinned".
     * @param pinned the nodes to pin
     */
    void setLockedNodes(Set<C> pinned);

    /**
     * Request an adjustment to the current positions of the nodes in the graph during the next iteration.
     * Intended to be invokable from threads other than the one performing the layout.
     * @param positions map specifying new positions for certain nodes, which should take effect
     *   during the next call to iterate()
     * @param resetNodes if true, this should adjust the graph's set of nodes to include only those in the map
     */
    void requestPositions(Map<C, Point2D.Double> positions, boolean resetNodes);

    /**
     * <p>
     * Iterate the energy layout algorithm. The data structure provided to this
     * method should not be changed during iteration. However, the graph's nodes may not be exactly
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
     * @param g the graph to layout (should not change while layout is being performed)
     */
    void iterate(Graph<C> g);

}
