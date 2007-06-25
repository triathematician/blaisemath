package scio.algebra;

/**
 * <b>GroupElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 8, 2007, 1:18 PM</i><br><br>
 *
 * Interface for standard group operations. Rather light implementation here!
 */
public interface GroupElement{
    /** Tests if an element is the identity. */
    public boolean isIdentity();
    /** Returns the inverse. */
    public GroupElement getInverse();    
    /** Group left action. */
    public GroupElement actLeft(GroupElement x);
    /** Group right action. Typically, copy paste the following to implement:
        public GroupElement actRight(GroupElement x){return x.actLeft(this);} */
    public GroupElement actRight(GroupElement x);
    /** Require a string representation. */
    public String toString();
    public String toShortString();
    public String toLongString();
}
