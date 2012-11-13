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
    /** Highlighted setting */
    Highlight,
    /** Selected item */
    Selected,
    /** De-highlight/de-emphasis */
    Obscure,
    /** Draw outline only */
    Outline,
    /** This hint means the graphic is not drawn and receives no events from its parent. */
    Hidden;
}
