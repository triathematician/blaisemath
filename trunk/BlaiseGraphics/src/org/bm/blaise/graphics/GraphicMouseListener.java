/*
 * GraphicMouseListener.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics;

import java.awt.Point;

/**
 * Allows a graphic to interact with a mouse, via the six standard mouse events
 * (enter, exit, press, drag, release, click).
 * Also provides an inner adapter {@link Adapter} to simplify implementations.
 * 
 * @author Elisha Peterson
 */
public interface GraphicMouseListener {
    
    /**
     * Whether listener is interested in a particular mouse location.
     * @param e a point
     * @return true if interested, false otherwise
     */
    public boolean interestedIn(Point p);

    /** 
     * Called when mouse enters a shape.
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mouseEntered(GraphicMouseEvent e);
    
    /** 
     * Called when mouse exits a shape.
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mouseExited(GraphicMouseEvent e);
    
    /** 
     * Called when mouse press on a shape.
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mousePressed(GraphicMouseEvent e);
    
    /** 
     * Called when mouse dragged on a shape.
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mouseDragged(GraphicMouseEvent e);
    
    /** 
     * Called when mouse released on a shape.
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mouseReleased(GraphicMouseEvent e);
    
    /** 
     * Called when mouse clicked on a shape .
     * @param e the source event (stores the point and graphic for the event)
     */
    public void mouseClicked(GraphicMouseEvent e);

    
    
    /** Adapter class with empty methods */
    public static class Adapter implements GraphicMouseListener {
        public boolean interestedIn(Point p) { return true; }
        public void mouseEntered(GraphicMouseEvent e) {}
        public void mouseExited(GraphicMouseEvent e) {}
        public void mousePressed(GraphicMouseEvent e) {}
        public void mouseDragged(GraphicMouseEvent e) {}
        public void mouseReleased(GraphicMouseEvent e) {}
        public void mouseClicked(GraphicMouseEvent e) {}
        
    } // INNER CLASS Adapter
    
    

    /** 
     * Class that also captures starting point of a drag event, 
     * making it easier to implement drag behavior.
     * Instead of working with all six mouse methods, subclasses can work with
     * two or three (dragInitiated, dragInProgress, and optionally dragCompleted).
     */
    public abstract static class Dragger extends Adapter {
        
        /** Stores the starting point of the drag */
        protected Point start;

        /** 
         * Called when the mouse is pressed, starting the drag
         * @param e the source event
         * @param start the initial drag point
         */
        public abstract void mouseDragInitiated(GraphicMouseEvent e, Point start);
        
        /** 
         * Called as mouse drag is in progress 
         * @param e the source event (stores the point and graphic for the event)
         * @param start the initial drag point
         */
        public abstract void mouseDragInProgress(GraphicMouseEvent e, Point start);
        
        /** 
         * Called when the mouse is released, finishing the drag. Does nothing by default.
         * @param e the source event (stores the point and graphic for the event)
         * @param start the initial drag point
         */
        public void mouseDragCompleted(GraphicMouseEvent e, Point start) {}

        @Override
        public final void mouseExited(GraphicMouseEvent e) { 
            if (start != null) 
                mouseReleased(e); 
        }
        
        @Override
        public final void mousePressed(GraphicMouseEvent e) { 
            start = e.point; 
            mouseDragInitiated(e, start); 
        }
        
        @Override
        public final void mouseDragged(GraphicMouseEvent e) { 
            if (start == null) mousePressed(e); 
            mouseDragInProgress(e, start); 
        }
        
        @Override
        public final void mouseReleased(GraphicMouseEvent e) { 
            if (start != null) {
                mouseDragCompleted(e, start);
                start = null; 
            } 
        }
        
    } // INNER CLASS Dragger

}
