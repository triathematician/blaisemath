/*
 * LineProcessor.java
 * Created Apr 13, 2010
 */

package visometry.line;

import java.awt.geom.Point2D;
import primitive.*;
import visometry.PlotProcessor;
import visometry.VPrimitiveEntry;
import visometry.Visometry;

/**
 * Converts primitives from double-based coordinates to window coordinates.
 *
 * @author Elisha Peterson
 */
class LineProcessor extends PlotProcessor<Double> {

    @Override
    protected void convert(VPrimitiveEntry entry, Visometry<Double> vis) {
        if (entry.local == null)
            return;

        if (entry.local instanceof Number)
        {
            entry.primitive = vis.getWindowPointOf( ((Number)entry.local).doubleValue() );
        }
        else if (entry.local instanceof Double[])
        {
            Double[] locArr = (Double[]) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[locArr.length];
            for (int i = 0; i < locArr.length; i++)
                winArr[i] = vis.getWindowPointOf(locArr[i]);
            entry.primitive = winArr;
        }
        else if (entry.local instanceof GraphicString)
        {
            GraphicString gs = (GraphicString) entry.local;
            if (gs.getAnchor() instanceof Double) {
                entry.primitive = new GraphicString<Point2D.Double>(vis.getWindowPointOf((Double) gs.getAnchor()), gs.string);
            }
        }
        else if (entry.local instanceof GraphicRuledLine)
        {
            GraphicRuledLine lRule = (GraphicRuledLine) entry.local;
            entry.primitive = new GraphicRuledLine<Point2D.Double> (
                    vis.getWindowPointOf((Double) lRule.start),
                    vis.getWindowPointOf((Double) lRule.end),
                    lRule.label,
                    lRule.ticks,
                    lRule.tickLabels );
        } else
            throw new IllegalArgumentException( "Unable to handle local primitive of type " + entry.local.getClass() + " (value = " + entry.local + ")" );

        entry.needsConversion = false;
    }

}
