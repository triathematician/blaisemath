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
    /** Default edge style */
    private PathStyle defaultStyle = new BasicPathStyle(new Color(0, 128, 0, 128), .5f);
    
    /** Weighted graph edge style delegate */
    private DefaultWeightedEdgeStyle wgc;
    
    /** Edge style delegate with default option and override for weighted graphs */
    private final Delegator<Object[], PathStyle> edgeStyle = new Delegator<Object[], PathStyle>() {
        public PathStyle of(Object[] src) { 
            return wg != null && wgc != null ? wgc.of(src)
                    : styles != null ? styles.of(src)
                    : defaultStyle;
        }
    };
    
    /** Default constructor */
    public GraphEdgeStyler() {
    }

    public void setGraph(Graph graph) {
        WeightedGraph newGraph = graph instanceof WeightedGraph ? (WeightedGraph) graph : null;
        if (newGraph != wg) {
            wg = newGraph;
            wgc = wg == null ? null : new DefaultWeightedEdgeStyle(defaultStyle, wg);
            pcs.firePropertyChange("styleDelegate", null, edgeStyle);
        }
    }

    public PathStyle getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(PathStyle defaultStyle) {
        if (this.defaultStyle != defaultStyle) {
            this.defaultStyle = defaultStyle;
            pcs.firePropertyChange("defaultStyle", null, defaultStyle);
        }
    }

    @Override
    public Delegator<Object[], PathStyle> getStyleDelegate() {
        return edgeStyle;
    }
    
}
