/*
 * PlaneGraph.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.HashSet;
import org.bm.blaise.scio.graph.NeighborSetInterface;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *  A collection of vertices with edges between the vertices.
 * </p>
 * @author ae3263
 */
public class PlaneGraph extends VPointSet<Point2D.Double> {

    PathStyle edgeStyle = new PathStyle(Color.GREEN);
    NeighborSetInterface nsi;

    public PlaneGraph(NeighborSetInterface nsi) {
        super(new Point2D.Double[]{});
        this.nsi = nsi;
        setup();
    }

    public PathStyle getEdgeStyle() {
        return edgeStyle;
    }

    public void setEdgeStyle(PathStyle edgeStyle) {
        this.edgeStyle = edgeStyle;
    }

    private void setup() {
        values = new Point2D.Double[nsi.getSize()];
        int n = nsi.getSize();
        for (int i = 0; i<n; i++) {
            values[i] = new Point2D.Double(5.0*Math.cos(2*Math.PI*i/n), 5.0*Math.sin(2*Math.PI*i/n));
        }
    }

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        if (values.length != nsi.getSize()) {
            setup();
        }
        super.paintComponent(vg);
        vg.setPathStyle(edgeStyle);
        int i = 0;
        int j = 0;
        for (Object o1 : nsi) {
            j = 0;
            for (Object o2 : nsi) {
                if (nsi.adjacent(o1, o2)) {
                    vg.drawSegment(getValue(i), getValue(j));
                }
                j++;
            }
            i++;
        }
    }

    public static class TestNSI extends HashSet<Integer> implements NeighborSetInterface<Integer> {

        public TestNSI() {
            for (int i = 0; i < 10; i++) {
                add(i);
            }
        }

        public int getSize() {
            return size();
        }

        public boolean adjacent(Integer v1, Integer v2) {
            // return true if common factors are shared
            int i1 = Math.abs(v1+1);
            int i2 = Math.abs(v2+1);
            int i = Math.min(i1, i2);
            while (i1%i!=0 || i2%i!=0) i--;
            return i == 1;
        }

    };
}
