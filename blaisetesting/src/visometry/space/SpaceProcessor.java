/*
 * SpaceProcessor.java
 * Created Apr 14, 2010
 */

package visometry.space;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import primitive.*;
import primitive.style.PathStyleShape;
import primitive.style.ShapeStyle;
import scio.coordinate.Point3D;
import visometry.PlotProcessor;
import visometry.Visometry;
import visometry.VPrimitiveEntry;

/**
 * Handles conversion of graphics primitives from planar type to window type. Supported primitives:
 * <ul>
 *      <li>A single point: <code>Point2D.Double</code>
 *      <li>An array of points: <code>Point2D.Double[]</code> (using a <code>PathStyleShape</code>, a <code>ShapeStyle</code>, or a <code>PointStyle</code>)
 *      <li>A point with attached string: <code>GraphicString</code>
 *      <li>A point with a radius parameter: <code>GraphicParPoint</code>
 *      <li>A path or shape: <code>Shape</code>
 *      <li>A mesh: <code>GraphicMesh</code>
 * </ul>
 * @author Elisha Peterson
 */
public class SpaceProcessor extends PlotProcessor<Point3D> {

    @Override
    protected void convert(VPrimitiveEntry entry, Visometry<Point3D> vis) {
        if (entry.local == null)
            return;

        if (entry.local instanceof Point3D)
        {
            entry.primitive = vis.getWindowPointOf((Point3D) entry.local);
        }
        else if (entry.local instanceof Point3D[] && (entry.style instanceof PathStyleShape || entry.style instanceof ShapeStyle))
        {
            Point3D[] locArr = (Point3D[]) entry.local;
            GeneralPath path = new GeneralPath();
            Point2D.Double winPt = vis.getWindowPointOf(locArr[0]);
            path.moveTo((float) winPt.x, (float) winPt.y);
            for (int i = 0; i < locArr.length; i++) {
                winPt = vis.getWindowPointOf(locArr[i]);
                path.lineTo((float) winPt.x, (float) winPt.y);
            }
            if (entry.style instanceof ShapeStyle)
                path.closePath();
            entry.primitive = path;
        }
        else if (entry.local instanceof Point3D[])
        {
            Point3D[] locArr = (Point3D[]) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[locArr.length];
            for (int i = 0; i < locArr.length; i++)
                winArr[i] = vis.getWindowPointOf(locArr[i]);
            entry.primitive = winArr;
        } 
        else if (entry.local instanceof GraphicString)
        {
            GraphicString gs = (GraphicString) entry.local;
            if (gs.getAnchor() instanceof Point3D)
                entry.primitive = new GraphicString<Point2D.Double>(vis.getWindowPointOf((Point3D) gs.getAnchor()), gs.getString());
        } 
        else if (entry.local instanceof GraphicMesh)
        {
            GraphicMesh lMesh = (GraphicMesh) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[lMesh.points.length];
            for (int i = 0; i < winArr.length; i++)
                winArr[i] = vis.getWindowPointOf((Point3D) lMesh.points[i]);
            entry.primitive = new GraphicMesh<Point2D.Double>(winArr, lMesh.segments, lMesh.areas);
        }
        else if (entry.local instanceof GraphicRuledLine)
        {
            GraphicRuledLine lRule = (GraphicRuledLine) entry.local;
            Point3D p3s = (Point3D) lRule.start, p3e = (Point3D) lRule.end;
            Point2D.Double p2s = vis.getWindowPointOf(p3s), p2e = vis.getWindowPointOf(p3e);
            double totalWinLength = p2s.distance(p2e);
            double[] newTicks = new double[lRule.ticks.length];
            for (int i = 0; i < lRule.ticks.length; i++)
                newTicks[i] = vis.getWindowPointOf(new Point3D(
                        (1-lRule.ticks[i])*p3s.x+lRule.ticks[i]*p3e.x,
                        (1-lRule.ticks[i])*p3s.y+lRule.ticks[i]*p3e.y,
                        (1-lRule.ticks[i])*p3s.z+lRule.ticks[i]*p3e.z)).distance(p2s) / totalWinLength;

            entry.primitive = new GraphicRuledLine<Point2D.Double> (
                    p2s, p2e,
                    newTicks,
                    lRule.tickLabels );
        } else
            throw new IllegalArgumentException( "Unable to handle local primitive of type " + entry.local.getClass() + " (value = " + entry.local + ")" );
        
        entry.needsConversion = false;
    }
}
