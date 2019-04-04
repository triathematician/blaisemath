package com.googlecode.blaisemath.graph.mod.layout;

/*
 * #%L
 * blaise-graphtheory3
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.mod.layout.IntervalIntersector.Interval;
import com.googlecode.blaisemath.graph.mod.layout.IntervalIntersector.TwoInts;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Algorithm to efficiently compute all points within a certain distance of each other.
 * @author petereb1
 */
public class DistanceFinder {
    
    /**
     * Finds all point pairs within a certain distance of each other. First sorts x and y dimensions
     * separately, and restricts search to just pairs that are within range of each other in both
     * dimensions.
     */
    public static <C> List<PointPair<C>> findPointsWithinDistance(Map<C, Point2D.Double> locs, double max) {
        List<C> indices = Lists.newArrayList(locs.keySet());
        List<Point2D.Double> points = Lists.newArrayList(locs.values());
        
        double halfMax = .5*max;
        List<Interval> xIntervals = Lists.newArrayList();
        List<Interval> yIntervals = Lists.newArrayList();
        for (int i = 0; i < indices.size(); i++) {
            Point2D.Double p = points.get(i);
            xIntervals.add(new Interval(i, p.x - halfMax, p.x + halfMax));
            yIntervals.add(new Interval(i, p.y - halfMax, p.y + halfMax));
        }
        List<TwoInts> xClose = IntervalIntersector.findIntersections(xIntervals);
        List<TwoInts> yClose = IntervalIntersector.findIntersections(yIntervals);
        return fineIntersections(xClose, yClose, indices, locs, max);
//        return fineIntersections(xClose, null, indices, locs, max);
    }
 
    /** Uses computed broad-phase intersections in separate x and y intervals to get all intersecting pairs. */
    private static <C> List<PointPair<C>> fineIntersections(List<TwoInts> xClose, List<TwoInts> yClose, List<C> indices, 
            Map<C, Point2D.Double> locs, double max) {
        Set<TwoInts> yCloseSet = Sets.newHashSet(yClose);
        List<PointPair<C>> res = Lists.newArrayList();
        for (TwoInts s : xClose) {
            if (yCloseSet.contains(s)) {
                C v0 = indices.get(s.x);
                C v1 = indices.get(s.y);
                Point2D.Double p0 = locs.get(v0);
                Point2D.Double p1 = locs.get(v1);
                double dist = p1.distance(p0);
                if (dist <= max) {
                    res.add(new PointPair<C>(v0, p0, v1, p1, dist));
                }
            }
        }
        return res;        
    }
    
    /** Tracks a pair of nearby points, and their distance. */
    public static class PointPair<C> {
        C item1;
        Point2D.Double point1;
        C item2;
        Point2D.Double point2;
        double distance;

        private PointPair(C item1, Point2D.Double point1, C item2, Point2D.Double point2, double distance) {
            this.item1 = item1;
            this.point1 = point1;
            this.item2 = item2;
            this.point2 = point2;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("(%.2f,%.2f) - (%.2f,%.2f) = %.2f", point1.x, point1.y, point2.x, point2.y, distance);
        }
    }
}
