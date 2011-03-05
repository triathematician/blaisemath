/*
 * VAbstractPoint.java
 * Created Apr 12, 2010
 */

package org.bm.blaise.specto.plottable;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.graphics.VPointEntry;
import utils.RelativePointBean;

/**
 * A point displayed on a window, w/ support for dragging
 * (supported by default in <code>VPointEntry</code>).
 *
 * @author Elisha Peterson
 */
public class VPoint<C> extends AbstractPlottable<C>
        implements RelativePointBean<C> {

    protected C point;
    protected VPointEntry en;

    public VPoint(C point) {
        this.point = point;
        en = new VPointEntry<C>(this);
    }

    public VPointEntry getGraphicEntry() { return en; }
    public C getPoint() { return point; }
    public void setPoint(C point) {
        if (!((this.point == null && point == null) || (this.point != null && this.point.equals(point)))) {
            this.point = point;
            firePlottableChanged(false);
        }
    }

    public void setPoint(C initial, C dragStart, C dragFinish) {
        C newPoint;
        if (initial instanceof Point2D) {
            Point2D i = (Point2D) initial, s = (Point2D) dragStart, f = (Point2D) dragFinish;
            newPoint = (C) new Point2D.Double(i.getX()+f.getX()-s.getX(), i.getY()+f.getY()-s.getY());
        } else
            newPoint = dragFinish;
        setPoint(newPoint);
    }

}
