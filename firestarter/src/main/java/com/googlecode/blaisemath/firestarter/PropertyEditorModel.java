package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.googlecode.blaisemath.editor.EditorRegistration;
import com.googlecode.blaisemath.editor.MPropertyEditorSupport;
import com.googlecode.blaisemath.util.FilteredListModel;
import static com.googlecode.blaisemath.util.Preconditions.checkState;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Provides lists of {@link PropertyEditor}s and corresponding components used
 * for editing the properties of a {@link PropertyModel}.
 * 
 * @author Elisha Peterson
 */
public final class PropertyEditorModel implements ListModel<Component> {

    /** Model providing which properties are being displayed/editable */
    private final PropertyModel model;
    
    /** List of property editors. */
    private PropertyEditor[] editors;
    /** List of component editors. */
    private final FilteredListModel<Component> components;
    
    /** Listens for changes to editors */
    private final PropertyChangeListener editorListener;
    /** Propagates changes from the underlying property model */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public PropertyEditorModel(PropertyModel m) {
        this.model = m;
        this.model.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            String name = evt.getPropertyName();
            assert name != null;
            for (int i = 0; i < model.getSize(); i++) {
                if (name.equals(model.getElementAt(i))) {
                    editors[i].setValue(evt.getNewValue());
                }
            }
            pcs.firePropertyChange(evt);
        });
        this.model.addListDataListener(new ListDataListener(){
            @Override
            public void intervalAdded(ListDataEvent e) {
                initEditors();
            }
            @Override
            public void intervalRemoved(ListDataEvent e) {
                initEditors();
            }
            @Override
            public void contentsChanged(ListDataEvent e) {
                initEditors();
            }
        });
        
        editorListener = this::handleEditorChange;
        
        components = new FilteredListModel<>();
        initEditors();
    }

    /** Constructs visual editor components & set up listening. */
    private void initEditors() {
        int size = model.getSize();
        editors = new PropertyEditor[size];
        List<Component> comps = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Class<?> type = model.getPropertyType(i);
            checkState(type != null, model.getElementAt(i)+" has null type for model "+model);
            Object val = model.getPropertyValue(i);
            editors[i] = EditorRegistration.getRegisteredEditor(val, model.getPropertyType(i));
            if (editors[i] != null) {
                editors[i].setValue(model.getPropertyValue(i));
                editors[i].addPropertyChangeListener(editorListener);
            }
            Component ci = editors[i] != null && editors[i].supportsCustomEditor() 
                    ? editors[i].getCustomEditor()
                    : new DefaultPropertyComponent(model, i);
            ci.setEnabled(ci instanceof DefaultPropertyComponent || model.isWritable(i));
            if (ci instanceof JComponent) {
                ((JComponent) ci).setBorder(null);
            }
            comps.add(ci);
        }
        components.setUnfilteredItems(comps);
    }

    /** Manages a change from one of the editors */
    private void handleEditorChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        for (int i = 0; i < model.getSize(); i++) {
            if (editors[i] instanceof PropertyEditorSupport 
                    && source == ((PropertyEditorSupport) editors[i]).getSource()) {
                Object newValue = ((MPropertyEditorSupport) source).getNewValue();
                model.setPropertyValue(i, newValue);
                editors[i].setValue(newValue);
            } else if (source == editors[i]) {
                Object newValue = editors[i].getValue();
                model.setPropertyValue(i, newValue);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">

    public PropertyModel getPropertyModel() {
        return model;
    }
    
    //</editor-fold>

    @Override
    public int getSize() {
        return components.getSize();
    }

    @Override
    public Component getElementAt(int index) {
        return components.getElementAt(index);
    }
    
    public PropertyEditor getPropertyEditor(int row) {
        return editors[row];
    }
    
    //<editor-fold defaultstate="collapsed" desc="ListDataListener METHODS">
    
    @Override
    public void addListDataListener(ListDataListener l) {
        components.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        components.removeListDataListener(l);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport METHODS">
    
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
