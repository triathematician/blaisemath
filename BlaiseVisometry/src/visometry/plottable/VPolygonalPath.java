/*
 * VPolygonalPath.java
 * Created Jan 29, 2011
 */

package visometry.plottable;

import org.bm.blaise.graphics.renderer.BasicStrokeRenderer;
import java.awt.geom.Point2D;
import utils.IndexedGetterSetter;
import visometry.graphics.VPolygonalPathEntry;

/**
 * Displays a set of points as specified in local coordinates. Points can be dragged by default
 * (build into the <code>PointSetEntry</code>).
 *
 * @author Elisha Peterson
 */
public class VPolygonalPath<C> extends AbstractPlottable<C>
        implements IndexedGetterSetter<C> {

    protected C[] point;
    protected VPolygonalPathEntry en;

    public VPolygonalPath(C[] point) {
        this.point = point;
        en = new VPolygonalPathEntry<C>(this);
    }

    public VPolygonalPath(C[] point, BasicStrokeRenderer rend) {
        this(point);
        en.setRenderer(rend);
    }

    public VPolygonalPathEntry getGraphicEntry() { return en; }

    public int getSize() { return point.length; }
    
    public C getElement(int i) { return point[i]; }
    public void setElement(int i, C point) {
        if (!((this.point[i] == null && point == null) || (this.point[i] != null && this.point[i].equals(point)))) {
            this.point[i] = point;
            firePlottableChanged(false);
        }
    }
    public void setElement(int i, C initial, C dragStart, C dragFinish) {
        C newPoint;
        if (initial instanceof Point2D) {
            Point2D in = (Point2D) initial, s = (Point2D) dragStart, f = (Point2D) dragFinish;
            newPoint = (C) new Point2D.Double(in.getX()+f.getX()-s.getX(), in.getY()+f.getY()-s.getY());
        } else
            newPoint = dragFinish;
        setElement(i, newPoint);
    }

}
