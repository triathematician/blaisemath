/**
 * SketchGesture.java
 * Created Oct 1, 2014
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


import com.googlecode.blaisemath.util.TransformedCoordinateSpace;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * <p>
 *   Partial implementation of {@link MouseGesture} that captures mouse event
 *   locations during move, press, and drag. Transforms the coordinates into local
 *   space if the source of the events is a {@link TransformedCoordinateSpace}.
 * </p>
 * <p>
 *   By default, a mouse release event triggers a call to {@link #complete()}.
 * </p>
 * 
 * @param <V> one of the super types of the view component
 * 
 * @author Elisha
 */
public abstract class MouseGestureSupport<V extends Component> extends MouseAdapter implements MouseGesture {
    
    /** Orchestrates the gesture */
    protected final GestureOrchestrator<V> orchestrator;
    /** The view being orchestrated */
    protected final V view;
    
    /** User-friendly name of the gesture */
    protected final String name;
    /** Description of the gesture */
    protected final String description;

    /** Whether gesture is currently active */
    protected boolean active = false;
    /** Where the mouse cursor currently is */
    protected Point2D movePoint = null;
    /** Where the mouse was pressed */
    protected Point2D pressPoint = null;
    /** Where the mouse is following a press/drag */
    protected Point2D locPoint = null;

    protected MouseGestureSupport(GestureOrchestrator<V> orchestrator, String name, String description) {
        this.orchestrator = orchestrator;
        this.view = orchestrator.getComponent();
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }    
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDesription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="GESTURE LIFECYCLE (EMPTY METHODS)">
    
    @Override
    public boolean activate() {
        active = true;
        return active;
    }
    
    @Override
    public void complete() {
        active = false;
    }
    
    @Override
    public void cancel() {
        active = false;
    }
    

    /**
     * Paint the gesture on the given view. Empty by default.
     * @param g the graphics canvas
     */
    public void paint(Graphics2D g) {
        // do nothing by default
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="DEFAULT MOUSE HANDLING">

    @Override
    public void mouseMoved(MouseEvent e) {
        movePoint = transformedPoint(e);
        view.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        movePoint = null;
        view.repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        pressPoint = transformedPoint(e);
        locPoint = pressPoint;
        view.repaint();
        e.consume();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        locPoint = transformedPoint(e);
        view.repaint();
        e.consume();
    }
    
    /**
     * Override default {@code mouseReleased} behavior to invoke {@link #mouseDragged(java.awt.event.MouseEvent)}
     * and to complete the gesture.
     * @param e mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragged(e);
        orchestrator.completeActiveGesture();
    }
    
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="UTILS">
    
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
    
    //</editor-fold>
    
}
