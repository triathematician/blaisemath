/*
 * PointBeanDragger.java
 * Created Jan 12, 2011
 */
package org.blaise.graphics;

import java.awt.Point;
import java.awt.geom.Point2D;
import org.blaise.util.DraggablePointBean;
import org.blaise.util.PointBean;

/**
 * Implementation of an object dragger using a point property pattern. Maintains
 * a record of the initial point and the relative movement to set a new point as
 * the object is being dragged.
 *
 * @see PointBean
 * @see DraggablePointBean
 *
 * @author elisha
 */
public final class PointBeanDragger extends AbstractGraphicDragger {

    private final DraggablePointBean<Point2D> bean;

    private transient Point2D beanStart;

    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public PointBeanDragger(final PointBean<Point2D> bean) {
        this.bean = new DraggablePointBean<Point2D>() {
            public void setPoint(Point2D initial, Point2D start, Point2D finish) {
                bean.setPoint(new Point2D.Double(
                        beanStart.getX() + finish.getX() - start.getX(),
                        beanStart.getY() + finish.getY() - start.getY()
                ));
            }
            public Point2D getPoint() {
                return bean.getPoint();
            }
            public void setPoint(Point2D p) {
                bean.setPoint(p);
            }
        };
    }

    /**
     * Construct with specified object that can get and set a point
     *
     * @param bean object that can get/set a point
     */
    public PointBeanDragger(final DraggablePointBean<Point2D> bean) {
        this.bean = bean;
    }

    @Override
    public void mouseDragInitiated(GraphicMouseEvent e, Point2D start) {
        beanStart = new Point2D.Double(bean.getPoint().getX(), bean.getPoint().getY());
    }

    @Override
    public void mouseDragInProgress(GraphicMouseEvent e, Point2D start) {
        bean.setPoint(beanStart, start, e.getGraphicLocation());
    }

    @Override
    public void mouseDragCompleted(GraphicMouseEvent e, Point2D start) {
        beanStart = null;
    }

}
