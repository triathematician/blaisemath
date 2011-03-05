/*
 * PointDirStyle.java
 * Created Apr 15, 2010
 */

package old.styles;

import old.styles.verified.ArrowStyle;
import old.styles.verified.AbstractPrimitiveStyle;
import old.primitives.GraphicPointDir;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Map;
import static org.bm.blaise.graphics.renderer.StyleUtils.*;

/**
 * Displays an anchor point together with a direction. Also supports a scaling parameter that
 * is applied uniformly when the elements are drawn. Uses an underlying ArrowStyle
 * to drawArray.
 * 
 * @author Elisha Peterson
 */
public class PointDirStyle extends AbstractPrimitiveStyle<GraphicPointDir<Point2D.Double>> {

    private static final Map<String,Class> $KEY_MAP = keyMap(
            $KEY_CENTERED, Boolean.class,
            $KEY_MAX_LENGTH, Double.class
            );

    /** Arrow style used to drawArray. */
    ArrowStyle baseStyle = new ArrowStyle();
    /** Whether arrows are centered at anchors. */
    boolean centered;
    /** Multiplier to scale the points. */
    double maxLength;

    /** Construct with provided color, shape, and arrow size */
    public PointDirStyle(Color color, ArrowStyle.ArrowShape shape, int size) {
        baseStyle = new ArrowStyle(color, shape, size);
    }

    public Class getTargetType() { return GraphicPointDir.class; }
    public Map<String, Class> getStyleTypes() { return $KEY_MAP; }

    /** @return true if arrows are centered at points */
    public boolean isCentered() { return centered; }
    /** @param center true if arrows are centered at points */
    public void setCentered(boolean center) { centered = center; }    
    /** @return current maximum arrow length drawn (applies to drawing multiples only) */
    public double getMaxLength() { return maxLength; }
    /** @param length new max arrow length */
    public void setMaxLength(double length) { maxLength = length; }
    /** @return base style for drawing */
    public ArrowStyle getBaseStyle() { return baseStyle; }
    /** Sets base style for drawing */
    public void setBaseStyle(ArrowStyle style) { this.baseStyle = style; }

    public void draw(Graphics2D canvas, GraphicPointDir<Point2D.Double> primitive) {
        Point2D.Double[] drawn = new Point2D.Double[2];
        if (centered) {
            drawn[0] = new Point2D.Double(primitive.anchor.x - .5*primitive.dir.x, primitive.anchor.y -.5*primitive.dir.y);
            drawn[1] = new Point2D.Double(primitive.anchor.x + .5*primitive.dir.x, primitive.anchor.y + .5*primitive.dir.y);
        } else {
            drawn[0] = primitive.anchor;
            drawn[1] = new Point2D.Double(primitive.anchor.x + primitive.dir.x, primitive.anchor.y + primitive.dir.y);
        }
        baseStyle.draw(canvas, drawn);
    }

    public boolean contained(GraphicPointDir<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return false;
    }

}
