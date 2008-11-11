/*
 * Graph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */

package scio.graph;

import java.util.Collection;
import java.util.HashSet;

/**
 * An abstract class representing a graph.
 * Implemented as a collection of vertices and edges between those vertices.
 * An abstract data type represents the vertex type.
 * This is just a first crack at a very simple implementation.
 * <br><br>
 * @author ae3263
 */
public class Graph<V> extends HashSet<V>{
    
    /** Stores the set of edges. */
    HashSet<Edge<V>> edges;
    
    /** Constructs with an empty graph, no edges. */
    public Graph(){
        super();
        edges=new HashSet<Edge<V>>();
    }
    
    /** Adds an edge between two of the vertices. */
    public void addEdge(V v1,V v2){
        edges.add(new Edge<V>(v1,v2));
    }
    
    /** Removes an edge between two of the vertices. */
    public void removeEdge(V v1,V v2){
        edges.remove(new Edge<V>(v1,v2));
    }
    
    /** Returns the set of edges. */
    public HashSet<Edge<V>> getEdges() { return edges; }
    
    /** Returns the set of vertices. */
    public HashSet<V> getVertices() { 
        HashSet<V> result = new HashSet<V>();
        for (Edge<V> e : edges) {
            result.add(e.getSink());
            result.add(e.getSource());
        }
        return result;
    }
    
    
    // INNER CLASSES
    
    /** Represents an edge of the given type of vertex. */
    public class Edge<V>{
        public Edge(V v1,V v2){this.v1=v1;this.v2=v2;}
        V v1;
        V v2;
        @Override
        public boolean equals(Object o){
            if(o instanceof Edge){
                return (v1.equals(((Edge<V>)o).v1)&&v2.equals(((Edge<V>)o).v2))
                    ||(v1.equals(((Edge<V>)o).v2)&&v2.equals(((Edge<V>)o).v1));
            }
            return false;
        }
        public V getSource() { return v1; }
        public V getSink() { return v2; }
    }
}
