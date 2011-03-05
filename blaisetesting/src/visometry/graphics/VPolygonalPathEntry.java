/**
 * VPolygonalPathEntry.java
 * Created Jan 29, 2011
 */
package visometry.graphics;

import org.bm.blaise.graphics.BasicShapeEntry;
import org.bm.blaise.graphics.renderer.BasicStrokeRenderer;
import java.awt.geom.GeneralPath;
import utils.IndexedGetter;
import visometry.Visometry;
import visometry.VisometryProcessor;

/**
 * An entry for a polygonal path, drawn using a stroke renderer.
 * @author Elisha
 */
public class VPolygonalPathEntry<C> extends AbstractVGraphicEntry<C> {

    /** Contains the local points */
    private final IndexedGetter<C> local;
    /** The window entries */
    private BasicShapeEntry window;

    /** Default renderer used to draw the points (may be null) */
    private BasicStrokeRenderer rend;

    /** Construct with specified bean to handle dragging (may be null) */
    public VPolygonalPathEntry(IndexedGetter<C> local) {
        this.local = local;
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        GeneralPath gp;
//        synchronized(local) {
            gp = processor.convertToPath(local, vis);
//        }
        if (window == null) {
            if (rend == null)
                window = new BasicShapeEntry(gp, true);
            else
                window = new BasicShapeEntry(gp, rend);
        } else
            window.setPrimitive(gp);
        window.setTooltip("Polygonal path");
        setUnconverted(false);
    }

    public BasicShapeEntry getWindowEntry() {
        return window;
    }

    public BasicStrokeRenderer getRenderer() {
        return rend;
    }

    public void setRenderer(BasicStrokeRenderer rend) {
        if (this.rend != rend) {
            this.rend = rend;
            setUnconverted(true);
        }
    }
}
