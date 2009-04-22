package zzDeprecated;

/**
 * <b>GroupElement.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 8, 2007, 1:18 PM</i><br><br>
 *
 * Defines an element of the group [x].
 */
public abstract class GroupElement implements Category.CObject,Category.MultiMorphism{
    
    // Abstract group methods
    
    /** Tests if an element is the identity. */
    public abstract boolean isIdentity();
    /** Returns the inverse. */
    public abstract GroupElement getInverse();    
    /** Group left action. */
    public abstract GroupElement actLeft(GroupElement x);
        
    // Implemented group methods
    
    /** Group right action. */
    public GroupElement actRight(GroupElement x){return x.actLeft(this);}
    
    
    // Implemented category methods
    
    public Category.MultiMorphism getIdentityMorphism(){return null;}
    public Category getCategory(){return null;}
}
