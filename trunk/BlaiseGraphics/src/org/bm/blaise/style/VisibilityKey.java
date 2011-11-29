/*
 * VisibilityKey.java
 * Created Jan 16, 2011
 */

package org.bm.blaise.style;

/**
 * The visibility status of a shape, used to set whether a shape is drawn,
 * hidden, highlighted, or otherwise emphasized.
 * 
 * @author Elisha
 */
public enum VisibilityKey {
    /** Default visibility setting */
    Regular,
    /** Highlight/emphasis setting */
    Highlight,
    /** Obscure setting */
    Obscure,
    /** Invisible setting (do not draw) */
    Invisible;
}
