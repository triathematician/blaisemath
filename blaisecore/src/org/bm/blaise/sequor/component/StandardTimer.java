/*
 * StandardTimer.java
 * Created on Jul 30, 2009
 */
package org.bm.blaise.sequor.component;

import java.awt.event.ActionListener;

/**
 * <p>
 *  Interface containing essential features of a timer.
 * </p>
 * 
 * @author Elisha Peterson
 */
public interface StandardTimer {

    //
    //
    // PATTERNS
    //
    //

    /**
     * Whether the timer reverses directions when it reaches "the end".
     * @return true if yes
     */
    public boolean isAutoReverseOn();

    /**
     * Sets auto-reversing feature on or off.
     * @param value true or false
     */
    public void setAutoReverseOn(boolean value);

    /**
     * Gets the number of times the timer repeats.
     * @return the number of times it repeats
     */
    public int getRepeatCount();

    /**
     * Sets the number of repeating cycles.
     * @param newCount the new repeat count
     */
    public void setRepeatCount(int newCount);
    
    /**
     * @return current status of the timer (playing, paused, etc.)
     */
    public TimerStatus getStatus();

    /**
     * Sets the status of the timer
     * @param newStatus new status to use
     */
    public void setStatus(TimerStatus newStatus);

    /**
     * Getter for whether the timer is playing.
     * @return true if timer is playing
     */
    public boolean isPlaying();

    /**
     * Getter for whether the timer is paused.
     * @return true if timer is paused
     */
    public boolean isPaused();

    /**
     * Getter for whether the timer is stopped.
     * @return true if timer is stopped
     */
    public boolean isStopped();

    //
    //
    // STATE FEATURES
    //
    //

    /** 
     * Tells the timer to start playing.
     * If paused, it should start from the current location.
     * If stopped, should restart from the beginning.
     * If playing, should do nothing.
     */
    public void play();

    /**
     * Tells the timer to pause.
     * If paused already, should start from the current location.
     * If stopped, should do nothing.
     * If playing, should pause at the current location.
     */
    public void pause();

    /**
     * Stops the timer.
     * If paused or playing, resets to initial location.
     * If already stopped, does nothing.
     */
    public void stop();

    /**
     * Stops the timer and restarts from the initial location.
     * Usually equivalent to calling <code>stop()</code> and <code>play()</code>.
     */
    public void restart();

    /**
     * Should slow down the timer, if it is playing.
     * If stopped or paused, should do nothing.
     * Should do precisely the opposite of <code>speedUp()</code>.
     */
    public void slowDown();

    /**
     * Should speed up the timer, if it is playing.
     * If stopped or paused, should do nothing.
     * Should do precisely the opposite of <code>slowDown()</code>.
     */
    public void speedUp();

    //
    //
    // ACTION EVENT HANDLING
    //
    //

    /**
     * Adds a listener to receive timer change events
     * @param l the interested listener
     */
    public void addActionListener(ActionListener l);

    /**
     * Removes a listener to stop receiving change events
     * @param l the listener
     */
    public void removeActionListener(ActionListener l);
}
