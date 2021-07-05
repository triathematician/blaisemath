package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.util.Colors.alpha
import com.googlecode.blaisemath.util.kotlin.javaTrim
import com.googlecode.blaisemath.util.kotlin.warning
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Stroke

/**
 * Factory class providing convenience methods for easily creating styles.
 */
object Styles {

    //region SVG STYLE ATTRIBUTE CONSTANTS

    @SvgAttribute
    const val FILL = "fill"
    @SvgAttribute
    const val FILL_OPACITY = "fill-opacity"

    @SvgAttribute
    const val STROKE = "stroke"
    @SvgAttribute
    const val STROKE_WIDTH = "stroke-width"
    @SvgAttribute
    const val STROKE_DASHES = "stroke-dasharray"
    @SvgAttribute
    const val STROKE_OPACITY = "stroke-opacity"

    @SvgAttribute
    const val OPACITY = "opacity"

    @SvgAttribute
    const val FONT = "font-family"
    @SvgAttribute
    const val FONT_SIZE = "font-size"
    /** Denotes weight of text  */
    @SvgAttribute
    const val FONT_WEIGHT = "font-weight"
    @SvgAttributeValue
    const val FONT_WEIGHT_NORMAL = "normal"
    @SvgAttributeValue
    const val FONT_WEIGHT_BOLD = "bold"
    /** Denotes style of text  */
    @SvgAttribute
    const val FONT_STYLE = "font-style"
    @SvgAttributeValue
    const val FONT_STYLE_NORMAL = "normal"
    @SvgAttributeValue
    const val FONT_STYLE_ITALIC = "italic"

    /** Denotes anchor of text relative to a point  */
    @SvgAttribute
    const val TEXT_ANCHOR = "text-anchor"
    @SvgAttributeValue
    const val TEXT_ANCHOR_START = "start"
    @SvgAttributeValue
    const val TEXT_ANCHOR_MIDDLE = "middle"
    @SvgAttributeValue
    const val TEXT_ANCHOR_END = "end"

    /** Denotes anchor of text baseline  */
    @SvgAttribute
    const val ALIGN_BASELINE = "alignment-baseline"
    @SvgAttributeValue
    const val ALIGN_BASELINE_BASELINE = "baseline"
    @SvgAttributeValue
    const val ALIGN_BASELINE_MIDDLE = "middle"
    @SvgAttributeValue
    const val ALIGN_BASELINE_HANGING = "hanging"

    //endregion

    //region CUSTOM STYLE ATTRIBUTE CONSTANTS

    const val ID = "id"
    const val MARKER = "marker"
    const val MARKER_RADIUS = "marker-radius"
    const val MARKER_ORIENT = "orient"

    /** Denotes offset from a point  */
    const val OFFSET = "offset"

    /** Tooltip text  */
    const val TOOLTIP = "tooltip"

    /** Associates text/baseline anchor settings with compass directions. */
    private val ANCHOR_KEY_LOOKUP = mapOf(
        TEXT_ANCHOR_START to mapOf(ALIGN_BASELINE_BASELINE to Anchor.SOUTHWEST, ALIGN_BASELINE_MIDDLE to Anchor.WEST, ALIGN_BASELINE_HANGING to Anchor.NORTHWEST),
        TEXT_ANCHOR_MIDDLE to mapOf(ALIGN_BASELINE_BASELINE to Anchor.SOUTH, ALIGN_BASELINE_MIDDLE to Anchor.CENTER, ALIGN_BASELINE_HANGING to Anchor.NORTH),
        TEXT_ANCHOR_END to mapOf(ALIGN_BASELINE_BASELINE to Anchor.SOUTHEAST, ALIGN_BASELINE_MIDDLE to Anchor.EAST, ALIGN_BASELINE_HANGING to Anchor.NORTHEAST)
    )
    /** Associates text anchors with compass directions. */
    private val TEXT_ANCHOR_LOOKUP = ANCHOR_KEY_LOOKUP.flatMap { (ta, m) -> m.values.map { it to ta } }.toMap()
    /** Associates align baselines with compass directions. */
    private val ALIGN_BASELINE_LOOKUP = ANCHOR_KEY_LOOKUP.values.flatMap { it.map { (ab, anchor) -> anchor to ab } }.toMap()

    //endregion

    //region DEFAULT STYLE VALUES

    private const val FONT_DEFAULT = "Dialog"
    private const val FONT_SIZE_DEFAULT = 12

