package com.googlecode.blaisemath.style

import com.google.common.base.Splitter
import com.google.common.base.Strings
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Iterables
import com.google.common.collect.Table
import com.google.common.primitives.Floats
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.primitive.Anchor
import com.googlecode.blaisemath.primitive.Marker
import com.googlecode.blaisemath.primitive.Markers
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Stream

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
 * Factory class providing convenience methods for easily creating styles.
 *
 * @author Elisha Peterson
 */
object Styles {
    private val LOG = Logger.getLogger(Styles::class.java.name)

    //region SVG STYLE ATTRIBUTE CONSTANTS
    @SvgAttribute
    val FILL: String? = "fill"

    @SvgAttribute
    val FILL_OPACITY: String? = "fill-opacity"

    @SvgAttribute
    val STROKE: String? = "stroke"

    @SvgAttribute
    val STROKE_WIDTH: String? = "stroke-width"

    @SvgAttribute
    val STROKE_DASHES: String? = "stroke-dasharray"

    @SvgAttribute
    val STROKE_OPACITY: String? = "stroke-opacity"

    @SvgAttribute
    val OPACITY: String? = "opacity"

    @SvgAttribute
    val FONT: String? = "font-family"

    @SvgAttribute
    val FONT_SIZE: String? = "font-size"

    /** Denotes weight of text  */
    @SvgAttribute
    val FONT_WEIGHT: String? = "font-weight"

    @SvgAttributeValue
    val FONT_WEIGHT_NORMAL: String? = "normal"

    @SvgAttributeValue
    val FONT_WEIGHT_BOLD: String? = "bold"

    /** Denotes style of text  */
    @SvgAttribute
    val FONT_STYLE: String? = "font-style"

    @SvgAttributeValue
    val FONT_STYLE_NORMAL: String? = "normal"

    @SvgAttributeValue
    val FONT_STYLE_ITALIC: String? = "italic"

    /** Denotes anchor of text relative to a point  */
    @SvgAttribute
    val TEXT_ANCHOR: String? = "text-anchor"

    @SvgAttributeValue
    val TEXT_ANCHOR_START: String? = "start"

    @SvgAttributeValue
    val TEXT_ANCHOR_MIDDLE: String? = "middle"

    @SvgAttributeValue
    val TEXT_ANCHOR_END: String? = "end"

    /** Denotes anchor of text baseline  */
    @SvgAttribute
    val ALIGN_BASELINE: String? = "alignment-baseline"

    @SvgAttributeValue
    val ALIGN_BASELINE_BASELINE: String? = "baseline"

    @SvgAttributeValue
    val ALIGN_BASELINE_MIDDLE: String? = "middle"

    @SvgAttributeValue
    val ALIGN_BASELINE_HANGING: String? = "hanging"

    //endregion
    //region CUSTOM STYLE ATTRIBUTE CONSTANTS
    val ID: String? = "id"
    val MARKER: String? = "marker"
    val MARKER_RADIUS: String? = "marker-radius"
    val MARKER_ORIENT: String? = "orient"

    /** Denotes offset from a point  */
    val OFFSET: String? = "offset"

    /** Tooltip text  */
    val TOOLTIP: String? = "tooltip"

    /** Associates text/baseline anchor settings with compass directions.  */
    private val ANCHOR_BASELINE_LOOKUP: Table<String?, String?, Anchor?>? = HashBasedTable.create()

