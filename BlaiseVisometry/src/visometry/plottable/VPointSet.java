/*
 * VAbstractPoint.java
 * Created Apr 12, 2010
 */

package visometry.plottable;

import org.bm.blaise.graphics.renderer.BasicPointRenderer;
import org.bm.blaise.graphics.renderer.PointRenderer;
import java.awt.geom.Point2D;
import utils.IndexedGetter;
import utils.IndexedGetterSetter;
import visometry.graphics.VGraphicEntry;
import visometry.graphics.VPointSetEntry;

/**
 * Displays a set of points as specified in local coordinates. Points can be dragged by default
 * (build into the <code>VPointSetEntry</code>).
 *
 * @author Elisha Peterson
 */
public class VPointSet<C> extends AbstractPlottable<C>
        implements IndexedGetterSetter.Relative<C> {

    protected C[] point;
    protected VPointSetEntry en;

    public VPointSet(C[] point) {
        this.point = point;
        en = new VPointSetEntry<C>(this);
    }

    public VPointSet(C[] point, PointRenderer rend) {
        this(point);
        en.setRenderer(rend);
    }

    public VPointSet(C[] point, IndexedGetter<PointRenderer> custom) {
        this(point);
        en.setIndexedRenderer(custom);
    }

    public VGraphicEntry getGraphicEntry() { return en; }

    public synchronized int getSize() { return point.length; }
    
    public synchronized C getElement(int i) { return point[i]; }
    public synchronized void setElement(int i, C point) {
        if (!((this.point[i] == null && point == null) || (this.point[i] != null && this.point[i].equals(point)))) {
            this.point[i] = point;
            en.setUnconverted(true);
            firePlottableChanged(true);
        }
    }
    public synchronized C[] getElement() { return point; }
    public synchronized void setElement(C[] point) {
        if (this.point != point)
            this.point = point;
        en.setUnconverted(true);
        firePlottableChanged(true);
    }

    public synchronized void setElement(int i, C initial, C dragStart, C dragFinish) {
        C newPoint;
        if (initial instanceof Point2D) {
            Point2D in = (Point2D) initial, s = (Point2D) dragStart, f = (Point2D) dragFinish;
            newPoint = (C) new Point2D.Double(in.getX()+f.getX()-s.getX(), in.getY()+f.getY()-s.getY());
        } else
            newPoint = dragFinish;
        setElement(i, newPoint);
    }

    //
    // DELEGATE PROPERTIES
    //

    public PointRenderer getPointRenderer() { 
        if (en.getRenderer() == null)
            en.setRenderer(new BasicPointRenderer());
        return en.getRenderer();
    }
    public void setPointRenderer(PointRenderer r) { en.setRenderer(r); }

    public IndexedGetter<PointRenderer> getIndexedPointRenderer() { return en.getIndexedRenderer(); }
    public void setIndexedPointRenderer(IndexedGetter<PointRenderer> renderer) { en.setIndexedRenderer(renderer); }

    public IndexedGetter<String> getTooltips() { return en.getTooltips(); }
    public void setTooltips(IndexedGetter<String> tooltips) { en.setTooltips(tooltips); }

    public IndexedGetter<String> getLabels() { return null; }
    public void setLabels(IndexedGetter<String> labels) { }
}
