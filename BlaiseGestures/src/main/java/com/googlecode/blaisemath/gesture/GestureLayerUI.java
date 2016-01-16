/**
 * SketchGestureLayer.java
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


import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.util.event.MouseEvents;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

/**
 * Adds a layer on top of a component to handle gestures. Creates a
 * {@link GestureOrchestrator} when the UI is installed, and propagates events
 * from that orchestrator.
 * 
 * @param <V> one of the super types of the view component
 * 
 * @author Elisha
 */
@Beta
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
        layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        orchestrator = new GestureOrchestrator<V>(layer.getView());
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        orchestrator.cancel(orchestrator.getActiveGesture());
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
            if (gesture instanceof MouseGestureSupport) {
                ((MouseGestureSupport)gesture).paint((Graphics2D) g);
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends V> l) {
        // propagate to active gesture if there is one
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture != null) {
            delegateMouseEvent(e, gesture, l);
        } else {
            // we're in discovery mode, give all gestures a chance to claim the event
            for (MouseGesture mg : orchestrator.getGestureStack()) {
                if (mg.activatesWith(e) && orchestrator.activate(mg)) {
                    delegateMouseEvent(e, mg, l);
                    break;
                }
            }
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends V> l) {
        MouseGesture gesture = orchestrator.getActiveGesture();
        if (gesture != null) {
            delegateMouseMotionEvent(e, gesture, l);
        } else {
            // we're in discovery mode, give all gestures a chance to claim the event
            for (MouseGesture mg : orchestrator.getGestureStack()) {
                if (mg.activatesWith(e) && orchestrator.activate(mg)) {
                    delegateMouseMotionEvent(e, mg, l);
                    break;
                }
            }
        }
    }
    
    /** Delegates the mouse event to the given gesture, updating the mouse loc in the process. */
    private void delegateMouseEvent(MouseEvent e, MouseGesture gesture, JLayer<? extends V> l) {
        MouseEvents.delegateEvent(e, gesture);
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            mouseLoc = e.getPoint();
            l.repaint();
        } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
            mouseLoc = null;
            l.repaint();
        }
        if (gesture.isConsuming()) {
            e.consume();
        }
    }
    
    /** Delegates the mouse motion event to the given gesture, updating the mouse loc in the process. */
    private void delegateMouseMotionEvent(MouseEvent e, MouseGesture gesture, JLayer<? extends V> l) {
        MouseEvents.delegateMotionEvent(e, gesture);
        if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
            mouseLoc = e.getPoint();
            l.repaint();
        }
        if (gesture.isConsuming()) {
            e.consume();
        }
    }
    
    //</editor-fold>
    
}