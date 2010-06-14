/*
 * GraphTableModel.java
 * Created May 15, 2010
 */

package graphexplorer;

import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.NodeValueGraph;
import org.bm.blaise.scio.graph.NodeValueGraphWrapper;

/**
 * Provides a model for displaying the table of vertices within a graph.
 * Supports displaying the index of a vertex, its label, its "object", and the degree of the vertex.
 * Requires a graph in the form of a <code>NeighborSetInterface</code>.
 *
 * @see Graph
 *
 * @author Elisha Peterson
 */
public class GraphTableModel extends AbstractTableModel {

    Graph graph = null;
    boolean valueGraph = false;

    /** Constructs without a graph */
    public GraphTableModel() {
    }

    /** Constructs with specified graph. */
    public GraphTableModel(Graph graph) {
        setGraph(graph);
    }
    
    /** @return graph */
    public Graph getGraph() { return graph; }
    /** Sets graph */
    public void setGraph(Graph graph) { 
        if (this.graph != graph) {
            this.graph = graph;
            valueGraph = graph instanceof NodeValueGraph;
            fireTableStructureChanged();
        }
    }

    //
    // TableModel METHODS
    //

    public int getRowCount() { return graph == null ? 0 : graph.order(); }

    private static final int            UCOL_OBJECT = 0, UCOL_DEGREE = 1;
    String[] UCOL_NAMES =             { "Object",        "# Connections" };
    Class[] UCOL_TYPES =              { Object.class,    Integer.class   };
    boolean[] UCOL_EDITABLE =         { false,           false           };

    private static final int           COL_OBJECT = 0,  COL_LABEL = 1, COL_DEGREE = 2;
    String[] COL_NAMES =             { "Object",        "Label",       "# Connections" };
    Class[] COL_TYPES =              { Object.class,    String.class,  Integer.class   };
    boolean[] COL_EDITABLE =         { false,           true,          false           };

    public int getColumnCount() { return valueGraph ? COL_NAMES.length : UCOL_NAMES.length; }
    @Override public String getColumnName(int col) { return valueGraph ? COL_NAMES[col] : UCOL_NAMES[col]; }
    @Override public Class<?> getColumnClass(int col) { return valueGraph ? COL_TYPES[col] : UCOL_TYPES[col]; }
    @Override public boolean isCellEditable(int row, int col) { return (valueGraph ? COL_EDITABLE[col] : UCOL_EDITABLE[col]) && (graph instanceof NodeValueGraphWrapper); }

    public Object getValueAt(int row, int col) {
        Object node = graph.nodes().get(row);
        if (valueGraph)
            switch (col) {
                case COL_OBJECT : return node;
                case COL_LABEL : return graph instanceof NodeValueGraph ? ((NodeValueGraph)graph).getValue(node).toString() : "";
                case COL_DEGREE : return graph.degree(node);
            }
        else
            switch (col) {
                case UCOL_OBJECT : return node;
                case UCOL_DEGREE : return graph.degree(node);
            }
        throw new IllegalArgumentException("Graph's table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (graph instanceof NodeValueGraphWrapper && col == COL_LABEL) {
            NodeValueGraphWrapper nvg = (NodeValueGraphWrapper) graph;
            try {
                nvg.setValue(graph.nodes().get(row), aValue);
                fireTableCellUpdated(row, col);
            } catch (Exception ex) {}
            return;
        }
        throw new IllegalArgumentException("Cannot set value of graph " + graph + " (row, col) = (" + row + ", " + col + ") to " + aValue);
    }




}
