/**
 * VClock.java
 * Created on Dec 17, 2009
 */

package org.bm.blaise.specto.plottable;

import java.util.Timer;
import java.util.TimerTask;
import org.bm.blaise.sequor.component.TimeClock;
import org.bm.blaise.specto.primitive.ClockStyle;
import org.bm.blaise.specto.visometry.AnimatingPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *    This class displays a draggable clock. The drawing is handled by the underlying <code>ClockStyle</code> class.
 * </p>
 * @author Elisha Peterson
 */
public class VClock<C> extends VInvisiblePoint<C> implements AnimatingPlottable<C> {

    ClockStyle style = new ClockStyle();

    public VClock(C value) {
        super(value);
        setAnimationOn(true);
    }

    public ClockStyle getStyle() {
        return style;
    }

    public void setStyle(ClockStyle style) {
        this.style = style;
    }

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.drawWithStyle(value, style);
    }

    @Override
    public String toString() {
        return "Clock";
    }

    boolean animating = true;

    public boolean isAnimationOn() {
        return animating;
    }

    Timer timer;
    TimerTask task;

    public void setAnimationOn(boolean newValue) {
        this.animating = newValue;
        if (animating) {
            if (timer == null)
                timer = new Timer();
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() { @Override public void run() { fireStateChanged(); } };
            timer.schedule(task, 0, 500);
        } else {
            if (task != null)
                task.cancel();
        }
    }

    public void recomputeAtTime(Visometry<C> vis, VisometryGraphics<C> canvas, TimeClock clock) {
    }
}
