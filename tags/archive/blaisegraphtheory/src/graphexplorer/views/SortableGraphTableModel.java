/*
 * SortableGraphTableModel.java
 * Created Aug 11, 2010
 */

package graphexplorer.views;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Provides sortable version of the graph table.
 * @author Elisha Peterson
 */
public class SortableGraphTableModel extends AbstractTableModel 
        implements TableModelListener {
    
    /** Base class */
    GraphTableModel gtm;
    /** This does the sorting */
    RowComparator comparator;
    /** The sort column */
    int sortColumn;
    /** Whether to sort ascending */
    boolean sortAsc = true;
    /** Stores the sorted indices: values are rows in original table, indices are rows in sorted version */
    ArrayList<Integer> sortRows;

    public SortableGraphTableModel() {
        this(new GraphTableModel(), GraphTableModel.COL_DEGREE);
    }

    public SortableGraphTableModel(GraphTableModel gtm, int col) {
        setBaseModel(gtm);
        sortColumn = col;
        comparator = new RowComparator();
    }

    /** @return list of actions which can be used to sort the table */
    public Action[] getSortActions() {
        return new Action[] {
            new SortAction(-1, true),
            new SortAction(GraphTableModel.COL_NODE, true),
            new SortAction(GraphTableModel.COL_LABEL, true),
            new SortAction(GraphTableModel.COL_DEGREE, false),
            new SortAction(GraphTableModel.COL_METRIC, false)
        };
    }

    public GraphTableModel getBaseModel() {
        return gtm;
    }
    
    public void setBaseModel(GraphTableModel gtm) {
        if (this.gtm != gtm) {
            if (this.gtm != null)
                this.gtm.removeTableModelListener(this);
            this.gtm = gtm;
            sortRows = null;
            gtm.addTableModelListener(this);
        }
    }

    /** @return current sort column */
    public int getSortColumn() {
        return sortColumn;
    }

    /** Sets current sort column */
    public void setSortColumn(int col) {
        sortColumn = col;
        resort();
    }

    public void tableChanged(TableModelEvent e) {
        if (e.getSource() == gtm) {
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
                fireTableStructureChanged();
            else {
                resort();
                fireTableDataChanged();
            }
        }
    }

    transient boolean sorting = false;

    /** Sorts the table and fires a change event */
    private void resort() {
        sorting = true;
        sortRows = new ArrayList<Integer>();
        for (int i = 0; i < getRowCount(); i++)
            sortRows.add(i);
        if (sortAsc)
            Collections.sort(sortRows, comparator);
        else
            Collections.sort(sortRows, Collections.reverseOrder(comparator));
        sorting = false;
        fireTableDataChanged();
    }

    @Override public String getColumnName(int col) { return gtm.getColumnName(col); }
    @Override public Class<?> getColumnClass(int col) { return gtm.getColumnClass(col); }
    public int getRowCount() { return gtm.getRowCount(); }
    public int getColumnCount() { return gtm.getColumnCount(); }

    public Object getValueAt(int row, int col) {
        if (sorting)
            return gtm.getValueAt(row, col);
        else {
            if (sortRows == null)
                resort();
            return gtm.getValueAt(sortRows.get(row), col);
        }
    }

    //
    // INNER CLASSES
    //

    /** A comparator for rows of the table */
    private class RowComparator implements Comparator<Integer> {
        public int compare(Integer row1, Integer row2) {
            if (sortColumn == -1)
                return row1-row2;
            else if (row1 == row2)
                return 0;
            Object val1 = gtm.getValueAt(row1, sortColumn);
            Object val2 = gtm.getValueAt(row2, sortColumn);
            if (val1 == null || val2 == null)
                return row1-row2;
            else if (!(val1 instanceof Comparable)) {
                val1 = val1.toString();
                val2 = val2.toString();
            }
            int result = ((Comparable)val1).compareTo(val2);
            if (result == 0)
                result = row1 - row2;
            return result;
        }
    }

    /** Sort actions */
    private class SortAction extends AbstractAction {
        int col;
        boolean asc;
        SortAction(int col, boolean asc) {
            super((col == -1 ? "No sorting" : ("Sort by " + gtm.colNames[col]))
                    + (asc ? "" : " (descending)"));
            this.col = col;
            this.asc = asc;
        }
        public void actionPerformed(ActionEvent e) {
            sortAsc = asc;
            setSortColumn(col);
        }
    }
}
