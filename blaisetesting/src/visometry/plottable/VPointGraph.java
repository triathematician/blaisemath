/**
 * VPointGraph.java
 * Created Jan 29, 2011
 */
package visometry.plottable;

import org.bm.blaise.graphics.renderer.BasicStrokeRenderer;
import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import org.bm.blaise.graphics.renderer.PointRenderer;
import org.bm.blaise.graphics.renderer.ShapeRenderer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.RelativePointBean;
import utils.IndexedGetter;
import utils.MapGetter;
import visometry.graphics.VCompositeGraphicEntry;
import visometry.graphics.VGraphicEntry;
import visometry.graphics.VPolygonalPathEntry;
import visometry.graphics.VSegmentEntry;

/**
 * Draws both a set of points and a set of edges on a plot.
 * There is the capability for per-point and per-edge rendering.
 * @param<C> base coordinate system
 * @author Elisha
 */
public class VPointGraph<C> extends VPointSet<C> {

    /** Stores edge data in synchronized manner */
    private final EdgeHandler edgeHandler = new EdgeHandler();

    /** The entire entry */
    private final VCompositeGraphicEntry comp = new VCompositeGraphicEntry();
    /** The edge entry (if a composite) */
    private final VCompositeGraphicEntry eEntry = new VCompositeGraphicEntry();
    /** The edge render map */
    private MapGetter<ShapeRenderer> edgeRend;

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    //
    // CONSTRUCTORS
    //

    /** Construct with points as an array, edges as an array of point-indices */
    public VPointGraph(C[] points, int[][] edges) { 
        super(points);
        initEdges(edges);
    }

    /** Construct with points as an array, edges as an array of point-indices */
    public VPointGraph(C[] points, int[][] edges, PointRenderer rend) { 
        super(points, rend);
        initEdges(edges);
    }

    /** Construct with points as an array, edges as an array of point-indices */
    public VPointGraph(C[] points, int[][] edges, IndexedGetter<PointRenderer> custom) {
        super(points, custom);
        initEdges(edges);
    }

    private void initEdges(int[][] edges) {
        edgeHandler.edges = edges;
        eEntry.setRenderer(new GraphicRendererProvider.PathProvider(
                new BasicStrokeRenderer(new Color(0, 128, 0, 128), .5f) ));
    }
    // </editor-fold>

    //
    // PROPERTIES
    //

    public synchronized int[][] getEdges() {
        return edgeHandler.getEdges();
    }
    
    public synchronized void setEdges(int[][] edges) {
        edgeHandler.setEdges(edges);
        eEntry.setUnconverted(true);
        firePlottableChanged(true);
    }

    public synchronized void setGraph(C[] nodes, int[][] edges) {
//        System.out.println("::setGraph started");
        if (this.point != nodes)
            this.point = nodes;
//        System.out.println("::setGraph 1");
        edgeHandler.setEdges(edges);
//        System.out.println("::setGraph 2");
        if (comp != null)
            comp.setUnconverted(true);
//        System.out.println("::setGraph 3");
        firePlottableChanged(true);
//        System.out.println(":: ... setGraph finished");
    }

    @Override
    public VGraphicEntry getGraphicEntry() { return comp; }

    @Override
    public void recompute() {
        if (edgeRend == null) {
            eEntry.replaceEntries(Arrays.asList(new VPolygonalPathEntry(edgeHandler)));
        } else {
            List<VSegmentEntry<C>> nue = new ArrayList<VSegmentEntry<C>>();
            for (int[] e : edgeHandler.getEdges())
                nue.add(new VSegmentEntry<C>(createGetter(e[0]), createGetter(e[1]), edgeRend.getElement(e)));
            eEntry.replaceEntries(nue);
        }
        comp.addAllEntries(Arrays.asList(eEntry, super.getGraphicEntry()));
        needsComputation = false;
    }

    /** Manages access to the edges, preventing synchronization errors */
    private class EdgeHandler implements IndexedGetter<C> {
        private int[][] edges;
        public synchronized int[][] getEdges() { return edges; }
        public synchronized void setEdges(int[][] edges) { this.edges = edges; }
        public synchronized int getSize() { return 3 * edges.length; }
        public synchronized C getElement(int i) { return i%3 == 2 ? null : point[edges[i/3][i%3]]; }
    }

    // <editor-fold defaultstate="collapsed" desc="Delegate Properties">
    //
    // DELEGATE PROPERTIES
    //

    public BasicStrokeRenderer getEdgeRenderer() {
        return (BasicStrokeRenderer) eEntry.getRenderer().getPathRenderer();
    }
    public void setEdgeRenderer(BasicStrokeRenderer r) {
        eEntry.setRenderer(new GraphicRendererProvider.PathProvider(r));
    }

    public MapGetter<ShapeRenderer> getEdgeCustomizer() { return edgeRend; }
    public void setEdgeCustomizer(MapGetter<ShapeRenderer> r) { 
        if (this.edgeRend != r) {
            this.edgeRend = r;
            firePlottableChanged(true);
        }
    }
    // </editor-fold>

    //
    // UTILITIES
    //

    private RelativePointBean<C> createGetter(final int index) {
        return new RelativePointBean<C>() {
            public C getPoint() { return point[index]; }
            public void setPoint(C point) {}
            public void setPoint(C initial, C dragStart, C dragFinish) {}
        };
    }

}