    val DEFAULT_SHAPE_STYLE = AttributeSet(FILL to Color.white, STROKE to Color.black, STROKE_WIDTH to 1f).immutable()
    val DEFAULT_PATH_STYLE = AttributeSet(STROKE to Color.black, STROKE_WIDTH to 1f).immutable()
    val DEFAULT_POINT_STYLE = AttributeSet(FILL to Color.white, STROKE to Color.black, STROKE_WIDTH to 1f, MARKER to Markers.CIRCLE, MARKER_RADIUS to 4).immutable()
    val DEFAULT_TEXT_STYLE = AttributeSet(FILL to Color.black, FONT to FONT_DEFAULT, FONT_SIZE to FONT_SIZE_DEFAULT, TEXT_ANCHOR to Anchor.SOUTHWEST).immutable()

    //endregion

    //region FACTORY METHODS

    /** Create style with fill/stroke. */
    fun fillStroke(fill: Color?, stroke: Color?) = AttributeSet(FILL to fill, STROKE to stroke)
    /** Create style with fill/stroke/width. */
    fun fillStroke(fill: Color?, stroke: Color?, width: Float) = AttributeSet(FILL to fill, STROKE to stroke, STROKE_WIDTH to width)
    /** Create style with stroke/width. */
    fun strokeWidth(stroke: Color?, width: Float) = AttributeSet(STROKE to stroke, STROKE_WIDTH to width)

    /** Create a style with given fill, size, and anchor */
    fun text(col: Color?, sz: Float, anchor: Anchor?) = AttributeSet(FILL to col, FONT_SIZE to sz, TEXT_ANCHOR to anchor)
    /** Create style for a marker with given radius */
    fun marker(marker: Marker?, fill: Color?, rad: Float) = AttributeSet(MARKER to marker, FILL to fill, MARKER_RADIUS to rad)

    val defaultColorModifier = ColorModifier()
    val defaultStrokeModifier = StrokeWidthModifier()

    fun defaultStyleContext() = StyleContext().apply {
        addModifier(defaultColorModifier)
        addModifier(defaultStrokeModifier)
    }

    //endregion

    //region TYPED GETTERS

    /** Test whether given style has a fill color. */
    fun hasFill(style: AttributeSet) = style[FILL] != null

    /** Get fill color from provided style. */
    fun fillColorOf(style: AttributeSet): Color? {
        val alpha = when {
            style.contains(FILL_OPACITY) -> (255 * style.getFloat(FILL_OPACITY, 1f)).toInt()
            style.contains(OPACITY) -> (255 * style.getFloat(OPACITY, 1f)).toInt()
            else -> -1
        }
        val fill = style.getColorOrNull(FILL)!!
        return when (alpha) {
            in 0..255 -> fill.alpha(alpha)
            else -> fill
        }
    }

    /** Test whether given style has stroke parameters: a stroke color and a positive stroke width. */
    fun hasStroke(style: AttributeSet): Boolean {
        val strokeWidth = style.getFloatOrNull(STROKE_WIDTH)
        return style.getColorOrNull(STROKE) != null && strokeWidth != null && strokeWidth > 0
    }

    /** Get stroke color from provided style. */
    fun strokeColorOf(style: AttributeSet): Color? {
        val alpha = when {
            style.contains(STROKE_OPACITY) -> (255 * style.getFloat(STROKE_OPACITY, 1f)).toInt()
            style.contains(OPACITY) -> (255 * style.getFloat(OPACITY, 1f)).toInt()
            else -> -1
        }
        val stroke = style.getColorOrNull(STROKE)!!
        return when (alpha) {
            in 0..255 -> stroke.alpha(alpha)
            else -> stroke
        }
    }

    /** Get font from the provided style. */
    fun fontOf(style: AttributeSet): Font? {
        val fontFace = style.getString(FONT, FONT_DEFAULT)
        val bold = if (FONT_WEIGHT_BOLD == style.getStringOrNull(FONT_WEIGHT)) Font.BOLD else 0
        val italic = if (FONT_STYLE_ITALIC == style.getStringOrNull(FONT_STYLE)) Font.ITALIC else 0
        val pointSize = style.getInteger(FONT_SIZE, FONT_SIZE_DEFAULT)
        return Font(fontFace, bold or italic, pointSize)
    }

    /** Set font parameters in style to given font. */
    fun setFont(style: AttributeSet, font: Font) {
        style.put(FONT, font.fontName)
        style.put(FONT_SIZE, font.size)
        if (font.isBold) style.put(FONT_WEIGHT, FONT_WEIGHT_BOLD)
        if (font.isItalic) style.put(FONT_STYLE, FONT_STYLE_ITALIC)
    }

