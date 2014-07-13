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

import java.awt.geom.Point2D;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;

/**
 * Provides methods for a layout scheme that has several iterations. The layout
 * class may have internal parameters that are used to accomplish the layout, and
 * an internal state which changes over the course of the layout. The methods here
 * allow the user to reset the layout scheme and to iterate the layout scheme.
 *
 * @author Elisha Peterson
 */
public interface IterativeGraphLayout {

    /**
     * Resets the layout algorithm's state, using specified map from nodes to positions.
     * These nodes should be exactly those specified in the graph with later calls to the
     * <code>iterate()</code> method.
     * @param positions a map describing the positions for all nodes in the layout
     */
    public void reset(Map<?, Point2D.Double> positions);

    /**
     * Request an adjustment to the current positions of the nodes in the graph during the next iteration.
     * @param positions map specifying new positions for certain nodes, which should take effect
     *   during the next call to iterate()
     * @param resetNodes if true, this should adjust the graph's set of nodes to include only those in the map
     */
    public void requestPositions(Map<?, Point2D.Double> positions, boolean resetNodes);

    /**
     * <p>
     * Iterate the energy layout algorithm. The graph's nodes may not be exactly
     * the same as for previous calls to iterate (i.e. some may have been added or removed).
     * If nodes are present for the first time, the algorithm should add in support for
     * those nodes. If nodes have been removed since the last iteration, the algorithm
     * should simply ignore those nodes.
     * </p><p>
     * If a request has been placed for new locations, the algorithm should adjust
     * the positions of the requested nodes.
     * </p>
     *
     * @param <V> node type of graph
     * @param g the graph to layout
     * @return map with current associating of nodes to points
     */
    public <V> Map<V,Point2D.Double> iterate(Graph<V> g);

    /**
     * @return index of current iteration (should reset to 0 whenever the reset method is called)
     */
    public int getIteration();

    /**
     * @return current value of a "cooling parameter"
     */
    public double getCoolingParameter();

    /**
     * @return current "energy" or some double representing the convergence status
     *   of the layout
     */
    public double getEnergyStatus();

    /**
     * Returns the current list of point locations.
     * @return current list of positions
     */
    public Map<?,Point2D.Double> getPositions();

}
