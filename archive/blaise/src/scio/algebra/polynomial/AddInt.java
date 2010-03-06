package scio.algebra.polynomial;

import scio.algebra.GroupElement;
import scio.algebra.GroupElementId;

/**
 * <b>AddInt.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 21, 2007, 1:56 PM</i><br><br>
 *
 * The additive group (Z,+) of integers.
 */
public class AddInt extends GroupElementId {
    /** Value of the integer */
    private int n;    
    /** Constructor */
    public AddInt(){n=0;}
    /** Initializes to given value */
    public AddInt(int n){this.n=n;}
    /** Returns value */
    public int get(){return n;}
    /** Returns the identity element. */
    public static GroupElementId getIdentity(){return new AddInt();}
    /** Gets the inverse (-n) */
    public GroupElement getInverse(){return new AddInt(-n);}
    /** Group action is addition */
    public GroupElement actLeft(GroupElement x){return new AddInt(n+((AddInt)x).get());}
    /** Returns if the underlying group is commutative. */
    public static boolean isCommutative(){return true;}
    /** Compare to another AddInt */
    public int compareTo(Object o){return n-((AddInt)o).get();}
}
