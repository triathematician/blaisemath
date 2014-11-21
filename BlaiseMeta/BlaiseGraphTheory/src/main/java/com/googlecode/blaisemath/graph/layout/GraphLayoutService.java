/*
 * GraphLayoutService.java
 * Created on Jul 3, 2012
 */
package com.googlecode.blaisemath.graph.layout;

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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.util.SetSelectionModel;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Performs a graph layout service, starting with a graph, a set of initial positions,
 * and parameters, and returning a mapping of nodes to locations. Intermediate and final
 * positions are reported via the {@link GraphLayoutCallback} parameter provided to
 * the layout method.
 *
 * @author Elisha Peterson
 */
public interface GraphLayoutService {
    
    /**
     * Get data model representing a set of nodes to pin.
     * @return pinnable model
     */
    SetSelectionModel getPinnedNodes();

    /**
     * Generates layout for a graph. When the layout is completed, the callback provides a mechanism
     * for returning intermediate results.
     * @param graph the graph
     * @param ic initial conditions (positions on some nodes), can be null if there are none
     * @param callback function to be executed when results are returned
     * @throws InterruptedException if running on a background thread which is interrupted
     */
    void layout(Graph graph, Map<Object, Point2D.Double> ic, GraphLayoutCallback callback) throws InterruptedException;


    //
    // IMPLEMENTATIONS
    //

    /** Implementation for a static layout */
    public static class StaticLayoutService implements GraphLayoutService {
        private final StaticGraphLayout layout;
        private final SetSelectionModel pinned = new SetSelectionModel();
        public StaticLayoutService(StaticGraphLayout layout) {
            this.layout = layout;
        }
        public SetSelectionModel getPinnedNodes() {
            return pinned;
        }
        public void layout(Graph graph, Map<Object, Point2D.Double> ic, 
                GraphLayoutCallback callback) throws InterruptedException {
            Map res = layout.layout(graph);
            callback.layoutCompleted(graph, ic, res);
        }
    }


}
