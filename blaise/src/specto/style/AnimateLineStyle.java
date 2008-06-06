/**
 * AnimateLineStyle.java
 * Created on Jun 3, 2008
 */

package specto.style;

import java.awt.Graphics2D;
import sequor.style.VisualStyle;

/**
 *
 * @author Elisha Peterson
 */
public class AnimateLineStyle extends Style {

    public AnimateLineStyle() { super ( VisualStyle.ANIMATE_LINE_STYLE_STRINGS ); }
    
    // STYLE CONSTANTS  
    
    public static final int ANIMATE_DRAW=0;
    public static final int ANIMATE_DOT=1;
    public static final int ANIMATE_TRACE=2;
    public static final int ANIMATE_TRAIL=3;

    @Override
    public void apply(Graphics2D g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
