/**
 * _SpacedPaths.java
 * Created on Sep 25, 2009
 */

package org.bm.blaise.specto.plane.data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import scio.diffeq.PathSpacers;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *   <code>_SpacedPaths</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class _SpacedPaths extends AbstractPlottable<Point2D.Double> {
    
    List<Point2D.Double[]> result;

    public _SpacedPaths() {
        ArrayList<Point2D.Double> testGrid = new ArrayList<Point2D.Double>();
        for (double x = -5.0; x <= 5.0; x += 0.5) {
            for (double y = -5.0; y <= 5.0; y += 0.5) {
                testGrid.add(new Point2D.Double(x, y));
            }
        }
        result = PathSpacers.getSpacedPaths(testGrid, new TestClass(), 1.0);
    }

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        for (Point2D.Double[] path : result) {
            vg.drawPath(path);
        }
    }


    static class TestClass implements PathSpacers.PSClass {
        public Point2D.Double[] getPath(Point2D.Double initialPoint) {
            Point2D.Double[] result = new Point2D.Double[10];
            result[0] = initialPoint;
            for (int i = 1; i < result.length; i++) {
                result[i] = new Point2D.Double(result[i-1].x + .03, result[i-1].y + .3);
            }
            return result;
        }
    }
}
