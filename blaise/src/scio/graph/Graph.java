/*
 * Graph.java
 * Created on Oct 16, 2007, 1:12:27 PM
 */

package scio.graph;

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
    HashSet<Edge<V>> edges;
    
    public void addEdge(V v1,V v2){
        edges.add(new Edge<V>(v1,v2));
    }
    
    public void removeEdge(V v1,V v2){
        edges.remove(new Edge<V>(v1,v2));
    }
    
    public Graph(){
        super();
        edges=new HashSet<Edge<V>>();
    }
    
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
    }
}
