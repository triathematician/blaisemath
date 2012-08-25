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
    /** Highlight/emphasis setting */
    Highlight,
    /** De-highlight/de-emphasis */
    Obscure,
    /** This hint means the graphic is not drawn and receives no events from its parent. */
    Hidden;
}
