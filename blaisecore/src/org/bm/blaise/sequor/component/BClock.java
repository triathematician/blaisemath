/**
 * BClock.java
 * Created on Sep 25, 2009
 */
package org.bm.blaise.sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   <code>BClock</code> is an implementation of the <code>ClockTimer</code> interface.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BClock implements ClockTimer, ActionListener {

    //
    //
    // PROPERTIES
    //
    //

    /** Current status of the timer. */
    TimerStatus status = TimerStatus.STOPPED;

    /** Whether the clock should reverse once max time is reached. */
    boolean autoReverse = false;

    /** Number of times to repeat the clock play loop. */
    int repeatCount = 0;

    /** Speed of the timer... controls the rate at which time advances. */
    int speed;

    /** Stores the value of the clock associated with the timer. */
    double time = 0;

    //
    //
    // CONSTRUCTOR
    //
    //
    public BClock() {
        timer = new BetterTimer(1);
        timer.addActionListener(this);
    }


    //
    //
    // BEAN PATTERNS
    //
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
    //
    // TimeClock Methods
    //
    //
    public double getTime() {
        return time;
    }


    //
    //
    // TIMER AND PLAY CONTROL METHODS
    //
    //

    /** Stores the actual timer. */
    transient BetterTimer timer;

    /**
     * This method controls the behavior of the timer... starting, stopping
     * and pausing the action of the timer.
     *
     * @param newStatus new status of the timer.
     */
    public void setStatus(TimerStatus newStatus) {
        if (status != newStatus) {
            status = newStatus;
            switch (status) {
                case PLAYING :
                    timer.start();
                    break;
                case PAUSED :
                    timer.stop();
                    break;
                case STOPPED :
                    timer.stop();
                    time = 0;
                    break;
            }
        }
    }

    /**
     * Updates the timer's step, updates the underlying time, and
     * causes events to be fired to notify listeners of the change
     * in time.
     */
    protected void incrementTimer() {
        time += Math.pow(1.5, speed);
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
        speed--;
    }

    public void speedUp() {
        speed++;
    }
    
    //
    //
    // EVENT HANDLING AND TIMER CONTROL
    //
    //

    /**
     * Handles action events that are passed to the clock. These
     * actions may come from the timer, or they may come from
     * external controlling class.
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (e == null) {
            return;
        }
        // an actual timer event
        if (e.getSource().equals(timer)) {
            if (isPlaying()) {
                incrementTimer();
            }
        } else {
            String s = e.getActionCommand();
            if (s.equals("play") || s.equals("start")) {
                play();
            } else if (s.equals("pause")) {
                pause();
            } else if (s.equals("stop")) {
                stop();
            } else if (s.equals("restart")) {
                restart();
            } else if (s.equals("slower")) {
                if (isPlaying()) {
                    slowDown();
                }
            } else if (s.equals("faster")) {
                if (isPlaying()) {
                    speedUp();
                }
            }
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
}
