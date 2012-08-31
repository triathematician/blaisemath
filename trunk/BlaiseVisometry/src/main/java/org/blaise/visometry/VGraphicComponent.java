/**
 * VGraphicComponent.java
 * Created on Jul 30, 2009
 */
package org.blaise.visometry;

import javax.swing.event.AncestorEvent;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.event.AncestorListener;
import org.blaise.graphics.GraphicComponent;

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
public class VGraphicComponent<C> extends GraphicComponent {

    /** Stores the tree of graphics primitives in local & window coords */
    protected final VGraphicRoot<C> vRoot;

    //
    // CONSTRUCTOR
    //

    /**
     * Construction of a generic plot component requires a visometry.
     * @param vis the visometry for the component
     */
    public VGraphicComponent(Visometry<C> vis) {
        vRoot = new VGraphicRoot<C>(vis);
        vRoot.initComponent(this);

//        root.setMouseEventFactory(new VGMouseEvent.Factory<C>(vis));
        root.addGraphic(vRoot.windowEntry);

        addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e) { getVisometry().setWindowBounds(getVisibleRect()); }
            @Override public void componentShown(ComponentEvent e) { getVisometry().setWindowBounds(getVisibleRect()); }
        });
        addAncestorListener(new AncestorListener(){
            public void ancestorAdded(AncestorEvent e) { getVisometry().setWindowBounds(getVisibleRect()); }
            public void ancestorRemoved(AncestorEvent e) {}
            public void ancestorMoved(AncestorEvent e) {}
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
    public VGraphicRoot<C> getVisometryGraphicRoot() {
        return vRoot;
    }


    //
    // COMPOSITE METHODS
    //

    /**
     * Adds a graphic object to the VGraphic tree.
     * @param gfc the graphic to add
     */
    public void addGraphic(VGraphic<C> gfc) {
        vRoot.addGraphic(gfc);
    }

    /**
     * Removes a graphic object to the VGraphic tree.
     * @param gfc the graphic to remove
     */
    public void removeGraphic(VGraphic<C> gfc) {
        vRoot.removeGraphic(gfc);
    }


    //
    // PAINT METHODS
    //

    /**
     * Hook for recomputation. Called first within the renderTo method.
     */
    protected void recompute() {
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

    /** Minimum time in ms for alert */
    private static final int THRESH = 100;
    /** Counters */
    private static int _rec = 0, _conv = 0, _draw = 0;
    /** Prints warning messages if any of the render steps takes too long */
    private void instrument(long t0, long t1, long t2, long t3) {
//        if (t1-t0 > THRESH)
//            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.WARNING, "Long plottables recompute {0}: {1}", new Object[]{++_rec, t1-t0});
//        if (t2-t1 > THRESH)
//            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.WARNING, "Long plottables conversion {0}: {1}", new Object[]{++_conv, t2-t1});
//        if (t3-t2 > THRESH)
//            Logger.getLogger(VGraphicComponent.class.getName()).log(Level.WARNING, "Long redraw {0}: {1}", new Object[]{++_draw, t3-t2});
    }
    //</editor-fold>

}
