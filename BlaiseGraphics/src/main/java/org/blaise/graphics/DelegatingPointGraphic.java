/**
 * DelegatingPointGraphic.java
 * Created Aug 21, 2012
 */
package org.blaise.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.style.StringStyle;

/**
 * Uses an {@link ObjectStyler} and a source object to draw a labeled point on a canvas.
 * 
 * @author Elisha
 */
public class DelegatingPointGraphic<Src> extends GraphicSupport {

    /** Source object */
    protected Src src;
    /** Location */
    protected Point2D pt;
    /** Manages delegators */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();

    public DelegatingPointGraphic() {
    }

    public DelegatingPointGraphic(Src src, Point2D pt) {
        this.src = src;
        this.pt = pt;
    }
    
    public Src getSourceObject() {
        return src;
    }
    
    public void setSourceObject(Src src) {
        this.src = src;
        fireGraphicChanged();
    }
    
    public Point2D getPoint() {
        return pt;
    }
    
    public void setPoint(Point2D pt) {
        this.pt = pt;
        fireGraphicChanged();
    }
    
    public void draw(Graphics2D canvas) {
        styler.getStyleDelegate().of(src).draw(pt, canvas, visibility);
        String label = styler.getLabelDelegate().of(src);
        if (label != null && label.length() > 0) {
            styler.getLabelStyleDelegate().of(src).draw(pt, label, canvas, visibility);
        }
    }

    public boolean contains(Point point) {
        return styler.getStyleDelegate().of(src).shape(pt).contains(point);
    }

    public boolean intersects(Rectangle box) {
        return styler.getStyleDelegate().of(src).shape(pt).intersects(box);
    }

}
