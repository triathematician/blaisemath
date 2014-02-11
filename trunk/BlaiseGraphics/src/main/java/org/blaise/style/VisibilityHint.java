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
    /** HIGHLIGHTed setting */
    HIGHLIGHT,
    /** SELECTED item */
    SELECTED,
    /** FADED, used to de-emphasize */
    FADED,
    /** Draw outline only */
    OUTLINE,
    /** This hint means the graphic is not drawn and receives no events from its parent. */
    HIDDEN;
}
