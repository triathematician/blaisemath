package scio.algebra.planar;

import java.util.ArrayList;
import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;
import scio.graph.GraphE;
import scio.algebra.planar.GraphGroupTerm;

/**
 * <b>PlanarGraphElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 11:02 AM</i><br><br>
 * 
 * Planar graph elements are simply graphs with subsets (no-repeat lists) of inputs
 * and outputs specified... this provides a canonical way to glue two graphs together!
 */
public class PlanarGraphTerm extends GraphGroupTerm {
    ArrayList<Integer> inputs;
    ArrayList<Integer> outputs;
    
    /** Default constructor. */
    public PlanarGraphTerm(){super();inputs=new ArrayList<Integer>();outputs=new ArrayList<Integer>();}
    public PlanarGraphTerm(int n,int m){
        super();
        inputs=new ArrayList<Integer>();for(int i=0;i<n;i++){inputs.add(i);}
        outputs=new ArrayList<Integer>();for(int i=0;i<m;i++){outputs.add(i);}        
    }
    public PlanarGraphTerm(GraphE g){super(g);inputs=new ArrayList<Integer>();outputs=new ArrayList<Integer>();}
        
    /** Usually not commutative! */
    public static boolean isCommutative(){return false;}
    /** New group action... # of inputs/outputs should be the same, although this
     * is not required. If not, as many elements are glued together as are
     * available.
     */
    public GroupElementId actLeft(GroupElement x){            
        PlanarGraphTerm ge=new PlanarGraphTerm(g);
        ge.g.glueTo(((PlanarGraphTerm)x).g,this.inputs,ge.outputs);
        return ge;
    }
    public static GroupElementId getIdentity(){return new PlanarGraphTerm();}
    public static GroupElementId getIdentity(int n){return new PlanarGraphTerm(n,n);}
}
