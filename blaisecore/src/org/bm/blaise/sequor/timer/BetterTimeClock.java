/**
 * BClock.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.sequor.timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   <code>BClock</code> is an implementation of the <code>ClockTimer</code> interface.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BetterTimeClock implements TimeClock, StandardTimer, ActionListener {

    //
    // PROPERTIES
    //

    /** Current status of the timer. */
    TimerStatus status = TimerStatus.STOPPED;
    /** Whether the clock should reverse once max time is reached. */
    boolean autoReverse = false;
    /** Number of times to repeat the clock play loop. */
    int repeatCount = 0;
    /** Speed of the timer... controls the rate at which time advances (speed up/slow down buttons). */
    int animationSpeed;
    /** The minimum time for the clock's range of values */
    double minTime = 0;
    /** The maximum time for the clock's range of values */
    double maxTime = Double.MAX_VALUE;

    /** Stores the value of the clock associated with the timer. */
    double time = 0;
    /** The direction of the clock's advance */
    boolean forward = true;
    /** The number of plays since last pressed */
    int playCount = 0;

    //
    // CONSTRUCTOR
    //

    public BetterTimeClock() {
        timer = new BetterTimer(50);
        timer.addActionListener(this);
    }


    //
    // BEAN PATTERNS
    //

    public boolean isAutoReverseOn() {
        return autoReverse;
    }

    public void setAutoReverseOn(boolean value) {
        autoReverse = value;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int newCount) {
        this.repeatCount = newCount;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }

    public double getMinTime() {
        return minTime;
    }

    public void setMinTime(double minTime) {
        this.minTime = minTime;
    }

    public TimerStatus getStatus() {
        return status;
    }

    public boolean isPlaying() {
        return status == TimerStatus.PLAYING;
    }

    public boolean isPaused() {
        return status == TimerStatus.PAUSED;
    }

    public boolean isStopped() {
        return status == TimerStatus.STOPPED;
    }

    //
    // TimeClock Methods
    //

    public double getTime() {
        return time;
    }


    //
    // TIMER AND PLAY CONTROL METHODS
    //

    /** Stores the actual timer. */
    transient BetterTimer timer;

    /**
     * This method controls the behavior of the timer... starting, stopping
     * and pausing the action of the timer.
     * @param newStatus new status of the timer.
     */
    public void setStatus(TimerStatus newStatus) {
        if (status != newStatus) {
            status = newStatus;
            switch (status) {
                case PLAYING :
                    timer.start();
                    fireActionPerformed(TimerStatus.PLAYING.name());
                    break;
                case PAUSED :
                    timer.stop();
                    fireActionPerformed(TimerStatus.PAUSED.name());
                    break;
                case STOPPED :
                    timer.stop();
                    time = minTime;
                    playCount = 0;
                    fireActionPerformed(TimerStatus.STOPPED.name());
                    break;
            }
            playAction.setEnabled(!isPlaying());
            pauseAction.setEnabled(!isStopped());
            stopAction.setEnabled(!isStopped());
            restartAction.setEnabled(true);
            slowDownAction.setEnabled(!isStopped());
            speedUpAction.setEnabled(!isStopped());
        }
    }

    /**
     * Updates the timer's step, updates the underlying time, and
     * causes events to be fired to notify listeners of the change
     * in time.
     */
    protected void incrementTimer() {
        if (forward) {
            time += Math.pow(1.5, animationSpeed);
            time = Math.min(time, maxTime);
            if (time == maxTime) {
                if (autoReverse)
                    forward = false;
                else {
                    playCount++;
                    if (playCount <= repeatCount)
                        time = minTime;
                    else
                        pause();
                }
            }
        } else {
            time -= Math.pow(1.5, animationSpeed);
            time = Math.max(time, minTime);
            if (time == minTime) {
                playCount++;
                if (playCount <= repeatCount) {
                    if (autoReverse)
                        forward = true;
                    else
                        time = maxTime;
                } else {
                    forward = true;
                    pause();
                }
            }
        }
        fireActionPerformed("Tick");
    }

    public void play() {
        setStatus(TimerStatus.PLAYING);
    }

    public void pause() {
        if (isPaused()) {
            setStatus(TimerStatus.PLAYING);
        } else {
            setStatus(TimerStatus.PAUSED);
        }
    }

    public void stop() {
        setStatus(TimerStatus.STOPPED);
    }

    public void restart() {
        stop();
        play();
    }

    public void slowDown() {
        animationSpeed--;
    }

    public void speedUp() {
        animationSpeed++;
    }
    
    //
    // EVENT HANDLING AND TIMER CONTROL
    //

    /**
     * Handles action events that are passed to the clock. These
     * actions may come from the timer, or they may come from
     * external controlling class.
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (e == null)
            return;
        if (e.getSource().equals(timer)) {
            if (isPlaying())
                incrementTimer();
        }
    }

    //
    // STANDARD ActionEvent CODE
    //

    protected ActionEvent actionEvent = null;
    protected EventListenerList listenerList = new EventListenerList();

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(String s) {
        actionEvent = new ActionEvent(this, 0, s);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(actionEvent);
            }
        }
    }

    //
    // CUSTOM ACTIONS
    //

    public Action getPlayAction() { return playAction; }    
    AbstractAction playAction = new AbstractAction("Play") {
            { putValue(SHORT_DESCRIPTION, "Play the timer from current location"); }
            public void actionPerformed(ActionEvent e) { play(); }
        };

    public Action getPauseAction() { return pauseAction; }
    AbstractAction pauseAction = new AbstractAction("Pause") {
            { putValue(SHORT_DESCRIPTION, "Pause the timer if playing, or unpause if paused"); setEnabled(false); }
            public void actionPerformed(ActionEvent e) { pause(); }
        };

    public Action getStopAction() { return stopAction; }
    AbstractAction stopAction = new AbstractAction("Stop") {
            { putValue(SHORT_DESCRIPTION, "Stop the timer and reset clock"); setEnabled(false); }
            public void actionPerformed(ActionEvent e) { stop(); }
        };

    public Action getRestartAction() { return restartAction; }
    AbstractAction restartAction = new AbstractAction("Restart") {
            { putValue(SHORT_DESCRIPTION, "Restart the timer (stop, then play)"); }
            public void actionPerformed(ActionEvent e) { restart(); }
        };

    public Action getSlowDownAction() { return slowDownAction; }
    AbstractAction slowDownAction = new AbstractAction("Slow down") {
            { putValue(SHORT_DESCRIPTION, "Slows the timer down"); setEnabled(false); }
            public void actionPerformed(ActionEvent e) { slowDown(); }
        };

    public Action getSpeedUpAction() { return speedUpAction; }
    AbstractAction speedUpAction = new AbstractAction("Speed up") {
            { putValue(SHORT_DESCRIPTION, "Speeds up the timer"); setEnabled(false); }
            public void actionPerformed(ActionEvent e) { speedUp(); }
        };
        
}
