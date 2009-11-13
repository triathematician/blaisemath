/**
 * DynamicPlottable.java
 * Created on Sep 5, 2009
 */

package org.bm.blaise.specto.visometry;

/**
 * <p>
 *   <code>DynamicPlottable</code> represents a class that may be edited with the mouse.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface DynamicPlottable<C> extends VisometryMouseInputListener<C> {

    /**
     * @return true if the element is editable, otherwise false
     */
    boolean isEditable();

    /**
     * Sets the editable status of the element.
     */
    void setEditable(boolean newValue);

}
