/*
 * MeshStyle.java
 * Created Apr 8, 2010
 */

package primitive.style;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import primitive.GraphicMesh;

/**
 * Provides stylization for a "mesh" defined via a grid of points and connections
 * between those points. For now, the coloring is uniform for each of the points.
 * 
 * @author Elisha Peterson
 */
public class MeshStyle extends AbstractPrimitiveStyle<GraphicMesh<Point2D.Double>> {

    /** Point style. */
    PointStyle pStyle = new PointStyle();
    /** Segment style. */
    PathStyle sStyle = new PathStyle();
    /** Area style. */
    ShapeStyle aStyle = new ShapeStyle();

    /** Visibility parameters. */
    boolean pVisible = false, sVisible = true, aVisible = true;

    /** Construct with default styles. */
    public MeshStyle() {
    }

    public Class getTargetType() {
        return GraphicMesh.class;
    }

    /** @return style used to fill in areas */
    public ShapeStyle getAreaStyle() { return aStyle; }
    /** Sets style used to fill in areas */
    public void setAreaStyle(ShapeStyle aStyle) { this.aStyle = aStyle; }
    /** @return true if areas are visible */
    public boolean isAreasVisible() { return aVisible; }
    /** Sets visibility status of areas */
    public void setAreasVisible(boolean aVisible) { this.aVisible = aVisible; }

    /** @return style used to drawArray points */
    public PointStyle getPointStyle() { return pStyle; }
    /** Sets style used to drawArray points */
    public void setPointStyle(PointStyle pStyle) { this.pStyle = pStyle; }
    /** @return true if points are visible */
    public boolean isPointsVisible() { return pVisible; }
    /** Sets visibility of points */
    public void setPointsVisible(boolean pVisible) { this.pVisible = pVisible; }

    /** @return style used to drawArray segments */
    public PathStyle getSegmentStyle() { return sStyle; }
    /** Sets style used to drawArray segments */
    public void setSegmentStyle(PathStyle sStyle) { this.sStyle = sStyle; }
    /** @return true if segments are visible */
    public boolean isSegmentsVisible() { return sVisible; }
    /** Sets visibility status of segments */
    public void setSegmentsVisible(boolean sVisible) { this.sVisible = sVisible; }



    public void draw(Graphics2D canvas, GraphicMesh<Point2D.Double> mesh) {
        if (aVisible) {
            for (int i = 0; i < mesh.areas.size(); i++) {
                Object[] pts = mesh.getArea(i);
                GeneralPath gp = new GeneralPath();
                Point2D p = (Point2D) pts[0];
                gp.moveTo((float) p.getX(), (float) p.getY());
                for (int j = 0; j < pts.length; j++) {
                    p = (Point2D) pts[j];
                    gp.lineTo((float) p.getX(), (float) p.getY());
                }
                gp.closePath();
                aStyle.draw(canvas, gp);
            }
        }
        if (sVisible) {
            Object[] pts = new Object[2];
            for (int i = 0; i < mesh.segments.length; i++) {
                pts = mesh.getSegment(i);
                sStyle.draw(canvas, new Line2D.Double((Point2D)pts[0], (Point2D)pts[1]));
            }
        }
        if (pVisible) {
            pStyle.drawArray(canvas, mesh.points);
        }
    }

    public boolean contained(GraphicMesh<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return false;
    }

}
