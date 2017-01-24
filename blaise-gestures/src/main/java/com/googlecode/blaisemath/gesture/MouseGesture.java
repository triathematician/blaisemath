/**
 * MouseGesture.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import javax.annotation.Nullable;

/**
 * <p>
 *   A gesture based on mouse events that can be "initiated" and "completed"
 *   or "canceled". Also provides user-friendly name and description fields.
 *   This is intended for temporarily taking over control of the default mouse
 *   handling for a component, as is done for drawing elements on a canvas.
 * </p>
 * <p>
 *   Prior to receiving mouse events, the gesture's {@link #initiate()} method
 *   will be called by the gesture orchestrator. While the gesture is "active",
 *   it receives all mouse events from the component. The gesture or the
 *   orchestrator may invoke the {@link #complete()} or {@link #cancel()} methods
 *   to either finalize the gesture or reset it.
 * </p>
 * 
 * @author elisha
 */
public interface MouseGesture extends MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Get the user-friendly name of the gesture
     * @return gesture name
     */
    String getName();

    /**
     * Get the user-friendly description of the gesture
     * @return gesture description
     */
    String getDesription();    
    
    /**
     * Return true if gesture has been initiated, but not completed or canceled.
     * @return active
     */
    boolean isActive();

    /**
     * Gets a desired cursor setting, or null if none.
     * @return cursor
     */
    @Nullable
    Integer getDesiredCursor();
    
    /**
     * Whether gesture accepts the given event.
     * @param event event to test
     * @return true if event should be passed to gesture
     */
    boolean shouldHandle(InputEvent event);
    
    /**
     * Whether gesture should be canceled when the given event occurs.
     * @param event event to test
     * @return true if event should cause gesture to be canceled
     */
    boolean cancelsWhen(InputEvent event);
        
    /**
     * Activate the gesture, preparing it to handle mouse events.
     * Should be called by the orchestrator, not directly invoked.
     * If this method returns false, the orchestrator will not activate the gesture.
     * @return true if initiation was successful, false otherwise
     */
    boolean activate();

    /**
     * Completes the gesture, performing final steps and resetting state.
     * Once completed, a gesture will no longer receive events.
     * Should be called by the orchestrator, not directly invoked.
     */
    void complete();

    /**
     * Disable the gesture, canceling any pending operations.
     * Once canceled, a gesture will no longer receive events.
     * Should be called by the orchestrator, not directly invoked.
     * @return true if the gesture was canceled, and should no longer receive events
     */
    boolean cancel();
    
}
