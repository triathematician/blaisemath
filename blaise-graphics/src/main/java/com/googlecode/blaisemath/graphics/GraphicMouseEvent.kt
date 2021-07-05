package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import junit.framework.TestCase
import java.awt.Component
import java.awt.event.MouseEvent
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
*/ /**
 * A mouse event that captures both the point of the event and the source
 * [Graphic] for the event.
 *
 * @author Elisha Peterson
 */
class GraphicMouseEvent(
        /** Source event  */
        private val baseEvent: MouseEvent?,
        loc: Point2D?, gfc: Graphic<*>?
) : MouseEvent(baseEvent.getSource() as Component,
        baseEvent.getID(), baseEvent.getWhen(), baseEvent.getModifiersEx(),
        baseEvent.getX(), baseEvent.getY(),
        baseEvent.getClickCount(), baseEvent.isPopupTrigger(), baseEvent.getButton()) {
    /** The graphic associated with the event.  */
    private var graphic: Graphic<*>?

    /** Location of event, in graphic coordinates.  */
    private val loc: Point2D?

    /**
     * Return the graphic source of the event.
     * @return graphic associated with the event
     */
    fun getGraphicSource(): Graphic<*>? {
        return graphic
    }

    /**
     * Change graphic source of event.
     * @param gr new source
     */
    fun setGraphicSource(gr: Graphic<*>?) {
        graphic = gr
    }

    /**
     * Return graphic coordinate location of event
     * @return location
     */
    fun getGraphicLocation(): Point2D? {
        return loc
    }

    override fun isConsumed(): Boolean {
        return super.isConsumed() || baseEvent.isConsumed()
    }

    override fun consume() {
        super.consume()
        baseEvent.consume()
    }

    /**
     * Provides a simple way to generate "graphics" mouse events,
     * e.g. those tied to a particular graphics entry. Making this a class
     * of its own allows subclasses to create "more interesting" events
     * associated with a `Graphic`, in particular enriching it
     * with more information, or associating it with the object responsible
     * for creating the `Graphic`.
     */
    class Factory {
        /**
         * Create and return an event associated with the specified entry and point.
         * @param event base event
         * @param loc location to use
         * @param gfc associated graphic
         * @return generic event
         */
        fun createEvent(event: MouseEvent?, loc: Point2D?, gfc: Graphic<*>?): GraphicMouseEvent? {
            return GraphicMouseEvent(event, loc, gfc)
        }
    }

    /**
     * Construct with specified graphic and point.
     * @param evt the associated awt event
     * @param loc the location of the event
     * @param gfc the graphic
     */
    init {
        if (baseEvent.isConsumed()) {
            consume()
        }
        graphic = gfc
        this.loc = loc
    }
}