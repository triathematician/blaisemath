/*
 * GraphLocationUpdater.java
 * Created Aug 2012
 */
package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
 * Object that listens for updates to a graph, either the entire graph or
 * just the locations of objects in the graph.
 *
 * @author Elisha Peterson
 */
public interface GraphLocationUpdater {

    /**
     * Called when the entire graph is updated.
     * @param graph the new graph
     * @param points locations of nodes in the graph
     */
    public void graphUpdated(Graph graph, Map<?,Point2D.Double> points);

    /**
     * Called when locations only are updated.
     * @param points locations of nodes in the graph
     */
    public void locationsUpdated(Map<?,Point2D.Double> points);



    /**
     * Adapter class that can be sub-classed for custom functionality
     */
    public static class GraphLocationUpdaterAdapter implements GraphLocationUpdater {
        public void graphUpdated(Graph graph, Map<?,Point2D.Double> points) {}
        public void locationsUpdated(Map<?,Point2D.Double> points) {}
    }

}
