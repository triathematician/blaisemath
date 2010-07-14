/**
 * PathStyleShape.java
 * Created on Aug 4, 2009
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * <p>
 *   <code>PathStyleShape</code> represents any style that is applied to paths or lines.
 *   The default options are the <code>Stroke</code> object and the color... I may
 *   consider changing this to alter the stroke directly via patterns here such as
 *   width, dashes, etc. Alternately, I may design a "builder" to construct various
 *   stroke types.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PathStyleShape extends AbstractPathStyle implements PrimitiveStyle<Shape> {

    /** Construct with default stroke and color black */
    public PathStyleShape() {}
    /** Construct with specific color. */
    public PathStyleShape(Color color) { super(color); }
    /** Construct with specific color/width. */
    public PathStyleShape(Color color, float width){ super(color, width); }
    /** Construct with specified color/stroke. */
    public PathStyleShape(Color color, BasicStroke stroke){ super(color, stroke); }
    

    @Override
    public String toString() {
        return "PathStyle [wid=" + getThickness() + "]";
    }

    public Class<? extends Shape> getTargetType() {
        return Shape.class;
    }

    public void draw(Graphics2D canvas, Shape path) {
        drawPath(canvas, path);
    }

    public void drawArray(Graphics2D canvas, Shape[] paths) {
        drawPaths(canvas, paths);
    }

    public boolean contained(Shape primitive, Graphics2D canvas, Point point) {
        // TODO - add logic
        return false;
    }

    public int containedInArray(Shape[] primitives, Graphics2D canvas, Point point) {
        return -1;
    }

}
