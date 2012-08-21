/**
 * PlaneGraphicsInSpace.java
 * Created on Nov 23, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.specto.plane.PlaneGraphics;
import org.bm.blaise.specto.visometry.Visometry;
import scio.coordinate.Point3D;

/**
 * <p>
 *    This class uses the xy-plane to draw 2d elements in 3d space. It requires
 *    an underlying space graphics object to use for drawing.
 *
 *    TODO - does not work yet. need to complete functionality.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneGraphicsInSpace extends PlaneGraphics {

    SpaceGraphics sg;

    public PlaneGraphicsInSpace(SpaceGraphics sg, Visometry<Point2D.Double> vis) {
        super(vis);
        this.sg = sg;
    }

    Point3D inclusion(Point2D.Double coordinate) {
        return inclusion(coordinate.x, coordinate.y);
    }

    Point3D inclusion(double x, double y) {
        return new Point3D(x, y, 0);
    }

    @Override
    public void drawPoint(Point2D.Double coordinate) {
        sg.drawPoint( inclusion(coordinate) );
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        sg.drawSegment( inclusion(x1, y1), inclusion(x2, y2) );
    }

    @Override
    public void drawSegment(Point2D.Double coord1, Point2D.Double coord2) {
        drawLine(coord1.x, coord1.y, coord2.x, coord2.y);
    }

    @Override
    public void drawPath(GeneralPath path) {
        List<Point3D[]> pathParts = new ArrayList<Point3D[]>();
        List<Point3D> curSeg = null;
        PathIterator pi = path.getPathIterator(null);
        double[] segment = new double[6];
        int segType = 0;
        while (! pi.isDone() ) {
            segType = pi.currentSegment(segment);
            if (segType == PathIterator.SEG_MOVETO) {
                if (curSeg != null && curSeg.size() > 1) {
                    pathParts.add(curSeg.toArray(new Point3D[]{}));
                }
                curSeg = new ArrayList<Point3D>();
                curSeg.add(inclusion(segment[0], segment[1]));
            } else if (segType == PathIterator.SEG_LINETO) {
                curSeg.add(inclusion(segment[0], segment[1]));
            }
            pi.next();
        }
        for(Point3D[] part : pathParts)
            sg.drawPath(part);
    }
}
