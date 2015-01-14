/**
 * PositionalAddingLayout.java
 * Created on Sep 17, 2014
 */
package com.googlecode.blaisemath.graph.modules.layout;

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

import com.google.common.collect.Maps;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;

/**
 * Positions graph nodes nearby connected nodes. The first layout parameter
 * is the approximate distance between a new node's position and the position
 * of connected nodes. Nodes with existing locations are not affected.
 * 
 * @author petereb1
 */
public class PositionalAddingLayout implements StaticGraphLayout<Double> {
    
    public Class<Double> getParametersType() {
        return Double.class;
    }

    public <C> Map<C, Point2D.Double> layout(Graph<C> g, Map<C, Point2D.Double> curLocations, Set<C> fixed, Double len) {
        Map<C, Point2D.Double> res = Maps.newHashMap();
        for (C node : g.nodes()) {
            if (curLocations.containsKey(node)) {
                res.put(node, curLocations.get(node));
            } else {
                double sx = 0;
                double sy = 0;
                int n = 0;
                for (C o : g.neighbors(node)) {
                    Point2D.Double p = curLocations.get(o);
                    if (p != null) {
                        sx += p.x;
                        sy += p.y;
                        n++;
                    }
                }
                if (n == 0) {
                    double theta = 2*Math.PI*Math.random();
                    res.put(node, new Point2D.Double(sx+2*len*Math.cos(theta), sy+2*len*Math.sin(theta)));
                } else if (n == 1) {
                    double theta = 2*Math.PI*Math.random();
                    res.put(node, new Point2D.Double(sx+len*Math.cos(theta), sy+len*Math.sin(theta)));
                } else {
                    res.put(node, new Point2D.Double(sx/n, sy/n));
                }
            }
        }
        return res;
    }

}
