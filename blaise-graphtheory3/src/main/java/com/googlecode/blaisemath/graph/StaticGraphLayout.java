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
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Performs a 2D layout on a graph, using a given set of parameters. Initial conditions may be provided directly to the
 * algorithm, but anything else impacting the layout should go into the parameters object.
 *
 * @param <P> object describing layout parameters
 * 
 * @author Elisha Peterson
 */
public interface StaticGraphLayout<P> extends ParameterSupplier<P> {

    /**
     * Perform layout on given graph, and return result.
     * @param <C> graph node type
     * @param g a graph written in terms of adjacencies
     * @param ic initial conditions
     * @param parameters parameters for the layout, e.g. radius
     * @return a mapping of points to vertices
     */
    <C> Map<C, Point2D.Double> layout(Graph<C> g, @Nullable Map<C, Point2D.Double> ic, P parameters);
    
}
