/*
 * DistributionTableModel.java
 * Created May 18, 2010
 */

package graphexplorer;

import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Provides a basic table for metric distributions
 * @author elisha
 */
public class DistributionTableModel extends AbstractTableModel {

    Graph graph;
    NodeMetric metric;
    /** Stores the values in the table. */
    Object[] values;
    /** Stores the number of each value in the table. */
    int[] counts;

    public <N> DistributionTableModel(Graph graph, NodeMetric<N> metric) {
        this.graph = graph;
        this.metric = metric;
        Map<N, Integer> map = GraphMetrics.computeDistribution(graph, metric);
        values = new Object[map.size()];
        counts = new int[map.size()];
        int i = 0;
        for (Entry<N, Integer> en : map.entrySet()) {
            values[i] = en.getKey();
            counts[i] = en.getValue();
            i++;
        }
    }

    String[] LABELS = { "Value", "Count" };
    Class[] TYPES = { Object.class, Integer.class };

    public int getRowCount() { return values.length; }
    public int getColumnCount() { return LABELS.length; }
    @Override public String getColumnName(int col) { return LABELS[col]; }
    @Override public Class<?> getColumnClass(int col) { return TYPES[col]; }

    public Object getValueAt(int row, int col) { return col == 0 ? values[row] : counts[row]; }

}
