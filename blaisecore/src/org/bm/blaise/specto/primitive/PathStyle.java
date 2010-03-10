/**
 * PathStyle.java
 * Created on Aug 4, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * <p>
 *   <code>PathStyle</code> represents any style that is applied to paths or lines.
 *   The default options are the <code>Stroke</code> object and the color... I may
 *   consider changing this to alter the stroke directly via patterns here such as
 *   width, dashes, etc. Alternately, I may design a "builder" to construct various
 *   stroke types.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PathStyle extends PrimitiveStyle<Shape> {

    //
    // PROPERTIES
    //

    /** Stroke for the stroke. */
    BasicStroke stroke = DEFAULT_STROKE;

    /** Color of stroke. */
    Color strokeColor = Color.BLACK;

    //
    // CONSTRUCTORS
    //

    /** Default constructor. */
    public PathStyle() {
    }

    public PathStyle(Color color) {
        this.strokeColor = color;
    }

    /** Construct with parameters. */
    public PathStyle(Color color, float width){
        this.strokeColor = color;
        this.stroke = new BasicStroke(width);
    }

    /** Construct with parameters. */
    public PathStyle(Color color, BasicStroke stroke){
        this.strokeColor = color;
        this.stroke = stroke;
    }

    //
    // UTILITIES
    //

    @Override
    public String toString() {
        return "PathStyle [wid=" + getThickness() + "]";
    }

    //
    // PATTERNS
    //

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(BasicStroke stroke) {
        this.stroke = stroke;
    }

    public float getThickness() {
        return stroke.getLineWidth();
    }

    public void setThickness(float width) {
        stroke = new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
    }

    public Color getColor() {
        return strokeColor;
    }

    public void setColor(Color color) {
        this.strokeColor = color;
    }

    //
    // IMPLEMENTATION
    //

    public void draw(Graphics2D canvas, Shape path, boolean selected) {
        canvas.setColor(strokeColor);
        if (selected) {
            setThickness(getThickness()*2);
            canvas.setStroke(stroke);
            canvas.draw(path);
            setThickness(getThickness()/2);
        } else {
            canvas.setStroke(stroke);
            canvas.draw(path);
        }
    }

    @Override
    public void draw(Graphics2D canvas, Shape[] paths, boolean selected) {
        canvas.setStroke(stroke);
        canvas.setColor(strokeColor);
        if (selected) {
            setThickness(getThickness()*2);
            canvas.setStroke(stroke);
            for (Shape p : paths)
                canvas.draw(p);
            setThickness(getThickness()/2);
        } else {
            canvas.setStroke(stroke);
            for (Shape p : paths)
                canvas.draw(p);
        }
    }
}
