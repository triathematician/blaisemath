package com.googlecode.blaisemath.util.swing

import com.googlecode.blaisemath.geom.ellipse2
import com.googlecode.blaisemath.util.Colors.alpha
import com.googlecode.blaisemath.util.Colors.lightened
import java.awt.*
import javax.swing.Icon

/*-
* #%L
* blaise-common
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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

/** Icon with the same width and height. */
abstract class SquareIcon(val size: Int) : Icon {
    override fun getIconWidth() = size
    override fun getIconHeight() = size
}

/** An icon that joins several other icons together on top of each other.  */
class CompositeIcon(vararg _icons: Icon) : Icon {
    private val icons = _icons

    override fun getIconWidth() = icons.maxOf { it.iconWidth }
    override fun getIconHeight() = icons.maxOf { it.iconHeight }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        icons.forEach { it.paintIcon(c, g, x, y) }
    }
}

/** An icon that joins several other icons together horizontally.  */
class JoinIcon(vararg _icons: Icon) : Icon {
    private val icons = _icons

    override fun getIconWidth() = icons.sumBy { it.iconWidth }
    override fun getIconHeight() = icons.sumBy { it.iconHeight }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        var xp = x
        icons.forEach {
            it.paintIcon(c, g, x, y)
            xp += it.iconWidth
        }
    }
}

/** An icon that displays a text string against a background shape  */
class LetterIcon private constructor(val letter: String, val color: Color, size: Int) : SquareIcon(size) {
    private val FONTNAME: String? = "Dialog"

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = color.lightened().alpha(128)
        g2.fill(ellipse2(x + 1, y + 1, size - 2, size - 2))
        g2.stroke = BasicStroke(2f)
        g2.draw(ellipse2(x + 1, y + 1, size - 2, size - 2))
        g2.font = Font(FONTNAME, Font.BOLD, size - 5)
        g2.color = color
        val bds = g2.fontMetrics.getStringBounds(letter, g)
        val lm = g2.fontMetrics.getLineMetrics(letter, g)
        g2.drawString(letter,
                x + .5f * size - .5f * bds.width.toFloat(),
                y + .5f * (size + 1) + .5f * bds.height.toFloat() + .5f * (lm.ascent + lm.descent) - lm.ascent)
    }
}