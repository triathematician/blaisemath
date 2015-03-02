/**
 * SketchGestureLayer.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

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


import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.googlecode.blaisemath.gesture.MouseGestureSupport;
import com.googlecode.blaisemath.gesture.MouseGesture;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.util.event.MouseEvents;
import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

/**
 * Adds a layer on top of a graphics canvas to allow for gestures.
 * 
 * @author Elisha
 */
public class JGraphicGestureLayerUI extends LayerUI<JGraphicComponent> {

    private JLayer<JGraphicComponent> layer;
    private GestureOrchestrator orchestrator;
    private final PropertyChangeListener gestureChangeListener;
    private Point mouseLoc = null;

    public JGraphicGestureLayerUI() {
        gestureChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        };
    }

    public GestureOrchestrator getGestureOrchestrator() {
        return orchestrator;
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        layer = (JLayer<JGraphicComponent>) c;
        orchestrator = new GestureOrchestrator(layer.getView());
        orchestrator.addPropertyChangeListener(gestureChangeListener);
        layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        orchestrator.setActiveGesture(null);
        orchestrator.removePropertyChangeListener(gestureChangeListener);
        layer.setLayerEventMask(0);
        layer = null;
        orchestrator = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture != null) {
            if (mouseLoc != null) {
                g.drawString(gesture.getName(), mouseLoc.x+2, mouseLoc.y-2);
            }
            if (gesture instanceof MouseGestureSupport) {
                ((MouseGestureSupport)gesture).paint((Graphics2D) g);
            }
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="DELEGATERS">

    public void finishActiveGesture() {
        if (orchestrator != null) {
            orchestrator.finishActiveGesture();
        }
    }

    public MouseGesture getActiveGesture() {
        return orchestrator == null ? null : orchestrator.getActiveGesture();
    }

    public void setActiveGesture(MouseGesture gesture) {
        if (orchestrator != null) {
            orchestrator.setActiveGesture(gesture);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JGraphicComponent> l) {
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture instanceof MouseListener) {
            MouseEvents.delegateEvent(e, (MouseListener) gesture);
            if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                mouseLoc = e.getPoint();
                l.repaint();
            } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                mouseLoc = null;
                l.repaint();
            }
            e.consume();
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JGraphicComponent> l) {
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture instanceof MouseMotionListener) {
            MouseEvents.delegateMotionEvent(e, (MouseMotionListener) gesture);
            if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
                mouseLoc = e.getPoint();
                l.repaint();
            }
            e.consume();
        }
    }
    
    //</editor-fold>
    
}