    //endregion
    //region DEFAULT STYLE VALUES
    private val FONT_DEFAULT: String? = "Dialog"
    private const val FONT_SIZE_DEFAULT = 12
    val DEFAULT_SHAPE_STYLE: AttributeSet? = AttributeSet.Companion.of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable()
    val DEFAULT_PATH_STYLE: AttributeSet? = AttributeSet.Companion.of(STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable()
    val DEFAULT_POINT_STYLE: AttributeSet? = AttributeSet.Companion.of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f, MARKER, Markers.CIRCLE, MARKER_RADIUS, 4)
            .immutable()
    val DEFAULT_TEXT_STYLE: AttributeSet? = AttributeSet.Companion.of(FILL, Color.black, FONT, FONT_DEFAULT, FONT_SIZE, FONT_SIZE_DEFAULT, TEXT_ANCHOR, Anchor.SOUTHWEST)
            .immutable()
    //region FACTORY METHODS
    /**
     * Create a basic shape style with given fill and stroke
     * @param fill fill color
     * @param stroke stroke color
     * @return shape style
     */
    fun fillStroke(fill: Color?, stroke: Color?): AttributeSet? {
        return AttributeSet.Companion.of(FILL, fill, STROKE, stroke)
    }

    /**
     * Create a basic shape style with given fill and stroke
     * @param fill fill color
     * @param stroke stroke color
     * @param width stroke width
     * @return shape style
     */
    fun fillStroke(fill: Color?, stroke: Color?, width: Float): AttributeSet? {
        return AttributeSet.Companion.of(FILL, fill, STROKE, stroke, STROKE_WIDTH, width)
    }

    /**
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    fun strokeWidth(stroke: Color?, width: Float): AttributeSet? {
        return AttributeSet.Companion.of(STROKE, stroke, STROKE_WIDTH, width)
    }

    /**
     * Create a style with given fill, size, and anchor
     * @param col fill color of text
     * @param sz font size
     * @param anchor anchor of text
     * @return text style
     */
    fun text(col: Color?, sz: Float, anchor: Anchor?): AttributeSet? {
        return AttributeSet.Companion.of(FILL, col, FONT_SIZE, sz, TEXT_ANCHOR, anchor)
    }

    /**
     * Create style for a marker with given radius
     * @param marker the marker shape
     * @param fill marker fill
     * @param rad the radius
     * @return style
     */
    fun marker(marker: Marker?, fill: Color?, rad: Float): AttributeSet? {
        return AttributeSet.Companion.of(MARKER, marker, FILL, fill, MARKER_RADIUS, rad)
    }

    /**
     * Modifies colors in a style set.
     * @return color modifier
     */
    fun defaultColorModifier(): StyleModifier? {
        return ColorModifier()
    }

    /**
     * Modifies stroke widths in a style set.
     * @return color modifier
     */
    fun defaultStrokeModifier(): StyleModifier? {
        return StrokeWidthModifier()
    }

    /**
     * Create default style context.
     * @return a default style context w/ no parent, but with a standard set of styles
     */
    fun defaultStyleContext(): StyleContext? {
        val res = StyleContext()
        res.addModifier(defaultColorModifier())
        res.addModifier(defaultStrokeModifier())
        return res
    }
    //endregion
    //region TYPED GETTERS
    /**
     * Test whether given style has fill parameters: a fill color.
     * @param style style object
     * @return true if fill
     */
    fun hasFill(style: AttributeSet?): Boolean {
        return style.get(FILL) != null
    }

    /**
     * Test whether given style has stroke parameters: a stroke color and a
     * positive stroke width.
     * @param style style object
     * @return true if stroke
     */
    fun hasStroke(style: AttributeSet?): Boolean {
        val stroke = style.getColor(STROKE)
        val strokeWidth = style.getFloat(STROKE_WIDTH)
        return stroke != null && strokeWidth != null && strokeWidth > 0
    }

    /**
     * Get fill color from provided style.
     * @param style style object
     * @return fill color
     */
    fun fillColorOf(style: AttributeSet?): Color? {
        val fill = style.getColor(FILL)
        val alpha = if (style.contains(FILL_OPACITY)) (255 * style.getFloat(FILL_OPACITY, 1f)) as Int else if (style.contains(OPACITY)) (255 * style.getFloat(OPACITY, 1f)) as Int else -1
        return if (alpha >= 0 && alpha <= 255) Colors.alpha(fill, alpha) else fill
    }

