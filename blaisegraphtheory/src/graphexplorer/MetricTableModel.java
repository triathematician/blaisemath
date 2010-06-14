/*
 * DistributionTableModel.java
 * Created May 18, 2010
 */

package graphexplorer;

import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.NodeValueGraph;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * Provides a basic table for metric distributions
 * @author elisha
 */
public class MetricTableModel extends AbstractTableModel {

    Graph graph = null;

    /** Constructs without a graph */
    public MetricTableModel() {}
    /** Constructs with specified graph. */
    public MetricTableModel(Graph graph) { this.graph = graph; }

    /** @return graph */
    public Graph getGraph() { return graph; }
    /** Sets graph */
    public void setGraph(Graph graph) { if (this.graph != graph) { this.graph = graph; fireTableDataChanged(); } }

    private static final int COL_VERTEX = 0, COL_LABEL = 1, COL_DEGREE = 2, COL_DEGREE2 = 3, COL_CLIQUECOUNT = 4;
    String[] COL_NAMES = { "Vertex", "Label", "Degree", "Degree-2", "Clique Count" };
    Class[] COL_TYPES = { Object.class, String.class, Integer.class, Integer.class, Integer.class };

    public int getRowCount() { return graph == null ? 0 : graph.order(); }
    public int getColumnCount() { return COL_NAMES.length; }
    @Override public String getColumnName(int col) { return COL_NAMES[col]; }
    @Override public Class<?> getColumnClass(int col) { return COL_TYPES[col]; }

    public Object getValueAt(int row, int col) {
        Object node = graph.nodes().get(row);
        switch(col) {
            case COL_VERTEX: return node;
            case COL_LABEL: return graph instanceof NodeValueGraph ? ((NodeValueGraph)graph).getValue(node).toString() : "";
            case COL_DEGREE: return GraphMetrics.DEGREE.getValue(graph, node);
            case COL_DEGREE2: return GraphMetrics.DEGREE2.getValue(graph, node);
            case COL_CLIQUECOUNT: return GraphMetrics.CLIQUE_COUNT.getValue(graph, node);
        }
        throw new IllegalArgumentException("Metric table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

}
