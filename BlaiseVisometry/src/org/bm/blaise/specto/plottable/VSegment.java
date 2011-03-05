/**
 * VSegment.java
 * Created Jan 29, 2011
 */
package visometry.plottable;

import org.bm.blaise.graphics.renderer.PointRenderer;
import org.bm.blaise.graphics.renderer.ShapeRenderer;
import utils.RelativePointBean;
import visometry.graphics.VCompositeGraphicEntry;
import visometry.graphics.VGraphicEntry;
import visometry.graphics.VSegmentEntry;

/**
 * Draws a pair of (draggable) points and the edge between them
 * @param<C> base coordinate system
 * @author Elisha
 */
public class VSegment<C> extends VPointSet<C> {

    VCompositeGraphicEntry comp;
    VSegmentEntry eEntry;

    private static <C> C[] toArr(C... pts) { return pts; }

    public VSegment(C point1, C point2) { super(toArr(point1, point2)); }
    public VSegment(C point1, C point2, PointRenderer rend) { super(toArr(point1, point2), rend); }

    @Override
    public VGraphicEntry getGraphicEntry() {
        if (comp == null) {
            comp = new VCompositeGraphicEntry();
            if (eEntry == null) {
                // edge entry is dynamic and updates automatically based on the point positions
                eEntry = new VSegmentEntry(
                        new RelativePointBean<C>() { public C getPoint() { return point[0]; }
                            public void setPoint(C point) {}
                            public void setPoint(C initial, C dragStart, C dragFinish) {} },
                        new RelativePointBean<C>() { public C getPoint() { return point[1]; }
                            public void setPoint(C point) {}
                            public void setPoint(C initial, C dragStart, C dragFinish) {} }
                        );
            }
            comp.addEntry(eEntry);
            comp.addEntry(super.getGraphicEntry());
        }
        return comp;
    }

    //
    // DELEGATE PROPERTIES
    //

    public ShapeRenderer getEdgeRenderer() { return eEntry == null ? null : eEntry.getRenderer(); }
    public void setEdgeRenderer(ShapeRenderer r) { eEntry.setRenderer(r); }

}
