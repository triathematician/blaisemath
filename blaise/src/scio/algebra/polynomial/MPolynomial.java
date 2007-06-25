package scio.algebra.polynomial;

import scio.algebra.GroupAlgebraElement;
import scio.algebra.GroupAlgebraSummand;
import scio.algebra.GroupElement;

/**
 * <b>Polynomial.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 21, 2007, 1:55 PM</i><br><br>
 *
 * Multivariable polynomial algebra
 */
public class MPolynomial extends GroupAlgebraElement<MAddInt>{
    /** Constructor: creates a new instance of Polynomial, initializes to zero. */
    public MPolynomial(){super();}
    /** Creates from a given algebra element. */
    public MPolynomial(GroupAlgebraElement<MAddInt> e){this();}
    /** Creates from a given algebra term. */
    public MPolynomial(GroupAlgebraSummand<MAddInt> e){this();appendTerm(e);}
    /** Commutative group! */
    public static boolean isCommutative(){return true;}
    /** New way to append. */
    public void appendTerm(float f,int[] x){appendTerm(f,new MAddInt(x));}
    /** Override getInverse mthod. */
    public GroupElement getInverse(){
        Polynomial result;
        if(terms.size()==1){return new MPolynomial((GroupAlgebraSummand<MAddInt>)terms.first().getInverse());}
        return null;
    }
    /** Override print method. */
    public String toString(){
        if(terms.size()==0){return "0";}
        String s="";
        for(GroupAlgebraSummand<MAddInt> t:terms.descendingSet()){
            String sc=t.coeffString();
            String st=t.getElement().toString();
            if(st=="1"){if(sc=="+"){s+="+1";}else if(sc=="-"){s+="-1";}else{s+=sc;}}
            else{s+=sc+st;}
        }
        return s;
    }
    /** Returns a copy of the current class */
    public MPolynomial clone(){
        MPolynomial copy=new MPolynomial();
        copy.append(this);
        return copy;
    }
}
