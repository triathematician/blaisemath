package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.util.Colors.alpha
import com.googlecode.blaisemath.util.Colors.lightened
import java.awt.Color
import kotlin.math.max

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
 * Maintains a collection of visibility hints that can be used to change how an
 * object is drawn. The [StyleContext] is responsible for switching out the
 * default style for an alternate style, as appropriate for these hints.
 */
object StyleHints {

    /** Style hint indicating an invisible, non-functional element.  */
    const val HIDDEN_HINT = "hidden"
    /** Style hint indicating an invisible but still functional element (receives mouse events)  */
    const val HIDDEN_FUNCTIONAL_HINT = "hidden_functional"
    /** Style hint indicating a selected element.  */
    const val SELECTED_HINT = "selected"
    /** Style hint indicating a highlighted element.  */
    const val HIGHLIGHT_HINT = "highlight"
    /** Style hint indicating an outlined element.  */
    const val OUTLINE_HINT = "outline"
    /** Style hint indicating a low-quality (but fast) rendered element.  */
    const val QUICK_RENDER_HINT = "quick_render"

    /** Applies hints to a color. */
    fun modifyColorsDefault(color: Color?, hints: Set<String>) = when {
        color == null -> null
        hints.contains(HIDDEN_HINT) -> color.alpha(0)
        hints.contains(HIGHLIGHT_HINT) -> color.lightened()
        else -> color
    }

    /** Applies hints to a stroke width. */
    fun modifyStrokeWidthDefault(width: Float?, hints: Set<String>): Float {
        val wid = if (width == null || width.isNaN() || width.isInfinite()) 1f else width
        return when {
            hints.contains(HIDDEN_HINT) -> 0f
            hints.contains(SELECTED_HINT) -> wid + 1f
            hints.contains(HIGHLIGHT_HINT) -> max(wid - 1f, wid / 2f)
            else -> wid
        }
    }

    fun isInvisible(hints: Set<String>) = hints.contains(HIDDEN_HINT) || hints.contains(HIDDEN_FUNCTIONAL_HINT)
    fun isFunctional(hints: Set<String>) = !hints.contains(HIDDEN_HINT)
}