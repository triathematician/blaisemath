/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package visometry;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import primitive.style.PointStyle;
import primitive.PrimitiveEntry;

/**
 * <p>
 *   This class is responsible for drawing a collection of primitive elements on a 2d graphics object.
 *   One can pass in a group of entries
 * </p>
 * 
 * @author Elisha Peterson
 */
public class SimplePlotRenderer {

    PointStyle defPt = new PointStyle();

    /** Draws all visible elements contained within the given iteration. */
    public void draw(Graphics2D gr, Iterable<? extends PrimitiveEntry> prims) {
        for (PrimitiveEntry pe : prims) {
            if (!pe.visible)
                continue;
            try {
                if (pe.style == null)
                    drawWithDefaultStyle(gr, pe.primitive);
                else if (pe.primitive.getClass() != pe.style.getTargetType() && pe.primitive.getClass().isArray())
                    pe.style.drawArray(gr, (Object[]) pe.primitive, pe.customizer);
                else
                    pe.style.draw(gr, pe.primitive, pe.customizer == null ? null : pe.customizer.get(pe.primitive));
            } catch (Exception ex) {
                System.out.println("SimplePlotRenderer.draw: error drawing: " + pe.primitive + " with style " + pe.style + " (" + ex + ")");
//                ex.printStackTrace();
            }
        }
    }

    private void drawWithDefaultStyle(Graphics2D gr, Object primitive) {
        if (primitive instanceof Point2D.Double)
            defPt.draw(gr, (Point2D.Double) primitive);
        else if (primitive instanceof Point2D.Double[])
            defPt.drawArray(gr, (Point2D.Double[]) primitive);
        else
            System.out.println("Unable to find appropriate style for primitive object " + primitive + " of type " + primitive.getClass());
    }

}
