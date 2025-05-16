package com.googlecode.blaisemath.graph.layout;

/*-
 * #%L
 * blaise-graph-theory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import static com.google.common.collect.Maps.newHashMap;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Uses set positions if available to position nodes; otherwise uses a static layout.
 *
 * @param <P> object describing layout parameters
 * 
 * @author John An, Elisha Peterson
 */
public class FixableStaticLayout<P> implements StaticGraphLayout<P> {
    
    private StaticGraphLayout<P> parent;
    private Map<?, Point2D.Double> positions;

    /**
     * Construct layout with given parent layout.
     * @param parent parent layout, used as a default
     */
    public FixableStaticLayout(StaticGraphLayout<P> parent) {
        this.parent = requireNonNull(parent);
        positions = newHashMap();
    }

    /**
     * Construct layout with given parent layout and fixed positions.
     * @param parent parent layout, used as a default
     * @param positions fixed positions
     */
    public FixableStaticLayout(StaticGraphLayout<P> parent, Map<?, Point2D.Double> positions) {
        this.parent = requireNonNull(parent);
        this.positions = positions;
    }

    @Override
    public P createParameters() {
        return parent.createParameters();
    }

    @Override
    public <C> Map<C, Point2D.Double> layout(Graph<C> g, @Nullable Map<C, Point2D.Double> ic, P parameters) {
        if (!positions.keySet().containsAll(g.nodes())) {
            positions = parent.layout(g, ic, parameters);
        }
        return (Map<C, Point2D.Double>) positions;
    }

    public Map<?, Point2D.Double> getPositions() {
        return positions;
    }

    public void setPositions(Map<?, Point2D.Double> positions) {
        this.positions = positions;
    }
    
}
