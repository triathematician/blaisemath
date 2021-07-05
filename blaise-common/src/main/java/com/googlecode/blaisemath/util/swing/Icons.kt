package com.googlecode.blaisemath.util.swing

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.*
import java.awt.geom.Ellipse2D
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
*/ /**
 * Utilities for creating icons.
 *
 * @author Elisha Peterson
 */
object Icons {
    /**
     * Create icon that composites one over another.
     * @param icons array of icons, in the order they will be drawn
     * @return composite icon
     */
    fun composite(vararg icons: Icon?): Icon? {
        return CompositeIcon(*icons)
    }

    /**
     * Create an icon by joining several horizontally.
     * @param icons the icons
     * @return joined icon
     */
    fun join(vararg icons: Icon?): Icon? {
        return JoinIcon(*icons)
    }

    /**
     * Create an icon with a letter (or text string), a color, and a size.
     * The icon displays a solid circle overlaid with the letter, using varying
     * shades of the provided color.
     * @param letter the icon letter/text
     * @param color the color
     * @param size the icon size
     * @return icon
     */
    fun letterIcon(letter: String?, color: Color?, size: Int): Icon? {
        return LetterIcon(letter, color, size)
    }
    //region INNER CLASSES
    /** An icon that joins several other icons together on top of each other.  */
    private class CompositeIcon private constructor(vararg icons: Icon?) : Icon {
        private val icons: Array<Icon?>?
        override fun getIconWidth(): Int {
            var max = 0
            for (i in icons) {
                max = Math.max(max, i.getIconWidth())
            }
            return max
        }

        override fun getIconHeight(): Int {
            var max = 0
            for (i in icons) {
                max = Math.max(max, i.getIconHeight())
            }
            return max
        }

        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            for (i in icons) {
                i.paintIcon(c, g, x, y)
            }
        }

        init {
            this.icons = icons
        }
    }

    /** An icon that joins several other icons together horizontally.  */
    private class JoinIcon private constructor(vararg icons: Icon?) : Icon {
        private val icons: Array<Icon?>?
        override fun getIconWidth(): Int {
            var sum = 0
            for (i in icons) {
                sum += i.getIconWidth()
            }
            return sum
        }

        override fun getIconHeight(): Int {
            var max = 0
            for (i in icons) {
                max = Math.max(max, i.getIconHeight())
            }
            return max
        }

        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            var xp = x
            for (i in icons) {
                i.paintIcon(c, g, xp, y)
                xp += i.getIconWidth()
            }
        }

        init {
            this.icons = icons
        }
    }

    /** An icon that displays a text string against a background shape  */
    private class LetterIcon private constructor(private val letter: String?, private val color: Color?, size: Int) : SquareIcon(size) {
        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            val g2 = g as Graphics2D?
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.setColor(Colors.alpha(Colors.lighterThan(color), 128))
            g2.fill(Ellipse2D.Double(x + 1, y + 1, size - 2, size - 2))
            g2.setStroke(BasicStroke(2f))
            g2.draw(Ellipse2D.Double(x + 1, y + 1, size - 2, size - 2))
            g2.setFont(Font(FONTNAME, Font.BOLD, size - 5))
            g2.setColor(color)
            val lett = letter
            val bds = g2.getFontMetrics().getStringBounds(lett, g)
            val lm = g2.getFontMetrics().getLineMetrics(lett, g)
            g2.drawString(lett,
                    x + .5f * size - .5f * bds.width as Float,
                    y + .5f * (size + 1) + .5f * bds.height as Float + .5f * (lm.ascent + lm.descent) - lm.ascent)
        }

        companion object {
            private val FONTNAME: String? = "Dialog"
        }
    } //endregion
}