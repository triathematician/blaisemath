/*
 * GraphListModel.java
 * Created Aug 10, 2010
 */

package graphexplorer.views;

import graphexplorer.controller.AbstractGraphController;
import graphexplorer.controller.GraphDecorController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * Provides a view of a graph via the list of nodes.
 * @author Elisha Peterson
 */
public class GraphListModel extends AbstractListModel
        implements PropertyChangeListener {

    /** The graph being tracked */
    AbstractGraphController gc = null;
    /** List of primary nodes */
    List primaryNodes = null;

    /** Constructs without a graph */
    public GraphListModel() {
    }

    /** Constructs with specified graph. */
    public GraphListModel(AbstractGraphController gc) {
        setController(gc);
    }

    /** @return underlying controller */
    public AbstractGraphController getController() {
        return gc;
    }

    /** Sets the controller that contains the data backed by this model. */
    public void setController(AbstractGraphController gc) {
        if (this.gc != gc) {
            if (this.gc != null)
                this.gc.removePropertyChangeListener(this);
            this.gc = gc;
            primaryNodes = new ArrayList(gc.getBaseGraph().nodes());
            if (gc != null)
                gc.addPropertyChangeListener(this);
            fireContentsChanged(this, 0, getSize());
        }
    }

    public int getSize() {
        if (gc == null) return 0;
        Graph graph = gc.getBaseGraph();
        return graph == null ? 0 : primaryNodes.size();
    }

    public Object getElementAt(int index) {
        if (gc == null || primaryNodes == null)
            return null;
        Object node = primaryNodes.get(index);
        Graph active = gc.getBaseGraph();
        if (active instanceof ValuedGraph && active.contains(node))
            return ((ValuedGraph)active).getValue(node);
        return node;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == gc) {
            if (evt.getPropertyName().equals(AbstractGraphController.$BASE))
                fireContentsChanged(this, 0, getSize());
        }
    }

}
