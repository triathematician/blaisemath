/**
 * VGraphicComponent.java
 * Created on Jul 30, 2009
 */
package com.googlecode.blaisemath.visometry;

/*
 * #%L
 * BlaiseVisometry
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

import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;

/**
 * <p>
 *      A plot window displaying a collection of objects defined in local coordinates.
 *      These elements are graphics primitives within a {@link VGraphicRoot} object.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 *
 * @author Elisha Peterson
 */
public class VGraphicComponent<C> extends JGraphicComponent {

    /** Logging global - minimum time in ms for alert */
    private static final int MS_TO_REPORT = 100;
    /** Logging global - counters */
    private static int N_REC = 0, N_CONV = 0, N_DRAW = 0;
    
    

    /** Stores the tree of graphics primitives in local & window coords */
    protected final VGraphicRoot<C,Graphics2D> vRoot;

    //
    // CONSTRUCTOR
    //

    /**
     * Construction of a generic plot component requires a visometry.
     * @param vis the visometry for the component
     */
    public VGraphicComponent(Visometry<C> vis) {
        vRoot = new VGraphicRoot<C,Graphics2D>(vis);
        vRoot.initComponent(this);

        root.addGraphic(vRoot.windowEntry);

        addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e) { 
                getVisometry().setWindowBounds(getVisibleRect()); 
            }
            @Override public void componentShown(ComponentEvent e) {
                getVisometry().setWindowBounds(getVisibleRect()); 
            }
        });
        addAncestorListener(new AncestorListener(){
            public void ancestorAdded(AncestorEvent e) { 
                getVisometry().setWindowBounds(getVisibleRect());
            }
            public void ancestorRemoved(AncestorEvent e) {
                // do nothing
            }
            public void ancestorMoved(AncestorEvent e) {
                // do nothing
            }
        });

        repaint();
    }


    //
    // PROPERTIES
    //

    /**
     * Return component's visometry.
     * @return visometry underlying the component
     */
    public Visometry<C> getVisometry() {
        return vRoot.getVisometry();
    }

    /**
     * Return root object containing local graphics.
     * @return local graphics object
     */
    public VGraphicRoot<C,Graphics2D> getVisometryGraphicRoot() {
        return vRoot;
    }


    //
    // COMPOSITE METHODS
    //

    /**
     * Adds a graphic object to the VGraphic tree.
     * @param gfc the graphic to add
     */
    public void addGraphic(VGraphic<C,Graphics2D> gfc) {
        vRoot.addGraphic(gfc);
    }

    /**
     * Removes a graphic object to the VGraphic tree.
     * @param gfc the graphic to remove
     */
    public void removeGraphic(VGraphic<C,Graphics2D> gfc) {
        vRoot.removeGraphic(gfc);
    }


    //
    // PAINT METHODS
    //

    /**
     * Hook for recomputation. Called first within the renderTo method.
     */
    protected void recompute() {
        // do nothing
    }

    /**
     * Renders to an arbitrary graphics canvas. The render process has several
     * steps: (i) recompute, (ii) reconvert, (iii) render underlay,
     * (iv) render primitives, (v) render overlay. This class instruments
     * the process, capturing the time taken for each of these steps.
     *
     * @param canvas the canvas to render to
     */
    @Override
    public final synchronized void renderTo(Graphics2D canvas) {
        // this will recompute objects if necessary
        long t0 = System.currentTimeMillis();
        recompute();

        // this ensures all entries are converted into window coordinates
        long t1 = System.currentTimeMillis();
        vRoot.reconvert();

        // this draws the entries using the active renderer
        long t2 = System.currentTimeMillis();
        super.renderTo(canvas);

        // complete instrumentation
        long t3 = System.currentTimeMillis();
        instrument(t0, t1, t2, t3);
    }


    //<editor-fold defaultstate="collapsed" desc="INSTRUMENTATION UTILS">
    //
    // INSTRUMENTATION UTILS
    //
    
    /** Prints warning messages if any of the render steps takes too long */
    private void instrument(long t0, long t1, long t2, long t3) {
        if (t1-t0 > MS_TO_REPORT) {
            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.FINE, 
                    "Long plottables recompute {0}: {1}", new Object[]{++N_REC, t1-t0});
        }
        if (t2-t1 > MS_TO_REPORT) {
            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.FINE, 
                    "Long plottables conversion {0}: {1}", new Object[]{++N_CONV, t2-t1});
        }
        if (t3-t2 > MS_TO_REPORT) {
            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.FINE, 
                    "Long redraw {0}: {1}", new Object[]{++N_DRAW, t3-t2});
        }
    }
    
    //</editor-fold>

}
