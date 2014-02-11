/**
 * StyleContextDefault.java
 * Created Dec 8, 2012
 */

package org.blaise.style;

import java.awt.Color;

/** 
 * Default instance of the style provider. This is an immutable class that
 * provides non-null, default styles.
 */
public final class StyleContextDefault implements StyleContext<Object> {
    
    //
    // SINGLETON INSTANCE
    //
    
    private static final StyleContextDefault INST = new StyleContextDefault();
    
    public static StyleContextDefault getInstance() {
        return INST;
    }
    
    //
    // STYLE OBJECTS
    //    
    
    protected static final ShapeStyle SOLID = new ShapeStyleBasic().fill(Color.white).stroke(Color.black);
    protected static final PathStyle PATH = new PathStyleBasic().stroke(Color.black);
    protected static final PointStyle POINT = new PointStyleBasic();
    protected static final TextStyle STRING = new TextStyleBasic();

    private StyleContextDefault() {
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

    public TextStyle getStringStyle(Object o) {
        return STRING;
    }

}