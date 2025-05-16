package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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


import com.googlecode.blaisemath.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;
import java.awt.Color;
import java.util.Set;

/**
 * Maintains a collection of visibility hints that can be used to change how an
 * object is drawn. The {@link StyleContext} is responsible for switching out the
 * default style for an alternate style, as appropriate for these hints.
 * 
 * @author Elisha Peterson
 */
public class StyleHints {
    
    /** Style hint indicating an invisible, non-functional element. */
    public static final String HIDDEN_HINT = "hidden";
    /** Style hint indicating an invisible but still functional element (receives mouse events) */
    public static final String HIDDEN_FUNCTIONAL_HINT = "hidden_functional";
    /** Style hint indicating a selected element. */
    public static final String SELECTED_HINT = "selected";
    /** Style hint indicating a highlighted element. */
    public static final String HIGHLIGHT_HINT = "highlight";
    /** Style hint indicating an outlined element. */
    public static final String OUTLINE_HINT = "outline";
    /** Style hint indicating a low-quality (but fast) rendered element. */
    public static final String QUICK_RENDER_HINT = "quick_render";

    // utility class - no instantiation
    private StyleHints() {
    }
    
    /**
     * Applies hints to a color
     * @param color the color to apply hints to
     * @param hints the hints to apply
     * @return transformed color
     */
    public static @Nullable Color modifyColorsDefault(@Nullable Color color, Set<String> hints) {
        if (color == null) {
            return null;
        } else if (hints.contains(HIDDEN_HINT)) {
            return Colors.alpha(color, 0);
        } else if (hints.contains(HIGHLIGHT_HINT)) {
            return Colors.lighterThan(color);
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
    public static float modifyStrokeWidthDefault(@Nullable Float width, Set<String> hints) {
        float wid = width == null || width.isNaN() || width.isInfinite() ? 1f : width;
        if (hints.contains(HIDDEN_HINT)) {
            return 0f;
        } else if (hints.contains(SELECTED_HINT)) {
            return wid + 1f;
        } else if (hints.contains(HIGHLIGHT_HINT)) {
            return Math.max(wid - 1f, wid / 2f);
        } else {
            return wid;
        }
    }
    
    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    public static boolean isInvisible(Set<String> hints) {
        return hints.contains(HIDDEN_HINT) || hints.contains(HIDDEN_FUNCTIONAL_HINT);
    }
    
    /**
     * Test whether given hints object is hidden
     * @param hints hints object
     * @return true if hints contains the hidden hint
     */
    public static boolean isFunctional(Set<String> hints) {
        return !hints.contains(HIDDEN_HINT);
    }
    
}