    /**
     * Get stroke color from provided style.
     * @param style style object
     * @return stroke color
     */
    fun strokeColorOf(style: AttributeSet?): Color? {
        val stroke = style.getColor(STROKE)
        val alpha = if (style.contains(STROKE_OPACITY)) (255 * style.getFloat(STROKE_OPACITY, 1f)) as Int else if (style.contains(OPACITY)) (255 * style.getFloat(OPACITY, 1f)) as Int else -1
        return if (alpha >= 0 && alpha <= 255) Colors.alpha(stroke, alpha) else stroke
    }

    /**
     * Get font from the provided style.
     * @param style style object
     * @return font
     */
    fun fontOf(style: AttributeSet?): Font? {
        val fontFace = style.getString(FONT, FONT_DEFAULT)
        val bold = if (FONT_WEIGHT_BOLD == style.getString(FONT_WEIGHT, null)) Font.BOLD else 0
        val italic = if (FONT_STYLE_ITALIC == style.getString(FONT_STYLE, null)) Font.ITALIC else 0
        val pointSize = style.getInteger(FONT_SIZE, FONT_SIZE_DEFAULT)
        return Font(fontFace, bold or italic, pointSize)
    }

    /**
     * Set font parameters in style to given font.
     * @param style style to set
     * @param font font
     */
    fun setFont(style: AttributeSet?, font: Font?) {
        style.put(FONT, font.getFontName())
        style.put(FONT_SIZE, font.getSize())
        if (font.isBold()) {
            style.put(FONT_WEIGHT, FONT_WEIGHT_BOLD)
        }
        if (font.isItalic()) {
            style.put(FONT_STYLE, FONT_STYLE_ITALIC)
        }
    }

