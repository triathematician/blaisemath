/*
 * Animator.java
 * Created on Sep 14, 2007, 8:30:26 AM
 */
package org.bm.blaise.specto.visometry;

import org.bm.blaise.sequor.timer.TimeClock;

/**
 * <p>
 *   This interface defines methods required for any plottable objects that can
 *   animate in some way. Animation can be a great thing for the user, so it is
 *   highly recommended that as many plottables as possible implement this method.
 * </p>
 * <p>
 *   Also supports a flag for turning animation on/off.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public interface AnimatingPlottable<C> {

    /**
     * Get current animation status.
     * @return true if this class will animate, otherwise false.
     */
    public boolean isAnimationOn();

    /**
     * Sets the current animation status.
     * @param newValue true if this class should animate, otherwise false
     */
    public void setAnimationOn(boolean newValue);

    /**
     * Recomputes values in the component at provided time.
     *
     * @param vis the visometry used for computing window coordinates
     * @param canvas the underlying canvas for painting objects
     * @param clock the clock for recomputing
     */
    public void recomputeAtTime(Visometry<C> vis, VisometryGraphics<C> canvas, TimeClock clock);

//    /**
//     * Paints the component subject to the current time of the provided clock.
//     * @param vg the graphics on which to paint
//     * @param clock the clock containing the current time
//     */
//    public void paintComponentAtTime(VisometryGraphics<C> vg, TimeClock clock);
}
