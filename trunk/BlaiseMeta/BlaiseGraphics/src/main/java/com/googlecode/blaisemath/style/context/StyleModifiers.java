/**
 * StyleModifiers.java
 * Created Jul 3, 2014
 */
package com.googlecode.blaisemath.style.context;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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
import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PathStyleBasic;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.ShapeStyleBasic;
import com.googlecode.blaisemath.style.Style;
import com.googlecode.blaisemath.style.TextStyle;
import com.googlecode.blaisemath.style.TextStyleBasic;
import com.googlecode.blaisemath.util.ColorUtils;

/**
 * Utility class for modifying styles.
 * @author Elisha
 */
public class StyleModifiers {
    
    /** Modifier that does nothing, but can be applied to any style. */
    private static final StyleModifier INERT_STYLE_MODIFIER = new StyleModifier() {
        @Override
        public Style apply(Style style, StyleHintSet hints) {
            return style;
        }
    };
    
    /** Default style modifier for shapes */
    private static final StyleModifier<ShapeStyle> SHAPE_STYLE_MODIFIER 
            = StyleModifiers.instanceOr(ShapeStyleBasic.class, new ShapeStyleBasicModifier(),
                    INERT_STYLE_MODIFIER);
    
    /** Default style modifier for paths */
    private static final StyleModifier<PathStyle> PATH_STYLE_MODIFIER
            = StyleModifiers.instanceOr(PathStyleBasic.class, new PathStyleBasicModifier(),
                    INERT_STYLE_MODIFIER);
    
    /** Default style modifier for points */
    private static final StyleModifier<PointStyle> POINT_STYLE_MODIFIER
            = StyleModifiers.instanceOr(PointStyleBasic.class, new PointStyleBasicModifier(),
                    INERT_STYLE_MODIFIER);
    
    /** Default style modifier for text */
    private static final StyleModifier<TextStyle> TEXT_STYLE_MODIFIER
            = StyleModifiers.instanceOr(TextStyleBasic.class, new TextStyleBasicModifier(),
                    INERT_STYLE_MODIFIER);

    // utility class - no instantiation
    private StyleModifiers() {
    }

    /**
     * Applies hints to a color
     * @param color the color to apply hints to
     * @param hints the hints to apply
     * @return transformed color
     */
    public static Color applyHints(Color color, StyleHintSet hints) {
        if (hints.contains(StyleHintSet.HIDDEN_HINT)) {
            return ColorUtils.alpha(color, 0);
        } else if (hints.contains(StyleHintSet.HILITE_HINT)) {
            return ColorUtils.lighterThan(color);
        } else if (hints.contains(StyleHintSet.SELECTED_HINT)) {
            return ColorUtils.lighterThan(color);
        } else {
            return color;
        }
    }
    
    /**
     * Applies hints to a stroke width
     * @param width the width to apply to
     * @param hints the hints to apply
     * @return transformed width
     */
    public static float applyStrokeHints(float width, StyleHintSet hints) {
        if (hints.contains(StyleHintSet.HIDDEN_HINT)) {
            return 0f;
        } else if (hints.contains(StyleHintSet.HILITE_HINT)) {
            return width;
        } else if (hints.contains(StyleHintSet.SELECTED_HINT)) {
            return width+1f;
        } else {
            return width;
        }
    }
    
    /**
     * Return modifier instance that does nothing.
     * @param <S> style type
     * @return inert modifier
     */
    public static <S extends Style> StyleModifier<S> inertStyleModifier() {
        return (StyleModifier<S>) INERT_STYLE_MODIFIER;
    }
    
    /**
     * Return modifier for a given style class, if style is an instance of that
     * class, otherwise return the given default modifier
     * @param <S> the base style
     * @param <T> the style type used for checking
     * @param cls the class to check
     * @param mod the modifier if style is an instance of this class
     * @param def the default modifier to use if not matched
     * @return compound modifier
     */
    public static <S extends Style, T extends S> StyleModifier<S> instanceOr(
            final Class<? extends T> cls, final StyleModifier<T> mod, 
            final StyleModifier<S> def) {
        return new StyleModifier<S>() {
            @Override
            public S apply(S style, StyleHintSet hints) {
                if (cls.isInstance(style)) {
                    return mod.apply((T) style, hints);
                } else {
                    return def.apply(style, hints);
                }
            }
        };
    }
    
    public static StyleModifier<ShapeStyle> shapeStyleModifier() {
        return SHAPE_STYLE_MODIFIER;
    }
    
    public static StyleModifier<PathStyle> pathStyleModifier() {
        return PATH_STYLE_MODIFIER;
    }
    
    public static StyleModifier<PointStyle> pointStyleModifier() {
        return POINT_STYLE_MODIFIER;
    }
    
    public static StyleModifier<TextStyle> textStyleModifier() {
        return TEXT_STYLE_MODIFIER;
    }
    
    
    //
    // INNER CLASSES
    //
    
    public static class ShapeStyleBasicModifier implements StyleModifier<ShapeStyleBasic> {
        @Override
        public ShapeStyleBasic apply(ShapeStyleBasic style, StyleHintSet hints) {
            return new ShapeStyleBasic()
                    .fill(applyHints(style.getFill(), hints))
                    .stroke(applyHints(style.getStroke(), hints))
                    .strokeWidth(applyStrokeHints(style.getStrokeWidth(), hints));
        }
    }
    
    public static class PathStyleBasicModifier implements StyleModifier<PathStyleBasic> {
        @Override
        public PathStyleBasic apply(PathStyleBasic style, StyleHintSet hints) {
            return new PathStyleBasic()
                    .stroke(applyHints(style.getStroke(), hints))
                    .strokeWidth(applyStrokeHints(style.getStrokeWidth(), hints));
        }
    }
    
    public static class PointStyleBasicModifier implements StyleModifier<PointStyleBasic> {
        @Override
        public PointStyleBasic apply(PointStyleBasic style, StyleHintSet hints) {
            return new PointStyleBasic()
                    .marker(style.getMarker())
                    .markerRadius(style.getMarkerRadius())
                    .fill(applyHints(style.getFill(), hints))
                    .stroke(applyHints(style.getStroke(), hints))
                    .strokeWidth(applyStrokeHints(style.getStrokeWidth(), hints));
        }        
    }
    
    public static class TextStyleBasicModifier implements StyleModifier<TextStyleBasic> {
        @Override
        public TextStyleBasic apply(TextStyleBasic style, StyleHintSet hints) {
            return new TextStyleBasic()
                    .fill(applyHints(style.getFill(), hints))
                    .font(style.getFont() == null ? null : style.getFont().deriveFont(Font.BOLD))
                    .offset(style.getOffset().getX(), style.getOffset().getY())
                    .textAnchor(style.getTextAnchor());
        }
    }
    
}
