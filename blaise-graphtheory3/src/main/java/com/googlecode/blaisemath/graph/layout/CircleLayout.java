package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Position nodes in a circle.
 *
 * @author Elisha Peterson
 */
public class CircleLayout implements StaticGraphLayout<CircleLayout.CircleLayoutParameters> {

    private static final CircleLayout INST = new CircleLayout();
    
    public static CircleLayout getInstance() {
        return INST;
    }
    
    @Override
    public String toString() {
        return "Position nodes in a circle";
    }
    
    @Override
    public CircleLayoutParameters createParameters() {
        return new CircleLayoutParameters();
    }
    
    @Override
    public <N> Map<N, Point2D.Double> layout(Graph<N> g, @Nullable Map<N, Point2D.Double> ic, CircleLayoutParameters p) {
        double radius = p.radius;
        int n = g.nodes().size();
        int i = 0;
        Map<N, Point2D.Double> result = Maps.newHashMap();
        for (N v : g.nodes()) {
            result.put(v, new Point2D.Double(radius * Math.cos(2 * Math.PI * i / n), radius * Math.sin(2 * Math.PI * i / n)));
            i++;
        }
        return result;
    }

    //region PARAMETERS

    /** Parameters associated with circle layout */
    public static class CircleLayoutParameters {
        private double radius = 100.0;

        public CircleLayoutParameters() {
        }

        public CircleLayoutParameters(double rad) {
            this.radius = rad;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }
    }

    //endregion
}
