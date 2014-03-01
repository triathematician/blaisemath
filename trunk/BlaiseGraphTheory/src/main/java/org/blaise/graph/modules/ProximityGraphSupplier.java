/**
 * ProximityGraphBuilder.java
 * Created Aug 18, 2012
 */

package org.blaise.graph.modules;

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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphSuppliers.GraphSupplierSupport;
import org.blaise.graph.SparseGraph;

/**
 * <p>
 *  Generates a graph in specified bounding box, where edges are added for points
 *  that are within a certain distance.
 * </p>
 * @author elisha
 */
public final class ProximityGraphSupplier extends GraphSupplierSupport<Point2D.Double>{

    private Rectangle2D.Double bounds = new Rectangle2D.Double();
    private double connectDistance = 1;
    
    /** Initialize without arguments */
    public ProximityGraphSupplier() {}

    public ProximityGraphSupplier(boolean directed, int nodes, Rectangle2D.Double bounds, double dst) {
        super(directed, nodes);
        this.bounds = bounds;
        this.connectDistance = dst;
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public double getConnectDistance() {
        return connectDistance;
    }

    public void setConnectDistance(double connectDistance) {
        this.connectDistance = connectDistance;
    }
    
    public Graph<Point2D.Double> get() {
        double x0 = bounds.x, y0 = bounds.y, x1 = bounds.getMaxX(), y1 = bounds.getMaxY();
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
        return new SparseGraph<Point2D.Double>(false, pts, edges);
    }

}
