/*
 * PathStyleBasic.java
 * Created Jan 12, 2011
 */

package org.blaise.style;

import static com.google.common.base.Preconditions.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Draws a path with arbitrary color and width.
 *
 * @author Elisha
 */
public class PathStyleBasic implements PathStyle {

    protected Color stroke = Color.black;
    protected float strokeWidth = 1f;

    public PathStyleBasic() {
    }
    
    @Override
    public String toString() {
        return String.format("PathStyleBasic[stroke=%s, strokeWidth=%.1f]", 
                stroke, strokeWidth);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    public PathStyleBasic stroke(Color stroke) {
        setStroke(stroke);
        return this;
    }

    public PathStyleBasic strokeWidth(float width) {
        setStrokeWidth(width);
        return this;
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public Color getStroke() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = checkNotNull(stroke);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float thickness) {
        this.strokeWidth = thickness;
    }
    
    //</editor-fold>

    
    public Shape shapeOfPath(Shape primitive) {
        return new BasicStroke(strokeWidth).createStrokedShape(primitive);
    }

    public void draw(Shape s, Graphics2D canvas, VisibilityHintSet visibility) {
        if(getStrokeWidth() <= 0f || getStroke() == null) {
            return;
        }

        canvas.setStroke(new BasicStroke(getStrokeWidth()));

        canvas.setColor(ColorUtils.applyHints(getStroke(), visibility));
        canvas.draw(s);

        canvas.setStroke(Styles.DEFAULT_STROKE);
    }

}
