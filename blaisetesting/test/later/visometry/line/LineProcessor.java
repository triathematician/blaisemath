/*
 * LineProcessor.java
 * Created Apr 13, 2010
 */

package later.visometry.line;

import java.awt.geom.Point2D;
import graphics.*;
import visometry.VisometryProcessor;
import visometry.graphics.VGraphicEntry;
import visometry.Visometry;

/**
 * Converts primitives from double-based coordinates to window coordinates.
 *
 * @author Elisha Peterson
 */
class LineProcessor extends VisometryProcessor<Double> {

    @Override
    protected void convert(VGraphicEntry entry, Visometry<Double> vis) {
        if (entry.local == null)
            return;

        if (entry.local instanceof Number)
        {
            entry.primitive = vis.toWindow( ((Number)entry.local).doubleValue() );
        }
        else if (entry.local instanceof Double[])
        {
            Double[] locArr = (Double[]) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[locArr.length];
            for (int i = 0; i < locArr.length; i++)
                winArr[i] = vis.toWindow(locArr[i]);
            entry.primitive = winArr;
        }
        else if (entry.local instanceof GraphicString)
        {
            GraphicString gs = (GraphicString) entry.local;
            if (gs.getAnchor() instanceof Double) {
                entry.primitive = new GraphicString<Point2D.Double>(vis.toWindow((Double) gs.getAnchor()), gs.string);
            }
        }
        else if (entry.local instanceof GraphicRuledLine)
        {
            GraphicRuledLine lRule = (GraphicRuledLine) entry.local;
            entry.primitive = new GraphicRuledLine<Point2D.Double> (
                    vis.toWindow((Double) lRule.start),
                    vis.toWindow((Double) lRule.end),
                    lRule.ticks,
                    lRule.tickLabels );
        } else
            throw new IllegalArgumentException( "Unable to handle local primitive of type " + entry.local.getClass() + " (value = " + entry.local + ")" );

        entry.needsConversion = false;
    }

}
