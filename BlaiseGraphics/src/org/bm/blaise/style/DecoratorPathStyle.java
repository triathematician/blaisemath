/*
 * DecoratorPathStyle.java
 * Created Aug 27, 2011
 */
package org.bm.blaise.style;

import java.awt.Color;

/**
 * A style that generally defers to a base style for thickness and color.
 * The resulting thickness is the <i>product</i> of the thickness of the base style
 * and the thickness of this class's style.
 * 
 * @author elisha
 */
public class DecoratorPathStyle extends BasicPathStyle {
    
    private final PathStyle base;
    
    /** Defer to base style, with specified thickness multiplier */
    public DecoratorPathStyle(PathStyle base, float thickness) {
        super(null, thickness);
        this.base = base;
    }
    
    /** Defer to base style, with specified color */
    public DecoratorPathStyle(PathStyle base, Color c) {
        super(c, 1f);
        this.base = base;
    }
    
    /** Defer to base style, with specified thickness multiplier and color. */
    public DecoratorPathStyle(PathStyle base, float thickness, Color get) {
        super(get, thickness);
        this.base = base;
    }

    @Override
    public Color getColor() {
        return color == null ? base.getColor() : color;
    }

    @Override
    public float getWidth() {
        return thickness * base.getWidth();
    }   
    
}
