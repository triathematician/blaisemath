/*
 * PointBeanDragger.java
 * Created Jan 12, 2011
 */
package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.Point;
import java.awt.geom.Point2D;
import com.googlecode.blaisemath.util.DraggablePointBean;
import com.googlecode.blaisemath.util.PointBean;

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
