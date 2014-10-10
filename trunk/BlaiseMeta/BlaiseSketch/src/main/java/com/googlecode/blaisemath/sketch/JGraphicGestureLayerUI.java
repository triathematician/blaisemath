/**
 * SketchGestureLayer.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.sketch;

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


import com.googlecode.blaisemath.gesture.SketchGesture;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.util.event.MouseEvents;
import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
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
    private SketchGesture activeGesture = null;
    private Point mouseLoc = null;
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public SketchGesture getActiveGesture() {
        return activeGesture;
    }

    public void setActiveGesture(SketchGesture activeGesture) {
        if (this.activeGesture != activeGesture) {
            if (this.activeGesture != null) {
                this.activeGesture.cancel();
            }
            this.activeGesture = activeGesture;
            if (this.activeGesture != null) {
                this.activeGesture.initiate();
            }
        }
    }
    
    //</editor-fold>

    /**
     * Tells the currently active gesture to finish.
     */
    public void finishGesture() {
        if (activeGesture != null) {
            activeGesture.finish(layer.getView());
            setActiveGesture(null);
        }
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        layer = (JLayer<JGraphicComponent>) c;
        layer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        setActiveGesture(null);
        layer.setLayerEventMask(0);
        layer = null;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if (activeGesture != null) {
            if (mouseLoc != null) {
                g.drawString(activeGesture.getName(), mouseLoc.x+2, mouseLoc.y-2);
            }
            activeGesture.paint(g, layer.getView());
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="MOUSE HANDLING">

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JGraphicComponent> l) {
        if (activeGesture != null) {
            MouseEvents.delegateEvent(e, activeGesture);
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
        if (activeGesture != null) {
            MouseEvents.delegateMotionEvent(e, activeGesture);
            if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
                mouseLoc = e.getPoint();
                l.repaint();
            }
            e.consume();
        }
    }
    
    //</editor-fold>
    
}
