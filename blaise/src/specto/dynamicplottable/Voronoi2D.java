/*
 * Voronoi2D.java
 * Created on Jun 2, 2008
 */

package specto.dynamicplottable;

import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import scio.coordinate.R2;
import scio.graph.Graph;
import scio.voronoi.VoronoiCells;
import scio.voronoi.VoronoiCells.Equidistant;
import specto.Plottable;
import specto.visometry.Euclidean2;

/**
 * <p>
 * Voronoi2D is a visualization of a Voronoi tesselation.
 * </p>
 * @author Elisha Peterson
 */
public class Voronoi2D extends DynamicPointSet2D {
    
    VoronoiCells tesselation;
    DynamicPointSet2D tessPoints;

    public Voronoi2D() {
        tessPoints = new DynamicPointSet2D();
        tessPoints.style.setValue(STYLE_POINTS_ONLY);
        add(new R2(-5,-8));
        add(new R2(-7,-6));
        add(new R2(-9,-14));
        add(new R2(-12,-9));
        style.setValue(STYLE_POINTS_ONLY);
    }

    @Override
    public void add(R2 newPoint) {
        Point2D point=new Point2D(newPoint);
        point.style.setValue(Point2D.LARGE);
        add(point);
    }

    @Override
    public void recompute() {
        Vector<R2> points = new Vector<R2> ();
        for ( Plottable p : plottables ) {
            if ( p instanceof Point2D ) { points.add(((Point2D)p).getPoint()); }
        }
        tesselation = new VoronoiCells (points);
        Vector<R2> eqPoints = new Vector<R2> ();
        for (VoronoiCells.Equidistant eq : tesselation.equis) {
            eqPoints.add(eq.point);
        }
        tessPoints.setPath(eqPoints);        
    }    

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        super.paintComponent(g, v);
        tessPoints.paintComponent(g, v);
        for (Graph.Edge e : tesselation.connections.getEdges()) {
            g.draw(v.lineSegment(((VoronoiCells.Equidistant)e.getSource()).point, ((VoronoiCells.Equidistant)e.getSink()).point));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        recompute();
        System.out.println("state change");
        super.stateChanged(e);
    }
    
    
}
