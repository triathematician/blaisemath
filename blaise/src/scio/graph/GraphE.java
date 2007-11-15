package scio.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

/**
 * <b>Graph.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>April 30, 2007, 12:11 PM</i><br><br>
 *
 * Stores a graph as a sorted set of edges (sorted first by source vertex and then
 * by sink vertex). The vertex is implicitly stored in the set of edges... although
 * I may later change this.
 * Trivial loops (an edge adjacent to no vertices) are allowed and stored as an edge from -1 to -1.
 */
public class GraphE extends TreeSet<Edge>{
    /** Global settings... do weights represent multi-edges or labels? */
    public boolean multiEdge=true;
    public boolean directed=true;
    
    /**
     * Constructor: creates a new instance of Graph; defaults to zero vertices
     */
    public GraphE(){super();}
    public GraphE(GraphE g){addAll(g);}
    
    public GraphE clone(){
        GraphE g=new GraphE();
        g.multiEdge=this.multiEdge;
        g.directed=this.directed;
        for(Edge e:this){g.addEdge(e.getSource(),e.getSink(),e.getWeight());}
        return g;
    }
    
    // Routines which change the structure of the graph
    
    /** Returns edge class between two specified labels. */
    public Edge getEdge(int a,int b){
        for(Edge e:this){if(e.equals(a,b)){return e;}}
        return null;
    }
    /** Adds an edge. All add/delete edge methods should be funnelled through addEdge(a,b,w). */
    public boolean addTrivialLoop(){return addEdge(-1,-1,1);}
    public void deleteTrivialLoops(){removeVertex(-1);}
    public boolean addEdge(Edge e){return addEdge(e.v0,e.v1,e.weight);}
    public boolean addEdge(int a,int b){return addEdge(a,b,1);}
    /** Workhorse method. */
    public boolean addEdge(int a,int b,int w){
        /** If undirected, ensure a<b.*/
        if(a>b&&!directed){int c=a;a=b;b=c;}
        /** When we want to just add on to the number of edges. */
        if(multiEdge||(a==-1&&b==-1)){
            if(a<0||b<0){a=-1;b=-1;}
            Edge e=getEdge(a,b);
            if(e==null){if(w<=0)return false;return add(new Edge(a,b,w));}
            if(!e.addWeight(w)){this.remove(e);}
            return true;
        }
        /** Default returning plain and simple new edge. */
        return add(new Edge(a,b,w));
    }
    /** Removes an edge from the graph. */
    public void removeEdge(int ev0,int ev1){remove(getEdge(ev0,ev1));}
    public void removeEdge(int ev0,int ev1,int w){
        if(multiEdge){addEdge(ev0,ev1,0-w);}else{this.remove(new Edge(ev0,ev1,w));}
    }
    
    // Views of the vertex set
    
    /** Returns list of vertices as an (ordered) TreeSet. Will not contain -1. */
    public TreeSet<Integer> getVertices(){
        TreeSet<Integer> vertices=new TreeSet<Integer>();
        for(Edge e:this){vertices.add(e.v0);vertices.add(e.v1);}
        vertices.remove(-1);
        return vertices;
    }
    /** Returns the number of distinct vertices of the graph */
    public int getNumVertices(){return getVertices().size();}
    /** Returns the maximum vertex number of the graph */
    public int getMaxVertex(){if(getVertices().size()==0){return 0;}return getVertices().last();}
    /** Returns the number of trivial loops. */
    public int getNumTrivialLoops(){return getLoopsAt(-1);}
    
    // String output of the graph
    
    /** Returns a string containing the (ordered) list of edges */
    public String toString(){
        String s="{";
        if(getNumTrivialLoops()>0){s+="("+getNumTrivialLoops()+")";}
        for(Edge e:this){if(!e.contains(-1)){s+=e.toString(multiEdge,directed);}}
        return s+"}";
    }
    
    // Properties of the graph
    
    /** Counts number of edges in the graph. */
    public int edgeCount(){
        int i=0;
        for(Edge e:this){i+=multiEdge?e.getWeight():1;}
        return i;
    }
    
    /** Determines if the graph is connected. */
    public boolean isConnected(){
        return getComponent(this.first().getSource()).edgeCount()==edgeCount();
    }
    
    /** Determines if the graph is a loop or collection of loops (all vertices have valency two). */
    public boolean isLoops(){
        for(Integer i:getVertices()){if(getValency(i)!=2){return false;}}
        return true;
    }
    
    // Local operations on the graph
    
