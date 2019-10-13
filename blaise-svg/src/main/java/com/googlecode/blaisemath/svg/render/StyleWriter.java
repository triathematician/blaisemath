package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;

public class StyleWriter {

    public static String id(AttributeSet style) {
        return style.getString(Styles.ID);
    }

    /**
     * Converts style object to SVG-compatible string representation
     * @param style input style
     * @return style string
     */
    public static String toString(AttributeSet style) {
        return new AttributeSetCoder().encode(svgCompatibleCopy(style));
    }

    /**
     * Creates a copy of the input style set for SVG styles.
     * @param style input style
     * @return style
     */
    private static @Nullable AttributeSet svgCompatibleCopy(@Nullable AttributeSet style) {
        if (style == null) {
            return null;
        }
        AttributeSet res = style.copy();
        // TODO - update opacity keys for alpha colors
        for (String c : style.getAttributes()) {
            Object col = style.get(c);
            if (col instanceof Color) {
                res.put(c, Colors.alpha((Color) col, 255));
            }
        }
        return res;
    }
}
