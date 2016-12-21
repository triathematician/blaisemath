/**
 * SketchGestureLayer.java
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


import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

/**
 * Adds a layer on top of a component to handle gestures. Creates a
 * {@link GestureOrchestrator} when the UI is installed, and propagates events
 * to that orchestrator.
 * 
 * @param <V> one of the super types of the view component
 * 
 * @author Elisha
 */
public final class GestureLayerUI<V extends Component> extends LayerUI<V> {

    /** Layer component */
    private JLayer<V> layer;
    /** Caches current mouse location, e.g. to paint */
    private Point mouseLoc = null;
    
    /** Manages active gesture */
    private GestureOrchestrator<V> orchestrator;
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    /**
     * Get the orchestrator associated with the layer's component.
     * @return orchestrator, or null if the UI has not been installed
     */
    @Nullable
    public GestureOrchestrator<V> getGestureOrchestrator() {
        return orchestrator;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="LayerUI METHODS">
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        layer = (JLayer<V>) c;
        layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK 
                | AWTEvent.MOUSE_MOTION_EVENT_MASK 
                | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        orchestrator = new GestureOrchestrator<>(layer.getView());
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        orchestrator.cancelActiveGesture();
        orchestrator = null;
        layer.setLayerEventMask(0);
        layer = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture != null) {
            if (mouseLoc != null) {
                g.drawString(gesture.getName(), mouseLoc.x+2, mouseLoc.y-2);
            }
            if (gesture instanceof ActivatingMouseGesture) {
                ((ActivatingMouseGesture)gesture).paint((Graphics2D) g);
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends V> l) {
        orchestrator.processEvent(e);
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            mouseLoc = e.getPoint();
            l.repaint();
        } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
            mouseLoc = null;
            l.repaint();
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends V> l) {
        orchestrator.processEvent(e);
        if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
            mouseLoc = e.getPoint();
            l.repaint();
        }
        // TODO - for mouse motion events, consider letting gesture update mouse cursor
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends V> l) {
        orchestrator.processEvent(e);
    }
    
    //</editor-fold>
    
}
