/**
 * PathSpacers.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.scio.diffeq;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 *   <code>PathSpacers</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class PathSpacers {

    /**
     * Returns a list of paths such that each point in the grid specified by ps is within a max distance of dist
     * from each of these paths.
     *
     * @param ps
     * @param dist
     * @return
     */
    public static List<Point2D.Double[]> getSpacedPaths(Collection<Point2D.Double> grid, PSClass ps, double dist) {
        ArrayList<Point2D.Double[]> result = new ArrayList<Point2D.Double[]>();
        ArrayList<Point2D.Double> gridPts = new ArrayList<Point2D.Double>(grid);
        if (gridPts.size() == 0) {
            return result;
        }
        while (!gridPts.isEmpty()) {
            // add path through that point
            result.add(ps.getPath(gridPts.remove(0)));
            // remove points within specified distance
            HashSet<Point2D.Double> closePts = new HashSet<Point2D.Double>();
            for (Point2D.Double p : gridPts) {
                for (Point2D.Double[] path : result) {
                    if (minDist(p, path) <= dist) {
                        closePts.add(p);
                        break;
                    }
                }
            }
            gridPts.removeAll(closePts);
        }
        return result;
    }

    static double minDist(Point2D.Double point, Point2D.Double[] path) {
        double minDist = Double.MAX_VALUE;
        double dist;
        for (int i = 0; i < path.length; i++) {
            dist = path[i].distanceSq(point);
            if (dist < minDist) {
                minDist = dist;
            }
        }
        return Math.sqrt(minDist);
    }

    public interface PSClass {

        public Point2D.Double[] getPath(Point2D.Double initialPoint);
    }
}
