/**
 * ShapeStyle.java
 * Created on Sep 6, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * <p>
 *   <code>ShapeStyle</code> represents a shape, with style determined by both
 *   a fill color and a stroke/stroke color.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ShapeStyle implements PrimitiveStyle<Shape> {

    //
    // CONSTANTS
    //

    /** Stroke outline */
    Stroke stroke = DEFAULT_STROKE;

    /** Stroke color */
    Color strokeColor = Color.BLACK;

    /** Fill of object */
    Color fillColor = Color.LIGHT_GRAY;

    /** Opacity of fill */
    float opacity = .5f;


    //
    // CONSTRUCTORS
    //

    /** Construct with defaults. */
    public ShapeStyle() {
    }

    /** Construct with stroke only */
    public ShapeStyle(PathStyle style) {
        stroke = style.stroke;
        strokeColor = style.strokeColor;
        fillColor = null;
    }

    /** Construct with stroke and fill color */
    public ShapeStyle(PathStyle style, Color fillColor) {
        stroke = style.stroke;
        strokeColor = style.strokeColor;
        this.fillColor = fillColor;
    }

    /** Construct with specified elements. */
    public ShapeStyle(Color strokeColor, Color fillColor) {
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }

    /** Construct with specified elements. */
    public ShapeStyle(Stroke stroke, Color strokeColor, Color fillColor, float opacity) {
        this.stroke = stroke;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.opacity = opacity;
    }

    //
    // UTILITIES
    //

    @Override
    public String toString() {
        return "ShapeStyle [stroke=" + strokeColor + ", fill=" + fillColor + ", opacity=" + opacity + "]";
    }


    //
    // BEANS
    //

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public float getFillOpacity() {
        return opacity;
    }

    public void setFillOpacity(float opacity) {
        this.opacity = opacity;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getThickness() {
        return (stroke instanceof BasicStroke) ? ((BasicStroke)stroke).getLineWidth() : -1;
    }

    public void setThickness(float width) {
        if (stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) stroke;
            stroke = new BasicStroke(width, bs.getEndCap(), bs.getLineJoin(), bs.getMiterLimit(), bs.getDashArray(), bs.getDashPhase());
        }
    }




    //
    // GRAPHICS METHODS
    //

    public void draw(Shape sh, Graphics2D canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            canvas.fill(sh);
            canvas.setComposite(AlphaComposite.SrcOver);
        }
        if (stroke != null && strokeColor != null) {
            canvas.setStroke(stroke);
            canvas.setColor(strokeColor);
            canvas.draw(sh);
        }
    }

    public void draw(Shape[] shapes, Graphics2D canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            for (Shape sh : shapes)
                canvas.fill(sh);
            canvas.setComposite(AlphaComposite.SrcOver);
        }
        if (stroke != null && strokeColor != null) {
            canvas.setStroke(stroke);
            canvas.setColor(strokeColor);
            for (Shape sh : shapes)
                canvas.draw(sh);
        }
    }
}
