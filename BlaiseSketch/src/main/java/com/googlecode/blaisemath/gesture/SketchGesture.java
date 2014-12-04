/**
 * SketchGesture.java
 * Created Oct 11, 2014
 */
package com.googlecode.blaisemath.gesture;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.gesture.swing.GestureOrchestrator;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Generic gesture based on mouse events that can be initiated and finished or canceled.
 * 
 * @param <C> the orchestrator for the gesture
 * @author elisha
 */
public interface SketchGesture<C extends GestureOrchestrator> extends MouseListener, MouseMotionListener {

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
    C getOrchestrator();
        
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
    
}
