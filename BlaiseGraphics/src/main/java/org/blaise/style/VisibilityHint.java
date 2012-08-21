/*
 * VisibilityHint.java
 * Created Jan 16, 2011
 */

package org.blaise.style;

/**
 * The visibility status of a shape, used to set whether a shape is drawn,
 * hidden, highlighted, or otherwise emphasized.
 *
 * @author Elisha
 */
public enum VisibilityHint {
    /** Default visibility setting */
    Regular,
    /** Highlight/emphasis setting */
    Highlight,
    /** Obscure setting */
    Obscure,
    /** Hidden setting (do not draw or receive events) */
    Hidden,
    /** Draws the outline only */
    Outline;
}
