/**
 * SegmentGraphic.java
 * Created Jan 23, 2011
 */
package dev.compound;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.blaise.graphics.BasicShapeGraphic;
import org.blaise.style.PointStyleBasic;
import org.blaise.style.Markers;
import org.blaise.style.ShapeStyle;
import org.blaise.style.VisibilityHint;

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
        
        start.setStyle(new PointStyleBasic()
                .marker(Markers.CIRCLE)
                .stroke(null)
                .markerRadius(2)
                .fill(Color.black));
        start.setVisibilityHint(VisibilityHint.HIDDEN, true);
        
        end.setStyle(new PointStyleBasic()
                .marker(Markers.ARROWHEAD));
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
