/**
 * AbstractAnimatingPlottable.java
 * Created on Sep 24, 2009
 */

package org.bm.blaise.specto.visometry;

/**
 * <p>
 *   <code>AbstractAnimatingPlottable</code> adds bean support for turning animations on/off to
 *   the basic plottable implementation.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class AbstractAnimatingPlottable<V> extends AbstractPlottable<V> implements AnimatingPlottable<V> {

    /** Whether animation is turned on. */
    boolean animating;

    public boolean isAnimationOn() {
        return animating;
    }

    public void setAnimationOn(boolean newValue) {
        animating = newValue;
    }

}
