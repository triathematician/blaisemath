package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.style.StyleHints.withStyleHintsApplied
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
*/

/** Creates a modified version of a style based on criteria provided as hints. */
interface StyleModifier {
    /**
     * Modify the attribute set according to the given set of hints.
     * @param style the attribute set to modify
     * @param hints the hints w/ modify instructions
     * @return the modified set
     */
    fun apply(style: AttributeSet, hints: Set<String>): AttributeSet
}


/** Apply style hints to colors in the [AttributeSet]. */
class ColorModifier : StyleModifier {
    override fun apply(style: AttributeSet, hints: Set<String>) = AttributeSet().apply {
        parent = style
        for (key in style.attributesOfType<Color>()) {
            put(key, style.getColor(key).withStyleHintsApplied(hints))
        }
    }
}

/**
 * Applies fixed, preset fill/stroke attributes to [AttributeSet] when [StyleHints.HIGHLIGHT_HINT] or
 * [StyleHints.SELECTED_HINT] is active.
 */
class PresetColorModifier : StyleModifier {
    var highlightFill: Color? = null
    var highlightStroke: Color? = null
    var selectFill: Color? = null
    var selectStroke: Color? = null

    override fun apply(style: AttributeSet, hints: Set<String>) = AttributeSet().apply {
        parent = style
        if (hints.contains(StyleHints.HIGHLIGHT_HINT)) {
            put(Styles.FILL, highlightFill)
            put(Styles.STROKE, highlightStroke)
        } else if (hints.contains(StyleHints.SELECTED_HINT)) {
            put(Styles.FILL, selectFill)
            put(Styles.STROKE, selectStroke)
        }
    }
}

/** Apply style hints to stroke-width in the [AttributeSet]. */
class StrokeWidthModifier : StyleModifier {
    override fun apply(style: AttributeSet, hints: Set<String>) = AttributeSet().apply {
        parent = style
        put(Styles.STROKE_WIDTH, StyleHints.strokeWithStyleHintsApplied(style.getFloat(Styles.STROKE_WIDTH), hints))
    }
}