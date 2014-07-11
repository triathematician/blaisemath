/*
 * IndexedPointBeanDragger.java
 * Created Oct 5, 2011
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

import java.awt.geom.Point2D;
import com.googlecode.blaisemath.util.DraggableIndexedPointBean;
import com.googlecode.blaisemath.util.IndexedPointBean;

/**
 * Implementation of an object dragger using an indexed point property pattern.
 *
 * @author Elisha Peterson
 */
public class IndexedPointBeanDragger extends AbstractGraphicDragger {

    private final DraggableIndexedPointBean<Point2D> bean;

    private transient Point2D beanStart;
    private transient int indexStart;

    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public IndexedPointBeanDragger(final IndexedPointBean<Point2D> bean) {
        this.bean = new DraggableIndexedPointBean<Point2D>() {
            public void setPoint(int i, Point2D initial, Point2D start, Point2D finish) {
                bean.setPoint(i, new Point2D.Double(
                        beanStart.getX() + finish.getX() - start.getX(),
                        beanStart.getY() + finish.getY() - start.getY()
                ));
            }
            public int indexOf(Point2D p, Point2D p2) {
                return bean.indexOf(p, p2);
            }
            public Point2D getPoint(int i) {
                return bean.getPoint(i);
            }
            public void setPoint(int i, Point2D p) {
                bean.setPoint(i, p);
            }
            public int getPointCount() {
                return bean.getPointCount();
            }
        };
    }

    /**
     * Construct with specified object that can get and set a point
     * @param bean object that can get/set a point
     */
    public IndexedPointBeanDragger(DraggableIndexedPointBean<Point2D> bean) {
        this.bean = bean;
    }

    @Override
    public void mouseDragInitiated(GraphicMouseEvent e, Point2D start) {
        indexStart = bean.indexOf(start, start);
        if (indexStart != -1) {
            beanStart = bean.getPoint(indexStart);
        }
    }

    @Override
    public void mouseDragInProgress(GraphicMouseEvent e, Point2D start) {
        if (indexStart != -1) {
            bean.setPoint(indexStart, beanStart, start, e.getGraphicLocation());
        }
    }

    @Override
    public void mouseDragCompleted(GraphicMouseEvent e, Point2D start) {
        beanStart = null;
        indexStart = -1;
    }

}
