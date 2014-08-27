/*
 * Styles.java
 * Created May 9, 2013
 */
package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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


import java.awt.Color;
import java.awt.Font;
import javax.annotation.Nullable;

/**
 * Factory class providing convenience methods for easily creating styles.
 * 
 * @author Elisha
 */
public final class Styles {
    
    public static final String FILL = "fill";
    public static final String STROKE = "stroke";
    public static final String STROKE_WIDTH = "stroke-width";
    public static final String MARKER = "marker";
    public static final String MARKER_RADIUS = "marker-radius";
    public static final String MARKER_ORIENT = "orient";
    public static final String FONT = "font-family";
    public static final String FONT_SIZE = "font-size";
    
    /** Denotes anchor of text relative to a point */
    public static final String TEXT_ANCHOR = "text-anchor";
    /** Denotes offset from a point */
    public static final String OFFSET = "offset";
    
    public static final AttributeSet DEFAULT_SHAPE_STYLE = AttributeSet
            .with(FILL, Color.white)
            .and(STROKE, Color.black)
            .and(STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_PATH_STYLE = AttributeSet
            .with(STROKE, Color.black)
            .and(STROKE_WIDTH, 1f)
            .immutable();
    
    public static final AttributeSet DEFAULT_POINT_STYLE = AttributeSet
            .with(FILL, Color.white)
            .and(STROKE, Color.black)
            .and(STROKE_WIDTH, 1f)
            .and(MARKER, Markers.CIRCLE)
            .and(MARKER_RADIUS, 4)
            .immutable();
    
    public static final AttributeSet DEFAULT_TEXT_STYLE = AttributeSet
            .with(FILL, Color.black)
            .and(FONT, "Dialog")
            .and(FONT_SIZE, 12f)
            .and(TEXT_ANCHOR, Anchor.SOUTHWEST)
            .immutable();
    
    // utility class
    private Styles() {
    }

    /**
     * Create a partial copy of the attribute set, with only those values matching
     * the given keys.
     * @param sty style to copy from
     * @param keys keys to copy
     * @return copied style
     */
    public static AttributeSet partialCopy(AttributeSet sty, String... keys) {
        AttributeSet res = new AttributeSet();
        for (String k : keys) {
            if (sty.contains(k)) {
                res.put(k, sty.get(k));
            }
        }
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="UTILITY STYLE/JAVA TRANSLATORS">
    
    public static Font getFont(AttributeSet style) {
        String fontFace = style.getString(Styles.FONT, "Dialog");
        Integer pointSize = style.getInteger(Styles.FONT_SIZE, 12);
        return new Font(fontFace, Font.PLAIN, pointSize);
    }
    
    public static void setFont(AttributeSet style, Font font) {
        style.put(Styles.FONT, font.getFontName());
        style.put(Styles.FONT_SIZE, font.getSize());
    }
    
    /**
     * Retrieve text anchor from style. Permits the anchor to be either a string
     * or an instance of {@link Anchor}.
     * @param style the style to get anchor from
     * @param def default anchor if there is none set
     * @return anchor
     */
    public static Anchor getAnchor(AttributeSet style, Anchor def) {
        Object styleAnchor = style.get(Styles.TEXT_ANCHOR);
        if (styleAnchor == null) {
            return def;
        } else if (styleAnchor instanceof String) {
            String styleAnchorText = (String) styleAnchor;
            try {
                return Anchor.valueOf(styleAnchorText);
            } catch (IllegalArgumentException x) {
                throw new IllegalStateException("A text anchor string must match"
                        + " a value of the Anchor enum, but was "+styleAnchorText, x);
            }
        } else if (styleAnchor instanceof Anchor) {
            return (Anchor) styleAnchor;
        } else {
            throw new IllegalStateException("The style "+Styles.TEXT_ANCHOR
                    +" should either be a string or an Anchor, but was "+styleAnchor);
        }
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
     * Create a basic shape style with given fill & stroke
     * @param fill fill color
     * @param stroke stroke color
     * @return shape style
     */
    public static AttributeSet fillStroke(@Nullable Color fill, @Nullable Color stroke) {
        return AttributeSet.with(FILL, fill).and(STROKE, stroke);
    }
   
    /**
     * Create a basic shape style with given fill & stroke
     * @param fill fill color
     * @param stroke stroke color
     * @param width stroke width
     * @return shape style
     */
    public static AttributeSet fillStroke(@Nullable Color fill, @Nullable Color stroke, float width) {
        return AttributeSet.with(FILL, fill).and(STROKE, stroke).and(STROKE_WIDTH, width);
    }
    
    /**
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    public static AttributeSet strokeWidth(Color stroke, float width) {
        return AttributeSet.with(STROKE, stroke).and(STROKE_WIDTH, width);
    }

    /**
     * Create a style with given fill, size, and anchor
     * @param col fill color of text
     * @param sz font size
     * @param anchor anchor of text
     * @return text style
     */
    public static AttributeSet text(Color col, float sz, Anchor anchor) {
        return AttributeSet.with(FILL, col)
                .and(FONT_SIZE, sz)
                .and(TEXT_ANCHOR, anchor);
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
                AttributeSet.with(StyleHints.HILITE_HINT, true));
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">

    /** Modifier that adjusts fill/stroke attributes to preset colors. */
    public static class PresetColorModifier implements StyleModifier {
        private Color highlightFill = null;
        private Color highlightStroke = null;
        private Color selectFill = null;
        private Color selectStroke = null;
                
        public AttributeSet apply(AttributeSet style, AttributeSet hints) {
            AttributeSet res = style;
            if (hints.contains(StyleHints.HILITE_HINT)) {
                res = new AttributeSet(res);
                res.put(Styles.FILL, highlightFill);
                res.put(Styles.STROKE, highlightStroke);
            }
            if (hints.contains(StyleHints.SELECTED_HINT)) {
                res = new AttributeSet(res);
                res.put(Styles.FILL, selectFill);
                res.put(Styles.STROKE, selectStroke);
            }
            return res;
        }

        //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
        //
        // PROPERTY PATTERNS
        //

        public Color getHighlightFill() {
            return highlightFill;
        }

        public void setHighlightFill(Color highlightFill) {
            this.highlightFill = highlightFill;
        }

        public Color getHighlightStroke() {
            return highlightStroke;
        }

        public void setHighlightStroke(Color highlightStroke) {
            this.highlightStroke = highlightStroke;
        }

        public Color getSelectFill() {
            return selectFill;
        }

        public void setSelectFill(Color selectFill) {
            this.selectFill = selectFill;
        }

        public Color getSelectStroke() {
            return selectStroke;
        }

        public void setSelectStroke(Color selectStroke) {
            this.selectStroke = selectStroke;
        }

        //</editor-fold>

    }

    /** Modifier that overrides all colors in the source style using the supplied hints. */
    public static class ColorModifier implements StyleModifier {
        public AttributeSet apply(AttributeSet style, AttributeSet hints) {
            AttributeSet res = new AttributeSet(style);
            for (String key : style.getAllAttributes(Color.class)) {
                res.put(key, StyleHints.modifyColorsDefault(style.getColor(key), hints));
            }
            return res;
        }
    }

    /** Modifier that modifies stroke-width in the supplied style using the supplied hints. */
    public static class StrokeWidthModifier implements StyleModifier {
        public AttributeSet apply(AttributeSet style, AttributeSet hints) {
            return new AttributeSet(style)
                    .and(STROKE_WIDTH, StyleHints.modifyStrokeWidthDefault(style.getFloat(STROKE_WIDTH), hints));
        }
    }
    
    //</editor-fold>
    
    
}
