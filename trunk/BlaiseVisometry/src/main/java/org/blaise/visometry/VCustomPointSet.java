/*
 * VCustomPointSet.java
 * Created Oct 5, 2011
 */
package org.blaise.visometry;

import java.awt.geom.Point2D;
import java.util.Set;
import org.blaise.graphics.CustomPointSetGraphic;
import org.blaise.graphics.IndexedGraphicHighlighter;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.util.DraggableIndexedPointBean;

/**
 * A set of draggable points defined in local coordinates. Properties of the objects
 * (including style, tooltips, locations, etc.) are managed by delegates.
 *
 * @param <C> the local coordinate
 * @param <Src> the type of object being displayed
 *
 * @author elisha
 */
public class VCustomPointSet<C, Src> extends VGraphicSupport<C> implements DraggableIndexedPointBean<C> {

    /** The points */
    protected C[] point;
    /** The window entry */
    protected CustomPointSetGraphic<Src> window = new CustomPointSetGraphic<Src>();

    /**
     * Construct point set with specified objects.
     * @param pointer object used for point locations
     */
    public VCustomPointSet(C[] initialPoint) {
        this.point = initialPoint;
        window.removeMouseListeners();
        window.removeMouseMotionListeners();
        window.addMouseListener(new VGMouseIndexedDragger<C>(this));
        window.addMouseListener(new IndexedGraphicHighlighter());
    }

    //
    // DraggablePointBean PROPERTIES
    //

    public synchronized C getPoint(int i) {
        return point[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!((this.point == null && point == null) || (this.point != null && this.point.equals(point)))) {
            this.point[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return point;
    }

    public synchronized void setPoint(C[] point) {
        this.point = point;
        setUnconverted(true);
    }

    public int getPointCount() {
        return point.length;
    }

    public synchronized int indexOf(Point2D pt, C dragStart) {
        return window.indexOf(pt, null);
    }

    public void setPoint(int index, C initial, C dragStart, C dragFinish) {
        setPoint(index, VBasicPoint.relativePoint(initial, dragStart, dragFinish));
    }

    //
    // PROPERTIES
    //

    public CustomPointSetGraphic<Src> getWindowEntry() {
        return window;
    }

    public Set<? extends Src> getObjects() {
        return window.getObjects();
    }

    public synchronized void setObjects(Set<? extends Src> objects) {
        window.setObjects(objects);
        setUnconverted(true);
    }

    public synchronized void setStyler(ObjectStyler<Src, PointStyle> styler) {
        window.setStyler(styler);
    }

    public ObjectStyler<Src, PointStyle> getStyler() {
        return window.getStyler();
    }

    //
    // CONVERSION
    //

    public synchronized void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        Point2D[] p = processor.convertToArray(point, vis);
        window.getPointManager().setLocationArray(p);
        setUnconverted(false);
    }

}
