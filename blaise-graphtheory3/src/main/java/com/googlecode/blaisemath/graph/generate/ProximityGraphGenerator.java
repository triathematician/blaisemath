package com.googlecode.blaisemath.graph.generate;

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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphGenerator;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.generate.ProximityGraphGenerator.ProximityGraphParameters;

/**
 * Generates a graph in specified bounding box, where edges are added for points that are within a certain distance.
 *
 * @author Elisha Peterson
 */
public final class ProximityGraphGenerator implements GraphGenerator<ProximityGraphParameters,Point2D.Double> {

    @Override
    public String toString() {
        return "Proximity Graph";
    }

    @Override
    public ProximityGraphParameters createParameters() {
        return new ProximityGraphParameters();
    }

    @Override
    public Graph<Point2D.Double> apply(ProximityGraphParameters parm) {
        int nodes = parm.getNodeCount();
        double x0 = parm.getBounds().getMinX();
        double x1 = parm.getBounds().getMaxX();
        double y0 = parm.getBounds().getMinY();
        double y1 = parm.getBounds().getMaxY();
        double connectDistance = parm.getConnectDistance();
        
        List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
        for (int i = 0; i < nodes; i++) {
            pts.add(new Point2D.Double(x0 + (x1 - x0) * Math.random(), y0 + (y1 - y0) * Math.random()));
        }
        List<Point2D.Double[]> edges = new ArrayList<Point2D.Double[]>();
        for (int i0 = 0; i0 < pts.size(); i0++) {
            for (int i1 = i0+1; i1 < pts.size(); i1++) {
                if (pts.get(i0).distance(pts.get(i1)) > connectDistance) {
                    continue;
                }
                edges.add(new Point2D.Double[]{pts.get(i0), pts.get(i1)});
            }
        }
        return GraphUtils.createFromArrayEdges(false, pts, edges);
    }
    
    //region PARAMETERS CLASS
    
    /** Parameters for proximity graph. */
    public static final class ProximityGraphParameters extends DefaultGeneratorParameters {
        private Rectangle2D bounds = new Rectangle2D.Double();
        private double connectDistance = 1;
        
        public ProximityGraphParameters() {
        }

        public ProximityGraphParameters(boolean directed, int nodes, Rectangle2D bounds, double dst) {
            super(directed, nodes);
            this.bounds = bounds;
            this.connectDistance = dst;
        }

        public Rectangle2D getBounds() {
            return bounds;
        }

        public void setBounds(Rectangle2D bounds) {
            this.bounds = bounds;
        }

        public double getConnectDistance() {
            return connectDistance;
        }

        public void setConnectDistance(double connectDistance) {
            this.connectDistance = connectDistance;
        }
    }
    
    //endregion

}
