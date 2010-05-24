/*
 * DistributionTableModel.java
 * Created May 18, 2010
 */

package graphexplorer;

import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph2;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * Provides a basic table for metric distributions
 * @author elisha
 */
public class MetricTableModel extends AbstractTableModel {

    Graph2 graph = null;

    /** Constructs without a graph */
    public MetricTableModel() {}
    /** Constructs with specified graph. */
    public MetricTableModel(Graph2 graph) { this.graph = graph; }

    /** @return graph */
    public Graph2 getGraph() { return graph; }
    /** Sets graph */
    public void setGraph(Graph2 graph) { this.graph = graph; fireTableDataChanged(); }

    private static final int COL_INDEX = 0, COL_LABEL = 1, COL_DEGREE = 2, COL_DEGREE2 = 3, COL_CLIQUECOUNT = 4;
    String[] COL_NAMES = { "Index", "Label", "Degree", "Degree-2", "Clique Count" };
    Class[] COL_TYPES = { Integer.class, String.class, Integer.class, Integer.class, Integer.class };

    public int getRowCount() { return graph == null ? 0 : graph.size(); }
    public int getColumnCount() { return COL_NAMES.length; }
    @Override public String getColumnName(int col) { return COL_NAMES[col]; }
    @Override public Class<?> getColumnClass(int col) { return COL_TYPES[col]; }

    public Object getValueAt(int row, int col) { 
        switch(col) {
            case COL_INDEX: return row;
            case COL_LABEL: return graph.getLabel(row);
            case COL_DEGREE: return GraphMetrics.DEGREE.getValue(graph, row);
            case COL_DEGREE2: return GraphMetrics.DEGREE2.getValue(graph, row);
            case COL_CLIQUECOUNT: return GraphMetrics.CLIQUE_COUNT.getValue(graph, row);
        }
        throw new IllegalArgumentException("Metric table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

}
