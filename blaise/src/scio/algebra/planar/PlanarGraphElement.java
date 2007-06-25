package scio.planar;

import java.util.ArrayList;
import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;
import scio.graph.Graph;
import scio.graph.GraphGroupElement;

/**
 * <b>PlanarGraphElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 11:02 AM</i><br><br>
 * 
 * Planar graph elements are simply graphs with subsets (no-repeat lists) of inputs
 * and outputs specified... this provides a canonical way to glue two graphs together!
 */
public class PlanarGraphElement extends GraphGroupElement {
    ArrayList<Integer> inputs;
    ArrayList<Integer> outputs;
    
    /** Default constructor. */
    public PlanarGraphElement(){super();inputs=new ArrayList<Integer>();outputs=new ArrayList<Integer>();}
    public PlanarGraphElement(int n,int m){
        super();
        inputs=new ArrayList<Integer>();for(int i=0;i<n;i++){inputs.add(i);}
        outputs=new ArrayList<Integer>();for(int i=0;i<m;i++){outputs.add(i);}        
    }
    public PlanarGraphElement(Graph g){super(g);inputs=new ArrayList<Integer>();outputs=new ArrayList<Integer>();}
        
    /** Usually not commutative! */
    public static boolean isCommutative(){return false;}
    /** New group action... # of inputs/outputs should be the same, although this
     * is not required. If not, as many elements are glued together as are
     * available.
     */
    public GroupElementId actLeft(GroupElement x){            
        PlanarGraphElement ge=new PlanarGraphElement(g);
        ge.g.glueTo(((PlanarGraphElement)x).g,this.inputs,ge.outputs);
        return ge;
    }
    public static GroupElementId getIdentity(){return new PlanarGraphElement();}
    public static GroupElementId getIdentity(int n){return new PlanarGraphElement(n,n);}
}
