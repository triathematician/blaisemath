/*
 * PlaneProcessor.java
 * Created Apr 14, 2010
 */

package visometry.plane;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import primitive.*;
import primitive.style.ArrowStyle;
import primitive.style.PathStyle;
import primitive.style.PointDirStyle;
import primitive.style.PathStylePoints;
import primitive.style.ShapeStyle;
import visometry.PlotProcessor;
import visometry.Visometry;
import visometry.VPrimitiveEntry;

/**
 * Handles conversion of graphics primitives from planar type to window type. Supported primitives:
 * <ul>
 *      <li>A single point: <code>Point2D.Double</code>
 *      <li>An array of points: <code>Point2D.Double[]</code> (using a <code>PathStyle</code>, a <code>ShapeStyle</code>, or a <code>PointStyle</code>)
 *      <li>A point with attached string: <code>GraphicString</code>
 *      <li>A point with a radius parameter: <code>GraphicParPoint</code>
 *      <li>A path or shape: <code>Shape</code>
 *      <li>A mesh: <code>GraphicMesh</code>
 * </ul>
 * @author Elisha Peterson
 */
public class PlaneProcessor extends PlotProcessor<Point2D.Double> {

    @Override
    protected void convert(VPrimitiveEntry entry, Visometry<Point2D.Double> vis) {
        if (entry.local == null)
            return;

        if (entry.local instanceof GraphicString)
        {
            convertGraphicString(entry, (GraphicString) entry.local, vis);
        }

        else if (entry.local instanceof GraphicString[]) {
            convertGraphicString(entry, (GraphicString[]) entry.local, vis);
        }

        else if (entry.local instanceof Point2D.Double)
        {
            entry.primitive = vis.getWindowPointOf((Point2D.Double) entry.local);
        }

        else if (entry.local instanceof Point2D.Double[] && (entry.style instanceof PathStyle || entry.style instanceof ShapeStyle))
        {
            Point2D.Double[] locArr = (Point2D.Double[]) entry.local;
            GeneralPath path = new GeneralPath();
            if (locArr.length > 0) {
                path.moveTo((float) locArr[0].x, (float) locArr[0].y);
                for (int i = 0; i < locArr.length; i++)
                    path.lineTo((float) locArr[i].x, (float) locArr[i].y);
                if (entry.style instanceof ShapeStyle)
                    path.closePath();
            }
            path.transform(((PlaneVisometry)vis).at);
            entry.primitive = path;
        }

        else if (entry.local instanceof Point2D.Double[][] && (entry.style instanceof PathStyle || entry.style instanceof ShapeStyle))
        {
            Point2D.Double[][] locArr = (Point2D.Double[][]) entry.local;
            Shape[] paths = new Shape[locArr.length];
            GeneralPath path;
            int j = 0;
            for (Point2D.Double[] arr : locArr) {
                paths[j] = path = new GeneralPath();
                path.moveTo((float) arr[0].x, (float) arr[0].y);
                for (int i = 0; i < arr.length; i++)
                    path.lineTo((float) arr[i].x, (float) arr[i].y);
                if (entry.style instanceof ShapeStyle)
                    path.closePath();
                path.transform(((PlaneVisometry)vis).at);
                j++;
            }
            entry.primitive = paths;
        }

        else if (entry.local instanceof Point2D.Double[])
        {
            Point2D.Double[] locArr = (Point2D.Double[]) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[locArr.length];
            for (int i = 0; i < locArr.length; i++)
                winArr[i] = vis.getWindowPointOf(locArr[i]);
            entry.primitive = winArr;
        }

        else if (entry.local instanceof Point2D.Double[][] && (entry.style instanceof ArrowStyle || entry.style instanceof PathStylePoints))
        {
            Point2D.Double[][] locArr = (Point2D.Double[][]) entry.local;
            ArrayList<Point2D.Double[]> winArr = new ArrayList<Point2D.Double[]>();
            for (int i = 0; i < locArr.length; i++) {
                Point2D.Double[] win2 = new Point2D.Double[locArr[i].length];
                for (int j = 0; j < locArr[i].length; j++)
                    win2[j] = vis.getWindowPointOf(locArr[i][j]);
                winArr.add(win2);
            }
            entry.primitive = winArr.toArray(new Point2D.Double[][]{});
        }

        else if (entry.local instanceof GraphicPointDir[] && entry.style instanceof PointDirStyle)
            convertGraphicPointDir(entry, (GraphicPointDir[]) entry.local, ((PointDirStyle) entry.style).getMaxLength(), vis);

        else if (entry.local instanceof Shape)
        {
            entry.primitive = ((PlaneVisometry)vis).at.createTransformedShape((Shape) entry.local);
        }

        else if (entry.local instanceof GraphicMesh)
        {
            GraphicMesh lMesh = (GraphicMesh) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[lMesh.points.length];
            for (int i = 0; i < winArr.length; i++)
                winArr[i] = vis.getWindowPointOf((Point2D.Double) lMesh.points[i]);
            entry.primitive = new GraphicMesh<Point2D.Double>(winArr, lMesh.segments, lMesh.areas);
        }

        else if (entry.local instanceof GraphicRuledLine)
        {
            GraphicRuledLine lRule = (GraphicRuledLine) entry.local;
            entry.primitive = new GraphicRuledLine<Point2D.Double> (
                    vis.getWindowPointOf((Point2D.Double) lRule.start),
                    vis.getWindowPointOf((Point2D.Double) lRule.end),
                    lRule.label,
                    lRule.ticks,
                    lRule.tickLabels );
        }

        else
            throw new IllegalArgumentException( "PlaneProcessor.convert: Unable to handle local primitive of type " + entry.local.getClass() + " (value = " + entry.local + ")" );
        
        entry.needsConversion = false;
    }

