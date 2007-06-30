package zzDeprecated;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * <b>Graph.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 15, 2007, 1:06 PM</i><br><br>
 *
 * Implements a graph=(V,E). Edges are assumed to be directed, and there may
 *   be several of the same category. It is easy to get the vertices adjacent to
 *   an edge, and a hashtable is implemented to easily get the edges adjacent to a
 *   vertex.
 */
public class Graph {
    /** Each pair of vertices is an edge. */
    public class Edge {
        Integer from,to;
        public Edge(Integer i,Integer j){from=i;to=j;}
        public boolean adjacentTo(int i){return (from==i||to==i);}
        public boolean startsAt(int i){return from==i;}
        public boolean endsAt(int i){return to==i;}
        public void shiftUp(int amount){from+=amount;to+=amount;}
    }
    /** The vertices */
    public HashSet<Integer> v;
    public int vertices(){return v.size();}
    public int maxVertex(){return Collections.max(v);}
    /**
     * The edges... parametrized by pairs of points. The object E is available
     *   to classify and/or label the edges if necessary. Hence the edge pairings
     *   are really just the set of key values. E could be null perhaps?
     */
    public Vector<Edge> e;
    /**
     * Lookup table to get edges incident to vertices. vs identifies edges
     *   starting at a vertex, ve edges ending there.
     */
    public HashMap<Integer,Vector<Edge>> vs,ve;
    
    /**
     * Constructor: creates a new instance of Graph
     */
    public Graph(){
        v=new HashSet<Integer>();
        e=new Vector<Edge>();
        vs=new HashMap<Integer,Vector<Edge>>();
        ve=new HashMap<Integer,Vector<Edge>>();
    }
    
    /** Adds edge between two vertices. Also updates the vertex hash tables. */
    public boolean addEdgeBetween(Integer i,Integer j){
        if(canAddEdge(i,j)){
            v.add(i);v.add(j);
            Edge edge=new Edge(i,j);
            e.add(edge);
            addAdjacency(edge);
            return true;
        }
        return false;
    }
    
    /** Adds an edge to the hash tables. */
    public void addAdjacency(Edge edge){
        if(vs.get(edge.from)==null){vs.put(edge.from,new Vector<Edge>());}
        vs.get(edge.from).add(edge);
        if(ve.get(edge.to)==null){vs.put(edge.to,new Vector<Edge>());}
        vs.get(edge.to).add(edge);
    }
    
    /** Determines if graph can be modified. Mostly for use in subclasses. */
    public boolean canAddEdge(int i,int j){return true;}
    public boolean canIdentify(int i,int j){return true;}
    public boolean canRemove(int i){return true;}
    
    /** Gets edges adjacent to the given vertex. */
    public Vector<Edge> adjacentTo(int i){
        Vector<Edge> ea=new Vector<Edge>();
        ea.addAll(vs.get(i));
        ea.addAll(ve.get(i));
        return ea;
    }
    public Vector<Integer> adjacentVertices(int i){
        Vector<Integer> vv=new Vector<Integer>();
        for(Edge edge:vs.get(i)){vv.add(edge.to);}
        for(Edge edge:ve.get(i)){vv.add(edge.from);}
        return vv;
    }
    public int degree(int i){return adjacentTo(i).size();}
    public boolean leaf(int i){return degree(i)==1;}
    public boolean empty(int i){return degree(i)==0;}
    /** Gets edges starting at the given vertex. */
    public Vector<Edge> startAt(int i){return vs.get(i);}
    public int degreeOut(int i){return vs.get(i).size();}
    public boolean sink(int i){return (!empty(i)&&degreeOut(i)==0);}
    public Vector<Integer> sinks(){
        Vector<Integer> vv=new Vector<Integer>();
        for(Integer i:v){if(sink(i)){vv.add(i);}}
        return vv;
    }
    /** Gets edges ending at the given vertex. */
    public Vector<Edge> endAt(int i){return ve.get(i);}
    public int degreeIn(int i){return ve.get(i).size();}
    public boolean source(int i){return (!empty(i)&&degreeIn(i)==0);}
    public Vector<Integer> sources(){
        Vector<Integer> vv=new Vector<Integer>();
        for(Integer i:v){if(source(i)){vv.add(i);}}
        return vv;
    }
    
    public boolean through(int i){return (degreeOut(i)>0&&degreeIn(i)>0);}
    
    /**
     * Returns integers contained in connected component of the given vertex.
     *   Currently not very efficient.
     */
    public HashSet<Integer> componentOf(Integer i){
        HashSet<Integer> component=new HashSet<Integer>();
        component.add(i);
        component.addAll(adjacentVertices(i));
        int sizeOld=component.size();
        int sizeNew;
        do{
            for(Integer j:component){component.addAll(adjacentVertices(j));}
            sizeNew=component.size();
        }while(sizeNew>sizeOld);
        return component;
    }
    
