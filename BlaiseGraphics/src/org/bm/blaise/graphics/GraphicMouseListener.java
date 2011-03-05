/*
 * GraphicMouseListener.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * For mouse cursor's interaction with shapes.
 * 
 * @author Elisha
 */
public interface GraphicMouseListener {

    /** Called when mouse enters a shape */
    public void mouseEntered(GraphicMouseEvent e);
    /** Called when mouse exits a shape */
    public void mouseExited(GraphicMouseEvent e);
    /** Called when mouse press on a shape */
    public void mousePressed(GraphicMouseEvent e);
    /** Called when mouse dragged on a shape */
    public void mouseDragged(GraphicMouseEvent e);
    /** Called when mouse released on a shape */
    public void mouseReleased(GraphicMouseEvent e);
    /** Called when mouse clicked on a shape */
    public void mouseClicked(GraphicMouseEvent e);

    /** Adapter class with empty methods */
    public static class Adapter implements GraphicMouseListener {
        public void mouseEntered(GraphicMouseEvent e) {}
        public void mouseExited(GraphicMouseEvent e) {}
        public void mousePressed(GraphicMouseEvent e) {}
        public void mouseDragged(GraphicMouseEvent e) {}
        public void mouseReleased(GraphicMouseEvent e) {}
        public void mouseClicked(GraphicMouseEvent e) {}
    }

    /** Class that also captures starting point of a drag event, making it easier to implement drag behavior */
    public abstract static class Dragger implements GraphicMouseListener {
        /** Stores the starting point of the drag */
        Point start;

        /** Called when the mouse is pressed, starting the drag */
        public abstract void mouseDragInitiated(GraphicMouseEvent e, Point start);
        /** Called as mouse drag is in progress */
        public abstract void mouseDragInProgress(GraphicMouseEvent e, Point start);
        /** Called when the mouse is released, finishing the drag */
        public void mouseDragCompleted(GraphicMouseEvent e, Point start) {}

        public final void mouseEntered(GraphicMouseEvent e) {}
        public final void mousePressed(GraphicMouseEvent e) { start = e.pt; mouseDragInitiated(e, start); }
        public final void mouseDragged(GraphicMouseEvent e) { if (start == null) mousePressed(e); mouseDragInProgress(e, start); }
        public final void mouseReleased(GraphicMouseEvent e) { if (start != null) { mouseDragCompleted(e, start); start = null; } }
        public final void mouseExited(GraphicMouseEvent e) { if (start != null) mouseReleased(e); }
        public void mouseClicked(GraphicMouseEvent e) {}
    }

    /** Interface that can get and set a point */
    public static interface PointBean {
        public Point2D getPoint();
        public void setPoint(Point2D p);
    }

    /** Implementation of an object dragger using a bean pattern */
    public static class PointDragger extends Dragger {
        PointBean bean;
        Point2D beanStart;
        public PointDragger(PointBean b) { this.bean = b; }
        @Override
        public void mouseDragInitiated(GraphicMouseEvent e, Point start) { beanStart = bean.getPoint(); }
        @Override
        public void mouseDragInProgress(GraphicMouseEvent e, Point start) { bean.setPoint(new Point2D.Double(beanStart.getX() + e.getX() - start.x, beanStart.getY() + e.getY() - start.y)); }
    };

}
