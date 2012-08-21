/**
 * GraphicSelectionModel.java
 * Created Aug 1, 2012
 */
package org.blaise.graphics;

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
                selected.clear();
                selected.addAll(selection);
                fireSelectionChanged();
            }
        }
    }
    //</editor-fold>
    
    
    private final EventListenerList listeners = new EventListenerList();

    private void fireSelectionChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener cl : listeners.getListeners(ChangeListener.class)) {
            cl.stateChanged(e);
        }
    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
}
