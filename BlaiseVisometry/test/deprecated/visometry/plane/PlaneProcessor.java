/*
 * PlaneProcessor.java
 * Created Apr 14, 2010
 */

package deprecated.visometry.plane;

import primitive.style.temp.ArrowStyle;
import primitive.style.temp.PathStylePoints;
import primitive.style.temp.ShapeStyle;
import primitive.style.temp.PointDirStyle;
import primitive.style.temp.PathStyleShape;
import primitive.style.temp.PointRadiusStyle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import graphics.*;
import primitive.style.*;
import visometry.VisometryProcessor;
import visometry.Visometry;
import visometry.graphics.VGraphicEntry;
import visometry.plane.PlaneVisometry;

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
class PlaneProcessor extends VisometryProcessor<Point2D.Double> {

    @Override
    protected void convert(VGraphicEntry entry, Visometry<Point2D.Double> vis) {
        if (entry.local == null)
            return;

        if (entry.local instanceof GraphicImage)
            convertGraphicImage(entry, (GraphicImage) entry.local, vis);
        else if (entry.local instanceof GraphicImage[])
            convertGraphicImage(entry, (GraphicImage[]) entry.local, vis);
        else if (entry.local instanceof GraphicPointFancy)
            convertGraphicPointFancy(entry, (GraphicPointFancy) entry.local, vis);
        else if (entry.local instanceof GraphicPointFancy[])
            convertGraphicPointFancy(entry, (GraphicPointFancy[]) entry.local, vis);

        else if (entry.local instanceof GraphicString)
            convertGraphicString(entry, (GraphicString) entry.local, vis);
        else if (entry.local instanceof GraphicString[])
            convertGraphicString(entry, (GraphicString[]) entry.local, vis);

        else if (entry.local instanceof Point2D.Double)
            entry.primitive = vis.toWindow((Point2D.Double) entry.local);

        else if (entry.local instanceof Point2D.Double[] && (entry.renderer instanceof PathStyleShape || entry.renderer instanceof ShapeStyle))
        {
            Point2D.Double[] locArr = (Point2D.Double[]) entry.local;
            GeneralPath path = new GeneralPath();
            if (locArr.length > 0) {
                path.moveTo((float) locArr[0].x, (float) locArr[0].y);
                for (int i = 0; i < locArr.length; i++)
                    path.lineTo((float) locArr[i].x, (float) locArr[i].y);
                if (entry.renderer instanceof ShapeStyle)
                    path.closePath();
            }
            path.transform(((PlaneVisometry)vis).at);
            entry.primitive = path;
        }

        else if (entry.local instanceof Point2D.Double[][] && (entry.renderer instanceof PathStyleShape || entry.renderer instanceof ShapeStyle))
        {
            Point2D.Double[][] locArr = (Point2D.Double[][]) entry.local;
            Shape[] paths = new Shape[locArr.length];
            GeneralPath path;
            int j = 0;
            for (Point2D.Double[] arr : locArr) {
                paths[j++] = path = new GeneralPath();
                if (arr.length > 0) {
                    path.moveTo((float) arr[0].x, (float) arr[0].y);
                    for (int i = 0; i < arr.length; i++)
                        path.lineTo((float) arr[i].x, (float) arr[i].y);
                    if (entry.renderer instanceof ShapeStyle)
                        path.closePath();
                    path.transform(((PlaneVisometry)vis).at);
                }
            }
            entry.primitive = paths;
        }

        else if (entry.local instanceof Point2D.Double[])
        {
            Point2D.Double[] locArr = (Point2D.Double[]) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[locArr.length];
            for (int i = 0; i < locArr.length; i++)
                winArr[i] = vis.toWindow(locArr[i]);
            entry.primitive = winArr;
        }

        else if (entry.local instanceof Point2D.Double[][] && (entry.renderer instanceof ArrowStyle || entry.renderer instanceof PathStylePoints))
        {
            Point2D.Double[][] locArr = (Point2D.Double[][]) entry.local;
            ArrayList<Point2D.Double[]> winArr = new ArrayList<Point2D.Double[]>();
            for (int i = 0; i < locArr.length; i++) {
                Point2D.Double[] win2 = new Point2D.Double[locArr[i].length];
                for (int j = 0; j < locArr[i].length; j++)
                    win2[j] = vis.toWindow(locArr[i][j]);
                winArr.add(win2);
            }
            entry.primitive = winArr.toArray(new Point2D.Double[][]{});
        }

        else if (entry.local instanceof GraphicPointDir[] && entry.renderer instanceof PointDirStyle)
            convertGraphicPointDir(entry, (GraphicPointDir[]) entry.local, ((PointDirStyle) entry.renderer).getMaxLength(), vis);

        else if (entry.local instanceof GraphicPointRadius[] && entry.renderer instanceof PointRadiusStyle)
            convertGraphicPointRadius(entry, (GraphicPointRadius[]) entry.local, ((PointRadiusStyle) entry.renderer).getMaxRadius(), vis);

        else if (entry.local instanceof Shape)
            entry.primitive = ((PlaneVisometry)vis).at.createTransformedShape((Shape) entry.local);

        else if (entry.local instanceof GraphicMesh)
        {
            GraphicMesh lMesh = (GraphicMesh) entry.local;
            Point2D.Double[] winArr = new Point2D.Double[lMesh.points.length];
            for (int i = 0; i < winArr.length; i++)
                winArr[i] = vis.toWindow((Point2D.Double) lMesh.points[i]);
            entry.primitive = new GraphicMesh<Point2D.Double>(winArr, lMesh.segments, lMesh.areas);
        }

        else if (entry.local instanceof GraphicRuledLine)
        {
            GraphicRuledLine lRule = (GraphicRuledLine) entry.local;
            entry.primitive = new GraphicRuledLine<Point2D.Double> (
                    vis.toWindow((Point2D.Double) lRule.start),
                    vis.toWindow((Point2D.Double) lRule.end),
                    lRule.ticks,
                    lRule.tickLabels );
        }

        else
            throw new IllegalArgumentException( "PlaneProcessor.convert: Unable to handle local primitive of type " + entry.local.getClass() + " (value = " + entry.local + ")" );
        
        entry.needsConversion = false;
    }

