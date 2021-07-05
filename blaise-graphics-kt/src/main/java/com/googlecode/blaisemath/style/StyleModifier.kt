package com.googlecode.blaisemath.style

import java.awt.Color

/** Creates a modified version of a style based on criteria provided as hints. */
interface StyleModifier: (AttributeSet, Set<String>) -> AttributeSet

/** Modifier that overrides all colors in the source style using the supplied hints. */
class ColorModifier : StyleModifier {
    override fun invoke(style: AttributeSet, hints: Set<String>) = AttributeSet(parent = style).apply {
        style.allAttributes.forEach { key -> put(key, StyleHints.modifyColorsDefault(style.getColor(key), hints)) }
    }
}

/** Modifier that modifies stroke-width in the supplied style using the supplied hints. */
class StrokeWidthModifier : StyleModifier {
    override fun invoke(style: AttributeSet, hints: Set<String>) = AttributeSet(parent = style).apply {
        put(Styles.STROKE_WIDTH, StyleHints.modifyStrokeWidthDefault(style.getFloat(Styles.STROKE_WIDTH), hints))
    }
}

/** Modifier that adjusts fill/stroke attributes to preset colors. */
class PresetColorModifier : StyleModifier {

    var highlightFill: Color? = null
    var highlightStroke: Color? = null
    var selectFill: Color? = null
    var selectStroke: Color? = null

    override fun invoke(style: AttributeSet, hints: Set<String>) = AttributeSet(parent = style).apply {
        if (hints.contains(StyleHints.HIGHLIGHT_HINT)) {
            put(Styles.FILL, highlightFill)
            put(Styles.STROKE, highlightStroke)
        }
        if (hints.contains(StyleHints.SELECTED_HINT)) {
            put(Styles.FILL, selectFill)
            put(Styles.STROKE, selectStroke)
        }
    }

}