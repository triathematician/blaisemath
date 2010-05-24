/*
 * GraphicMesh.java
 * Created Apr 8, 2010
 */

package primitive;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;

/**
 * Represents a graphic "mesh" consisting of several points, several connections between the points,
 * and several areas between these points. Useful e.g. for 3D surfaces or for 2D parametric areas.
 *
 * @author Elisha Peterson
 */
public class GraphicMesh<C> {

    /** Points used in the mesh */
    public C[] points;
    /** Segments in the mesh */
    public int[][] segments;
    /** Areas in the mesh */
    public List<int[]> areas;
    /** A float scale associated with each area in the mesh, useful e.g. for highlighting colors. */
    public float[] colors;


    /**
     * Constructs the graphic mesh with specified arguments.
     * @param points the points comprising the mesh
     * @param segments the segments of the mesh; each entry is an array describing the indices of the two points on the segment
     * @param areas the areas defined within the mesh; each entry is an array describing the boundary points of the mesh
     */
    public GraphicMesh(C[] points, int[][] segments, List<int[]> areas) {
        this.points = points;
        this.segments = segments;
        this.areas = areas;
    }

    /** @return the i'th segment in coordinates */
    public Object[] getSegment(int i) {
        return new Object[] { points[segments[i][0]], points[segments[i][1]] };
    }

    /** @return the i'th area in coordinates */
    public Object[] getArea(int i) {
        Object[] result = new Object[areas.get(i).length];
        for (int j = 0; j < result.length; j++)
            result[j] = points[areas.get(i)[j]];
        return result;
    }


    /**
     * Constructs a grid mesh using an array of points
     * @param points a grid of points
     * @param classType the component type of the array
     * @return the mesh
     */
    public static <T> GraphicMesh<T> createGridMesh(T[][] points, Class<? extends T> classType) {
        final int n = points.length;
        final int m = points[0].length;

        // add points
        T[] pts = (T[]) Array.newInstance(classType, n*m);
        int pos = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                pts[pos++] = points[i][j];

        // add segments
        int[][] segments = new int[2*n*m-n-m][2];
        pos = 0;
        for (int i = 0; i < n; i++)
            for (int j = 1; j < m; j++)
                segments[pos++] = new int[] { i*n+j, i*n+j-1 };
        for (int j = 0; j < m; j++)
            for (int i = 1; i < n; i++)
                segments[pos++] = new int[] { i*n+j, (i-1)*n+j };

        // add areas
        List<int[]> areas = new AbstractList<int[]>() {
            @Override
            public int[] get(int index) {
                int j = (index % (m-1)) + 1;
                int i = (index / (m-1)) + 1;
                return new int[] { (i-1)*n+j-1, i*n+j-1, i*n+j, (i-1)*n+j };
            }
            @Override public int size() { return (n-1) * (m-1); }
        };

        return new GraphicMesh<T>(pts, segments, areas);
    }
}
