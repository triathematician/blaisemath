package com.googlecode.blaisemath.graphics.swing

import com.google.common.graph.EndpointPair
import com.googlecode.blaisemath.coordinate.OrientedPoint2D
import com.googlecode.blaisemath.graphics.DelegatingPrimitiveGraphic
import com.googlecode.blaisemath.graphics.PrimitiveGraphic
import com.googlecode.blaisemath.graphics.impl.DelegatingNodeLinkGraphic
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.graphics.swing.render.*
import com.googlecode.blaisemath.primitive.AnchoredIcon
import com.googlecode.blaisemath.primitive.AnchoredImage
import com.googlecode.blaisemath.primitive.AnchoredText
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.ObjectStyler
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.*
import java.awt.geom.Point2D
import javax.swing.Icon

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
 * Factory methods for creating basic java2d-based graphics.
 *
 * @author Elisha Peterson
 */
object JGraphics {
    /** Default stroke of 1 unit width.  */
    val DEFAULT_STROKE: BasicStroke? = BasicStroke(1.0f)

    /** Default composite  */
    val DEFAULT_COMPOSITE: Composite? = AlphaComposite.getInstance(AlphaComposite.SRC_OVER)

    //region FACTORIES
    fun path(primitive: Shape?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_PATH_STYLE.copy(), PathRenderer.Companion.getInstance())
    }

    fun path(primitive: Shape?, style: AttributeSet?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, PathRenderer.Companion.getInstance())
    }

    fun <S> path(source: S?, primitive: Shape?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Shape?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, PathRenderer.Companion.getInstance())
    }

    fun shape(primitive: Shape?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_SHAPE_STYLE.copy(), ShapeRenderer.Companion.getInstance())
    }

    fun shape(primitive: Shape?, style: AttributeSet?): PrimitiveGraphic<Shape?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, ShapeRenderer.Companion.getInstance())
    }

    fun <S> shape(source: S?, primitive: Shape?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Shape?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, ShapeRenderer.Companion.getInstance())
    }

    fun point(primitive: Point2D?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.Companion.getInstance())
    }

    fun point(primitive: Point2D?, style: AttributeSet?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, MarkerRenderer.Companion.getInstance())
    }

    fun <S> point(source: S?, primitive: Point2D?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Point2D?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, MarkerRenderer.Companion.getInstance())
    }

    fun marker(primitive: OrientedPoint2D?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_POINT_STYLE.copy(), MarkerRenderer.Companion.getInstance())
    }

    fun marker(primitive: OrientedPoint2D?, style: AttributeSet?): PrimitiveGraphic<Point2D?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, MarkerRenderer.Companion.getInstance())
    }

    fun <S> marker(source: S?, primitive: OrientedPoint2D?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, Point2D?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, MarkerRenderer.Companion.getInstance())
    }

    fun text(primitive: AnchoredText?): PrimitiveGraphic<AnchoredText?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, Styles.DEFAULT_TEXT_STYLE.copy(), TextRenderer.Companion.getInstance())
    }

    fun text(primitive: AnchoredText?, style: AttributeSet?): PrimitiveGraphic<AnchoredText?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, style, TextRenderer.Companion.getInstance())
    }

    fun <S> text(source: S?, primitive: AnchoredText?, styler: ObjectStyler<S?>?): DelegatingPrimitiveGraphic<S?, AnchoredText?, Graphics2D?>? {
        return DelegatingPrimitiveGraphic(source, primitive, styler, TextRenderer.Companion.getInstance())
    }

    fun image(primitive: AnchoredImage?): PrimitiveGraphic<AnchoredImage?, Graphics2D?>? {
        return PrimitiveGraphic(primitive, AttributeSet.EMPTY, ImageRenderer.Companion.getInstance())
    }

    fun image(x: Double, y: Double, wid: Double, ht: Double, image: Image?, ref: String?): PrimitiveGraphic<AnchoredImage?, Graphics2D?>? {
        return PrimitiveGraphic(AnchoredImage(x, y, wid, ht, image, ref), AttributeSet.EMPTY, ImageRenderer.Companion.getInstance())
    }

    fun icon(icon: AnchoredIcon?): PrimitiveGraphic<AnchoredIcon?, Graphics2D?>? {
        return PrimitiveGraphic(icon, AttributeSet.EMPTY, IconRenderer.Companion.getInstance())
    }

    fun icon(icon: Icon?, x: Double, y: Double): PrimitiveGraphic<AnchoredIcon?, Graphics2D?>? {
        return PrimitiveGraphic(AnchoredIcon(x, y, icon), AttributeSet.EMPTY, IconRenderer.Companion.getInstance())
    }

    fun <S> nodeLink(): DelegatingNodeLinkGraphic<S?, EndpointPair<S?>?, Graphics2D?>? {
        return DelegatingNodeLinkGraphic(MarkerRenderer.Companion.getInstance(), TextRenderer.Companion.getInstance(), PathRenderer.Companion.getInstance())
    } //endregion
}