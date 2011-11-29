/*
 * GraphicIndexedPointDragger.java
 * Created Oct 5, 2011
 */
package org.bm.blaise.graphics;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.bm.util.DraggableIndexedPointBean;
import org.bm.util.IndexedPointBean;

/**
 * Implementation of an object dragger using an indexed point property pattern.
 * 
 * @author Elisha Peterson
 */
public class GraphicIndexedPointDragger extends GraphicMouseListener.Dragger {
    
    private final DraggableIndexedPointBean<Point2D> bean;
    private transient Point2D beanStart;
    private transient int indexStart;

    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public GraphicIndexedPointDragger(final IndexedPointBean<Point2D> bean) {
        this.bean = new DraggableIndexedPointBean<Point2D>(){
            public void setPoint(int i, Point2D initial, Point2D start, Point2D finish) {
                bean.setPoint(i, new Point2D.Double(
                        beanStart.getX() + finish.getX() - start.getX(), 
                        beanStart.getY() + finish.getY() - start.getY()
                        ));
            }
            public int indexOf(Point2D p, Point2D p2) { return bean.indexOf(p, p2); }
            public Point2D getPoint(int i) { return bean.getPoint(i); }
            public void setPoint(int i, Point2D p) { bean.setPoint(i, p); }
            public int getPointCount() { return bean.getPointCount(); }
        };
    }
    
    
    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public GraphicIndexedPointDragger(final DraggableIndexedPointBean<Point2D> bean) {
        this.bean = bean;
    }

    @Override
    public void mouseDragInitiated(GraphicMouseEvent e, Point start) {
        indexStart = bean.indexOf(start, start);
        if (indexStart != -1)
            beanStart = bean.getPoint(indexStart);
    }

    @Override
    public void mouseDragInProgress(GraphicMouseEvent e, Point start) {
        if (indexStart != -1)
            bean.setPoint(indexStart, beanStart, start, e.getPoint());
    }
    
}
