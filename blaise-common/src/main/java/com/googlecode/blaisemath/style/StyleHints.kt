package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color

/*
* #%L
* BlaiseGraphics
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
 * Maintains a collection of visibility hints that can be used to change how an
 * object is drawn. The [StyleContext] is responsible for switching out the
 * default style for an alternate style, as appropriate for these hints.
 *
 * @author Elisha Peterson
 */
object StyleHints {
    /** Style hint indicating an invisible, non-functional element.  */
    val HIDDEN_HINT: String? = "hidden"

    /** Style hint indicating an invisible but still functional element (receives mouse events)  */
    val HIDDEN_FUNCTIONAL_HINT: String? = "hidden_functional"

    /** Style hint indicating a selected element.  */
    val SELECTED_HINT: String? = "selected"

    /** Style hint indicating a highlighted element.  */
    val HIGHLIGHT_HINT: String? = "highlight"

    /** Style hint indicating an outlined element.  */
    val OUTLINE_HINT: String? = "outline"

    /** Style hint indicating a low-quality (but fast) rendered element.  */
    val QUICK_RENDER_HINT: String? = "quick_render"

    /**
     * Applies hints to a color
     * @param color the color to apply hints to
     * @param hints the hints to apply
     * @return transformed color
     */
    fun modifyColorsDefault(color: Color?, hints: MutableSet<String?>?): Color? {
        return if (color == null) {
            null
        } else if (hints.contains(HIDDEN_HINT)) {
            Colors.alpha(color, 0)
        } else if (hints.contains(HIGHLIGHT_HINT)) {
            Colors.lighterThan(color)
        } else {
            color
        }
    }

    /**
     * Applies hints to a stroke width
     * @param width the width to apply to
     * @param hints the hints to apply
     * @return transformed width
     */
    fun modifyStrokeWidthDefault(width: Float?, hints: MutableSet<String?>?): Float {
        val wid = if (width == null || width.isNaN() || width.isInfinite()) 1f else width
        return if (hints.contains(HIDDEN_HINT)) {
            0f
        } else if (hints.contains(SELECTED_HINT)) {
            wid + 1f
        } else if (hints.contains(HIGHLIGHT_HINT)) {
            Math.max(wid - 1f, wid / 2f)
        } else {
            wid
        }
    }

    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    fun isInvisible(hints: MutableSet<String?>?): Boolean {
        return hints.contains(HIDDEN_HINT) || hints.contains(HIDDEN_FUNCTIONAL_HINT)
    }

    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    fun isFunctional(hints: MutableSet<String?>?): Boolean {
        return !hints.contains(HIDDEN_HINT)
    }
}