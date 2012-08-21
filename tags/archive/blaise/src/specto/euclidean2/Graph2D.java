/**
 * Graph2D.java
 * Created on Nov 11, 2008
 */

package specto.euclidean2;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import scio.coordinate.R2;
import scio.graph.Graph;
import scio.graph.Graph.Edge;
import sequor.component.RangeTimer;

/**
 * <p>
 * This class represents a standard graph G=(V,E), displaying edges and vertices.
 * For the moment, the points will be aligned around a circle with the edges in the middle.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class Graph2D extends DynamicPointSet2D {
    Graph<? extends Object> graph;
    HashMap<Object,Point2D> locator;
    
    public Graph2D() {
        Graph<Integer> g = new Graph<Integer>();
        g.addEdge(0,3);
        g.addEdge(2,4);
        g.addEdge(0,4);
        g.addEdge(2,3);
        g.addEdge(1,4);
        g.addEdge(1,5);
        setGraph(g);
        style.setValue(STYLE_POINTS_ONLY);
    }
    
    public Graph2D(Graph<?> graph){
        setGraph(graph);
        style.setValue(STYLE_POINTS_ONLY);
    }  
    
    public void setGraph(Graph<?> graph) {
        clear();
        this.graph = graph;
        locator = new HashMap<Object,Point2D>();
        HashSet vertices = graph.getVertices();
        int n = vertices.size();
        int i = 0;
        for (Object v : vertices) {
            locator.put(v, new Point2D(R2.getPolar(1,i*2*Math.PI/(double)n)));
            i++;
        }
        for (Point2D p : locator.values()) {
            add(p);
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        super.paintComponent(g, v);
        for(Edge e : graph.getEdges()) {
            g.draw(v.lineSegment(locator.get(e.getSource()).getPoint(), locator.get(e.getSink()).getPoint()));
        }
    }

    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
        super.paintComponent(g, v, t);
        for(Edge e : graph.getEdges()) {
            g.draw(v.lineSegment(locator.get(e.getSource()).getPoint(), locator.get(e.getSink()).getPoint()));
        }
    }
}
