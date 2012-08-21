/*
 * LabeledPointGraphic.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.graphics.compound;

import java.awt.Point;
import org.bm.blaise.graphics.BasicPointGraphic;
import org.bm.blaise.graphics.BasicStringGraphic;
import org.bm.blaise.graphics.Graphic;
import org.bm.blaise.style.PointStyle;
import org.bm.blaise.style.StringStyle;
import java.awt.geom.Point2D;
import org.bm.blaise.graphics.GraphicComposite;
import org.bm.util.PointBean;

/**
 * Displays a point together with a label.
 *
 * @author Elisha
 */
public class LabeledPointGraphic extends GraphicComposite
        implements PointBean<Point2D> {

    /** Stores the point */
    private final BasicPointGraphic point;
    /** Stores the string */
    private final BasicStringGraphic string;

    //
    // CONSTRUCTORS
    //

    /** Construct labeled point with given primitive and default style */
    public LabeledPointGraphic(Point2D p, String s) { 
        this(p, s, null); 
    }

    /** Construct with given primitive and style. */
    public LabeledPointGraphic(Point2D p, String s, PointStyle style) {
        addGraphic(point = new BasicPointGraphic(p, style));
        addGraphic(string = new BasicStringGraphic(p, s));
    }

    
    //
    // PROPERTIES
    //

    public Point2D getPoint() { return point.getPoint(); }
    public void setPoint(Point2D p) { point.setPoint(p); fireGraphicChanged(); }

    public PointStyle getPointStyle() { return point.getStyle(); }
    public void setPointStyle(PointStyle style) { point.setStyle(style); }

    
    public String getString() { return string.getString(); }
    public void setString(String s) { string.setString(s); fireGraphicChanged(); }

    public StringStyle getStringStyle() { return string.getStyle(); }
    public void setStringStyle(StringStyle style) { string.setStyle(style); }

    
    //
    // EVENT HANDLING
    //

    @Override
    public void graphicChanged(Graphic source) {
        if (source == point)
            string.setPoint(getPoint());
        super.graphicChanged(source);
    }

    
    
    @Override
    public boolean contains(Point p) {
        // override to prevent dragging and tips on the string
        return this.point.contains(p);
   }    
    
    
}
