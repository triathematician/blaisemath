/*
 * Voronoi2D.java
 * Created on Jun 2, 2008
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Map.Entry;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import scio.coordinate.R2;
import scio.graph.Graph;
import scio.random.Random2D;
import scio.voronoi.VoronoiCells;
import sequor.style.LineStyle;
import specto.Plottable;
import sequor.style.PointStyle;

/**
 * <p>
 * Voronoi2D is a visualization of a Voronoi tesselation.
 * </p>
 * @author Elisha Peterson
 */
public class Voronoi2D extends DynamicPointSet2D {
    
    VoronoiCells tesselation;
    PointSet2D tessPoints;
    DynamicPointSet2D hull;

    public Voronoi2D() {
        setName("Voronoi");
        tessPoints = new PointSet2D();
        tessPoints.style.setValue(LineStyle.POINTS_ONLY);
        tessPoints.setColorModel(this.getColorModel());
        hull = new DynamicPointSet2D();
        hull.style.setValue(STYLE_FILLED_NO_POINTS);
        hull.setColor(Color.PINK);
        style.setValue(STYLE_POINTS_ONLY);
        
        for (int i = 0; i < 60; i++) { add(Random2D.rectangle(-5,-5,5,5)); }
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
        hull.setPath(tesselation.hull);
    }    

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        hull.paintComponent(g, v);
        super.paintComponent(g, v);
        tessPoints.paintComponent(g, v);
        g.setColor(getColor());
        for (Graph.Edge e : tesselation.connections.getEdges()) {
            R2 p1 = ((VoronoiCells.Equidistant)e.getSource()).point;
            R2 p2 = ((VoronoiCells.Equidistant)e.getSink()).point;
            g.draw(v.lineSegment(p1, p2));
        }
        for (Entry e : tesselation.spokes.entrySet()) {
            g.draw(v.ray((R2) e.getKey(), (Double) e.getValue(), 3.0));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
    }
    
    
}
