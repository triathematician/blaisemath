/*
 * GraphLayoutCallback.java
 * Created on Feb 13, 2012
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

import java.awt.geom.Point2D;
import java.util.Map;
import com.googlecode.blaisemath.graph.Graph;

/**
 * Contains an asynchronous callback method to be used in conjunction
 * with {@link GraphLayoutService}.
 *
 * @author petereb1
 */
public interface GraphLayoutCallback {

    /**
     * Can be used to return intermediate results of a layout algorithm. May or may
     * not be called by a {@link GraphLayoutService}.
     * @param graph the original graph
     * @param steps # of steps completed
     * @param energy energy level
     * @param positions positions returned in most recent layout iteration
     */
    public void intermediateLayout(Graph graph, int steps, float energy, Map<Object, Point2D.Double> positions);



    /**
     * Called when layout results are completed
     * @param graph the original graph
     * @param ic the initial conditions
     * @param result the resulting positions
     */
    public void layoutCompleted(Graph graph, Map<Object, Point2D.Double> ic, Map<Object, Point2D.Double> result);

}
