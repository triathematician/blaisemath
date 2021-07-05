package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

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
*/ /**
 * Render elements on a graphics context, and computes bounding boxes and intersections as needed.
 *
 * @param <S> the type of object to render
 * @param <G> the type of object used for rendering
 * @author Elisha Peterson
</G></S> */
interface Renderer<S, G> {
    /**
     * Render the given object on the given graphics canvas.
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where to render it
     */
    open fun render(primitive: S?, style: AttributeSet?, canvas: G?)

    /**
     * Get the bounding box for the drawn object
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return bounding box around the object
     */
    open fun boundingBox(primitive: S?, style: AttributeSet?, canvas: G?): Rectangle2D?

    /**
     * Test whether rendered primitive contains the given point.
     * @param point the point to test
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return true if rendered primitive contains point
     */
    open fun contains(point: Point2D?, primitive: S?, style: AttributeSet?, canvas: G?): Boolean

    /**
     * Test whether rendered primitive intersects the given rectangle.
     * @param rect rectangle to test intersection with
     * @param primitive the object to render
     * @param style the style used for rendering
     * @param canvas where content is rendered
     * @return true if rendered primitive intersects rectangle
     */
    open fun intersects(rect: Rectangle2D?, primitive: S?, style: AttributeSet?, canvas: G?): Boolean
}