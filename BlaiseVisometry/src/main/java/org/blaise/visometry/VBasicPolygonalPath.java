/**
 * VBasicPolygonalPath.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import com.google.common.base.Objects;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import org.blaise.graphics.BasicShapeGraphic;
import org.blaise.style.PathStyle;

/**
 * An entry for a polygonal path, drawn using a stroke renderer.
 *
 * @author Elisha
 */
public class VBasicPolygonalPath<C> extends VGraphicSupport<C> {

    /** Contains the local points */
    private C[] pathCoordinates;
    /** The drawn path */
    private final BasicShapeGraphic window = new BasicShapeGraphic(null);

    /** Construct with specified bean to handle dragging (may be null) */
    public VBasicPolygonalPath(C[] local) {
        this.pathCoordinates = local.clone();
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public BasicShapeGraphic getWindowGraphic() {
        return window;
    }

    public synchronized C getPoint(int i) {
        return pathCoordinates[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!(Objects.equal(point, pathCoordinates[i]))) {
            this.pathCoordinates[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return pathCoordinates.clone();
    }

    public synchronized void setPoint(C[] point) {
        this.pathCoordinates = point.clone();
        setUnconverted(true);
    }

    public PathStyle getStyle() {
        return (PathStyle) window.getStyle();
    }

    public void setStyle(PathStyle rend) {
        window.setStyle(rend);
    }
    
    //</editor-fold>
    

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        GeneralPath gp = processor.convertToPath(Arrays.asList(pathCoordinates), vis);
        window.setPrimitive(gp);
        window.setDefaultTooltip("Polygonal path");
        setUnconverted(false);
    }
}
