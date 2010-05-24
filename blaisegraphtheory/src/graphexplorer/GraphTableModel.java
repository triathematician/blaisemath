/*
 * GraphTableModel.java
 * Created May 15, 2010
 */

package graphexplorer;

import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph2;
import org.bm.blaise.scio.graph.NeighborSetInterface;
import org.bm.blaise.scio.graph.NeighborSetUtils;
import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * Provides a model for displaying the table of vertices within a graph.
 * Supports displaying the index of a vertex, its label, its "object", and the degree of the vertex.
 * Requires a graph in the form of a <code>NeighborSetInterface</code>.
 *
 * @see NeighborSetInterface
 *
 * @author Elisha Peterson
 */
public class GraphTableModel extends AbstractTableModel {

    NeighborSetInterface graph = null;

    /** Constructs without a graph */
    public GraphTableModel() {
    }

    /** Constructs with specified graph. */
    public GraphTableModel(NeighborSetInterface graph) {
        this.graph = graph;
    }
    
    /** @return graph */
    public NeighborSetInterface getGraph() { return graph; }
    /** Sets graph */
    public void setGraph(NeighborSetInterface graph) { this.graph = graph; fireTableDataChanged(); }

    //
    // TableModel METHODS
    //

    public int getRowCount() { return graph == null ? 0 : graph.size(); }

    private static final int      COL_INDEX = 0,    COL_LABEL = 1,  COL_OBJECT = 2, COL_DEGREE = 3;
    String[] COL_NAMES =        { "Index",          "Label",        "Object",       "# Connections" };
    Class[] COL_TYPES =         { Integer.class,    String.class,   Object.class,   Integer.class   };
    boolean[] COL_EDITABLE =    { false,            true,           false,          false           };

    public int getColumnCount() { return COL_NAMES.length; }
    @Override public String getColumnName(int col) { return COL_NAMES[col]; }
    @Override public Class<?> getColumnClass(int col) { return COL_TYPES[col]; }
    @Override public boolean isCellEditable(int row, int col) { return COL_EDITABLE[col] && (graph instanceof SimpleGraph); }

    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0 : return row;
            case 1 : return graph.getLabel(row);
            case 2 : return graph.getObject(row);
            case 3 : return graph instanceof Graph2 ? GraphMetrics.DEGREE.getValue((Graph2) graph, row) : NeighborSetUtils.degree(graph, row);
        }
        throw new IllegalArgumentException("Graph's table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (graph instanceof SimpleGraph && col == COL_LABEL) {
            SimpleGraph sg = (SimpleGraph) graph;
            sg.setLabel(row, (String) aValue);
            fireTableCellUpdated(row, col);
            return;
        }
        throw new IllegalArgumentException("Cannot set value of graph " + graph + " (row, col) = (" + row + ", " + col + ") to " + aValue);
    }




}
