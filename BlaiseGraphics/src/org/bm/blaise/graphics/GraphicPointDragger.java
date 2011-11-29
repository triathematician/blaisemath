/*
 * GraphicPointDragger.java
 * Created Jan 12, 2011
 */
package org.bm.blaise.graphics;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.bm.util.DraggablePointBean;
import org.bm.util.PointBean;

/**
 * Implementation of an object dragger using a point property pattern.
 * @author elisha
 */
public class GraphicPointDragger extends GraphicMouseListener.Dragger {
    
    private final DraggablePointBean<Point2D> bean;
    private transient Point2D beanStart;

    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public GraphicPointDragger(final PointBean<Point2D> bean) {
        this.bean = new DraggablePointBean<Point2D>(){
            public void setPoint(Point2D initial, Point2D start, Point2D finish) {
                bean.setPoint(new Point2D.Double(
                        beanStart.getX() + finish.getX() - start.getX(), 
                        beanStart.getY() + finish.getY() - start.getY()
                        ));
            }
            public Point2D getPoint() { return bean.getPoint(); }
            public void setPoint(Point2D p) { bean.setPoint(p); }
        };
    }
    
    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public GraphicPointDragger(final DraggablePointBean<Point2D> bean) {
        this.bean = bean;
    }

    @Override
    public void mouseDragInitiated(GraphicMouseEvent e, Point start) {
        beanStart = bean.getPoint();
    }

    @Override
    public void mouseDragInProgress(GraphicMouseEvent e, Point start) {
        bean.setPoint(beanStart, start, e.getPoint());
    }
    
}