/*
 * AbstractPrimitiveStyle.java
 * Created May 13, 2010
 */

package old.styles.verified;

import old.other.StyleMap;
import graphics.renderer.ShapeRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;
import java.util.Map;

/**
 * Implements default behavior for drawing/checking containment for arrays of primitives.
 * Can be used as a superclass for primitive styles that do not inherit from another type.
 * Also defaults to NO customization keys.
 * @author Elisha Peterson
 */
public abstract class AbstractPrimitiveStyle<C> implements ShapeRenderer<C> {

    public Map<String, Class> getStyleTypes() {
        return Collections.emptyMap();
    }

    public void draw(Graphics2D canvas, C primitive) {
        draw(canvas, primitive, StyleMap.NULL);
    }

    public void drawArray(Graphics2D canvas, C[] primitives) {
        drawArray(canvas, primitives, StyleMap.NULL);
    }

    public void drawArray(Graphics2D canvas, C[] primitives, StyleMap customizer) {
        for (C p : primitives)
            draw(canvas, p, customizer);
    }

    public boolean contained(Point point, C primitive, Graphics2D canvas) {
        return contained(point, primitive, canvas, StyleMap.NULL);
    }

    public int containedInArray(Point point, C[] primitives, Graphics2D canvas) {
        return containedInArray(point, primitives, canvas, StyleMap.NULL);
    }

    public int containedInArray(Point point, C[] primitives, Graphics2D canvas, StyleMap customizer) {
        // reverse order of draw
        for (int i = primitives.length - 1; i >= 0; i--)
            if (contained(point, primitives[i], canvas, customizer))
                return i;
        return -1;
    }
}
