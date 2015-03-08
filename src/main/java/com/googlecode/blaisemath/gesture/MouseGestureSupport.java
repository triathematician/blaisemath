/**
 * SketchGesture.java
 * Created Oct 1, 2014
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


import com.googlecode.blaisemath.util.TransformedCoordinateSpace;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Partial implementation of {@link MouseGesture} that captures mouse event
 * locations during move, press, and drag. Transforms the coordinates into local
 * space if the source of the events is a {@link TransformedCoordinateSpace}.
 * 
 * @author Elisha
 */
public abstract class MouseGestureSupport extends MouseAdapter implements MouseGesture {
    
    /** Orchestrates the gesture */
    protected final GestureOrchestrator orchestrator;
    /** User-friendly name of the gesture */
    protected final String name;
    /** Description of the gesture */
    protected final String description;
    /** Consuming flag */
    private boolean consuming = true;

    /** Where the mouse cursor currently is */
    protected Point2D movePoint = null;
    /** Where the mouse was pressed */
    protected Point2D pressPoint = null;
    /** Where the mouse is following a press/drag */
    protected Point2D locPoint = null;

    protected MouseGestureSupport(GestureOrchestrator orchestrator, String name, String description) {
        this.orchestrator = orchestrator;
        this.name = name;
        this.description = description;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    @Override
    public GestureOrchestrator getOrchestrator() {
        return orchestrator;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDesription() {
        return description;
    }

    @Override
    public boolean isConsuming() {
        return consuming;
    }

    public void setConsuming(boolean consuming) {
        this.consuming = consuming;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="DEFAULT MOUSE HANDLING">

    @Override
    public void mouseMoved(MouseEvent e) {
        movePoint = transformedPoint(e);
        if (e.getSource() instanceof Component) {
            ((Component) e.getSource()).repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        movePoint = null;
        if (e.getSource() instanceof Component) {
            ((Component) e.getSource()).repaint();
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        pressPoint = transformedPoint(e);
        locPoint = pressPoint;
        if (e.getSource() instanceof Component) {
            ((Component) e.getSource()).repaint();
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        locPoint = transformedPoint(e);
        if (e.getSource() instanceof Component) {
            ((Component) e.getSource()).repaint();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragged(e);
        finish();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="HOOK METHODS">

    @Override
    public void initiate() {
        // do nothing by default
    }
    
    @Override
    public void cancel() {
        // do nothing by default
    }
    
    @Override
    public void finish() {
        // do nothing by default
    }

    /**
     * Paint the gesture on the given view. Empty by default.
     * @param g the graphics canvas
     */
    public void paint(Graphics2D g) {
        // do nothing by default
    }
    
    //</editor-fold>

    
    /**
     * Return a transformed mouse event location if the event's source is a 
     * {@link TransformedCoordinateSpace}, otherwise just the event's point.
     * @param evt the mouse event
     * @return transformed point
     */
    public static Point2D transformedPoint(MouseEvent evt) {
        return evt.getSource() instanceof TransformedCoordinateSpace 
                ? ((TransformedCoordinateSpace)evt.getSource()).toGraphicCoordinate(evt.getPoint())
                : evt.getPoint();
    }
    
}
