package com.googlecode.blaisemath.graph.mod.layout;

import com.google.common.collect.Maps;
import java.awt.geom.Point2D;
import java.util.Map;
import org.junit.Test;

public class DistanceFinderTest {

    @Test
    public void testFindPointsWithinDistance() {
        Map<Integer, Point2D.Double> pts = Maps.newHashMap();
        for (int i = 0; i < 100; i++) {
            pts.put(i, new Point2D.Double(Math.random(), Math.random()));
        }
        
        DistanceFinder.findPointsWithinDistance(pts, 0.1).forEach(System.out::println);
    }
    
}
