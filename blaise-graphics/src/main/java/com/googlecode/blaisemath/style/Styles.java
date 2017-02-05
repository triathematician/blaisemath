/*
 * Styles.java
 * Created May 9, 2013
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
import com.google.common.primitives.Floats;
import static com.googlecode.blaisemath.style.Anchor.*;
import com.googlecode.blaisemath.util.Colors;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Factory class providing convenience methods for easily creating styles.
 * 
 * @author Elisha
 */
public final class Styles {

    private static final Logger LOG = Logger.getLogger(Styles.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="STYLE ATTRIBUTE CONSTANTS">
    
    public static final String ID = "id";
    
    @SvgAttribute
    public static final String OPACITY = "opacity";
    
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
    
    public static final String MARKER = "marker";
    public static final String MARKER_RADIUS = "marker-radius";
    
    public static final String MARKER_ORIENT = "orient";
    
    @SvgAttribute
    public static final String FONT = "font-family";
    @SvgAttribute
    public static final String FONT_SIZE = "font-size";
    private static final int FONT_SIZE_DEFAULT = 12;
    
    /** Denotes weight of text */
    @SvgAttribute
    public static final String FONT_WEIGHT = "font-weight";
    public static final String FONT_WEIGHT_NORMAL = "normal";
    public static final String FONT_WEIGHT_BOLD = "bold";
    
    /** Denotes style of text */
    @SvgAttribute
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_STYLE_NORMAL = "normal";
    public static final String FONT_STYLE_ITALIC = "italic";
    
    
    /** Denotes anchor of text relative to a point */
    @SvgAttribute
    public static final String TEXT_ANCHOR = "text-anchor";
    public static final String TEXT_ANCHOR_START = "start";
    public static final String TEXT_ANCHOR_MIDDLE = "middle";
    public static final String TEXT_ANCHOR_END = "end";
    
    /** Denotes anchor of text baseline */
    @SvgAttribute
    public static final String ALIGN_BASELINE = "alignment-baseline";
    public static final String ALIGN_BASELINE_BASELINE = "baseline";
    public static final String ALIGN_BASELINE_MIDDLE = "middle";
    public static final String ALIGN_BASELINE_HANGING = "hanging";
            
    /** Denotes offset from a point */
    public static final String OFFSET = "offset";
    
    /** Tooltip text */
    public static final String TOOLTIP = "tooltip";
    
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
    
    //</editor-fold>

    public static final AttributeSet DEFAULT_SHAPE_STYLE = AttributeSet
            .of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_PATH_STYLE = AttributeSet
            .of(STROKE, Color.black, STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_POINT_STYLE = AttributeSet
            .of(FILL, Color.white, STROKE, Color.black, STROKE_WIDTH, 1f)
            .and(MARKER, Markers.CIRCLE)
            .and(MARKER_RADIUS, 4)
            .immutable();
    
    public static final AttributeSet DEFAULT_TEXT_STYLE = AttributeSet
            .of(FILL, Color.black, FONT, "Dialog", FONT_SIZE, 12f)
            .and(TEXT_ANCHOR, Anchor.SOUTHWEST)
            .immutable();
    
    // utility class
    private Styles() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="UTILITY STYLE/JAVA TRANSLATORS">
    
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
        if (style.contains(Styles.OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.OPACITY, 1f)));
        }
        if (style.contains(Styles.FILL_OPACITY)) {
            fill = Colors.alpha(fill, (int) (255*style.getFloat(Styles.FILL_OPACITY, 1f)));
        }
        return fill;
    }
    
    /**
     * Get stroke color from provided style.
     * @param style style object
     * @return stroke color
     */
    public static Color strokeColorOf(AttributeSet style) {
        Color stroke = style.getColor(Styles.STROKE);
        if (style.contains(Styles.OPACITY)) {
            stroke = Colors.alpha(stroke, (int) (255*style.getFloat(Styles.OPACITY, 1f)));
        }
        if (style.contains(Styles.STROKE_OPACITY)) {
            stroke = Colors.alpha(stroke, (int) (255*style.getFloat(Styles.STROKE_OPACITY, 1f)));
        }
        return stroke;
    }
    
    /**
     * Get font from the provided style.
     * @param style style object
     * @return font
     */
    public static Font fontOf(AttributeSet style) {
        String fontFace = style.getString(Styles.FONT, "Dialog");
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
     * Get stroke from the provided style.
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
                Logger.getLogger(Styles.class.getName()).log(Level.WARNING,
                        "Invalid dash pattern: "+dashes, x);
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
        Object anchor = style.get(Styles.TEXT_ANCHOR);
        if (anchor instanceof Anchor) {
            return (Anchor) anchor;
        } else if (isAnchorName(anchor)) {
            return Anchor.valueOf((String) anchor);
        }
        
        Object baseline = style.get(Styles.ALIGN_BASELINE);
        if (anchor instanceof String && baseline instanceof String) {
            return anchorFromAttributes((String) anchor, (String) baseline, def);
        }
        return def;
    }
    
    private static boolean isAnchorName(Object anchor) {
        if (!(anchor instanceof String)) {
            return false;
        }
        for (Anchor a : Anchor.values()) {
            if (anchor.equals(a.name())) {
                return true;
            }
        }
        return false;
    }
    
    private static Anchor anchorFromAttributes(String anchor, String baseline, Anchor def) {
        return ANCHOR_BASELINE_LOOKUP.contains(anchor, baseline) 
                ? ANCHOR_BASELINE_LOOKUP.get(anchor, baseline)
                : def;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="STYLE SET FACTORY METHODS">
    
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
     * @param rad the radius
     * @return style
     */
    public static AttributeSet marker(Marker marker, float rad) {
        return AttributeSet.of(MARKER, marker, MARKER_RADIUS, rad);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MODIFIER & CONTEXT FACTORY METHODS">
    
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
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MODIFIER UTILITIES">
    
    /**
     * Return highlight-modified version of the style set.
     * @param style style to modify
     * @return default modified style for highlighting
     */
    public static AttributeSet withHighlight(AttributeSet style) {
        return defaultStyleContext().applyModifiers(style,
                AttributeSet.of(StyleHints.HILITE_HINT, true));
    }
    
}
