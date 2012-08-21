/*
 * BlaisePalette.java
 * Created on Oct 21, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.Color;
import static java.awt.Color.*;

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
    public Color func1();
    public Color func1dark();
    public Color func1light();
    public Color func2();
    public Color func2dark();
    public Color func2light();
    public Color vector();

    final public static Color VERY_DARK_GRAY = new Color(32, 32, 32);
    final public static Color MED_DARK_GRAY = new Color(96, 96, 96);
    final public static Color MED_LIGHT_GRAY = new Color(160, 160, 160);
    final public static Color VERY_LIGHT_GRAY = new Color(224, 224, 224);

    final public static BlaisePalette BLACK_WHITE = new BlaisePalette() {
        public Color axisLabel() { return MED_DARK_GRAY; }
        public Color axis() { return GRAY; }
        public Color grid() { return LIGHT_GRAY; }

        public Color func1dark() { return BLACK; }
        public Color func1() { return VERY_DARK_GRAY; }
        public Color func1light() { return DARK_GRAY; }

        public Color func2dark() { return GRAY; }
        public Color func2() { return MED_LIGHT_GRAY; }
        public Color func2light() { return LIGHT_GRAY; }

        public Color vector() { return DARK_GRAY; }
    };

    final public static Color VERY_DARK_BLUE = new Color(64, 64, 128);
    final public static Color DARK_BLUE = new Color(96, 96, 160);
    final public static Color VERY_LIGHT_BLUE = new Color(224, 224, 255);

    final public static Color VERY_DARK_RED = new Color(128, 64, 64);
    final public static Color DARK_RED = new Color(160, 96, 96);
    final public static Color MED_DARK_RED = new Color(192, 128, 128);

    final public static Color GREEN_ALT = new Color(96, 192, 96);

    final public static Color BROWN = new Color(160, 160, 0);
    final public static Color MED_LIGHT_BROWN = new Color(192, 192, 64);
    final public static Color LIGHT_BROWN = new Color(224, 224, 96);

    final public static BlaisePalette STANDARD = new BlaisePalette() {
        public Color axisLabel() { return VERY_DARK_BLUE; }
        public Color axis() { return DARK_BLUE; }
        public Color grid() { return VERY_LIGHT_BLUE; }

        public Color func1dark() { return VERY_DARK_RED; }
        public Color func1() { return DARK_RED; }
        public Color func1light() { return MED_DARK_RED; }

        public Color func2dark() { return BROWN; }
        public Color func2() { return MED_LIGHT_BROWN; }
        public Color func2light() { return LIGHT_BROWN; }

        public Color vector() { return GREEN_ALT; }
    };

}
