/**
 * BeanEditorSupport.java
 * Created on Jun 30, 2009
 */
package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
import com.googlecode.blaisemath.util.ReflectionUtils;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.event.ListDataEvent;

/**
 * <p>
 *   Uses bean information about an object, gathered by introspection, to provide
 *   editable attributes of that object.
 * </p>
 * <p>
 *   Contains a filtered list model, which allows for {@link ListDataEvent}s to be fired.
 *   This is typically done when the filter properties change.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class DefaultBeanEditorModel extends FilteredPropertyList implements BeanEditorModel {

    /** Object of this class. */
    private Object bean;    
    /** List of property editors. */
    private PropertyEditor[] editors;
    /** List of component editors. */
    private Component[] components;
    
    /** Listens for changes to editors */
    private final PropertyChangeListener editorListener;
    
    /** Handles listeners for changes to editors. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructs for specified bean.
     * @param bean the underlying object.
     */
    public DefaultBeanEditorModel(Object bean) {
        editorListener = new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                handleEditorChange(evt);
            }
        };
        
        this.bean = bean;
        BeanInfo info = ReflectionUtils.getBeanInfo(bean.getClass());
        setProperties(info.getPropertyDescriptors());
    }

    /**
     * Constructs the visual components that are used for editing. This should be
     * called immediately after any refilter command.
     */
    private void initEditors() {
        editors = new PropertyEditor[getSize()];
        components = new Component[getSize()];
        for (int i = 0; i < getSize(); i++) {
            PropertyDescriptor pd = getElementAt(i);
            editors[i] = EditorRegistration.getEditor(bean, pd);
            editors[i].addPropertyChangeListener(editorListener);
            components[i] = editors[i].supportsCustomEditor() ? editors[i].getCustomEditor()
                    : new DefaultPropertyComponent(bean, pd);
            components[i].setEnabled(pd.getWriteMethod() != null 
                    && pd.getReadMethod() != null);
            if (components[i] instanceof JComponent) {
                ((JComponent) components[i]).setBorder(null);
            }
        }
    }

    /** 
     * Get the bean object represented by this class.
     * @return the underlying object.
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Returns the Java type info for the property at the given row.
     * @param i the row
     * @return property type
     */
    @Override
    public Class<?> getValueType(int i) {
        return getElementAt(i).getPropertyType();
    }

    /**
     * Returns value at given position.
     * @param row the row
     * @return value at the row, null if there is none
     */
    @Override
    public Object getValue(int row) {
        return ReflectionUtils.tryInvokeRead(bean, getElementAt(row));
    }

    /**
     * Sets property in given row.
     * @param row
     * @param value
     */
    @Override
    public void setValue(int row, Object value) {
        ReflectionUtils.tryInvokeWrite(bean, getElementAt(row), value);
    }

    /**
     * Get the editor component at the given row
     * @param row row of editor
     * @return editor component at specified position. 
     */
    @Override
    public Component getEditor(int row) {
        return components[row];
    }

    //
    // EVENT HANDLING
    //

    /** Individual editors will fire property changes that are handled by this class. */
    private void handleEditorChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        for (int i = 0; i < getSize(); i++) {
            if (editors[i] instanceof PropertyEditorSupport 
                    && source == ((PropertyEditorSupport) editors[i]).getSource()) {
                Object oldValue = ((PropertyEditor) source).getValue();
                Object newValue = ((MPropertyEditorSupport) source).getNewValue();
                setValue(i, newValue);
                editors[i].setValue(newValue);
                pcs.firePropertyChange(getElementAt(i).getDisplayName(), oldValue, newValue);
            } else if (source == editors[i]) {
                Object oldValue = getValue(i);
                Object newValue = editors[i].getValue();
                setValue(i, newValue);
                pcs.firePropertyChange(getElementAt(i).getDisplayName(), oldValue, evt.getNewValue());
            }
        }
    }

    @Override
    protected void fireContentsChanged(Object source, int index0, int index1) {
        if (index0 != index1) {
            initEditors();
        }
        super.fireContentsChanged(source, index0, index1);
    }

    @Override
    protected void fireIntervalAdded(Object source, int index0, int index1) {
        initEditors();
        super.fireIntervalAdded(source, index0, index1);
    }

    @Override
    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        initEditors();
        super.fireIntervalRemoved(source, index0, index1);
    }

    //
    // PROPERTY CHANGE
    //

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }


}
