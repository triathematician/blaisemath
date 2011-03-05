/*
 * GraphicMouseListener.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.specto.graphics;

import utils.RelativePointBean;
import org.bm.blaise.graphics.GraphicMouseEvent;
import org.bm.blaise.graphics.GraphicMouseListener;
import utils.IndexedGetterSetter;

/**
 * For mouse cursor's interaction with shapes.
 *
 * @param <C> underlying coordinate
 * @author Elisha
 */
public interface VGraphicMouseListener<C> {
    /** Called when mouse enters a shape */
    public void mouseEntered(VGraphicMouseEvent<C> e);
    /** Called when mouse exits a shape */
    public void mouseExited(VGraphicMouseEvent<C> e);
    /** Called when mouse press on a shape */
    public void mousePressed(VGraphicMouseEvent<C> e);
    /** Called when mouse dragged on a shape */
    public void mouseDragged(VGraphicMouseEvent<C> e);
    /** Called when mouse released on a shape */
    public void mouseReleased(VGraphicMouseEvent<C> e);
    /** Called when mouse clicked on a shape */
    public void mouseClicked(VGraphicMouseEvent<C> e);

    /** Provides an adapter that converts regular events to VGME's by simple casting. */
    public GraphicMouseListener adapter();

    /** Adapter class with empty methods */
    public static class Adapter<C> implements VGraphicMouseListener<C> {
        public void mouseEntered(VGraphicMouseEvent<C> e) {}
        public void mouseExited(VGraphicMouseEvent<C> e) {}
        public void mousePressed(VGraphicMouseEvent<C> e) {}
        public void mouseDragged(VGraphicMouseEvent<C> e) {}
        public void mouseReleased(VGraphicMouseEvent<C> e) {}
        public void mouseClicked(VGraphicMouseEvent<C> e) {}

        public GraphicMouseListener adapter() {
            return new GraphicMouseListener(){
                public void mouseEntered(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mouseEntered((VGraphicMouseEvent<C>) e); }
                public void mouseExited(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mouseExited((VGraphicMouseEvent<C>) e); }
                public void mousePressed(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mousePressed((VGraphicMouseEvent<C>) e); }
                public void mouseDragged(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mouseDragged((VGraphicMouseEvent<C>) e); }
                public void mouseReleased(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mouseReleased((VGraphicMouseEvent<C>) e); }
                public void mouseClicked(GraphicMouseEvent e) { VGraphicMouseListener.Adapter.this.mouseClicked((VGraphicMouseEvent<C>) e); }
            };
        }
    }

    /** Class that also captures starting point of a drag event, making it easier to implement drag behavior */
    public abstract static class Dragger<C> extends Adapter<C> {
        /** Stores the starting point of the drag */
        C start;

        /** Called when the mouse is pressed, starting the drag */
        public abstract void mouseDragInitiated(VGraphicMouseEvent<C> e, C start);
        /** Called as mouse drag is in progress */
        public abstract void mouseDragInProgress(VGraphicMouseEvent<C> e, C start);
        /** Called when the mouse is released, finishing the drag */
        public void mouseDragCompleted(VGraphicMouseEvent<C> e, C start) {}

        @Override public final void mousePressed(VGraphicMouseEvent<C> e) { start = e.local; mouseDragInitiated(e, start); }
        @Override public final void mouseDragged(VGraphicMouseEvent<C> e) { if (start == null) mousePressed(e); mouseDragInProgress(e, start); }
        @Override public final void mouseReleased(VGraphicMouseEvent<C> e) { if (start != null) { mouseDragCompleted(e, start); start = null; } }
        @Override public final void mouseExited(VGraphicMouseEvent<C> e) { if (start != null) mouseReleased(e); }
    }

    /** Implementation of an object dragger using a bean pattern */
    public static class PointDragger<C> extends Dragger<C> {
        RelativePointBean<C> bean;
        C beanStart;
        public PointDragger(RelativePointBean<C> b) { this.bean = b; }
        @Override public void mouseDragInitiated(VGraphicMouseEvent<C> e, C start) { beanStart = bean.getPoint(); }
        @Override public void mouseDragInProgress(VGraphicMouseEvent<C> e, C start) { bean.setPoint(beanStart, start, e.local); }
    };

    /** Implementation of an object dragger using an indexed bean pattern */
    public static class IndexedPointDragger<C> extends Dragger<C> {
        IndexedGetterSetter.Relative<C> bean;
        int i;
        C beanStart;
        public IndexedPointDragger(IndexedGetterSetter.Relative<C> b, int i) { this.bean = b; this.i = i; }
        @Override public void mouseDragInitiated(VGraphicMouseEvent<C> e, C start) { beanStart = bean.getElement(i); }
        @Override public void mouseDragInProgress(VGraphicMouseEvent<C> e, C start) { bean.setElement(i, beanStart, start, e.local); }
    };

}
