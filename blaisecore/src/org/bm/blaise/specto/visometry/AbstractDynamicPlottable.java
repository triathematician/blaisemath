/*
 * AbstractDynamicPlottable.java
 * Created on Sep 14, 2007, 7:55:27 AM
 */
package org.bm.blaise.specto.visometry;

/**
 * <p>
 *  This class takes the <code>AbstractPlottable</code> class and adds in basic support for mouse event handling.
 *  In order to properly handle incoming events, the mouse positions should be passed through the visometry to
 *  convert coordinates and determine whether the object has actually been clicked.
 * </p>
 * <p>
 *  The class also adds in two flags: <code>adjustable</code> and <code>editable</code>, which can be used to determine
 *  whether it is ready to receive and respond to mouse events.
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class AbstractDynamicPlottable<C> extends AbstractPlottable<C> implements DynamicPlottable<C> {

    //
    //
    // PROPERTIES
    //
    //

    /** Whether the plottable is being changed.*/
    public boolean adjusting = false;

    /** Whether the plottable can be edited in some way. */
    public boolean editable = true;

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return true if the element is adjusting, otherwise false */
    public boolean isAdjusting() {
        return adjusting;
    }

    /** @return true if the element is editable, otherwise false */
    //@Override
    public boolean isEditable() {
        return visible && editable;
    }

    /** Sets the editable status of the element. */
    public void setEditable(boolean newValue) {
        editable = newValue;
    }

    //
    //
    // HANDLING MOUSE EVENTS
    //
    //

    public void mousePressed(VisometryMouseEvent<C> e) {
        if (isClickablyCloseTo(e)) {
            adjusting = true;
        }
    }

    public void mouseReleased(VisometryMouseEvent<C> e) {
        mouseDragged(e);
        adjusting = false;
    }
    
    public void mouseClicked(VisometryMouseEvent<C> e) {
    }

    public void mouseDragged(VisometryMouseEvent<C> e) {
    }

    public void mouseEntered(VisometryMouseEvent<C> e) {
    }

    public void mouseExited(VisometryMouseEvent<C> e) {
    }

    public void mouseMoved(VisometryMouseEvent<C> e) {
    }
}
