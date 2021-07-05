package com.googlecode.blaisemath.graphics.swing.render

import com.google.common.base.Preconditions
import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.ClippedImageTestFrame
import com.googlecode.blaisemath.graphics.swing.ContextMenuTestFrame
import com.googlecode.blaisemath.graphics.swing.HelloWorldTestFrame
import com.googlecode.blaisemath.graphics.swing.PanAndZoomTestFrame
import com.googlecode.blaisemath.graphics.swing.SelectionTestFrame
import com.googlecode.blaisemath.graphics.swing.TooltipTestFrame
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.Styles
import junit.framework.TestCase
import java.awt.Graphics2D
import java.awt.Shape

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
 * This style draws the specified text along a path, rather than drawing a regular path.
 *
 * @author Elisha Peterson
 */
class TextPathRenderer
/** Default constructor.  */
    : PathRenderer() {
    protected var textStyle: AttributeSet? = AttributeSet()
    protected var pathText: String? = "LABEL"
    protected var stretch = false
    override fun toString(): String {
        return String.format("TextPathStyle[textStyle=%s, pathText=%s, stretch=%s",
                textStyle, pathText, stretch)
    }
    //region BUILDERS
    /**
     * Set text style and return pointer to object
     * @param style the style
     * @return this
     */
    fun textStyle(style: AttributeSet?): TextPathRenderer? {
        setTextStyle(style)
        return this
    }

    /**
     * Set path text and return pointer to object
     * @param text the new text
     * @return this
     */
    fun pathText(text: String?): TextPathRenderer? {
        setPathText(text)
        return this
    }

    /**
     * Set "stretch" attribute and return point to object
     * @param stretch new stretch value
     * @return this
     */
    fun stretch(stretch: Boolean): TextPathRenderer? {
        setStretch(stretch)
        return this
    }
    //endregion
    //region PROPERTIES
    /**
     * Get style of text
     * @return text style
     */
    fun getTextStyle(): AttributeSet? {
        return textStyle
    }

    /**
     * Set style of text
     * @param style new style
     */
    fun setTextStyle(style: AttributeSet?) {
        textStyle = Preconditions.checkNotNull(style)
    }

    /**
     * Get text drawn on shape
     * @return text
     */
    fun getPathText(): String? {
        return pathText
    }

    /**
     * Set text drawn on shape
     * @param text the new text
     */
    fun setPathText(text: String?) {
        pathText = Preconditions.checkNotNull(text)
    }

    /**
     * Get stretch attribute
     * @return stretch
     */
    fun isStretch(): Boolean {
        return stretch
    }

    /**
     * Set stretch attribute
     * @param stretch new stretch value
     */
    fun setStretch(stretch: Boolean) {
        this.stretch = stretch
    }

    //endregion
    override fun render(primitive: Shape?, style: AttributeSet?, canvas: Graphics2D?) {
        val f = Styles.fontOf(textStyle)
        canvas.setFont(f)
        canvas.setStroke(TextStroke(pathText, f, stretch, false))
        canvas.setColor(textStyle.getColor(Styles.FILL))
        canvas.draw(primitive)
    }
}