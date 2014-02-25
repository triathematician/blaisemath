/*
 * PlotProcessor.java
 * Created Jul 2009 (based on earlier work)
 */

package org.blaise.visometry;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  Contains necessary functions to convert points, arrays of points, etc. from
 *  visometry coordinates to window coordinates for rendering.
 * </p>
 *
 * @param <C> type of the local coordinate
 *
 * @author Elisha Peterson
 */
public class VisometryProcessor<C> {

    /** Converts a local point to window point */
    public Point2D.Double convert(C point, Visometry<C> vis) {
        return vis.toWindow(point);
    }

    /** Converts an array of local points to window points */
    public Point2D.Double[] convertToArray(C[] point, Visometry<C> vis) {
        Point2D.Double[] result = new Point2D.Double[point.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = vis.toWindow(point[i]);
        }
        return result;
    }

    /** Converts a list of local points to window points */
    public List<Point2D.Double> convertToList(Iterable<C> point, Visometry<C> vis) {
        List<Point2D.Double> result = new ArrayList<Point2D.Double>();
        for (C c : point) {
            result.add(vis.toWindow(c));
        }
        return result;
    }

    /** Converts an iterable of local points to a window path */
    public GeneralPath convertToPath(Iterable<C> point, Visometry<C> vis) {
        GeneralPath gp = new GeneralPath();
        boolean started = false;
        for (C c : point) {
            if (c == null) {
                started = false;
                continue;
            }
            Point2D.Double win = vis.toWindow(c);
            if (!started) {
                gp.moveTo((float) win.getX(), (float) win.getY());
            } else {
                gp.lineTo((float) win.getX(), (float) win.getY());
            }
            started = true;
        }
        return gp;
    }

}
