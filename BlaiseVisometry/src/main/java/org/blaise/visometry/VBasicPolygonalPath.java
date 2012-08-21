/**
 * VBasicPolygonalPath.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import java.awt.geom.GeneralPath;
import org.blaise.graphics.BasicShapeGraphic;
import org.blaise.style.BasicPathStyle;

/**
 * An entry for a polygonal path, drawn using a stroke renderer.
 *
 * @author Elisha
 */
public class VBasicPolygonalPath<C> extends VGraphicSupport<C> {

    /** Contains the local points */
    private C[] local;
    /** The window entries */
    private BasicShapeGraphic window = new BasicShapeGraphic(null, null);

    /** Default renderer used to draw the points (may be null) */
    private BasicPathStyle rend;

    /** Construct with specified bean to handle dragging (may be null) */
    public VBasicPolygonalPath(C[] local) {
        this.local = local;
    }

    public synchronized C getPoint(int i) {
        return local[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!((this.local == null && point == null) || (this.local != null && this.local.equals(point)))) {
            this.local[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return local;
    }

    public synchronized void setPoint(C[] point) {
        this.local = point;
        setUnconverted(true);
    }

    public BasicShapeGraphic getWindowEntry() {
        return window;
    }

    public BasicPathStyle getStyle() {
        return rend;
    }

    public void setStyle(BasicPathStyle rend) {
        if (this.rend != rend) {
            this.rend = rend;
            window.setStyle(rend);
        }
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        GeneralPath gp;
//        synchronized(local) {
            gp = processor.convertToPath(local, vis);
//        }
        if (window == null) {
            if (rend == null)
                window = new BasicShapeGraphic(gp, true);
            else
                window = new BasicShapeGraphic(gp, rend);
        } else
            window.setPrimitive(gp);
        window.setTooltip("Polygonal path");
        setUnconverted(false);
    }
}
