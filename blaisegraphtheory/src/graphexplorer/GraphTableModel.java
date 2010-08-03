/*
 * GraphTableModel.java
 * Created May 15, 2010
 */

package graphexplorer;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * Provides a model for displaying a table of vertices within a graph. Displays the index of a vertex, its label
 * (if a labeled graph), its screen location, the degree, and (optionally) its value for a particular metric.
 *
 * @see Graph
 *
 * @author Elisha Peterson
 */
public final class GraphTableModel extends AbstractTableModel
        implements PropertyChangeListener {

    /** The graph being tracked */
    GraphController gc = null;
    /** Tracks the values for the metric for each vertex */
    transient List<Number> values;

    /** Constructs without a graph */
    public GraphTableModel() {
    }

    /** Constructs with specified graph. */
    public GraphTableModel(GraphController gc) {
        setController(gc);
    }

    public void setController(GraphController gc) {
        if (this.gc != gc) {
            if (this.gc != null)
                this.gc.removePropertyChangeListener(this);
            this.gc = gc;
            if (gc != null)
                gc.addPropertyChangeListener(this);
            updateValues();
        }
    }

    /** Updates table values based on current GraphController */
    private void updateValues() {
        if (gc == null)
            fireTableStructureChanged();
        else {
            boolean startNull = values == null;
            values = gc.getMetricValues();
            if (startNull != (values == null))
                fireTableStructureChanged();
            else
                fireTableDataChanged();
        }
    }

    //
    // TableModel METHODS
    //

    public int getRowCount() {
        if (gc == null) return 0;
        Graph graph = gc.getActiveGraph();
        return graph == null ? 0 : graph.order();
    }

    private static final int           COL_OBJECT = 0,  COL_LABEL = 1, COL_LOC = 2,  COL_DEGREE = 3, COL_METRIC = 4;
    String[] COL_NAMES =             { "Node",          "Label",       "Location",   "Degree",       "Metric" };
    Class[] COL_TYPES =              { Object.class,    String.class,  String.class, Integer.class,  Number.class };

    public int getColumnCount() {
        return (gc == null || gc.getMetric() == null) ? 4 : 5;
    }

    @Override public String getColumnName(int col) {
        return COL_NAMES[col];
    }

    @Override public Class<?> getColumnClass(int col) {
        return COL_TYPES[col];
    }

    public Object getValueAt(int row, int col) {
        Graph graph = gc.getActiveGraph();
        Object node = graph.nodes().get(row);
        switch (col) {
            case COL_OBJECT :
                return node;
            case COL_LABEL :
                Object value = graph instanceof ValuedGraph ? ((ValuedGraph)graph).getValue(node) : null;
                return value == null ? "" : value.toString();
            case COL_LOC:
                Point2D.Double pos = gc.getPositions().get(node);
                return String.format("(%.3f,%.3f)", pos.x, pos.y);
            case COL_DEGREE :
                return graph.degree(node);
            case COL_METRIC:
                return values == null ? null : values.get(row);
        }
        throw new IllegalArgumentException("Graph's table does not contain entry at (row, col) = (" + row + ", " + col + ").");
    }

    public boolean isLabelColumn(int col) {
        return col == COL_LABEL;
    }

    @Override public boolean isCellEditable(int row, int col) {
        return gc.getActiveGraph() instanceof ValuedGraph && col == COL_LABEL;
    }
    
    @Override
    public void setValueAt(Object aValue, int row, int col) {
        Graph graph = gc.getActiveGraph();
        if (!isCellEditable(row, col))
            throw new IllegalArgumentException("Cannot set value of graph " + graph + " (row, col) = (" + row + ", " + col + ") to " + aValue);

        ((ValuedGraph)graph).setValue(graph.nodes().get(row), aValue);
        fireTableCellUpdated(row, col);
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        System.out.println("gtm pc: " + evt.getSource() + " " + evt.getNewValue());
        if (evt.getSource() == gc) {
            if (evt.getPropertyName().equals("primary") || evt.getPropertyName().equals("metric")) {
                updateValues();
            } else if (evt.getPropertyName().equals("positions"))
                fireTableDataChanged();
        } else if (evt.getSource() instanceof GraphControllerMaster) {
            if (evt.getPropertyName().equals("active")) {
                setController(((GraphControllerMaster)evt.getSource()).getActiveController());
            }
        }
    }

}
