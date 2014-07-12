/*
 * PointUtils.java
 * Created Aug 28, 2013
 */
package com.googlecode.blaisemath.graph.view;

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
import javax.annotation.Nullable;

/**
 * Utilities for working with points, useful to support link graph displays.
 * 
 * @author Elisha
 */
public class PointUtils {
   
    /**
     * Create and return bounding box around a given set of pounds. Returns null
     * if there is 0 points, and a box with side length {@code margin}
     * around the point if there is just 1 point. (If the {@code margin} is 0, returns
     * a box of side length 1.)
     * @param pts the points
     * @param margin additional padding to include around the box
     * @return bounding box
     */
    public static @Nullable Rectangle2D.Double boundingBox(Iterable<? extends Point2D> pts, double margin) {
        double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE, maxx = -Double.MAX_VALUE, maxy = -Double.MAX_VALUE;
        int count = 0;
        for (Point2D p : pts) {
            minx = Math.min(minx, p.getX());
            miny = Math.min(minx, p.getY());
            maxx = Math.max(maxx, p.getX());
            maxy = Math.max(maxx, p.getY());
            count++;
        }
        if (count == 0) {
            return null;
        } else if (count == 1) {
            double m = margin == 0 ? .5 : margin;
            return new Rectangle2D.Double(minx-m, miny-m, 2*m, 2*m);
        } else {
            return new Rectangle2D.Double(minx-margin, miny-margin, maxx-minx+2*margin, maxy-miny+2*margin);
        }
    }
    
}