    /** Converts "fancy" graphic points */
    private static void convertGraphicPointFancy(VGraphicEntry entry, GraphicPointFancy loc, Visometry<Point2D.Double> vis) {
        if (loc.getAnchor() instanceof Point2D.Double) {
            GraphicPointFancy<Point2D.Double> result;
            entry.primitive = result = new GraphicPointFancy<Point2D.Double>(
                    vis.toWindow((Point2D.Double) loc.getAnchor()),
                    loc.getString(), loc.getRadius(), loc.getColor());
            result.setLocation(result.anchor);
        }
    }

    /** Converts graphic strings */
    private static void convertGraphicPointFancy(VGraphicEntry entry, GraphicPointFancy[] locArr, Visometry<Point2D.Double> vis) {
        GraphicPointFancy[] winArr = new GraphicPointFancy[locArr.length];
        for (int i = 0; i < locArr.length; i++) {
            if (locArr[i].getAnchor() instanceof Point2D.Double) {
                winArr[i] = new GraphicPointFancy<Point2D.Double>(
                        vis.toWindow((Point2D.Double) locArr[i].getAnchor()),
                        locArr[i].getString(), locArr[i].getRadius(), locArr[i].getColor());
                winArr[i].setLocation((Point2D.Double) winArr[i].anchor);
            }
        }
        entry.primitive = winArr;
    }

    /** Converts graphic strings */
    private static void convertGraphicString(VGraphicEntry entry, GraphicString loc, Visometry<Point2D.Double> vis) {
        if (loc.getAnchor() instanceof Point2D.Double) {
            GraphicString<Point2D.Double> result;
            entry.primitive = result = new GraphicString<Point2D.Double>(vis.toWindow((Point2D.Double) loc.getAnchor()), loc.getString());
            result.setLocation(result.anchor);
        }
    }