    /** Determines if the graph is connected. */
    public boolean connected(){return (componentOf(v.iterator().next()).size()==vertices());}
    
    /**
     * Adds extra vertices at the beginning of the graph, and shifts all edges.
     * Consequently resets the hash table.
     */
    public void shiftUp(int amount){
        for(Integer i:v){i+=amount;}
    }
    
    /** Reduces a graph by subtracting out all vertices which are not used. */
    public void removeEmptyVertices(){
        Vector<Integer> vv=new Vector<Integer>();
        for(Integer i:vv){if(empty(i)){vv.add(i);}}
        removeVertices(vv);
    }
    
    /** Removes a given vertex if possible. */
    public boolean removeVertex(Integer i){
        if(canRemove(i)){
            for(Edge edge:vs.get(i)){edge=null;}
            for(Edge edge:ve.get(i)){edge=null;}
            while(e.remove(null)){}
            vs.remove(i);
            ve.remove(i);
            v.remove(i);
            return true;
        }
        return false;
    }
    public boolean removeVertices(Vector<Integer> vv){for(Integer i:vv){if(!removeVertex(i)){return false;}}return true;}
    
    /** Identifies two vertices, and removes the second. */
    public boolean identify(Integer v1,Integer v2){
        if(!canIdentify(v1,v2)){return false;}
        for(Edge edge:vs.get(v2)){edge.from=v1;}
        for(Edge edge:ve.get(v2)){edge.to=v1;}
        vs.get(v1).addAll(vs.get(v2));
        ve.get(v1).addAll(ve.get(v2));
        return removeVertex(v2);
    }
    /** Identifies all vertices in a set to the same element. */
    public boolean identify(Vector<Integer> vv){
        Integer commonVertex=vv.firstElement();
        vv.remove(commonVertex);
        for(Integer i:vv){if(!identify(commonVertex,i)){return false;}}
        return true;
    }
    /** Identifies vertices in one set with vertices in another set, by position. */
    public boolean identify(Vector<Integer> vv1,Vector<Integer> vv2){
        if(vv1.size()!=vv2.size()){return false;}
        for(int i=0;i<vv1.size();i++){if(!canIdentify(vv1.get(i),vv2.get(i))){return false;}}
        return true;
    }
    
    /**
     * Identifies vertices v1 in lg1 with vertices v2 in lg2, provided the vertices
     *   have elements with compatible orientations!
     */
    public static Graph Glue(Graph g1,Vector<Integer> v1,Graph g2,Vector<Integer> v2){
        if(v1.size()!=v2.size()){return null;}
        Graph g=new Graph();
        for(Edge edge:g1.e){g.addEdgeBetween(edge.from,edge.to);}
        for(Edge edge:g2.e){g.addEdgeBetween(edge.from+g1.maxVertex(),edge.to+g1.maxVertex());}
        if(!g.identify(v1,v2)){return null;}
        return g;
    }
}

/** Restricts to loops (vertices have just one edge. */
class LoopGraph extends Graph {
    public LoopGraph(){super();}
    /** Only permits one in edge, one out edge at each vertex. */
    public boolean canAddEdge(int i,int j){return (sink(i)&&source(j));}
    public boolean canIdentify(int i,int j){return (degreeIn(i)+degreeIn(j)<2&&degreeOut(i)+degreeOut(j)<2);}
    public boolean canRemove(int i){return true;}
    
    /** Returns edge starting at a point, or null if there is none. */
    public Edge from(int i){if(sink(i)){return null;} return vs.get(i).firstElement();}
    /** Edge ending at a point, or null if there is none. */
    public Edge to(int i){if(source(i)){return null;} return ve.get(i).firstElement();}
    
    /** Returns path through the given vertex. Stops if path returns to vertex. */
    public Vector<Integer> path(int i){
        Vector<Integer> path=new Vector<Integer>();
        path.add(i);
        if(sink(i)){return path;}
        // looks forward
        int j=i;
        do{j=from(j).to;path.add(j);}while(degreeOut(j)==1&&!(j==i));
        if(path.lastElement()==i){return path;}
        // looks backward
        j=i;
        do{j=to(j).from;path.add(0,j);}while(degreeIn(j)==1);
        return path;
    }
    /** Returns sink point of path through a given vertex. */
    public Integer pathEnd(int i){return path(i).lastElement();}
    /** Returns source point of path through a given vertex. */
    public Integer pathBegin(int i){return path(i).firstElement();}
    
    /** Returns all paths in a loop graph. */
    public Vector<Vector<Integer>> allPaths(){
        Vector<Vector<Integer>> paths=new Vector<Vector<Integer>>();
        Vector<Integer> usedVertices=new Vector<Integer>();
        for(Integer i:v){
            if(!usedVertices.contains(i)){
                Vector<Integer> path=path(i);
                paths.add(path);
                usedVertices.addAll(path);
            }
        }
        return paths;
    }
}