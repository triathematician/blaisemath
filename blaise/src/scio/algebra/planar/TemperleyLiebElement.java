package scio.algebra.planar;

import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraSummand;
import scio.algebra.GroupElement;
import scio.algebra.permutation.Permutation;
import scio.graph.Edge;

/**
 * <b>TemperleyLiebAlgebra.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 26, 2007, 10:02 AM</i><br><br>
 *
 * Uses the TemperleyLiebElement class and the GroupAlgebra class to create
 * a working platform for the Temperley-Lieb algebra. Routines contained herein are
 * intended to relate to the complex-algebra with TL elements as generators... meaning
 * sums of TL Elements. Anything relating to something other than this should be contained
 * within the TemperleyLiebElement class.
 */
public class TemperleyLiebElement extends GroupAlgebraElement<TemperleyLiebTerm> {
    /** Number of strands */
    private int n;
    
    /** Constructor: creates a new instance of TemperleyLiebAlgebra */
    public TemperleyLiebElement() {
        super();
        n=2;
    }
    /** Initializes to a specific n value. */
    public TemperleyLiebElement(int n){
        super();
        this.n=n;
    }
    /** Initializes to another TL algebra element. */
    public TemperleyLiebElement(TemperleyLiebElement e){
        this.n=e.n;
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:e.terms){appendTerm(t.getWeight(),new TemperleyLiebTerm(t.getElement()));}
    }
    
    /** Checks to see if two terms can be added together... if so, add their weights/kink numbers; result is contained in t1 */
    public boolean addIfPossible(GroupAlgebraSummand<TemperleyLiebTerm> t1,GroupAlgebraSummand<TemperleyLiebTerm> t2){
        if(t1.compareTo(t2)==0){
            t1.setWeight(t1.getElement().getTotalWeight(t1)+t2.getElement().getTotalWeight(t2));
            t1.getElement().kinks=0;
            t1.getElement().positive=true;
            t1.getElement().g.deleteTrivialLoops();
            return true;
        }
        return false;
    }
    
    /** Returns TemperleyLiebAlgebra element with crossings removed. */
    public static TemperleyLiebElement removeCrossings(TemperleyLiebTerm e){
        TemperleyLiebElement result=new TemperleyLiebElement(e.n);
        Edge[] ce=e.getCrossedEdges();
        if(ce==null){result.appendTerm(e);}else{
            int p1=ce[0].getSource();
            int p2=ce[1].getSource();
            int p3=ce[0].getSink();
            int p4=ce[1].getSink();
            float COEFF1=+1;
            float COEFF2=+1;
            // Determine coefficients, based on where the edges are... copy down the points first to determine this
            // The above occurs by default (both positive). The only case where this doesn't happen is if the two strands
            // are both vertically oriented (i.e. a simple X). In this case, the opposite source/sink pairing (p1-p4,p2-p3) is (+1),
            // and the source/source plus sink/sink pairing (p1-p2,p3-p4) is (-1).
            // To check for the "X", both sources must be inputs, both sinks outputs... then change COEFF2 to -1
            if(e.inputs.contains(p1)&&e.inputs.contains(p2)&&e.outputs.contains(p3)&&e.outputs.contains(p4)){COEFF2=-1;}
            // Now create the new term and add in the edges
            TemperleyLiebTerm e1=new TemperleyLiebTerm(e);
            e1.getGraph().remove(ce[0]);
            e1.getGraph().remove(ce[1]);
            e1.addEdge(p1,p4);
            e1.addEdge(p2,p3);
            result.append(COEFF1,removeCrossings(e1));
            TemperleyLiebTerm e2=new TemperleyLiebTerm(e);
            e2.getGraph().remove(ce[0]);
            e2.getGraph().remove(ce[1]);
            e2.addEdge(p1,p2);
            e2.addEdge(p3,p4);
            result.append(COEFF2,removeCrossings(e2));
        }
        return result;
    }
    
    /** Removes all crossings in current algebra element. */
    public void removeCrossings(){
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:getTermList()){
            TemperleyLiebElement rc=removeCrossings(t.getElement());
            //System.out.println("before: "+t.getElement().toString()+" & after: "+rc.toString());
            result.append(t.getWeight(),rc);
        }
        this.terms=result.terms;
    }
    
    /** Returns the symmetrizer on n elements as an element of the TL algebra...
     * this is a weighted sum of all permutations.
     */
    public static TemperleyLiebElement getSymmetrizer(int n){
        Permutation p=new Permutation(n);
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        float weight=1; //divide by/(float)factorial(n);
        while(p.hasNext()){
            TemperleyLiebTerm e=new TemperleyLiebTerm(n);
            e.setToPermutation(p);
            result.appendTerm(weight,e);
            p=p.next();
        }
        TemperleyLiebTerm e=new TemperleyLiebTerm(n);
        e.setToPermutation(p);
        result.appendTerm(weight,e);
        return result;
    }
    public static long factorial(int n){return(n<=1)?1:n*factorial(n-1);}
    /** Returns the anti-symmetrizer */
    public static TemperleyLiebElement getAntiSymmetrizer(int n){
        Permutation p=new Permutation(n);
        TemperleyLiebElement result=new TemperleyLiebElement(n);
        float weight=1/factorial(n);
        while(p.hasNext()){
            TemperleyLiebTerm e=new TemperleyLiebTerm(n);
            e.setToPermutation(p);
            result.appendTerm(weight,e);
            p=p.next();
        }
        return result;
    }
    /** Returns whether there are any terms with crossings. */
    public boolean hasCrossings(){
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms){if(t.getElement().hasCrossings()){return true;}}
        return false;
    }
    /** String output. Defaults to parenthetical notation if all elements are basis
     * elements, otherwise gives explicit notation.
     */
    public String toString(){
        if(terms.size()==0){return "0";}
        String s="";
        if(!hasCrossings()){
            for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms.descendingSet()){
                s+=t.coeffString()+"("+t.getElement().toParenString()+")";
            }
            return s;
        }
        for(GroupAlgebraSummand<TemperleyLiebTerm> t:terms.descendingSet()){
            s+=t.coeffString()+t.getElement().toString();
        }
        return s;
    }
    /** Override actLeft so that computation is no longer done "in place" */
    public TemperleyLiebElement actLeft(GroupElement x1,GroupElement x2){
        return (TemperleyLiebElement)new TemperleyLiebElement((TemperleyLiebElement)x1).actLeft(x2);
    }
}