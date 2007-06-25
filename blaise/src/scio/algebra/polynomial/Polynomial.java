package scio.algebra.polynomial;

import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraSummand;
import scio.algebra.GroupElement;

/**
 * <b>Polynomial.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 21, 2007, 1:55 PM</i><br><br>
 *
 * Standard polynomial algebra... coefficients are floats, implemented with x^0,...x^n as an
 * additive group. Does not prohibit negative coefficients.
 */
public class Polynomial extends GroupAlgebraElement<AddInt>{
    /** Constructor: creates a new instance of Polynomial, initializes to zero. */
    public Polynomial(){super();}
    /** Creates from a given algebra element. */
    public Polynomial(GroupAlgebraElement<AddInt> e){this();}
    /** Creates from a given algebra term. */
    public Polynomial(GroupAlgebraSummand<AddInt> e){this();appendTerm(e);}
    /** Commutative group! */
    public static boolean isCommutative(){return true;}
    /** New way to append. */
    public void appendTerm(float f,int x){
        appendTerm(f,new AddInt(x));
    }
    /** Override getInverse mthod. */
    public GroupElement getInverse(){
        Polynomial result;
        if(terms.size()==1){return new Polynomial((GroupAlgebraSummand<AddInt>)terms.first().getInverse());}
        return null;
    }
    /** Override print method. */
    public String toString(){
        if(terms.size()==0){return "0";}
        String VAR="x";
        String s="";
        for(GroupAlgebraSummand<AddInt> t:terms.descendingSet()){
            s+=t.coeffString();
            Integer e=t.getElement().get();
            if(e==0){if(t.getWeight()==1||t.getWeight()==-1){s+="1";}} else if(e==1){s+=VAR;} else {s+=VAR+"^"+e.toString();}
        }
        return s;
    }
    /** Returns a copy of the current class */
    public Polynomial clone(){
        Polynomial copy=new Polynomial();
        copy.append(this);
        return copy;
    }
    // public Polynomial actLeft(Polynomial p2){return clone().actLeft(p2);}
}
