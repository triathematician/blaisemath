/**
 * Cells.java
 * Created on May 27, 2008
 */

package scio.voronoi;

import java.util.HashSet;
import java.util.Vector;
import scio.coordinate.R2;

/**
 * This class takes in several points and generates the Voronoi cells, consisting
 * of the set of points closest to each in the set of cells.
 * @author Elisha Peterson
 */
public class Cells {

    public void compute(Vector<R2> points) {
        Vector<Equidistant> eqs = new Vector<Equidistant>();
        
        // Step 1: generate set of equidistant points
        for (R2 p : points) {
            for (R2 q : points) {
                if (p.equals(q)) { continue; }
                for (R2 r : points) {
                    if (p.equals(r) || q.equals(r)) { continue; }
                    // change this to point equidistant from p/q/r
                    eqs.add(new Equidistant(p, q, r, new R2()));
                }
            }
        }
        
        // Step 2: toss unnecessary points        
        HashSet<Equidistant> remove = new HashSet<Equidistant>();
        for (Equidistant ep : eqs) {
            for (R2 p : points) { 
                if (ep.closer(p)) { remove.add(ep); }
            }
        }
        
        // Step 3: construct graph with connections supplied by equidistant points
        
        // Step 4: add rays
    }
    
    // helper inner class
    public static class Equidistant {
        public R2 p1;
        public R2 p2;
        public R2 p3;
        public R2 point;
        double distance;

        public Equidistant(R2 p1, R2 p2, R2 p3, R2 point) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.point = point;
            distance = p1.distance(point);
        }
        
        public boolean closer(R2 p) {
            return (p.distance(point) < distance);
        }
    }
    
}
