/*
 * FrequencyTableModel.java
 * Created May 18, 2010
 */

package util.stats.views;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.table.AbstractTableModel;
import util.stats.MapStatUtils;

/**
 * <p>
 * Transforms a list of values into a frequency table.
 * </p>
 * 
 * @author elisha
 */
public class FrequencyTableModel extends AbstractTableModel {

    /** Stores the values in the table. */
    Object[] values;
    /** Stores the number of each value in the table. */
    int[] counts;

    /** Construct the distribution table given a list of values. */
    public <N> FrequencyTableModel(List<N> values) {
        Map<N, Integer> map = MapStatUtils.distribution(values);
        this.values = new Object[map.size()];
        counts = new int[map.size()];
        int i = 0;
        for (Entry<N, Integer> en : map.entrySet()) {
            this.values[i] = en.getKey();
            counts[i] = en.getValue();
            i++;
        }
    }

    /** @return the i'th value */
    public Object getValue(int i) {
        return values[i];
    }

    /** @return the distribution of counts */
    public int[] getCounts() {
        return counts;
    }

    String[] LABELS = { "Value", "Count" };
    Class[] TYPES = { Object.class, Integer.class };

    public int getRowCount() { return values == null ? 0 : values.length; }
    public int getColumnCount() { return LABELS.length; }
    @Override public String getColumnName(int col) { return LABELS[col]; }
    @Override public Class<?> getColumnClass(int col) { return TYPES[col]; }

    public Object getValueAt(int row, int col) { return values == null ? null : col == 0 ? values[row] : counts[row]; }

}
