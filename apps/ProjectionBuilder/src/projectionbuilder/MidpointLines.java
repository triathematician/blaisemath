/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package projectionbuilder;

import scio.coordinate.Point3D;

/**
 *
 * @author ae3263
 */
public class MidpointLines {

    /**
     * Returns the point equidistant between the two provided lines. If the lines
     * intersect, this is the point of intersection. If they do not, it is the
     * midpoint of the segment connecting the closest two lines. If the lines are
     * paralle, returns halfway between the first two provided points.
     * @param pt11 first point on the first line
     * @param pt12 second point on the first line
     * @param pt21 first point on the second line
     * @param pt22 second point on the second line
     * @return the midpoint
     */
    static public Point3D midPointWithPoints(Point3D pt11, Point3D pt12, Point3D pt21, Point3D pt22) {
        return midpointWithDir(
                pt11,
                pt12.minus(pt11),
                pt21,
                pt22.minus(pt21) );
    }

    /**
     * Returns the point equidistant between the two provided lines. If the lines
     * intersect, this is the point of intersection. If they do not, it is the
     * midpoint of the segment connecting the closest two lines. If the lines are
     * paralle, returns halfway between the first two provided points.
     * @param pt1 a point on the first line
     * @param dir1 direction vector of the first line
     * @param pt2 a point on the second line
     * @param dir2 direction vector of the second line
     * @return the midpoint
     */
    static public Point3D midpointWithDir(Point3D pt1, Point3D dir1, Point3D pt2, Point3D dir2) {
        Point3D cross = dir1.crossProduct(dir2);
        double nCross = cross.magnitudeSq();
        if (nCross == 0)
            return new Point3D(
                    .5 * (pt1.x + pt2.x),
                    .5 * (pt1.y + pt2.y),
                    .5 * (pt1.z + pt2.z) );
        Point3D xDelta = pt2.minus(pt1);
        double s = cross.dotProduct(xDelta.crossProduct(dir2)) / nCross;
        double t = cross.dotProduct(xDelta.crossProduct(dir1)) / nCross;
        return new Point3D(
                .5 * (pt1.x + pt2.x + s * dir1.x + t * dir2.x),
                .5 * (pt1.y + pt2.y + s * dir1.y + t * dir2.y),
                .5 * (pt1.z + pt2.z + s * dir1.z + t * dir2.z) );
    }

}
