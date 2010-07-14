/*
 * GraphTableModel.java
 * Created May 15, 2010
 */

package graphexplorer;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * Provides a model for displaying a table of vertices within a graph. Displays the index of a vertex, its label
 * (if a labeled graph), its screen location, the degree, and (optionally) its value for a particular metric.
 *
 * @see Graph
 *
 * @author Elisha Peterson
 */
public class GraphTableModel extends AbstractTableModel {

    Graph graph = null;
    NodeMetric metric = null;
    boolean valueGraph = false;
    
    /** Tracks the values for the metric for each vertex */
    transient List<Number> values;

    /** Constructs without a graph */
    public GraphTableModel() {
    }

    /** Constructs with specified graph. */
    public GraphTableModel(Graph graph) {
        setGraph(graph);
    }

    /** Constructs with specified graph and metric. */
    public GraphTableModel(Graph graph, NodeMetric metric) {
        initialize(graph, metric);
    }

    public void initialize(Graph graph, NodeMetric metric) {
        this.graph = graph;
        this.metric = metric;
        values = (metric == null || graph == null) ? null : metric.getAllValues(graph);
        fireTableDataChanged();
    }
    
    /** @return graph */
    public Graph getGraph() { return graph; }
    
    /** Sets graph */
    public void setGraph(Graph graph) { 
        if (this.graph != graph) {
            this.graph = graph;
            valueGraph = graph instanceof ValuedGraph;
            values = (metric == null || graph == null) ? null : metric.getAllValues(graph);
            fireTableStructureChanged();
        }
    }

    /** @return metric */
    public NodeMetric getMetric() {
        return metric;
    }

    /** Sets metric */
    public void setMetric(NodeMetric metric) {
        if (this.metric != metric) {
            this.metric = metric;
            values = (metric == null || graph == null) ? null : metric.getAllValues(graph);
            fireTableDataChanged();
        }
    }

    //
    // TableModel METHODS
    //

    public int getRowCount() { return graph == null ? 0 : graph.order(); }

    // separate setups for labeled and unlabeled graphs

    private static final int            UCOL_OBJECT = 0, UCOL_LOC = 1, UCOL_DEGREE = 2, UCOL_METRIC = 3;
    String[] UCOL_NAMES =             { "Node",          "Location",   "Degree",        "Metric" };
    Class[] UCOL_TYPES =              { Object.class,    String.class, Integer.class,   Number.class   };

    private static final int           COL_OBJECT = 0,  COL_LABEL = 1, COL_LOC = 2,  COL_DEGREE = 3, COL_METRIC = 4;
    String[] COL_NAMES =             { "Node",          "Label",       "Location",   "Degree",       "Metric" };
    Class[] COL_TYPES =              { Object.class,    String.class,  String.class, Integer.class,  Number.class };

    public int getColumnCount() { 
        int result = 5; 
        if (!valueGraph) result--;
        if (metric==null) result--;
        return result;
    }

    @Override public String getColumnName(int col) {
        return valueGraph ? COL_NAMES[col] : UCOL_NAMES[col];
    }

    @Override public Class<?> getColumnClass(int col) {
        return valueGraph ? COL_TYPES[col] : UCOL_TYPES[col];
    }

    public Object getValueAt(int row, int col) {
        Object node = graph.nodes().get(row);
        if (valueGraph)
            switch (col) {
                case COL_OBJECT : return node;
                case COL_LABEL : 
                    Object value = ((ValuedGraph)graph).getValue(node);
                    return value == null ? "" : value.toString();
                case COL_LOC: return "TBD";
                case COL_DEGREE : return graph.degree(node);
                case COL_METRIC : return values.get(row);
            }
        else
            switch (col) {
                case UCOL_OBJECT : return node;
                case UCOL_LOC: return "TBD";
                case UCOL_DEGREE : return graph.degree(node);
                case UCOL_METRIC : return values.get(row);
            }
        throw new IllegalArgumentException("Graph's table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

    @Override public boolean isCellEditable(int row, int col) {
        return valueGraph && col == COL_LABEL;
    }
    
    @Override
    public void setValueAt(Object aValue, int row, int col) {
        if (!isCellEditable(row, col))
            throw new IllegalArgumentException("Cannot set value of graph " + graph + " (row, col) = (" + row + ", " + col + ") to " + aValue);

        ((ValuedGraph)graph).setValue(graph.nodes().get(row), aValue);
        fireTableCellUpdated(row, col);
    }

}
