/**
 * SketchGesture.java
 * Created Oct 1, 2014
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


import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;

/**
 * Handler for mouse gestures, tuned to creating objects.
 * @param <C> target component type
 * @author Elisha
 */
public abstract class SketchGesture<C> extends MouseAdapter {
    
    private final String name;
    private final String description;

    public SketchGesture(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDesription() {
        return description;
    }

    /** Enable the gesture, preparing it to be used. */
    public void initiate() {
        // do nothing by default
    }
    
    /** Disable the gesture, cancelling any pending operations. */
    public void cancel() {
        // do nothing by default
    }
    
    /**
     * Finish the gesture, applying the result.
     * @param comp the target component
     */
    public void finish(C comp) {
        // do nothing by default
    }

    /**
     * Paint the gesture on the given view. Empty by default.
     * @param g the graphics canvas
     * @param view the context object for the gesture
     */
    public void paint(Graphics g, C view) {
        // do nothing by default
    }
    
    public static class CreateLineGesture extends SketchGesture {
        public CreateLineGesture() {
            super("Draw line", "Click on the canvas and drag to create a line.");
        }
    }
    
    public static class CreatePathGesture extends SketchGesture {
        public CreatePathGesture() {
            super("Draw path", "Click on the canvas and drag to sketch a path.");
        }
    }
    
    public static class CreateRectangleGesture extends SketchGesture {
        public CreateRectangleGesture() {
            super("Draw rectangle", "Click on the canvas and drag to create a rectangle.");
        }
    }
    
    public static class CreateCircleGesture extends SketchGesture {
        public CreateCircleGesture() {
            super("Draw circle", "Click on the canvas and drag to create a circle.");
        }
    }
    
    public static class CreateEllipseGesture extends SketchGesture {
        public CreateEllipseGesture() {
            super("Draw ellipse", "Click on the canvas and drag to create an ellipse.");
        }
    }
    
    public static class CreateTextGesture extends SketchGesture {
        public CreateTextGesture() {
            super("Place text", "Click on the canvas where you want to add text.");
        }
    }
    
    public static class CreateImageGesture extends SketchGesture {
        public CreateImageGesture() {
            super("Place image", "Click on the canvas where you want to place an image.");
        }
    }
    
}
