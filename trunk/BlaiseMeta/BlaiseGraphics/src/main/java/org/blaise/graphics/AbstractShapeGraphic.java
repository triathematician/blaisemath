/*
 * BasicShapeGraphic.java
 * Created Jan 2011 (adapted much from earlier blaise code)
 */

package org.blaise.graphics;

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


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nonnull;
import org.blaise.style.PathStyle;
import org.blaise.style.ShapeStyle;
import org.blaise.style.ShapeStyleBasic;
import org.blaise.style.Styles;

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
public abstract class AbstractShapeGraphic extends GraphicSupport {

    /** The object that will be drawn. */
    protected Shape primitive;
    /** Whether to use stroke or fill style (if not specified) */
    protected boolean strokeOnly;

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     */
    protected AbstractShapeGraphic(Shape primitive) {
        this(primitive, false);
    }

    /**
     * Construct with no style (will use the default)
     * @param primitive the shape to draw
     * @param strokeOnly determines whether to use the solid style or the path/edge style
     */
    protected AbstractShapeGraphic(Shape primitive, boolean strokeOnly) {
        this.primitive = primitive;
        this.strokeOnly = strokeOnly;
    }

    @Override
    public String toString() {
        return "ShapeGraphic:"+primitive;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Return the shape for the graphic.
     * @return shape
     */
    public Shape getPrimitive() {
        return primitive;
    }

    /**
     * Set the shape for the graphic.
     * @param primitive shape
     */
    public void setPrimitive(Shape primitive) {
        this.primitive = primitive;
        fireGraphicChanged();
    }
    
    /**
     * Return flag indicating whether only stroke of shape is drawn.
     * @return stroke only flag
     */
    public boolean isStrokeOnly() {
        return strokeOnly;
    }

    /**
     * Set flag indicating whether only stroke of shape is drawn.
     * @param strokeOnly stroke only flag
     */
    public void setStrokeOnly(boolean strokeOnly) {
        if (this.strokeOnly != strokeOnly) {
            this.strokeOnly = strokeOnly;
            fireGraphicChanged();
        }
    }
    
    //</editor-fold>

    
    /**
     * Subclasses should override to return their draw style for the graphic.
     * @return style
     */
    @Nonnull 
    protected abstract ShapeStyle drawStyle();
    

    /** Return true if painting as a stroke. */
    protected boolean paintingAsStroke() {
        ShapeStyle style = drawStyle();
        if (strokeOnly) {
            return true;
        } else if (style instanceof ShapeStyleBasic) {
            ShapeStyleBasic bss = (ShapeStyleBasic) style;
            return bss.getFill() == null;
        } else {
            return style instanceof PathStyle;
        }
    }

    @Override
    public boolean contains(Point2D point) {
        ShapeStyle style = drawStyle();
        if (!paintingAsStroke()) {
            return primitive.contains(point);
        } else if (!(style instanceof PathStyle)) {
            return Styles.DEFAULT_STROKE.createStrokedShape(primitive).contains(point);
        } else {
            Shape shape = ((PathStyle)style).shapeOfPath(primitive);
            return shape != null && shape.contains(point);
        }
    }

    @Override
    public boolean intersects(Rectangle2D box) {
        ShapeStyle style = drawStyle();
        if (!paintingAsStroke() && primitive.intersects(box)) {
            return true;
        }
        float thickness = !(style instanceof PathStyle) ? 1f : ((PathStyle)style).getStrokeWidth();
        return new BasicStroke(thickness).createStrokedShape(primitive).intersects(box);
    }

    @Override
    public void draw(Graphics2D canvas) {
        drawStyle().draw(primitive, canvas);
    }

}
