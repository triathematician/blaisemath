/**
 * GraphicSelectionModel.java
 * Created Aug 1, 2012
 */
package org.blaise.graphics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   Model maintaining a collection of selected graphics, and notifying listeners
 *   when that set changes. Modeled in part after {@link ListSelectionModel}.
 * </p>
 * @author elisha
 */
public class GraphicSelectionModel {

    private final Set<Graphic> selected = Collections.synchronizedSet(new HashSet<Graphic>());

    /** Initialize without arguments */
    public GraphicSelectionModel() {
    }
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //
    
    public Set<Graphic> getSelection() {
        return selected;
    }

    public synchronized void setSelection(Set<Graphic> selection) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            synchronized (selected) {
                HashSet<Graphic> old = new HashSet<Graphic>(selected);
                selected.clear();
                selected.addAll(selection);
                pcs.firePropertyChange("selection", old, selected);
            }
        }
    }

    public synchronized void addSelection(Graphic g) {
        if (g != null && !selected.contains(g)) {
            HashSet<Graphic> old = new HashSet<Graphic>(selected);
            selected.add(g);
            pcs.firePropertyChange("selection", old, selected);
        }
    }

    public synchronized void removeSelection(Graphic g) {
        HashSet<Graphic> old = new HashSet<Graphic>(selected);
        if (g != null && selected.remove(g)) {
            pcs.firePropertyChange("selection", old, selected);
        }
    }

    /**
     * Toggle selection status of g
     * @param g graphic
     */
    public synchronized void toggleSelection(Graphic g) {
        if (g != null) {
            HashSet<Graphic> old = new HashSet<Graphic>(selected);
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

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    //</editor-fold>

}
