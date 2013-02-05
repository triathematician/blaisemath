/**
 * DefaultStyleContext.java
 * Created Dec 8, 2012
 */

package org.blaise.style;

import java.awt.Color;

/** 
 * Default instance of the style provider. This is an immutable class that
 * provides non-null, default styles.
 */
public final class DefaultStyleContext implements StyleContext<Object> {
    
    private static DefaultStyleContext INST = new DefaultStyleContext();
    
    public static DefaultStyleContext getInstance() {
        return INST;
    }
    
    
    final ShapeStyle SOLID = new BasicShapeStyle(Color.white, Color.black);
    final PathStyle PATH = new BasicPathStyle(Color.black);
    final PointStyle POINT = new BasicPointStyle();
    final StringStyle STRING = new BasicStringStyle();

    private DefaultStyleContext() {
    }
        
    public ShapeStyle getShapeStyle(Object o) {
        return SOLID;
    }

    public PathStyle getPathStyle(Object o) {
        return PATH;
    }

    public PointStyle getPointStyle(Object o) {
        return POINT;
    }

    public StringStyle getStringStyle(Object o) {
        return STRING;
    }

}