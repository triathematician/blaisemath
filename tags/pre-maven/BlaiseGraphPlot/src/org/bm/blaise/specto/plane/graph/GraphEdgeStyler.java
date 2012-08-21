/*
 * GraphEdgeStyler.java
 * Created Oct 12, 2011
 */
package org.bm.blaise.specto.plane.graph;

import java.awt.Color;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.style.BasicPathStyle;
import org.bm.blaise.style.ObjectStyler;
import org.bm.blaise.style.PathStyle;
import org.bm.util.Delegator;

/**
 * Provides some basic styling for graph edges, e.g. weighted graphs, beyond that
 * of a standard {@link ObjectStyler}
 *
 * @author elisha
 */
public class GraphEdgeStyler extends ObjectStyler<Object[], PathStyle> {

    /** The graph (if weighted)... null if not */
    private WeightedGraph wg;

    /** Weighted graph edge style delegate */
    private DefaultWeightedEdgeStyle wgc;
    /** Default edge style */
    private PathStyle DEFAULT_EDGE_STYLE = new BasicPathStyle(new Color(0, 128, 0, 128), .5f);
    /** Edge style delegate with default option and override for weighted graphs */
    private final Delegator<Object[], PathStyle> DEFAULT_EDGE_STYLER = new Delegator<Object[], PathStyle>() {
        public PathStyle of(Object[] src) {
            return wg != null && wgc != null ? wgc.of(src)
                    : styles != null ? styles.of(src)
                    : DEFAULT_EDGE_STYLE;
        }
    };

    /** Default constructor */
    public GraphEdgeStyler() {
    }

    public void setGraph(Graph graph) {
        WeightedGraph newGraph = graph instanceof WeightedGraph ? (WeightedGraph) graph : null;
        if (newGraph != wg) {
            wg = newGraph;
            wgc = wg == null ? null : new DefaultWeightedEdgeStyle(DEFAULT_EDGE_STYLE, wg);
            pcs.firePropertyChange("styleDelegate", null, DEFAULT_EDGE_STYLER);
        }
    }

    public PathStyle getDefaultStyle() {
        return DEFAULT_EDGE_STYLE;
    }

    public void setDefaultStyle(PathStyle defaultStyle) {
        if (this.DEFAULT_EDGE_STYLE != defaultStyle) {
            this.DEFAULT_EDGE_STYLE = defaultStyle;
            pcs.firePropertyChange("defaultStyle", null, defaultStyle);
        }
    }

    @Override
    public Delegator<Object[], PathStyle> getStyleDelegate() {
        return super.getStyleDelegate() == null ? DEFAULT_EDGE_STYLER : super.getStyleDelegate();
    }

}
