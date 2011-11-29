/*
 * StyleUtils.java
 * Created Jan 8, 2011
 */

package org.bm.blaise.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.util.Map;
import java.util.TreeMap;

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

    
    
//    /** Turns array of String,Class, ... into a style key map */
//    public static Map<String,Class> keyMap(Object... arr) {
//        TreeMap<String,Class> result = new TreeMap<String,Class>();
//        for (int i = 0; i < arr.length; i+=2)
//            result.put((String) arr[i], (Class) arr[i+1]);
//        return result;
//    }
    
    
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
    public static final class DefaultStyleProvider implements StyleProvider {
        final BasicShapeStyle SOLID = new BasicShapeStyle(Color.white, Color.black);
        final BasicPathStyle PATH = new BasicPathStyle(Color.black);
        final BasicPointStyle POINT = new BasicPointStyle();
        final BasicStringStyle STRING = new BasicStringStyle();

        public ShapeStyle getShapeStyle() { return SOLID; }
        public PathStyle getPathStyle() { return PATH; }
        public PointStyle getPointStyle() { return POINT; }
        public StringStyle getStringStyle() { return STRING; }
    } // INNER CLASS StyleProvider.Default
    
    /** 
     * Generates a style provider that provides a path style only, and otherwise defers to the parent.
     * @param parent the parent factory
     * @param pr the path style
     * @return factory for providing default paths
     */
    public static StyleProvider pathDelegator(StyleProvider parent, final PathStyle pr) {
        return new StyleDelegator(parent) {
            @Override public PathStyle getPathStyle() { return pr; }
        };
    }
    
    //</editor-fold>
    
}
