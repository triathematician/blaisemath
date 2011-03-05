/**
 * VClock.java
 * Created on Dec 17, 2009
 */

package later.visometry.plottable;

import java.util.Timer;
import java.util.TimerTask;
import primitive.style.temp.ClockStyle;
import visometry.plottable.VPoint;

/**
 * <p>
 *    This class displays a draggable clock. The drawing is handled by the underlying <code>ClockStyle</code> class.
 * </p>
 * @author Elisha Peterson
 */
public class VClock<C> extends VPoint<C> {

    public VClock(C value) {
        super(value, new ClockStyle());
        setAnimationOn(true);
    }

    @Override
    public String toString() {
        return "Clock";
    }

    boolean animating = false;
    Timer timer;
    TimerTask task;

    void setAnimationOn(boolean newValue) {
        animating = newValue;
        if (animating) {
            if (timer == null)
                timer = new Timer();
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() { @Override public void run() { 
                firePlottableChanged();
            } };
            timer.schedule(task, 0, 1000);
        } else {
            if (task != null)
                task.cancel();
        }
    }

    //
    // STYLE METHODS
    //

    /** @return current style of clock */
    public ClockStyle getStyle() { return (ClockStyle) entry.renderer; }
    /** Sets current clock style */
    public void setStyle(ClockStyle style) { entry.renderer = style; firePlottableStyleChanged(); }
}
