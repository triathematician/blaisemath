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

import com.google.common.annotations.Beta;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * <p>
 *   A gesture based on mouse events that can be initiated and finished or canceled.
 *   Also provides user-friendly name and description fields.
 * </p>
 * <p>
 *   Prior to receiving mouse events, the gesture's {@link #activate()} method will
 *   be called. When the gesture is completed, it may yield control by calling
 *   {@link #complete()}, or if another gesture takes over control the {@link #cancel()}
 *   method will be called.
 * </p>
 * 
 * @author elisha
 */
@Beta
public interface MouseGesture extends MouseListener, MouseMotionListener {

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
     * Whether gesture consumes mouse events that it receives, forbidding others to use them.
     * @return true if consuming, else false
     */
    boolean isConsuming();
    
    
    // LIFECYCLE
    
    /**
     * Return true if the gesture can use the given event to activate.
     * @param evt mouse event to test
     * @return true if the gesture calls "activatesWith" on the point, false otherwise
     */
    boolean activatesWith(MouseEvent evt);
        
    /**
     * Activate the gesture, preparing it to handle mouse events.
     * Should be called by the orchestrator, not directly invoked.
     * If this method returns false, the orchestrator will not activate the gesture.
     * 
     * @return true if activation was successful, false otherwise
     */
    boolean activate();

    /**
     * Disable the gesture, canceling any pending operations.
     * Should be called by the orchestrator, not directly invoked.
     */
    void cancel();

    /**
     * Completes the gesture, performing final steps and resetting state.
     * Should be called by the orchestrator, not directly invoked.
     */
    void complete();
    
}