    /**
     * Get stroke from the provided style. For dashed lines, because of a potential performance issue, it is recommended
     * to use a patched version of draw in PathRenderer rather than [Graphics2D.draw] if the shape
     * to be drawn is several magnitudes larger than the canvas (e.g. zoomed in very far).
     * See https://bugs.openjdk.java.net/browse/JDK-6620013.
     */
    fun strokeOf(style: AttributeSet): Stroke? {
        val strokeWidth: Float = style.getFloat(STROKE_WIDTH, 1f)
        val dashes = style.getStringOrNull(STROKE_DASHES)
        if (!dashes.isNullOrEmpty()) {
            try {
                val fDashes = dashes.split(",").map { it.javaTrim() }.map { it.toFloat() }.toFloatArray()
                return BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, fDashes, 0.0f)
            } catch (x: NumberFormatException) {
                warning<Styles>("Invalid dash pattern: $dashes", x)
            }
        }
        return BasicStroke(strokeWidth)
    }

    /** Retrieve text anchor from style. Permits the anchor to be either a string or an instance of [Anchor]. */
    fun anchorOf(style: AttributeSet?, def: Anchor?): Anchor? {
        if (style == null) return def

        var anchor = style[TEXT_ANCHOR]
        var baseline = style[ALIGN_BASELINE]

        // allow [Anchor] object or name in the anchor field
        when {
            anchor == null && baseline == null -> return def
            anchor is Anchor -> return anchor
            isAnchorName(anchor) -> return Anchor.valueOf(anchor as String)
            else -> {} // do nothing
        }

        // update default values
        when {
            anchor == null && baseline is String -> anchor = TEXT_ANCHOR_START
            baseline == null && anchor is String -> baseline = ALIGN_BASELINE_BASELINE
        }

        // lookup anchor from components
        return when {
            anchor is String && baseline is String -> anchorFromAttributes(anchor, baseline, def)
            else -> def
        }
    }

    //endregion

    //region ANCHOR CONVERSIONS

    /**
     * Create an anchor from the given anchor string and baseline string.
     * If either argument is null/invalid, a default value is assumed.
     */
    fun toAnchor(textAnchor: String?, alignBaseline: String?): Anchor? {
        val ta = if (!(TEXT_ANCHOR_START == textAnchor || TEXT_ANCHOR_MIDDLE == textAnchor || TEXT_ANCHOR_END == textAnchor)) TEXT_ANCHOR_START else textAnchor
        val ab = if (!(ALIGN_BASELINE_BASELINE == alignBaseline || ALIGN_BASELINE_MIDDLE == alignBaseline || ALIGN_BASELINE_HANGING == alignBaseline)) ALIGN_BASELINE_BASELINE else alignBaseline
        return anchorFromAttributes(ta, ab, Anchor.SOUTHWEST)
    }

    /** Get the text-anchor attribute of the given anchor. */
    fun toTextAnchor(anchor: Anchor) = TEXT_ANCHOR_LOOKUP[anchor] ?: TEXT_ANCHOR_START
    /** Get the text-anchor attribute of the given anchor. */
    fun toTextAnchor(anchorName: String) = toTextAnchor(if (isAnchorName(anchorName)) Anchor.valueOf(anchorName) else Anchor.SOUTHWEST)

    /** Get the alignment-baseline attribute of the given anchor. */
    fun toAlignBaseline(anchor: Anchor) = ALIGN_BASELINE_LOOKUP[anchor]
    /** Get the alignment-baseline attribute of the given anchor. */
    fun toAlignBaseline(anchorName: String) = toAlignBaseline(if (isAnchorName(anchorName)) Anchor.valueOf(anchorName) else Anchor.SOUTHWEST)

    /** Tests whether given argument is a string and an anchor name. */
    fun isAnchorName(anchor: Any?) = anchor is String && Anchor.values().any { it.name == anchor }

    private fun anchorFromAttributes(anchor: String, baseline: String, def: Anchor?) = ANCHOR_KEY_LOOKUP[anchor]?.get(baseline) ?: def

    //endregion

    //region MODIFIER UTILS

    /** Return highlight-modified version of the style set. */
    fun withHighlight(style: AttributeSet) = defaultStyleContext().applyModifiers(style, StyleHints.HIGHLIGHT_HINT)

    //endregion
}