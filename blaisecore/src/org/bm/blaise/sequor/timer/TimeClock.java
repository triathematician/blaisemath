/**
 * TimeClock.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.sequor.timer;

import java.awt.event.ActionListener;

/**
 * <p>
 *   <code>TimeClock</code> is a simple interface that may be called to retrieve a time.
 *   It is particularly intended to be used in combination with a timer.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface TimeClock {
    /**
     * Returns time of the clock.
     * @return time, as a double
     */
    public double getTime();

    //
    // ACTION EVENT HANDLING
    //

    /**
     * Adds a listener to receive timer change events
     * @param l the interested listener
     */
    public void addActionListener(ActionListener l);

    /**
     * Removes a listener to stop receiving change events
     * @param l the listener
     */
    public void removeActionListener(ActionListener l);
}
