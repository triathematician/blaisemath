/**
 * AnimateLineStyle.java
 * Created on Jun 3, 2008
 */

package sequor.style;

import java.awt.Graphics2D;
import sequor.style.VisualStyle;

/**
 *
 * @author Elisha Peterson
 */
public class AnimateLineStyle extends Style {

    // STYLE CONSTANTS  
    
    public static final int ANIMATE_DRAW = 0;
    public static final int ANIMATE_TRACE = 1;
    public static final int ANIMATE_TRAIL = 2;
    public static final int ANIMATE_DOT = 3;
    public static final int ANIMATE_VEC = 4;

    // ANIMATING LINE STYLES

    public static final String[] ANIMATE_LINE_STYLE_STRINGS = {
        "Draw", "Trace", "Trail", "Dot", "Vec"
    };

    public AnimateLineStyle() { 
        super ( ANIMATE_LINE_STYLE_STRINGS );
    }

    public AnimateLineStyle(int style) {
        this();
        setValue(style);
    }
    

    @Override
    public void apply(Graphics2D g) {
    }

}
