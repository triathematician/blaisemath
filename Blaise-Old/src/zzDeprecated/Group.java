/*
 * Group.java
 *
 * Created on March 7, 2007, 10:24 AM
 *
 * Interface for mathematical groups. Groups have a set of operations,
 *   basic operations on those GroupElements, and four rules they must obey:<br><br>
 *
 *   1) the operation is associative (a*b)*c=a*(b*c).<br>
 *   2) there is an identity GroupElement e with x*e=e*x=x for all x.<br>
 *   3) each GroupElement has an inverse... same on the left and the right: x*x1=e=x1*x.<br>
 *   4) possibly, the group is abelian a*b=b*a.<br><br>
 *
 * As a category, the group GroupElements are both objects and morphisms... where
 *   the morphisms are both left and right multiplication. Philosophically, the operation
 *   is implemented in the element class rather than in the group class itself.
 */

package zzDeprecated;

public abstract class Group implements Category {
    
    // Implemented group methods
    
    /** Applies the operation. */
    public GroupElement operate(GroupElement x1,GroupElement x2){return x1.actLeft(x2);}
    /** Says if the group is boolean. */
    protected boolean commutative;
    /** Tests to see if an GroupElement is the identity. */
    public boolean isIdentity(GroupElement x){return x.isIdentity();}
    /** Returns the inverse. */
    public GroupElement inverseOf(GroupElement x){return x.getInverse();}
    /** Determines if the group is commutative. */
    public boolean isCommutative(){return commutative;}
    /** Tests to see if it contains a morphism... same test as below. */
    public boolean contains(MultiMorphism m){return contains((CObject)(GroupElement)m);}
    /** Composes morphisms (for a group, these are just GroupElements). */
    public MultiMorphism compositionOf(MultiMorphism m1,MultiMorphism m2){return operate((GroupElement)m1,(GroupElement)m2);}
    
    // Non-implemented Methods
    
    /** Returns the identity GroupElement. */
    public abstract GroupElement getIdentity();
    /** Tests to see if it contains an object. */
    public abstract boolean contains(CObject o);
    /** Tests to see if the group is valid. */
    public abstract boolean wellDefined();
}
