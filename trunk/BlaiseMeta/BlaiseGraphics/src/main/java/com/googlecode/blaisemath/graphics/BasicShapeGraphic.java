/*
 * BasicShapeGraphic.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.ShapeStyle;
import java.awt.Shape;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A shape or path with an associated style.
 * If the style is null, then the shape will be drawn as either a path or solid shape,
 * depending on the provided {@code strokeOnly} parameter.
 *
 * @see ShapeStyle
 * @see PathStyle
 *
 * @author Elisha Peterson
 */
public class BasicShapeGraphic extends AbstractShapeGraphic {

    /** The associated style */
    @Nullable 
    protected ShapeStyle style;

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     */
    public BasicShapeGraphic(Shape primitive) {
        this(primitive, null);
    }

    /**
     * Construct with given primitive and style.
     * @param primitive the shape to draw
     * @param style style used to draw
     */
    public BasicShapeGraphic(Shape primitive, ShapeStyle style) {
        super(primitive);
        setStyle(style);
    }

    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    /**
     * Return the style for the graphic.
     * @return style
     */
    @Nullable 
    public ShapeStyle getStyle() {
        return style;
    }

    /**
     * Sets the style for the graphic
     * @param style the style
     */
    public final void setStyle(@Nullable ShapeStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>

    
    /** 
     * Return the actual style used for drawing
     * @return style for drawing
     */
    @Nonnull 
    @Override
    protected ShapeStyle drawStyle() {
        if (style instanceof PathStyle) {
            return parent.getPathStyle((PathStyle) style, this, styleHints);
        } else {
            return parent.getShapeStyle(style, this, styleHints);
        }
    }
    
}
