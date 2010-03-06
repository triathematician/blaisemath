/*
 * LineStyle.java
 * Created 2008
 */

package sequor.style;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * Stores default options for line styles.
 * </p>
 *
 * @author ae3263
 */
public class LineStyle extends Style {

    // STYLE CONSTANTS

    public static final int VERY_THIN = 0;
    public static final int THIN = 1;
    public static final int MEDIUM = 2;
    public static final int THICK = 3;
    public static final int VERY_THICK = 4;
    public static final int DOTTED = 5;
    public static final int VERY_DOTTED = 6;
    public static final int POINTS_ONLY = 7;

    public static final String[] LINE_STYLE_STRINGS = {
        "Very thin", "Thin", "Medium", "Thick", "Very thick", 
        "Dotted", "Very Dotted",
        "Points only"
    };

    public static final float[] dash2={2.0f,4.0f};
    public static final float[] dash3={4.0f,4.0f};
    
    public static final Stroke[] STROKES = {
        new BasicStroke(0.5f),
        new BasicStroke(1.0f),
        new BasicStroke(2.0f),
        new BasicStroke(3.0f),
        new BasicStroke(4.0f),
        new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash2,0.0f),
        new BasicStroke(1.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f,dash3,0.0f),
        new BasicStroke()
    };
    
    // GENERAL STYLE OPTIONS
    
    protected Stroke stroke;
    
    // CONSTRUCTOR
    
    public LineStyle() { 
        super(LINE_STYLE_STRINGS);
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                stroke = STROKES[getValue()];
            }
        });
    }
    
    public LineStyle(int style) { 
        this();
        setValue(style);
    }
    

    // REQUIRED METHODS
    
    @Override
    public void apply(Graphics2D g) {
        if (stroke != null) {
            g.setStroke(stroke);
        }
    }
}
