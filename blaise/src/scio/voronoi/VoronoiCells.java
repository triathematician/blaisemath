/**
 * VoronoiCells.java
 * Created on May 27, 2008
 */

package scio.voronoi;

import java.util.HashSet;
import java.util.Vector;
import scio.coordinate.R2;
import scio.graph.Graph;

/**
 * This class takes in several points and generates the Voronoi cells, consisting
 * of the set of points closest to each in the set of cells.
 * @author Elisha Peterson
 */
public class VoronoiCells {
    
    public Vector<Equidistant> equis;
    public Graph<Equidistant> connections;

    public VoronoiCells(Vector<R2> points) {
        
        // Step 1: generate set of equidistant points
        equis = new Vector<Equidistant>();
        for (int i = 0; i < points.size(); i++) {
            for (int j = i+1; j < points.size(); j++) {
                for (int k = j+1; k < points.size(); k++) {
                    equis.add(new Equidistant(points.get(i), points.get(j), points.get(k)));
                }
            }
        }
        //System.out.println("equis: " + equis);
        
        // Step 2: toss unnecessary points        
        HashSet<Equidistant> remove = new HashSet<Equidistant>();
        for (Equidistant ep : equis) {
            for (R2 pt : points) { 
                if (ep.closerTo(pt)) { remove.add(ep); }
            }
        }
        equis.removeAll(remove);
        
        // Step 3: construct graph with connections supplied by equidistant points
        connections = new Graph<Equidistant>();
        for (int i = 0; i < equis.size(); i++) {
            for (int j = i+1; j < equis.size(); j++) {
                if (equis.get(i).adjacent(equis.get(j))) {
                    connections.addEdge(equis.get(i), equis.get(j));
                }
            }
        }
        //System.out.println("equis: " + equis);
        
        // Step 4: add rays
    }
    
    /** Prints the tesselation. */
    @Override
    public String toString() {
        return equis.toString();
    }
    
    /** Inner class stores the point which is equidistant from three other specified points. */
    public static class Equidistant {
        Vector<R2> points;
        public R2 point;
        double distance;

        public Equidistant(R2 p1, R2 p2, R2 p3) {
            points = new Vector<R2>();
            points.add(p1);
            points.add(p2);
            points.add(p3);
            this.point = R2.threePointCircleCenter(p1, p2, p3);
            distance = p1.distance(point);
        }
        
        /** Returns true if the specified point is within distance of this equidistant point. */
        public boolean closerTo(R2 pt) {
            if (points.contains(pt)) { return false; }
            return pt.distanceTo(point) < distance;
        }
        
        /** Returns true if this point is adjacent to another. */
        public boolean adjacent(Equidistant e2) {
            return (points.contains(e2.points.get(0))) ?
                ( points.contains(e2.points.get(1)) || points.contains(e2.points.get(2)) ) :
                ( points.contains(e2.points.get(1)) && points.contains(e2.points.get(2)) ) ;
        }
        
        /** Returns string representation of the point. */
        @Override
        public String toString() {
            return point.toString();
        }
    }
    
}
