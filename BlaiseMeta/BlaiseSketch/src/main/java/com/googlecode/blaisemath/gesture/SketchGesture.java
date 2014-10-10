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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Handler for mouse gestures, tuned to creating objects.
 * @param <C> target component type
 * @author Elisha
 */
public abstract class SketchGesture<C> extends MouseAdapter {
    
    protected final String name;
    protected final String description;

    protected Point2D movePoint = null;
    protected Point2D pressPoint = null;
    protected Point2D locPoint = null;

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
    
    //<editor-fold defaultstate="collapsed" desc="DEFAULT MOUSE HANDLING">

    @Override
    public void mouseMoved(MouseEvent e) {
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        movePoint = gc.toGraphicCoordinate(e.getPoint());
        gc.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        movePoint = null;
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        gc.repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        pressPoint = gc.toGraphicCoordinate(e.getPoint());
        locPoint = gc.toGraphicCoordinate(e.getPoint());
        gc.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        locPoint = gc.toGraphicCoordinate(e.getPoint());
        gc.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragged(e);
    }
    
    //</editor-fold>

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
    
}
