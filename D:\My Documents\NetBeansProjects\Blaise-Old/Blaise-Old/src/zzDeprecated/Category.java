/*
 * <b>CCategory.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 7, 2007, 9:40 AM</i><br><br>
 *
 * An interface for a categorical category. A category must describe how to
 *   compose morphisms, as well as be able to test for containing objects and
 *   morphisms, and to determine commutativity perhaps.
 */

package zzDeprecated;

/**
 *
 * @author ae3263
 */
public interface Category {
    /** Composes morphisms. */
    MultiMorphism compositionOf(MultiMorphism m1,MultiMorphism m2);
    /** Tests to see if it contains an object. */
    boolean contains(CObject o);
    /** Tests to see if it contains a morphism. */
    boolean contains(MultiMorphism m);
    /** Tests to see if the category is valid. */
    boolean wellDefined();
    
    /** Describes an object in the category. */
    public interface CObject {
        /** Returns containing category. */
        public Category getCategory();
        /** Returns the identity morphism on this object. */
        public MultiMorphism getIdentityMorphism();
    }
    
    /** Describes a collection of morphisms in the category. */
    public interface MultiMorphism {
        /** Returns containing category. */
        public Category getCategory();
        /** Tests if this is the identity morphism */
        public boolean isIdentity();        
    }
    
    /** Describes a morphism in the category. */
    public interface Morphism extends MultiMorphism {
        /** Returns source object. */
        public CObject getFrom();
        /** Returns destination object. */
        public CObject getTo();
    }
}
