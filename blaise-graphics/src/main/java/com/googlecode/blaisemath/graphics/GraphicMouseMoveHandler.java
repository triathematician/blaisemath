package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.googlecode.blaisemath.coordinate.CoordinateBean;
import com.googlecode.blaisemath.coordinate.DraggableCoordinate;
import java.awt.geom.Point2D;

/**
 * Implementation of an object drag using a point property pattern. Maintains
 * a record of the initial point and the relative movement to set a new point as
 * the object is being dragged.
 *
 * @see CoordinateBean
 * @see DraggableCoordinate
 *
 * @author Elisha Peterson
 */
public final class GraphicMouseMoveHandler extends GraphicMouseDragHandler {

    private final DraggableCoordinate<Point2D> bean;

    private Point2D beanStart;

    /**
     * Construct with specified object that can get and set a point
     *
     * @param bean object that can get/set a point
     */
    public GraphicMouseMoveHandler(final DraggableCoordinate<Point2D> bean) {
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
