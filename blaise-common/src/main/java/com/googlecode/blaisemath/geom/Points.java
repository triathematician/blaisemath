package com.googlecode.blaisemath.geom;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Iterables;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Utilities for working with points.
 * 
 * @author Elisha Peterson
 */
public class Points {
    
    private Points() {
    }

    /**
     * Formats a point with n decimal places in the form (a, b).
     * @param p the point to format
     * @param n number of decimal places
     * @return formatted point, e.g. (2.1,-3.0)
     */
    public static String format(Point2D p, int n) {
        return String.format("(%."+n+"f, %."+n+"f)", p.getX(), p.getY());
    }
   
    /**
     * Create and return bounding box around a given set of pounds. Returns null
     * if there is 0 points, and a box with side length {@code margin}
     * around the point if there is just 1 point. (If the {@code margin} is 0, returns
     * a box of side length 1.)
     * @param pts the points
     * @param inset additional padding to include around the box
     * @return bounding box, null if there are no points
     */
    public static Rectangle2D.@Nullable Double boundingBox(Iterable<? extends Point2D> pts, double inset) {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        int count = 0;
        for (Point2D p : pts) {
            minx = Math.min(minx, p.getX());
            miny = Math.min(miny, p.getY());
            maxx = Math.max(maxx, p.getX());
            maxy = Math.max(maxy, p.getY());
            count++;
        }
        if (count == 0) {
            return null;
        } else if (count == 1) {
            double m = inset == 0 ? .5 : inset;
            return new Rectangle2D.Double(minx - m, miny - m, 2 * m, 2 * m);
        } else {
            return new Rectangle2D.Double(minx - inset, miny - inset,
                    maxx - minx + 2 * inset, maxy - miny + 2 * inset);
        }
    }

    /**
     * Compute the average location of a set of points.
     * @param locs points (should be non-empty)
     * @return average loc
     * @throws IllegalArgumentException if argument is empty
     */
    public static Point2D average(Iterable<? extends Point2D> locs) {
        checkArgument(locs != null && Iterables.size(locs) > 0);
        double sumx = 0;
        double sumy = 0;
        int count = 0;
        for (Point2D p : locs) {
            sumx += p.getX();
            sumy += p.getY();
            count++;
        }
        return new Point2D.Double(sumx/count, sumy/count);
    }
    
}
