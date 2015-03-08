/**
 * SketchGesture.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 Elisha Peterson
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

/**
 * A gesture based on mouse events that can be initiated and finished or canceled.
 * Also provides user-friendly name and description fields.
 * 
 * Prior to receiving mouse events, the gesture's {@link #initiate()} method will
 * be called. When the gesture is completed, it may yield control by calling
 * {@link #finish()}, or if another gesture takes over control the {@link #cancel()}
 * method will be called.
 * 
 * @author elisha
 */
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
     * Get the orchestrator that manages the gesture's state
     * @return orchestrator
     */
    GestureOrchestrator getOrchestrator();
        
    /**
     * Enable the gesture, preparing it to be used.
     */
    void initiate();

    /**
     * Disable the gesture, canceling any pending operations.
     */
    void cancel();

    /**
     * Finish the gesture, applying the result.
     */
    void finish();

    /**
     * Whether gesture consumes mouse events that it receives, forbidding others to use them.
     * @return true if consuming, else false
     */
    boolean isConsuming();
    
}