    /** Converts graphic strings */
    private static void convertGraphicString(VGraphicEntry entry, GraphicString[] locArr, Visometry<Point2D.Double> vis) {
        GraphicString[] winArr = new GraphicString[locArr.length];
        for (int i = 0; i < locArr.length; i++) {
            if (locArr[i].getAnchor() instanceof Point2D.Double) {
                winArr[i] = new GraphicString<Point2D.Double>(vis.toWindow((Point2D.Double) locArr[i].getAnchor()), locArr[i].getString());
                winArr[i].setLocation((Point2D.Double) winArr[i].anchor);
            }
        }
        entry.primitive = winArr;
    }

    /** Converts graphic point dir array, updating for maximum lengths */
    private static void convertGraphicPointDir(VGraphicEntry entry, GraphicPointDir[] locArr, double maxLengthPermitted, Visometry<Point2D.Double> vis) {
        Point2D.Double zeroLoc = vis.toWindow(new Point2D.Double(0, 0));

        GraphicPointDir[] winArr = new GraphicPointDir[locArr.length];
        Point2D.Double dirLoc;
        Point2D.Double dir;
        double maxLengthSq = 0;
        for (int i = 0; i < locArr.length; i++) {
            dirLoc = vis.toWindow((Point2D.Double) locArr[i].dir);
            dirLoc.x -= zeroLoc.x; dirLoc.y -= zeroLoc.y;
            maxLengthSq = Math.max(maxLengthSq, dirLoc.x*dirLoc.x + dirLoc.y*dirLoc.y);
            winArr[i] = new GraphicPointDir(vis.toWindow((Point2D.Double) locArr[i].anchor), dirLoc);
        }
        for (int i = 0; i < locArr.length; i++) {
            ((Point2D.Double)winArr[i].dir).x *= maxLengthPermitted / Math.sqrt(maxLengthSq);
            ((Point2D.Double)winArr[i].dir).y *= maxLengthPermitted / Math.sqrt(maxLengthSq);
        }
        entry.primitive = winArr;
    }

    /** Converts graphic point rad array, updating for maximum radii */
    private static void convertGraphicPointRadius(VGraphicEntry entry, GraphicPointRadius[] locArr, double maxRadPermitted, Visometry<Point2D.Double> vis) {
        GraphicPointRadius[] winArr = new GraphicPointRadius[locArr.length];
        double maxRad = 0;
        for (int i = 0; i < locArr.length; i++)
            maxRad = Math.max(maxRad, locArr[i].rad);

        for (int i = 0; i < locArr.length; i++) {
            Point2D.Double anchor = (Point2D.Double) locArr[i].anchor;
            winArr[i] = new GraphicPointRadius(vis.toWindow(anchor), locArr[i].rad / maxRad * maxRadPermitted / 2.0);
        }
        entry.primitive = winArr;
    }

    private void convertGraphicImage(VGraphicEntry entry, GraphicImage loc, Visometry<Double> vis) {
        if (loc.getAnchor() instanceof Point2D.Double) {
            GraphicImage<Point2D.Double> prim = new GraphicImage<Point2D.Double>(
                    vis.toWindow((Point2D.Double) loc.getAnchor()),
                    loc.image, loc.highlight);
            entry.primitive = prim;
            if (loc.corner != null && loc.corner instanceof Point2D.Double)
                prim.corner = vis.toWindow((Point2D.Double) loc.corner);
        }
    }

    private void convertGraphicImage(VGraphicEntry entry, GraphicImage[] locArr, Visometry<Double> vis) {
        GraphicImage[] winArr = new GraphicImage[locArr.length];
        for (int i = 0; i < locArr.length; i++)
            if (locArr[i].getAnchor() instanceof Point2D.Double) {
                GraphicImage<Point2D.Double> prim = new GraphicImage<Point2D.Double>(
                        vis.toWindow((Point2D.Double) locArr[i].getAnchor()),
                        locArr[i].image, locArr[i].highlight);
                winArr[i] = prim;
                if (locArr[i].corner != null && locArr[i].corner instanceof Point2D.Double)
                    prim.corner = vis.toWindow((Point2D.Double) locArr[i].corner);
            }
        entry.primitive = winArr;
    }
}