    /**
     * Get stroke from the provided style. For dashed lines, because of a potential performance issue, it is recommended
     * to use a patched version of draw in PathRenderer rather than [Graphics2D.draw] if the shape
     * to be drawn is several magnitudes larger than the canvas (e.g. zoomed in very far).
     * See https://bugs.openjdk.java.net/browse/JDK-6620013.
     *
     * @param style style object
     * @return stroke
     */
    fun strokeOf(style: AttributeSet?): Stroke? {
        val strokeWidth: Float = style.getFloat(STROKE_WIDTH, 1f)
        val dashes = style.getString(STROKE_DASHES, null)
        if (!Strings.isNullOrEmpty(dashes)) {
            val sDashes = Splitter.on(",").trimResults().split(dashes)
            try {
                val fDashes = Floats.stringConverter().convertAll(sDashes)
                val fArr = FloatArray(Iterables.size(fDashes))
                var i = 0
                for (f in fDashes) {
                    fArr[i] = f ?: 0f
                    i++
                }
                return BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10.0f, fArr, 0.0f)
            } catch (x: NumberFormatException) {
                LOG.log(Level.WARNING, "Invalid dash pattern: $dashes", x)
            }
        }
        return BasicStroke(strokeWidth)
    }

    /**
     * Retrieve text anchor from style. Permits the anchor to be either a string
     * or an instance of [Anchor].
     * @param style the style to get anchor from
     * @param def default anchor if there is none set
     * @return anchor
     */
    fun anchorOf(style: AttributeSet?, def: Anchor?): Anchor? {
        if (style == null) {
            return def
        }
        var anchor = style[TEXT_ANCHOR]
        var baseline = style[ALIGN_BASELINE]
        if (anchor == null && baseline == null) {
            return def
        } else if (anchor is Anchor) {
            return anchor as Anchor?
        } else if (isAnchorName(anchor)) {
            return Anchor.valueOf(anchor as String?)
        }
        if (anchor == null && baseline is String) {
            anchor = TEXT_ANCHOR_START
        } else if (baseline == null && anchor is String) {
            baseline = ALIGN_BASELINE_BASELINE
        }
        return if (anchor is String && baseline is String) {
            anchorFromAttributes(anchor as String?, baseline as String?, def)
        } else def
    }
    //endregion
    //region ANCHOR CONVERSIONS
    /**
     * Create an anchor from the given anchor string and baseline string.
     * If either argument is null/invalid, a default value is assumed.
     * @param textAnchor anchor string
     * @param alignBaseline baseline string
     * @return anchor
     */
    fun toAnchor(textAnchor: String?, alignBaseline: String?): Anchor? {
        val ta = if (!(TEXT_ANCHOR_START == textAnchor || TEXT_ANCHOR_MIDDLE == textAnchor || TEXT_ANCHOR_END == textAnchor)) TEXT_ANCHOR_START else textAnchor
        val ab = if (!(ALIGN_BASELINE_BASELINE == alignBaseline || ALIGN_BASELINE_MIDDLE == alignBaseline || ALIGN_BASELINE_HANGING == alignBaseline)) ALIGN_BASELINE_BASELINE else alignBaseline
        return anchorFromAttributes(ta, ab, Anchor.SOUTHWEST)
    }

    /**
     * Get the text-anchor attribute of the given anchor.
     * @param anchor anchor an Anchor or string anchor name
     * @return text-anchor attribute
     */
    fun toTextAnchor(anchor: Anchor?): String? {
        for (cell in ANCHOR_BASELINE_LOOKUP.cellSet()) {
            if (cell.value == anchor) {
                return cell.rowKey
            }
        }
        return TEXT_ANCHOR_START
    }

    /**
     * Get the text-anchor attribute of the given anchor.
     * @param anchorName the string name of an Anchor
     * @return text-anchor attribute
     */
    fun toTextAnchor(anchorName: String?): String? {
        return toTextAnchor(if (isAnchorName(anchorName)) Anchor.valueOf(anchorName) else Anchor.SOUTHWEST)
    }

    /**
     * Get the alignment-baseline attribute of the given anchor.
     * @param anchor anchor an Anchor or string anchor name
     * @return alignment-baseline attribute
     */
    fun toAlignBaseline(anchor: Anchor?): String? {
        for (cell in ANCHOR_BASELINE_LOOKUP.cellSet()) {
            if (cell.value == anchor) {
                return cell.columnKey
            }
        }
        return ALIGN_BASELINE_BASELINE
    }

    /**
     * Get the alignment-baseline attribute of the given anchor.
     * @param anchorName the string name of an Anchor
     * @return alignment-baseline attribute
     */
    fun toAlignBaseline(anchorName: String?): String? {
        return toAlignBaseline(if (isAnchorName(anchorName)) Anchor.valueOf(anchorName) else Anchor.SOUTHWEST)
    }

    /**
     * Tests whether given argument is a string and an anchor name.
     * @param anchor to test
     * @return true if its a string anchor name
     */
    fun isAnchorName(anchor: Any?): Boolean {
        return anchor is String && Stream.of(*Anchor.values()).anyMatch { a: Anchor? -> a.name == anchor }
    }

    private fun anchorFromAttributes(anchor: String?, baseline: String?, def: Anchor?): Anchor? {
        return if (ANCHOR_BASELINE_LOOKUP.contains(anchor, baseline)) ANCHOR_BASELINE_LOOKUP.get(anchor, baseline) else def
    }
    //endregion
    //region MODIFIER UTILS
    /**
     * Return highlight-modified version of the style set.
     * @param style style to modify
     * @return default modified style for highlighting
     */
    fun withHighlight(style: AttributeSet?): AttributeSet? {
        return defaultStyleContext().applyModifiers(style, StyleHints.HIGHLIGHT_HINT)
    } //endregion

    init {
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_BASELINE, Anchor.SOUTHWEST)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_MIDDLE, Anchor.WEST)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_HANGING, Anchor.NORTHWEST)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_BASELINE, Anchor.SOUTH)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_MIDDLE, Anchor.CENTER)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_HANGING, Anchor.NORTH)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_BASELINE, Anchor.SOUTHEAST)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_MIDDLE, Anchor.EAST)
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_HANGING, Anchor.NORTHEAST)
    }
}