/*
 * PointBeanDragger.java
 * Created Jan 12, 2011
 */
package com.googlecode.blaisemath.graphics.core;

/*
 * #%L
 * BlaiseGraphics
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



import com.googlecode.blaisemath.util.coordinate.CoordinateBean;
import com.googlecode.blaisemath.util.coordinate.DraggableCoordinate;
import java.awt.geom.Point2D;

/**
 * Implementation of an object dragger using a point property pattern. Maintains
 * a record of the initial point and the relative movement to set a new point as
 * the object is being dragged.
 *
 * @see CoordinateBean
 * @see DraggableCoordinate
 *
 * @author elisha
 */
public final class GraphicMoveHandler extends GMouseDragHandler {

    private final DraggableCoordinate<Point2D> bean;

    private transient Point2D beanStart;

    /**
     * Construct with specified object that can get and set a point
     *
     * @param bean object that can get/set a point
     */
    public GraphicMoveHandler(final DraggableCoordinate<Point2D> bean) {
        this.bean = bean;
    }

    @Override
    public void mouseDragInitiated(GMouseEvent e, Point2D start) {
        beanStart = new Point2D.Double(bean.getPoint().getX(), bean.getPoint().getY());
    }

    @Override
    public void mouseDragInProgress(GMouseEvent e, Point2D start) {
        bean.setPoint(beanStart, start, e.getGraphicLocation());
        e.getGraphicSource().fireGraphicChanged();
    }

    @Override
    public void mouseDragCompleted(GMouseEvent e, Point2D start) {
        beanStart = null;
        e.getGraphicSource().fireGraphicChanged();
    }

}
