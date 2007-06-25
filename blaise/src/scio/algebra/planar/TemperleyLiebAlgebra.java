package scio.planar;

import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraTerm;
import scio.graph.Edge;
import java.util.ArrayList;

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
public class TemperleyLiebAlgebra extends GroupAlgebraElement<TemperleyLiebElement> {
    /** Number of strands */
    private int n;
    
    /** Constructor: creates a new instance of TemperleyLiebAlgebra */
    public TemperleyLiebAlgebra() {
        super();
        n=2;
    }
    public TemperleyLiebAlgebra(int n){
        super();
        this.n=n;
    }
    
    /** Returns TemperleyLiebAlgebra element with crossings removed. */
    public static TemperleyLiebAlgebra removeCrossings(TemperleyLiebElement e){
        TemperleyLiebAlgebra result=new TemperleyLiebAlgebra(e.n);
        float COEFF1=+1;
        float COEFF2=-1;
        Edge[] ce=e.getCrossedEdges();
        if(ce==null){result.appendTerm(e);}
        else{
            TemperleyLiebElement e1=new TemperleyLiebElement(e);
            e1.getGraph().remove(ce[0]);
            e1.getGraph().remove(ce[1]);
            e1.addEdge(ce[0].getSource(),ce[1].getSource());
            e1.addEdge(ce[0].getSink(),ce[1].getSink());
            result.append(COEFF1,removeCrossings(e1));
            TemperleyLiebElement e2=new TemperleyLiebElement(e);
            e2.getGraph().remove(ce[0]);
            e2.getGraph().remove(ce[1]);
            e2.addEdge(ce[0].getSource(),ce[1].getSink());
            e2.addEdge(ce[0].getSink(),ce[1].getSource());
            result.append(COEFF2,removeCrossings(e2));
        }
        return result;
    }  
    
    /** Removes all crossings in current algebra element. */
    public void removeCrossings(){
        TemperleyLiebAlgebra result=new TemperleyLiebAlgebra(n);
        for(GroupAlgebraTerm<TemperleyLiebElement> t:getTermList()){
            result.append(t.getWeight(),removeCrossings(t.getElement()));
        }
    }    
    
    /** Returns the symmetrizer on n elements as an element of the TL algebra...
     * this is a weighted sum of all permutations.
     */
    public static TemperleyLiebAlgebra getSymmetrizer(int n){
        // TODO must write a sum of permutations and disambiguate crossings!
        TemperleyLiebElement e=new TemperleyLiebElement(n);
        TemperleyLiebAlgebra result=new TemperleyLiebAlgebra(n);
        float weight=1/factorial(n);
        while(e.hasNext()){result.appendTerm(weight,e);e=e.next();}
        result.appendTerm(weight,e);
        return result;
    }    
    public static long factorial(int n){return(n<=1)?1:n*factorial(n-1);}    
    /** Returns the anti-symmetrizer */
    public static TemperleyLiebAlgebra getAntiSymmetrizer(int n){
        // TODO must write a sum of permutations and disambiguate crossings!
        TemperleyLiebElement e=new TemperleyLiebElement(n);
        TemperleyLiebAlgebra result=new TemperleyLiebAlgebra(n);
        float weight=1/factorial(n);
        while(e.hasNext()){result.appendTerm(weight,e);e=e.next();}
        result.appendTerm(weight,e);
        return result;
    }        
    /** String output */
    public String toString(){
        if(elements.size()==0){return "0";}
        String s="";
        for(GroupAlgebraTerm<TemperleyLiebElement> t:elements){
            s+=t.coeffString()+t.getElement().getParenString();
        }
        return s;
    }
}
