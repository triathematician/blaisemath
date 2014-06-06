/*
 * PointStyleDecorated.java
 * Created Aug 27, 2011
 */
package org.blaise.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.google.common.base.Preconditions.*;
import java.awt.Color;
import javax.annotation.Nullable;

/**
 * A style that generally defers to a base style, except for radius and color.
 * The relative radius is a value greater than 0 that multiplies with the base radius.
 * 
 * @author elisha
 */
public class PointStyleDecorated extends PointStyleSupport {
    
    protected PointStyleSupport base;
    protected Float relativeRadius;
    protected Color fill;

    public PointStyleDecorated() {
    }
    
    @Override
    public String toString() {
        return String.format("PointStyleDecorated[baseStyle=%s, fill=%s, relativeMarkerRadius=%.1f]", 
                base, fill, relativeRadius);
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** 
     * Sets fillColor color & returns pointer to object
     * @param c
     * @return 
     */
    public PointStyleDecorated fill(@Nullable Color c) {
        setFill(c);
        return this;
    }

    /** 
     * Sets radius & returns pointer to object
     * @param radius
     * @return
     */
    public PointStyleDecorated relativeMarkerRadius(float radius) {
        setRelativeMarkerRadius(radius);
        return this;
    }

    /** 
     * Sets base style and returns this
     * @param base
     * @return
     */
    public PointStyleDecorated baseStyle(PointStyleSupport base) {
        setBaseStyle(base);
        return this;
    }

    // </editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public PointStyleSupport getBaseStyle() {
        return base;
    }
    
    public void setBaseStyle(PointStyleSupport r) {
        this.base = checkNotNull(r);
    }

    @Nullable 
    public Color getFill() {
        return fill;
    }
    
    public void setFill(@Nullable Color fill) {
        this.fill = fill;
    }

    public float getRelativeMarkerRadius() {
        return relativeRadius;
    }

    public final void setRelativeMarkerRadius(float r) {
        relativeRadius = Math.max(r, 0);
    }
    
    // </editor-fold>
    
    
    @Override 
    public Marker getMarker() { 
        return base.getMarker();
    }
        
    @Override
    public float getMarkerRadius() { 
        return relativeRadius * base.getMarkerRadius();
    }
    
    @Override 
    protected ShapeStyle getShapeStyle() {
        if (fill == null) {
            return base.getShapeStyle();
        }
        ShapeStyleBasic r = (ShapeStyleBasic) base.getShapeStyle();
        return new ShapeStyleBasic()
                .fill(fill).stroke(r.getStroke()).strokeWidth(r.getStrokeWidth());
    }
    
}
