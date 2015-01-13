/**
 * AffineTransformBuilder.java
 * Created Dec 14, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import java.awt.geom.AffineTransform;

/**
 * Builder object for {@link AffineTransform}.
 * @author elisha
 */
public final class AffineTransformBuilder {
    
    private final AffineTransform res = new AffineTransform();

    public AffineTransformBuilder translate(double dx, double dy) {
        res.translate(dx, dy);
        return this;
    }
    
    public AffineTransformBuilder scale(double rx, double ry) {
        res.scale(rx, ry);
        return this;
    }
    
    public AffineTransform build() {
        return res;
    }
    
}
