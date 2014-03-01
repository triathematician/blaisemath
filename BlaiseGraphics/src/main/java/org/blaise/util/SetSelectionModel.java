/**
 * SetSelectionModel.java
 * Created Aug 1, 2012
 */
package org.blaise.util;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    
    public static final String SELECTION_PROPERTY = "selection";

    private final Set<G> selected = Sets.newHashSet();

    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Initialize without arguments */
    public SetSelectionModel() {
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public synchronized Set<G> getSelection() {
        return ImmutableSet.copyOf(selected);
    }

    public synchronized void setSelection(Set<G> selection) {
        if (!selection.containsAll(selected) || !selected.containsAll(selection)) {
            Set<G> old = getSelection();
            selected.clear();
            selected.addAll(selection);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public synchronized void addSelection(G g) {
        if (g != null && !selected.contains(g)) {
            Set<G> old = getSelection();
            selected.add(g);
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    public synchronized void removeSelection(G g) {
        if (g != null && selected.remove(g)) {
            Set<G> old = getSelection();
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    /**
     * Toggle selection status of g
     * @param g object to toggle
     */
    public synchronized void toggleSelection(G g) {
        if (g != null) {
            Set<G> old = getSelection();
            if (selected.contains(g)) {
                selected.remove(g);
            } else {
                selected.add(g);
            }
            pcs.firePropertyChange(SELECTION_PROPERTY, old, getSelection());
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

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
