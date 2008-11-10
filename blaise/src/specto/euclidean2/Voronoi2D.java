/*
 * Voronoi2D.java
 * Created on Jun 2, 2008
 */

package specto.euclidean2;

import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import scio.coordinate.R2;
import scio.graph.Graph;
import scio.random.Random2D;
import scio.voronoi.VoronoiCells;
import specto.Plottable;
import specto.euclidean2.Euclidean2;
import specto.style.PointStyle;

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
        setName("Voronoi");
        tessPoints = new DynamicPointSet2D();
        tessPoints.style.setValue(STYLE_POINTS_ONLY);
        for (int i = 0; i < 15; i++) { add(Random2D.rectangle(-15,-15,-10,-10)); }
        add(new R2(-5,-8));
        add(new R2(-7,-6));
        add(new R2(-9,-14));
        add(new R2(-12,-9));
        style.setValue(STYLE_POINTS_ONLY);
    }

    @Override
    public void add(R2 newPoint) {
        Point2D point=new Point2D(newPoint);
        point.style.setValue(PointStyle.LARGE);
        add(point);
    }

    @Override
    public void recompute(Euclidean2 v) {
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
        super.stateChanged(e);
    }
    
    
}
