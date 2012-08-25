/*
 * StyleUtils.java
 * Created Jan 8, 2011
 */

package org.blaise.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.util.Set;

/**
 * Provides standard style keys, and useful utilities for style classes.
 * @author elisha
 */
public class StyleUtils {
    /** Utility class */
    private StyleUtils(){}

    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);
    /** Default composite */
    public static Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
    
    
    //<editor-fold defaultstate="collapsed" desc="COLOR MODIFIERS">
    //
    // COLOR MODIFIERS
    //

    /**
     * Produces a color that is lighter than the specified color
     * @param c source color
     * @return new color
     */
    public static Color lighterThan(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()), c.getAlpha());
    } 
    
    /**
     * Produces a color that is "blander" than the specified color (reducing saturation)
     * @param c source color
     * @return new color
     */
    public static Color blanderThan(Color c) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        Color c2 = Color.getHSBColor(hsb[0], .5f*hsb[1], hsb[2]);
        return new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), c.getAlpha());
    }

    /**
     * Changes specified color based on visibility hints
     * @param color color to change
     * @param hints hints to apply
     * @return modified color
     */
    public static Color applyHints(Color color, Set<VisibilityHint> hints) {
        return hints == null ? color
                : hints.contains(VisibilityHint.Highlight) ? lighterThan(color)
                : hints.contains(VisibilityHint.Obscure) ? blanderThan(color)
                : color;
    }

    private static int lighten(int i) {
        return i + Math.min(64, (255-i)/2);
    }
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="StyleProvider IMPLEMENTATIONS">
    //
    // StyleProvider IMPLEMENTATIONS
    //
    
    /** Default instance */
    public static final DefaultStyleProvider DEFAULT_STYLE_PROVIDER = new DefaultStyleProvider();
    
    /** Default instance of the style provider */
    public static final class DefaultStyleProvider implements StyleProvider<Object> {
        final ShapeStyle SOLID = new BasicShapeStyle(Color.white, Color.black);
        final PathStyle PATH = new BasicPathStyle(Color.black);
        final PointStyle POINT = new BasicPointStyle();
        final StringStyle STRING = new BasicStringStyle();

        public ShapeStyle getShapeStyle(Object o) { return SOLID; }
        public PathStyle getPathStyle(Object o) { return PATH; }
        public PointStyle getPointStyle(Object o) { return POINT; }
        public StringStyle getStringStyle(Object o) { return STRING; }
    } // INNER CLASS StyleProvider.Default
        
    //</editor-fold>
    
}
