/**
 * SegmentGraphic.java
 * Created Jan 23, 2011
 */
package org.bm.blaise.graphics.compound;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import org.bm.blaise.graphics.BasicShapeGraphic;
import org.bm.blaise.style.BasicPointStyle;
import org.bm.blaise.shape.ShapeLibrary;
import org.bm.blaise.style.ShapeStyle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bm.blaise.style.VisibilityKey;

/**
 * Displays a segment between two points.
 * By default displays an arrowhead at the end, and allows the endpoints of the arrow to be moved.
 *
 * @author Elisha
 */
public class SegmentGraphic extends TwoPointGraphicSupport {

    /** Entry with the line */
    protected BasicShapeGraphic lineEntry;

    /** Construct segment between specified points */
    public SegmentGraphic(Point2D start, Point2D end) {
        super(start, end);
    }

    @Override
    protected void initGraphics() {
        // ensure line is added first
        addGraphic(lineEntry = new BasicShapeGraphic(new GeneralPath(), true));        
        super.initGraphics();
        
        start.setStyle(new BasicPointStyle()
                .shape(ShapeLibrary.CIRCLE)
                .stroke(null)
                .radius(2)
                .fill(Color.black));
        start.setVisibility(VisibilityKey.Invisible);
        
        end.setStyle(new BasicPointStyle()
                .shape(ShapeLibrary.ARROWHEAD));
    }

    @Override
    protected void pointsUpdated() {
        double angle = Math.atan2(end.getPoint().getY() - start.getPoint().getY(), end.getPoint().getX() - start.getPoint().getX());
        start.setAngle(angle+Math.PI);
        end.setAngle(angle);
        lineEntry.setPrimitive(new Line2D.Double(start.getPoint(), end.getPoint()));
    }

    public ShapeStyle getLineStyle() { 
        return lineEntry.getStyle();
    }
    
    public void setLineStyle(ShapeStyle r) { 
        lineEntry.setStyle(r); 
    }
    
}
