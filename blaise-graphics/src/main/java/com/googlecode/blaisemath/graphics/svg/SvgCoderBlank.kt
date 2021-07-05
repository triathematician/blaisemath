package com.googlecode.blaisemath.graphics.svg

import com.google.common.annotations.Beta
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.style.AttributeSet
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/*-
* #%L
* blaise-graphics
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
 * Placeholder to decode SVG as an empty string.
 * @author Elisha Peterson
 */
@Beta
class SvgCoderBlank : SvgCoder() {
    override fun encode(obj: SvgGraphic?): String? {
        return UNSUPPORTED_SVG
    }

    override fun decode(str: String?): SvgGraphic? {
        return EmptySvgGraphic()
    }

    override fun graphicFrom(comp: JGraphicComponent?): SvgGraphic? {
        return EmptySvgGraphic()
    }

    /** Graphic for empty/missing SVG content.  */
    private class EmptySvgGraphic : SvgGraphic() {
        override fun getStyle(): AttributeSet? {
            return AttributeSet.EMPTY
        }

        override fun renderTo(canvas: Graphics2D?) {
            // do nothing
        }

        override fun boundingBox(canvas: Graphics2D?): Rectangle2D? {
            return null
        }

        override fun contains(point: Point2D?, canvas: Graphics2D?): Boolean {
            return false
        }

        override fun intersects(box: Rectangle2D?, canvas: Graphics2D?): Boolean {
            return false
        }
    }

    companion object {
        /** String produced when unable to export SVG  */
        val UNSUPPORTED_SVG: String? = "<!-- Unsupported><svg></svg>"
    }
}