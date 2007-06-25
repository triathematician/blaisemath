package scio.algebra.planar;

import scio.graph.*;
import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;

/**
 * <b>GraphGroupElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 9:20 AM</i><br><br>
 *
 * Wrapper class which implements a basic graph operation (appending two graphs together).
 * Specialized graph implementations should almost always override validEdge/actLeft.
 * The actual graph is stored in the "Graph" class, as an ordered collection of edges.
 */
public class GraphGroupTerm extends GroupElementId{
    protected Graph g;
    public GraphGroupTerm(){g=new Graph();}
    public GraphGroupTerm(Graph g){if(valid(g)){this.g=new Graph(g);}}
    
    public Graph getGraph(){return g;}
    
    /** Fundamental validity check! */
    public boolean validEdge(int a,int b,int w){return true;}
    
    /** Fundamental group action! Defaults to gluing together two graphs (appending). */    
    public GroupElementId actLeft(GroupElement x){
        GraphGroupTerm ge=new GraphGroupTerm(g);
        ge.g.glueTo(((GraphGroupTerm)x).g,null);
        if(!valid(ge.g)){return null;}
        return ge;
    }
        
    /** Adding edges requires validity checks... override these methods to
     * limit the structure of the graph. */
    public boolean valid(Graph g){for(Edge e:g){if(!validEdge(e))return false;}return true;}
    public boolean validEdge(Edge e){return validEdge(e.getSource(),e.getSink(),e.getWeight());}
    public boolean validEdge(int a,int b){return validEdge(a,b,1);}
    public void addEdge(int a,int b){addEdge(a,b,1);}
    public void addEdge(int a,int b,int w){if(validEdge(a,b,w)){g.addEdge(a,b,w);}}
    
    /** Methods inherited from the group structure. */
    public static boolean isCommutative(){return true;}
    public static GroupElementId getIdentity(){return new GraphGroupTerm();}
    public GroupElementId getInverse(){return null;}

    public int compareTo(Object o){return 0;}
}
