/**
 * TimeClock.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.sequor.component;

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
}
