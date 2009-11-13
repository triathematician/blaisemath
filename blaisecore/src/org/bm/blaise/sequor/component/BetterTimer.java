/**
 * BetterTimer.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   <code>BetterTimer</code> improves functionality of the current
 *   <code>Timer</code> class, providing start and stop functionality.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BetterTimer extends java.util.Timer {


    //
    //
    // PROPERTIES
    //
    //
    /** Number of ticks to count before stopping. */
    int maxTicks = Integer.MAX_VALUE;
    /** Delay in milliseconds */
    long period = 100;


    //
    //
    // IMMUTABLE PROPERTIES
    //
    //
    final ActionEvent actionEvent = new ActionEvent(this, 0, "Tick");


    //
    //
    // CONSTRUCTORS
    //
    //
    public BetterTimer() {
    }

    public BetterTimer(long period) {
        this.period = period;
    }


    //
    //
    // BEAN PATTERNS
    //
    //
    public int getMaxTicks() {
        return maxTicks;
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
    //
    //
    // TIMER CONTROL
    //
    //
    /** Stores current number of ticks. */
    transient int tickCount = 0;
    /** Stores the current task. */
    TimerTask task;

    /**
     * Starts the timer, which will start sending out <code>ActionEvent</code>s
     * separated by the current period in milliseconds.
     */
    public void start() {
        task = new TimerTask() {
            @Override
            public void run() {
                tickCount++;
                if (tickCount >= maxTicks) {
                    stop();
                } else {
                    fireActionPerformed();
                }
            }
        };
        schedule(task, 0, period);
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        task.cancel();
    }
    //
    //
    // ACTION EVENT HANDLING
    //
    //
    protected EventListenerList listenerList = new EventListenerList();

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
}
