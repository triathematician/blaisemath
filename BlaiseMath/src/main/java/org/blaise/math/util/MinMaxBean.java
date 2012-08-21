/*
 * MinMaxBean.java
 * Created on Apr 7, 2010
 */

package org.blaise.math.util;

/**
 * <p>
 *   Interface guaranteeing retrieval of a "minimum" and "maximum" of specified type.
 * </p>
 * @author Elisha Peterson
 */
public interface MinMaxBean<C> {

    /**
     * @return minimum value
     */
    public C getMinimum();

    /**
     * @return maximum value
     */
    public C getMaximum();

}
