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
            .and(STROKE_WIDTH, 1f);
    public static final AttributeSet DEFAULT_PATH_STYLE = AttributeSet
            .with(STROKE, Color.black)
            .and(STROKE_WIDTH, 1f);
    public static final AttributeSet DEFAULT_POINT_STYLE = AttributeSet
            .with(FILL, Color.white)
            .and(STROKE, Color.black)
            .and(STROKE_WIDTH, 1f)
            .and(MARKER, Markers.CIRCLE)
            .and(MARKER_RADIUS, 4);
    public static final AttributeSet DEFAULT_TEXT_STYLE = AttributeSet
            .with(FILL, Color.black)
            .and(FONT, "Dialog")
            .and(FONT_SIZE, 12f)
            .and(TEXT_ANCHOR, Anchor.SOUTHWEST);

    
    // utility class
    private Styles() {
    }
    
    public static Font getFont(AttributeSet style) {
        String fontFace = style.getString(Styles.FONT, "Dialog");
        Integer pointSize = style.getInteger(Styles.FONT_SIZE, 12);
        return new Font(fontFace, Font.PLAIN, pointSize);
    }
    
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
     * Create a path style with a stroke color and width
     * @param stroke stroke color
     * @param width stroke width
     * @return path style
     */
    public static AttributeSet strokeWidth(Color stroke, float width) {
        return AttributeSet.with(STROKE, stroke).and(STROKE_WIDTH, width);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MODIFIER FACTORY METHODS">
    
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
    
    //</editor-fold>
    
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
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">

    /** Modifier that overrides all colors in the source style using the supplied hints. */
    public static class ColorModifier implements StyleModifier {
        public AttributeSet apply(AttributeSet style, AttributeSet hints) {
            AttributeSet res = new AttributeSet(style);
            for (String key : style.getAllAttributes(Color.class)) {
                res.put(key, StyleHints.applyHints(style.getColor(key), hints));
            }
            return res;
        }
    }

    /** Modifier that modifies stroke-width in the supplied style using the supplied hints. */
    public static class StrokeWidthModifier implements StyleModifier {
        public AttributeSet apply(AttributeSet style, AttributeSet hints) {
            return new AttributeSet(style)
                    .and(STROKE_WIDTH, StyleHints.applyStrokeHints(style.getFloat(STROKE_WIDTH), hints));
        }
    }
    
    //</editor-fold>
    
    
}
