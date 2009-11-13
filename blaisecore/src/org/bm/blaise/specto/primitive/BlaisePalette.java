/*
 * BlaisePalette.java
 * Created on Oct 21, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.Color;

/**
 * <p>
 *   This class maintains a set of colors for various mathematical objects. This
 *   can be used to provide uniform styling to visual objects. Also contains a few
 *   immutable class implementations.
 * </p>
 * @author Elisha Peterson
 */
public interface BlaisePalette {

    public Color grid();
    public Color axis();
    public Color axisLabel();
    public Color function();
    public Color vector();

    final public static BlaisePalette BLACK_WHITE = new BlaisePalette() {
        public Color grid() { return Color.LIGHT_GRAY; }
        public Color axisLabel() { return Color.GRAY; }
        public Color axis() { return Color.GRAY; }
        public Color function() { return Color.BLACK; }
        public Color vector() { return Color.DARK_GRAY; }
    };

    final public static Color DARK_BLUE = new Color(92, 92, 160);
    final public static Color DARKER_BLUE = new Color(92, 92, 128);
    final public static Color VERY_LIGHT_BLUE = new Color(216, 216, 255);

    final public static Color DARK_RED = new Color(160, 64, 64);

    final public static Color GREEN_ALT = new Color(100, 192, 100);

    final public static BlaisePalette STANDARD = new BlaisePalette() {
        public Color grid() { return VERY_LIGHT_BLUE; }
        public Color axis() { return DARK_BLUE; }
        public Color axisLabel() { return DARKER_BLUE; }
        public Color function() { return DARK_RED; }
        public Color vector() { return GREEN_ALT; }
    };

}
