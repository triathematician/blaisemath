/*
 * SpaceBox.java
 * Created 2009
 */

package visometry.space;

import java.util.ArrayList;
import primitive.GraphicMesh;
import primitive.style.MeshStyle;
import scio.coordinate.Point3D;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;

/**
 * This is a "demo" class designed to test out the 3d visometry, consisting of a collection
 * of boxes in the first octant.
 * @author Elisha Peterson
 */
public class SpaceBox extends Plottable<Point3D> {

    /** Construts the eight cubes */
    public SpaceBox() {
        MeshStyle style = new MeshStyle();
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.15, .15, .15), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.55, .15, .15), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.15, .55, .15), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.15, .15, .55), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.55, .55, .15), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.55, .15, .55), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.15, .55, .55), .3, .3, .3), style));
        addPrimitive(new VPrimitiveEntry(getBox(new Point3D(.65, .65, .65), .3, .3, .3), style));
    }

    /** Returns a mesh object representing a cube with specified dimensions */
    public GraphicMesh<Point3D> getBox(Point3D base, double l, double w, double h) {
        Point3D[] points = new Point3D[] {
            base, base.plus(l,0,0), base.plus(0,w,0), base.plus(0,0,h),
            base.plus(l,w,0), base.plus(l,0,h), base.plus(0,w,h), base.plus(l,w,h)
        };
        int[][] edges = new int[][] { {0,1}, {0,2}, {0,3}, {1,4}, {1,5}, {2,4}, {2,6}, {3,5}, {3,6}, {4,7}, {5,7}, {6,7} };
        int[][] areas = new int[][] { {0,1,4,2}, {0,2,6,3}, {0,3,5,1}, {1,4,7,5}, {2,6,7,4}, {3,5,7,6} };
        ArrayList<int[]> areaList = new ArrayList<int[]>();
        for (int[] a : areas) areaList.add(a);
        return new GraphicMesh<Point3D>( points, edges, areaList );
    }



}
