/*
 * RandomBoxLayout.java
 * Created 2010
 */
package com.googlecode.blaisemath.graph.mod.layout;

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
import com.googlecode.blaisemath.graph.mod.layout.RandomBoxLayout.BoxLayoutParameters;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Position nodes at random locations in a box.
 *
 * @author elisha
 */
public class RandomBoxLayout implements StaticGraphLayout<BoxLayoutParameters> {

    private static final RandomBoxLayout INST = new RandomBoxLayout();

    public static RandomBoxLayout getInstance() {
        return INST;
    }
    
    @Override
    public String toString() {
        return "Position nodes randomly in a rectangle";
    }

    @Override
    public BoxLayoutParameters createParameters() {
        return new BoxLayoutParameters();
    }
    
    @Override
    public Map layout(Graph g, Map ic, Set fixed, BoxLayoutParameters parm) {
        Random r = new Random();
        Map<Object, Point2D.Double> result = Maps.newHashMap();
        double minx = parm.getBounds().getMinX();
        double miny = parm.getBounds().getMinY();
        double maxx = parm.getBounds().getMaxX();
        double maxy = parm.getBounds().getMaxY();
        for (Object v : g.nodes()) {
            double x = r.nextDouble();
            double y = r.nextDouble();
            result.put(v, new Point2D.Double(x*minx+(1-x)*maxx, y*miny+(1-y)*maxy));
        }
        return result;
    }
    
    /** Parameters associated with circle layout */
    public static class BoxLayoutParameters {
        private Rectangle2D.Double bounds = new Rectangle2D.Double(-10, -10, 20, 20);

        public BoxLayoutParameters() {
        }

        public BoxLayoutParameters(Rectangle2D.Double bounds) {
            this.bounds = bounds;
        }
        
        public Rectangle2D.Double getBounds() {
            return bounds;
        }

        public void setBounds(Rectangle2D.Double bounds) {
            this.bounds = bounds;
        }
    }
}
