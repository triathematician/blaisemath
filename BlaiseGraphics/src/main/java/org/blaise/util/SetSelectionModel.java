/**
 * SetSelectionModel.java
 * Created Aug 1, 2012
 */
package org.blaise.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListSelectionModel;

/**
 * <p>
 *   Model maintaining a collection of selected graphics, and notifying listeners
 *   when that set changes. Modeled in part after {@link ListSelectionModel}.
 * </p>
 * @param <G> type of object that can be selected
 * @author elisha
 */
public class SetSelectionModel<G> {

    private final Set<G> selected = Collections.synchronizedSet(new HashSet<G>());

    /** Initialize without arguments */
    public SetSelectionModel() {
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Set<G> getSelection() {
        return selected;
    }

    public synchronized void setSelection(Set<G> selection) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            synchronized (selected) {
                HashSet<G> old = new HashSet<G>(selected);
                selected.clear();
                selected.addAll(selection);
                pcs.firePropertyChange("selection", old, selected);
            }
        }
    }

    public synchronized void addSelection(G g) {
        if (g != null && !selected.contains(g)) {
            HashSet<G> old = new HashSet<G>(selected);
            selected.add(g);
            pcs.firePropertyChange("selection", old, selected);
        }
    }

    public synchronized void removeSelection(G g) {
        HashSet<G> old = new HashSet<G>(selected);
        if (g != null && selected.remove(g)) {
            pcs.firePropertyChange("selection", old, selected);
        }
    }

    /**
     * Toggle selection status of g
     * @param g object to toggle
     */
    public synchronized void toggleSelection(G g) {
        if (g != null) {
            HashSet<G> old = new HashSet<G>(selected);
            if (selected.contains(g)) {
                selected.remove(g);
            } else {
                selected.add(g);
            }
            pcs.firePropertyChange("selection", old, selected);
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    //</editor-fold>

}
