package scio.algebra;

/**
 * <b>GroupElementId.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>May 8, 2007, 9:40 AM</i><br><br>
 * 
 * An abstract implementation of the group element class with the ability to
 * call up the identity statically. Extending classes should override:
 *  > Constructor!
 *  > getIdentity method
 *  > actLeft method
 *  > isIdentity method (optional, but may choose to override this!)
 */
public abstract class GroupElementId implements GroupElement,Comparable{
    /** Default constructor: worthless for now, but need it in child classes! */
    public GroupElementId(){}
    /** Returns the identity element... should always overwrite! */
    public static GroupElementId getIdentity(){return null;}
    /** Returns if the underlying group is commutative. */
    public static boolean isCommutative(){return false;}
    /** Implements identity check class. */
    public boolean isIdentity(){return this.equals(getIdentity());}
    /** Group right action... depends on the left action. */
    public GroupElement actRight(GroupElement x){return x.actLeft(this);}

    /** Comparing two group algebra elements... */
    public abstract int compareTo(Object o);
}
