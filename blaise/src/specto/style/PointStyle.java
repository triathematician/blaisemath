/**
 * PointStyle.java
 * Created on Jun 3, 2008
 */

package specto.style;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Vector;
import sequor.style.VisualStyle;

/**
 *
 * @author Elisha Peterson
 */
public class PointStyle extends Style {
    
    // CONSTRUCTORS
    
    public PointStyle() { super(VisualStyle.POINT_STYLE_STRINGS); }
    public PointStyle(int style) { super(VisualStyle.POINT_STYLE_STRINGS); setStyle(style); }
    
    // STYLE SETTINGS
    
    public static final int SMALL=0;
    public static final int MEDIUM=1;
    public static final int LARGE=2;
    public static final int RING=3;
    public static final int CONCENTRIC=4;
    
    // BEANS
    
    public int getStyle() {return value;}
    public void setStyle(int newValue){setValue(newValue);}

    @Override
    public void apply(Graphics2D g) {}
    
    /** Draws point at specified center based on current setting. */
    public void draw(Graphics2D g, Point2D.Double center) {
        switch(value) {
            case SMALL:
                g.fill(dotShape(center,1));
                break;
            case MEDIUM:
                g.fill(dotShape(center,2));
                break;
            case LARGE:
                g.fill(dotShape(center,4));
                break;
            case RING:
                g.fill(dotShape(center,3));
                g.setStroke(VisualStyle.MEDIUM_STROKE);
                g.draw(dotShape(center,5));
                break;
            case CONCENTRIC:
                g.setStroke(VisualStyle.THICK_STROKE);
                g.draw(dotShape(center,5));
                break;
        }
    }    
    
    /** Draws several points at specified centers based on current setting. */
    public void draw(Graphics2D g, Vector<Point2D.Double> centers) {
        switch(value) {
            case SMALL:
                for(Point2D.Double center : centers) {
                    g.fill(dotShape(center,1));
                }
                break;
            case MEDIUM:
                for(Point2D.Double center : centers) {
                    g.fill(dotShape(center,2));
                }
                break;
            case LARGE:
                for(Point2D.Double center : centers) {
                    g.fill(dotShape(center,4));
                }
                break;
            case RING:
                g.setStroke(VisualStyle.MEDIUM_STROKE);
                for(Point2D.Double center : centers) {
                    g.fill(dotShape(center,3));
                    g.draw(dotShape(center,5));
                }
                break;
            case CONCENTRIC:
                g.setStroke(VisualStyle.THICK_STROKE);
                for(Point2D.Double center : centers) {
                    g.draw(dotShape(center,5));
                }
                break;
        }
    }
    
    /** Returns a circle with specified center and radius (window coordinates). */
    public static Shape dotShape(Point2D.Double center, double radius) {
        return new Ellipse2D.Double(center.x-radius, center.y-radius, 2*radius, 2*radius);
    }
}
