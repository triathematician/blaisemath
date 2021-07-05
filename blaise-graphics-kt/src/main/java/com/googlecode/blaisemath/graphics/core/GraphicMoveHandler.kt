package com.googlecode.blaisemath.graphics.core

import com.googlecode.blaisemath.coordinate.DraggableHasCoordinate
import com.googlecode.blaisemath.util.geom.point2
import java.awt.geom.Point2D

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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

/**
 * Drags object via point property. Stores initial point and relative movement to set a new point as object is dragged.
 * @see HasCoordinate
 * @see DraggableHasCoordinate
 */
class GraphicMoveHandler(private val bean: DraggableHasCoordinate<Point2D>) : GMouseDragHandler() {

    private var beanStart: Point2D? = null

    override fun mouseDragInitiated(e: GMouseEvent, start: Point2D?) {
        beanStart = point2(bean.point)
    }

    override fun mouseDragInProgress(e: GMouseEvent, start: Point2D) {
        bean.setPoint(beanStart!!, start, e.graphicLocation)
    }

    override fun mouseDragCompleted(e: GMouseEvent, start: Point2D) {
        beanStart = null
    }

}