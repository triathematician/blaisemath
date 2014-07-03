/**
 * StyleModifiers.java
 * Created Jul 3, 2014
 */
package org.blaise.style.context;

import com.google.common.base.Predicate;
import org.blaise.graphics.Graphic;
import org.blaise.style.PathStyle;
import org.blaise.style.PointStyle;
import org.blaise.style.ShapeStyle;
import org.blaise.style.TextStyle;

/**
 * Utility class for modifying styles.
 * @author Elisha
 */
public class StyleModifiers {
    
    /** Style hint indicating a hidden element. */
    public static final String HIDDEN_HINT = "hide";
    /** Style hint indicating a selected element. */
    public static final String SELECTED_HINT = "selected";
    /** Style hint indicating a highlighted element. */
    public static final String HILITE_HINT = "hilite";
    
    /** Filter that can be applied to pass only visible graphics */
    public static final Predicate<Graphic> VISIBLE_FILTER = new Predicate<Graphic>(){
        @Override
        public boolean apply(Graphic input) { 
            return !input.getStyleHints().contains(StyleModifiers.HIDDEN_HINT); 
        }
    };
    
    
    // utility class - no instantiation
    private StyleModifiers() {
    }

    public static ShapeStyle apply(ShapeStyle shapeStyle, StyleHintSet hints) {
    }
    
    public static PathStyle apply(PathStyle pathStyle, StyleHintSet hints) {
    }
    
    public static TextStyle apply(TextStyle textStyle, StyleHintSet hints) {
    }
    
    public static PointStyle apply(PointStyle pointStyleStyle, StyleHintSet hints) {
    }
    
}