    /** Removes a vertex from the graph, and all edges incident to it. */
    public void removeVertex(int v){
        TreeSet<Edge> removals=new TreeSet<Edge>();
        for(Edge e:this){if(e.contains(v))removals.add(e);}
        removeAll(removals);
    }
    /** Glues two vertices together... replaces all v1's with v2's. Returns the removed element (v1). */
    public int glueVertices(int v1,int v2){
        GraphE gnb2=getNeighborhood(v1);
        this.removeAll(gnb2);
        for(Edge e:gnb2){e.relabel(v1,v2);addEdge(e);}
        return v1;
    }
    /** Returns the number of loops to a given vertex. */
    public int getLoopsAt(int v){float i=0;for(Edge e:this){i+=e.weightedEquals(v,v);}return ((Float)i).intValue();}
    /** Returns the valency of a given vertex. */
    public int getValency(int v){
        if(v==-1)return 0;
        int i=0;
        for(Edge e:getNeighborhood(v)){
            if(e.isLoop()){i+=multiEdge?e.getWeight():1;}
            i+=multiEdge?e.getWeight():1;
        }
        return i;
    }
    /** Returns closed neighborhood of a vertex. */
    public GraphE getNeighborhood(int v){
        GraphE g=new GraphE();
        for(Edge e:this){if(e.contains(v))g.add(e);}
        return g;
    }
    
    /** Returns connected component of a vertex. Works recursively, by "growing" the list of
     * vertices contained in the component. Concurrently places all edges
     */
    public GraphE getComponent(int v){
        // stores the edges not part of the component
        GraphE unused=new GraphE(this);
        // stores the edges contained in the component
        GraphE used=new GraphE();
        // stores all vertices in the component
        TreeSet<Integer> vertices=new TreeSet<Integer>();
        // stores vertices added on during each loop
        TreeSet<Integer> newVertices=new TreeSet<Integer>();
        // start with a single vertex
        newVertices.add(v);
        do{
            // first, add on neighborhoods of all new vertices
            for(Integer i:newVertices){used.addAll(unused.getNeighborhood(i));}
            // remove those edges from the starting graph
            unused.removeAll(used);
            // reset the integers stored in newVertices/vertices collections
            newVertices.addAll(used.getVertices());
            newVertices.removeAll(vertices);
            vertices.addAll(newVertices);
        }while(newVertices.size()>0);
        return used;
    }
    /** Returns *all* components of a graph, as a list of graphs. */
    public ArrayList<GraphE> getAllComponents(){
        ArrayList<GraphE> result=new ArrayList<GraphE>();
        TreeSet<Integer> verticesUnused=new TreeSet<Integer>();
        verticesUnused.add(-1);verticesUnused.addAll(getVertices());
        GraphE remainingGraph=new GraphE(this);
        while(verticesUnused.size()>0){
            int i=verticesUnused.first();
            GraphE gComponent=remainingGraph.getComponent(i);
            //System.out.println("i="+i+"; Graph="+gComponent.toString());
            remainingGraph.removeAll(gComponent);
            verticesUnused.clear();
            verticesUnused.addAll(remainingGraph.getVertices());
            result.add(gComponent);
        }
        return result;
    }
    /** Returns all components as lists of integers (starting at lowest number), provided the graph is a collection of loops. */
    public ArrayList<ArrayList<Integer>> getLoops(){
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        for(GraphE g:getAllComponents()){
            if(!g.isLoops()){continue;}
            TreeSet<Integer> v=g.getVertices();
            ArrayList<Integer> r=new ArrayList<Integer>();
            if(v.size()==0){continue;}
            r.add(v.first());
            for(int i=1;i<v.size();i++){
                TreeSet<Integer> adj=g.getAdjacency(r.get(i-1));
                if(i==1){r.add(adj.first());}
                else{if(r.get(i-2)==adj.first()){r.add(adj.last());}else{r.add(adj.first());}}
            }
            result.add(r);
        }
        return result;
    }
    
    /** Contracts an edge in the graph (glues endpoints together & removes the edge). */
    public int contractEdge(int ev0,int ev1){
        removeEdge(ev0,ev1);
        if(ev0<ev1){return glueVertices(ev1,ev0);}
        return glueVertices(ev0,ev1);
    }
    
    
    // Global operations on the graph
    
    /** Gets a list of integers adjacent to a given vertex. */
    public TreeSet<Integer> getAdjacency(int v){
        TreeSet<Integer> result=new TreeSet<Integer>();
        result.addAll(getNeighborhood(v).getVertices());
        result.remove(v);
        return result;
    }
    /** Populates a mapping from each vertex to all adjacent vertices. */
    public TreeMap<Integer,TreeSet<Integer>> getAllAdjacencies(){
        TreeMap<Integer,TreeSet<Integer>> result=new TreeMap<Integer,TreeSet<Integer>>();
        result.put(-1,new TreeSet<Integer>());
        for(int i:getVertices()){result.put(i,new TreeSet<Integer>());}
        for(Edge e:this){
            //System.out.println(result.toString());
            result.get(e.getSource()).add(e.getSink());
            result.get(e.getSink()).add(e.getSource());
        }
        return result;
    }
    
