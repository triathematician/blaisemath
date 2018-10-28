/*
 * Styles.java
 * Created May 9, 2013
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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


import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.primitives.Floats;
import com.googlecode.blaisemath.graphics.swing.PathRenderer;
import static com.googlecode.blaisemath.style.Anchor.*;
import com.googlecode.blaisemath.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Factory class providing convenience methods for easily creating styles.
 * 
 * @author Elisha Peterson
 */
public final class Styles {

    private static final Logger LOG = Logger.getLogger(Styles.class.getName());
    
    //region SVG STYLE ATTRIBUTE CONSTANTS
    
    @SvgAttribute
    public static final String FILL = "fill";
    @SvgAttribute
    public static final String FILL_OPACITY = "fill-opacity";
    
    @SvgAttribute
    public static final String STROKE = "stroke";
    @SvgAttribute
    public static final String STROKE_WIDTH = "stroke-width";
    @SvgAttribute
    public static final String STROKE_DASHES = "stroke-dasharray";
    @SvgAttribute
    public static final String STROKE_OPACITY = "stroke-opacity";

    @SvgAttribute
    public static final String OPACITY = "opacity";
    
    @SvgAttribute
    public static final String FONT = "font-family";
    @SvgAttribute
    public static final String FONT_SIZE = "font-size";
    
    /** Denotes weight of text */
    @SvgAttribute
    public static final String FONT_WEIGHT = "font-weight";
    @SvgAttributeValue
    public static final String FONT_WEIGHT_NORMAL = "normal";
    @SvgAttributeValue
    public static final String FONT_WEIGHT_BOLD = "bold";
    
    /** Denotes style of text */
    @SvgAttribute
    public static final String FONT_STYLE = "font-style";
    @SvgAttributeValue
    public static final String FONT_STYLE_NORMAL = "normal";
    @SvgAttributeValue
    public static final String FONT_STYLE_ITALIC = "italic";
    
    
    /** Denotes anchor of text relative to a point */
    @SvgAttribute
    public static final String TEXT_ANCHOR = "text-anchor";
    @SvgAttributeValue
    public static final String TEXT_ANCHOR_START = "start";
    @SvgAttributeValue
    public static final String TEXT_ANCHOR_MIDDLE = "middle";
    @SvgAttributeValue
    public static final String TEXT_ANCHOR_END = "end";
    
    /** Denotes anchor of text baseline */
    @SvgAttribute
    public static final String ALIGN_BASELINE = "alignment-baseline";
    @SvgAttributeValue
    public static final String ALIGN_BASELINE_BASELINE = "baseline";
    @SvgAttributeValue
    public static final String ALIGN_BASELINE_MIDDLE = "middle";
    @SvgAttributeValue
    public static final String ALIGN_BASELINE_HANGING = "hanging";

    //endregion

    //region CUSTOM STYLE ATTRIBUTE CONSTANTS

    public static final String ID = "id";

    public static final String MARKER = "marker";
    public static final String MARKER_RADIUS = "marker-radius";

    public static final String MARKER_ORIENT = "orient";
            
    /** Denotes offset from a point */
    public static final String OFFSET = "offset";
    
    /** Tooltip text */
    public static final String TOOLTIP = "tooltip";

