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

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

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
    
    // LIFECYCLE
        
    /**
     * Activate the gesture, preparing it to handle mouse events.
     * Should be called by the orchestrator, not directly invoked.
     * If this method returns false, the orchestrator will not activate the gesture.
     * @return true if initiation was successful, false otherwise
     */
    boolean activate();

    /**
     * Completes the gesture, performing final steps and resetting state.
     * Should be called by the orchestrator, not directly invoked.
     */
    void complete();

    /**
     * Disable the gesture, canceling any pending operations.
     * Should be called by the orchestrator, not directly invoked.
     */
    void cancel();
    
}