    /** Converts graphic strings */
    private static void convertGraphicString(VPrimitiveEntry entry, GraphicString loc, Visometry<Point2D.Double> vis) {
        if (loc.getAnchor() instanceof Point2D.Double) {
            GraphicString<Point2D.Double> result;
            entry.primitive = result = new GraphicString<Point2D.Double>(vis.getWindowPointOf((Point2D.Double) loc.getAnchor()), loc.getString());
            result.setLocation(result.anchor);
        }
    }

    /** Converts graphic strings */
    private static void convertGraphicString(VPrimitiveEntry entry, GraphicString[] locArr, Visometry<Point2D.Double> vis) {
        GraphicString[] winArr = new GraphicString[locArr.length];
        for (int i = 0; i < locArr.length; i++) {
            if (locArr[i].getAnchor() instanceof Point2D.Double) {
                winArr[i] = new GraphicString<Point2D.Double>(vis.getWindowPointOf((Point2D.Double) locArr[i].getAnchor()), locArr[i].getString());
                winArr[i].setLocation((Point2D.Double) winArr[i].anchor);
            }
        }
        entry.primitive = winArr;
    }

    /** Converts graphic point dir array, updating for maximum lengths */
    private static void convertGraphicPointDir(VPrimitiveEntry entry, GraphicPointDir[] locArr, double maxLengthPermitted, Visometry<Point2D.Double> vis) {
        Point2D.Double zeroLoc = vis.getWindowPointOf(new Point2D.Double(0, 0));

        GraphicPointDir[] winArr = new GraphicPointDir[locArr.length];
        Point2D.Double dirLoc;
        Point2D.Double dir;
        double maxLengthSq = 0;
        for (int i = 0; i < locArr.length; i++) {
            dirLoc = vis.getWindowPointOf((Point2D.Double) locArr[i].dir);
            dirLoc.x -= zeroLoc.x; dirLoc.y -= zeroLoc.y;
            maxLengthSq = Math.max(maxLengthSq, dirLoc.x*dirLoc.x + dirLoc.y*dirLoc.y);
            winArr[i] = new GraphicPointDir(vis.getWindowPointOf((Point2D.Double) locArr[i].anchor), dirLoc);
        }
        for (int i = 0; i < locArr.length; i++) {
            ((Point2D.Double)winArr[i].dir).x *= maxLengthPermitted / Math.sqrt(maxLengthSq);
            ((Point2D.Double)winArr[i].dir).y *= maxLengthPermitted / Math.sqrt(maxLengthSq);
        }
        entry.primitive = winArr;
    }
}