    /** Reduces the vertex label set to remove gaps */
    public void relabelVertices(){
        TreeSet<Integer> vertices=getVertices();
        Iterator<Integer> vi=vertices.iterator();
        TreeMap<Integer,Integer> mapping=new TreeMap<Integer,Integer>();
        mapping.put(-1,-1);
        for(int i=1;i<=vertices.size();i++){mapping.put(vi.next(),i);}
        for(Edge e:this){e.relabel(mapping);}
    }
    /** Adds a constant to the set of labels */
    public void addToLabels(int n){for(Edge e:this){if(!e.isTrivial()){e.addToLabel(n);}}}
    /** Adds a constant n to the set of labels above i */
    public void addToLabelsAbove(int n,int i){for(Edge e:this){if(!e.isTrivial()){e.addToLabelAbove(n,i);}}}
    /** Reverses order of graph edges. */
    public void reverseEdges(){for(Edge e:this){e.reverse();}}
    /** Reverses numbering of the vertices */
    public void reverseLabels(){
        Iterator<Integer> verts=getVertices().iterator();
        Iterator<Integer> newVerts=getVertices().descendingSet().iterator();
        TreeMap<Integer,Integer> mapping=new TreeMap<Integer,Integer>();
        mapping.put(-1,-1);
        for(int i=1;i<=getVertices().size();i++){mapping.put(verts.next(),newVerts.next());}
        for(Edge e:this){e.relabel(mapping);}
    }
    /** Finds (first) vertex of the specified degree. Returns 0 if there are none. */
    public int getVertexOfDegree(int d){
        for(Integer i:getVertices()){if(getValency(i)==d){return i;}}
        return 0;
    }
    /** Contracts all vertices of degree two. Uses recursion. Stops when number of edges is constant.
     * If the graph is directed, requires compatibility of orientation.
     */
    public void contractDegreeTwo(){
        int i;
        int ec=0;
        GraphE tempNbhd=new GraphE();
        while(ec!=edgeCount()){
            ec=edgeCount();
            i=getVertexOfDegree(2);
            if(i==0){return;}
            if(getNeighborhood(i).getNumVertices()==1){
                removeVertex(i);
                addEdge(-1,-1);
            } else {
                int newSource=0;
                int newSink=0;
                for(Edge e:this){
                    if(e.startsAt(i)){newSink=e.getSink();}
                    if(e.endsAt(i)){newSource=e.getSource();}
                }
                removeVertex(i);
                addEdge(newSource,newSink);
            }
        }
    }
    /** Glues two graphs together using the specified map (pairing of vertex labels). The keys are the
     * values in g2 that will be mapped over. Returns amount added to second graph before gluing.
     * Map keys are elements of Graph g, values are elements of this graph.
     */
    public int glueTo(GraphE g,Map<Integer,Integer> map){
        GraphE g2=(GraphE)g.clone();
        int plus=getMaxVertex();
        g2.addToLabels(plus);
        for(Edge e:g2){addEdge(e);}
        if(map!=null){for(int key:map.keySet()){glueVertices(key+plus,map.get(key));}}
        return plus;
    }
    /** Glues two graphs together using the specified map (pairing of vertex labels).
     *  The first set of integers are the values in g2, the second the values in the current graph. */
    public int glueTo(GraphE g2,ArrayList<Integer> a,ArrayList<Integer> b){
        TreeMap<Integer,Integer> map=new TreeMap<Integer,Integer>();
        Iterator<Integer> ia=a.iterator();
        Iterator<Integer> ib=b.iterator();
        while(ia.hasNext()&&ib.hasNext()){map.put(ia.next(),ib.next());}
        return glueTo(g2,map);
    }
    
    
    // PROCEDURES YET TO IMPLEMENT
    
    /** Performs surgery operation */
    public GraphE surgery(GraphE x){return null;}
    /** Performs closing operation */
    public GraphE closeOff(){return null;}
    
    // Return views of the graph as a matrix
    
    /** Returns the *ordered* & weighted matrix representation of the graph */
    public float[][] getWeightedMatrix(){
        int n=getMaxVertex();
        float[][] matrix=new float[n][n];
        for(Edge e:this){matrix[e.getSource()][e.getSink()]=e.getWeight();}
        return matrix;
    }
    /** Returns the *ordered* matrix representation (just 0's and 1's) */
    public float[][] getOrderedMatrix(){
        int n=getMaxVertex();
        float[][] matrix=new float[n][n];
        for(Edge e:this){matrix[e.getSource()][e.getSink()]=1;}
        return matrix;
    }
    /** Returns the *unordered* matrix representation (just 0's and 1's) */
    public float[][] getUnOrderedMatrix(){
        int n=getMaxVertex();
        float[][] matrix=new float[n][n];
        for(Edge e:this){matrix[e.getSource()][e.getSink()]=1;matrix[e.getSink()][e.getSource()]=1;}
        return matrix;
    }
    
}