    /** Associates text/baseline anchor settings with compass directions. */
    private static final Table<String, String, Anchor> ANCHOR_BASELINE_LOOKUP = HashBasedTable.create();
    static {
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_BASELINE, SOUTHWEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_MIDDLE, WEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_START, ALIGN_BASELINE_HANGING, NORTHWEST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_BASELINE, SOUTH);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_MIDDLE, CENTER);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_MIDDLE, ALIGN_BASELINE_HANGING, NORTH);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_BASELINE, SOUTHEAST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_MIDDLE, EAST);
        ANCHOR_BASELINE_LOOKUP.put(TEXT_ANCHOR_END, ALIGN_BASELINE_HANGING, NORTHEAST);
    }
    
    //endregion

    //region DEFAULT STYLE VALUES

    private static final String FONT_DEFAULT = "Dialog";
    private static final int FONT_SIZE_DEFAULT = 12;

    public static final AttributeSet DEFAULT_SHAPE_STYLE = AttributeSet
            .of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_PATH_STYLE = AttributeSet
            .of(STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_POINT_STYLE = AttributeSet
            .of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f, MARKER, Markers.CIRCLE, MARKER_RADIUS, 4)
            .immutable();
    
    public static final AttributeSet DEFAULT_TEXT_STYLE = AttributeSet
            .of(FILL, Color.black, FONT, FONT_DEFAULT, FONT_SIZE, FONT_SIZE_DEFAULT, TEXT_ANCHOR, Anchor.SOUTHWEST)
            .immutable();

    //endregion
    
    // utility class
    private Styles() {
    }

    //region FACTORY METHODS

    public static AttributeSet defaultShapeStyle() {
        return DEFAULT_SHAPE_STYLE;
    }

    public static AttributeSet defaultPathStyle() {
        return DEFAULT_PATH_STYLE;
    }

    public static AttributeSet defaultPointStyle() {
        return DEFAULT_POINT_STYLE;
    }

    public static AttributeSet defaultTextStyle() {
        return DEFAULT_TEXT_STYLE;
    }

    /**
     * Create a basic shape style with given fill and stroke
     * @param fill fill color
     * @param stroke stroke color
     * @return shape style
     */
    public static AttributeSet fillStroke(@Nullable Color fill, @Nullable Color stroke) {
        return AttributeSet.of(FILL, fill, STROKE, stroke);
    }

    /**
     * Create a basic shape style with given fill and stroke
     * @param fill fill color
     * @param stroke stroke color
     * @param width stroke width
     * @return shape style
     */
    public static AttributeSet fillStroke(@Nullable Color fill, @Nullable Color stroke, float width) {
        return AttributeSet.of(FILL, fill, STROKE, stroke, STROKE_WIDTH, width);
    }

    /**
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    public static AttributeSet strokeWidth(Color stroke, float width) {
        return AttributeSet.of(STROKE, stroke, STROKE_WIDTH, width);
    }

    /**
     * Create a style with given fill, size, and anchor
     * @param col fill color of text
     * @param sz font size
     * @param anchor anchor of text
     * @return text style
     */
    public static AttributeSet text(Color col, float sz, Anchor anchor) {
        return AttributeSet.of(FILL, col, FONT_SIZE, sz, TEXT_ANCHOR, anchor);
    }

    /**
     * Create style for a marker with given radius
     * @param marker the marker shape
     * @param fill marker fill
     * @param rad the radius
     * @return style
     */
    public static AttributeSet marker(Marker marker, Color fill, float rad) {
        return AttributeSet.of(MARKER, marker, FILL, fill, MARKER_RADIUS, rad);
    }

    /**
     * Modifies colors in a style set.
     * @return color modifier
     */
    public static StyleModifier defaultColorModifier() {
        return new ColorModifier();
    }

    /**
     * Modifies stroke widths in a style set.
     * @return color modifier
     */
    public static StyleModifier defaultStrokeModifier() {
        return new StrokeWidthModifier();
    }

    /**
     * Create default style context.
     * @return a default style context w/ no parent, but with a standard set of styles
     */
    public static StyleContext defaultStyleContext() {
        StyleContext res = new StyleContext();
        res.addModifier(defaultColorModifier());
        res.addModifier(defaultStrokeModifier());
        return res;
    }

    //endregion
    
    //region TYPED GETTERS
    
    /**
     * Test whether given style has fill parameters: a fill color.
     * @param style style object
     * @return true if fill
     */
    public static boolean hasFill(AttributeSet style) {
        return style.get(Styles.FILL) != null;
    }
    
    /**
     * Test whether given style has stroke parameters: a stroke color and a
     * positive stroke width.
     * @param style style object
     * @return true if stroke
     */
    public static boolean hasStroke(AttributeSet style) {
        Color stroke = style.getColor(Styles.STROKE);
        Float strokeWidth = style.getFloat(Styles.STROKE_WIDTH);
        return stroke != null && strokeWidth != null && strokeWidth > 0;
    }
    
    /**
     * Get fill color from provided style.
     * @param style style object
     * @return fill color
     */
    public static Color fillColorOf(AttributeSet style) {
        Color fill = style.getColor(Styles.FILL);
        int alpha = style.contains(Styles.FILL_OPACITY) ? (int) (255*style.getFloat(Styles.FILL_OPACITY, 1f))
                : style.contains(Styles.OPACITY) ? (int) (255*style.getFloat(Styles.OPACITY, 1f))
                : -1;
        return alpha >= 0 && alpha <= 255 ? Colors.alpha(fill, alpha) : fill;
    }
    
    /**
     * Get stroke color from provided style.
     * @param style style object
     * @return stroke color
     */
    public static Color strokeColorOf(AttributeSet style) {
        Color stroke = style.getColor(Styles.STROKE);
        int alpha = style.contains(Styles.STROKE_OPACITY) ? (int) (255*style.getFloat(Styles.STROKE_OPACITY, 1f))
                : style.contains(Styles.OPACITY) ? (int) (255*style.getFloat(Styles.OPACITY, 1f))
                : -1;
        return alpha >= 0 && alpha <= 255 ? Colors.alpha(stroke, alpha) : stroke;
    }
    
    /**
     * Get font from the provided style.
     * @param style style object
     * @return font
     */
    public static Font fontOf(AttributeSet style) {
        String fontFace = style.getString(Styles.FONT, FONT_DEFAULT);
        int bold = FONT_WEIGHT_BOLD.equals(style.getString(Styles.FONT_WEIGHT, null)) ? Font.BOLD : 0;
        int italic = FONT_STYLE_ITALIC.equals(style.getString(Styles.FONT_STYLE, null)) ? Font.ITALIC : 0;
        Integer pointSize = style.getInteger(Styles.FONT_SIZE, FONT_SIZE_DEFAULT);
        return new Font(fontFace, bold | italic, pointSize);
    }
    
    /**
     * Set font parameters in style to given font.
     * @param style style to set
     * @param font font
     */
    public static void setFont(AttributeSet style, Font font) {
        style.put(FONT, font.getFontName());
        style.put(FONT_SIZE, font.getSize());
        if (font.isBold()) {
            style.put(FONT_WEIGHT, FONT_WEIGHT_BOLD);
        }
        if (font.isItalic()) {
            style.put(FONT_STYLE, FONT_STYLE_ITALIC);
        }
    }
    
    /**
     * Get stroke from the provided style. For dashed lines, because of a potential
     * performance issue, it is recommended to use {@link PathRenderer#drawPatched(java.awt.Shape, java.awt.Graphics2D)}
     * rather than {@link Graphics2D#draw(java.awt.Shape)} if the shape to be drawn
     * is several magnitudes larger than the canvas (e.g. zoomed in very far).
     * See https://bugs.openjdk.java.net/browse/JDK-6620013.
     * 
     * @param style style object
     * @return stroke
     */
    public static Stroke strokeOf(AttributeSet style) {
        float strokeWidth = style.getFloat(Styles.STROKE_WIDTH, 1f);
        String dashes = style.getString(Styles.STROKE_DASHES, null);
        if (!Strings.isNullOrEmpty(dashes)) {
            Iterable<String> sDashes = Splitter.on(",").trimResults().split(dashes);
            try {
                Iterable<Float> fDashes = Floats.stringConverter().convertAll(sDashes);
                float[] fArr = new float[Iterables.size(fDashes)];
                int i = 0;
                for (Float f : fDashes) {
                    fArr[i] = f == null ? 0f : f;
                    i++;
                }
                return new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, 
                        BasicStroke.JOIN_MITER, 10.0f, fArr, 0.0f);
            } catch (NumberFormatException x) {
                LOG.log(Level.WARNING, "Invalid dash pattern: "+dashes, x);
            }
        }
        return new BasicStroke(strokeWidth);
    }
    
    /**
     * Retrieve text anchor from style. Permits the anchor to be either a string
     * or an instance of {@link Anchor}.
     * @param style the style to get anchor from
     * @param def default anchor if there is none set
     * @return anchor
     */
    public static Anchor anchorOf(AttributeSet style, Anchor def) {
        if (style == null) {
            return def;
        }
        
        Object anchor = style.get(Styles.TEXT_ANCHOR);
        Object baseline = style.get(Styles.ALIGN_BASELINE);
        
        if (anchor == null && baseline == null) {
            return def;
        } else if (anchor instanceof Anchor) {
            return (Anchor) anchor;
        } else if (isAnchorName(anchor)) {
            return Anchor.valueOf((String) anchor);
        }
        
        if (anchor == null && baseline instanceof String) {
            anchor = Styles.TEXT_ANCHOR_START;
        } else if (baseline == null && anchor instanceof String) {
            baseline = Styles.ALIGN_BASELINE_BASELINE;
        }
        if (anchor instanceof String && baseline instanceof String) {
            return anchorFromAttributes((String) anchor, (String) baseline, def);
        }
        return def;
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
    public static Anchor toAnchor(@Nullable String textAnchor, @Nullable String alignBaseline) {
        String ta = !(TEXT_ANCHOR_START.equals(textAnchor) || TEXT_ANCHOR_MIDDLE.equals(textAnchor) || TEXT_ANCHOR_END.equals(textAnchor))
                ? TEXT_ANCHOR_START : textAnchor;
        String ab = !(ALIGN_BASELINE_BASELINE.equals(alignBaseline) || ALIGN_BASELINE_MIDDLE.equals(alignBaseline) || ALIGN_BASELINE_HANGING.equals(alignBaseline))
                ? ALIGN_BASELINE_BASELINE : alignBaseline;
        return anchorFromAttributes(ta, ab, Anchor.SOUTHWEST);
    }
    
    /**
     * Get the text-anchor attribute of the given anchor.
     * @param anchor anchor an Anchor or string anchor name
     * @return text-anchor attribute
     */
    public static String toTextAnchor(Anchor anchor) {
        for (Cell<String,String,Anchor> cell : ANCHOR_BASELINE_LOOKUP.cellSet()) {
            if (cell.getValue() == anchor) {
                return cell.getRowKey();
            }
        }
        return TEXT_ANCHOR_START;
    }
    
    /**
     * Get the text-anchor attribute of the given anchor.
     * @param anchorName the string name of an Anchor
     * @return text-anchor attribute
     */
    public static String toTextAnchor(String anchorName) {
        return toTextAnchor(isAnchorName(anchorName) ? Anchor.valueOf(anchorName) : Anchor.SOUTHWEST);
    }
    
    /**
     * Get the alignment-baseline attribute of the given anchor.
     * @param anchor anchor an Anchor or string anchor name
     * @return alignment-baseline attribute
     */
    public static String toAlignBaseline(Anchor anchor) {
        for (Cell<String,String,Anchor> cell : ANCHOR_BASELINE_LOOKUP.cellSet()) {
            if (cell.getValue() == anchor) {
                return cell.getColumnKey();
            }
        }
        return ALIGN_BASELINE_BASELINE;
    }
    
    /**
     * Get the alignment-baseline attribute of the given anchor.
     * @param anchorName the string name of an Anchor
     * @return alignment-baseline attribute
     */
    public static String toAlignBaseline(String anchorName) {
        return toAlignBaseline(isAnchorName(anchorName) ? Anchor.valueOf(anchorName) : Anchor.SOUTHWEST);
    }
    
    /**
     * Tests whether given argument is a string and an anchor name.
     * @param anchor to test
     * @return true if its a string anchor name
     */
    public static boolean isAnchorName(Object anchor) {
        return anchor instanceof String && Stream.of(Anchor.values()).anyMatch(a -> a.name().equals(anchor));
    }
    
    private static Anchor anchorFromAttributes(String anchor, String baseline, Anchor def) {
        return ANCHOR_BASELINE_LOOKUP.contains(anchor, baseline) ? ANCHOR_BASELINE_LOOKUP.get(anchor, baseline) : def;
    }
    
    //endregion
    
    //region MODIFIER UTILS
    
    /**
     * Return highlight-modified version of the style set.
     * @param style style to modify
     * @return default modified style for highlighting
     */
    public static AttributeSet withHighlight(AttributeSet style) {
        return defaultStyleContext().applyModifiers(style, StyleHints.HILITE_HINT);
    }

    //endregion
    
}
