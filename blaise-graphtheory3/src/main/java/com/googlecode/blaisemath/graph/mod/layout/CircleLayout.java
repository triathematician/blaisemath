/*
 * CircleLayout.java
 * Created 2010
 */
package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import java.awt.geom.Point2D;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Position nodes in a circle.
 * @author elisha
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
    public Map layout(Graph g, @Nullable Map ic, CircleLayoutParameters parm) {
        double radius = parm.radius;
        Map<Object, Point2D.Double> result = Maps.newHashMap();
        int size = g.nodeCount();
        int i = 0;
        for (Object v : g.nodes()) {
            result.put(v, new Point2D.Double(
                    radius * Math.cos(2 * Math.PI * i / size), 
                    radius * Math.sin(2 * Math.PI * i / size)));
            i++;
        }
        return result;
    }
    
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
}
