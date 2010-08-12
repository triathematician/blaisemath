/*
 * DistributionTableModel.java
 * Created May 18, 2010
 */

package graphexplorer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * Provides a basic table for metric distributions
 * @author elisha
 */
public class DistributionTableModel extends AbstractTableModel {

    /** Stores the values and counts in the table */
    Map<Object, Integer> distrib;

    /** Stores the values in the table. */
    Object[] values;
    /** Stores the number of each value in the table. */
    int[] counts;

    public <N> DistributionTableModel(List<N> values) {
        Map<N, Integer> map = GraphMetrics.distribution(values);
        this.values = new Object[map.size()];
        counts = new int[map.size()];
        int i = 0;
        for (Entry<N, Integer> en : map.entrySet()) {
            this.values[i] = en.getKey();
            counts[i] = en.getValue();
            i++;
        }
    }

    String[] LABELS = { "Value", "Count" };
    Class[] TYPES = { Object.class, Integer.class };

    public int getRowCount() { return values == null ? 0 : values.length; }
    public int getColumnCount() { return LABELS.length; }
    @Override public String getColumnName(int col) { return LABELS[col]; }
    @Override public Class<?> getColumnClass(int col) { return TYPES[col]; }

    public Object getValueAt(int row, int col) { return values == null ? null : col == 0 ? values[row] : counts[row]; }

}
