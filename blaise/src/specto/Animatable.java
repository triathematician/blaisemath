/*
 * Animator.java
 * Created on Sep 14, 2007, 8:30:26 AM
 */

package specto;

import java.awt.Graphics2D;
import sequor.component.RangeTimer;

/**
 * This interface handles animation of objects on the plot window. By default, most Plottable's
 * should be animatable. The methods required for this simply pass a timer into the
 * paint component method!
 * <br><br>
 * @author Elisha Peterson
 */
public interface Animatable<V extends Visometry> {    
    public abstract void paintComponent(Graphics2D g,V v,RangeTimer t);    
    /** Returns number of steps used in the animation. */
    public abstract int getAnimatingSteps();
    /** Sets animation properties. */
    public void setAnimationOn(boolean newValue);
    /** Gets animation properties. */
    public boolean isAnimationOn();
}
