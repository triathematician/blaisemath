/**
 * IndexedBeanEditorSupport.java
 * Created on Aug 19, 2009
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
import com.googlecode.blaisemath.util.ReflectionUtils;
import java.awt.Component;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JComponent;

/**
 * <p>
 *   Models a list of properties corresponding to an {@link IndexedPropertyDescriptor},
 *   for a parent object.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class IndexedBeanEditorModel extends AbstractListModel implements BeanEditorModel {

    /** The parent object */
    private final Object parent;
    /** Descriptor */
    private final IndexedPropertyDescriptor ipd;
    /** Size of the indexed bean */
    private int size;
    
    /** List of property editors. */
    protected PropertyEditor[] editors;
    /** List of component editors. */
    protected Component[] components;
    
    /** Handles listeners for changes to editors. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructs for specified object and indexed property.
     * @param parent the parent object
     * @param ipd the indexed property
     */
    public IndexedBeanEditorModel(Object parent, IndexedPropertyDescriptor ipd) {
        if (parent == null || ipd == null) {
            throw new IllegalArgumentException("IndexedBeanEditorSupport cannot be constructed with null objects!");
        }
        this.parent = parent;
        this.ipd = ipd;
        Object[] arr;
        arr = (Object[]) ReflectionUtils.tryInvokeRead(parent, ipd);
        if (arr == null) {
            arr = new Object[0];
        }
        size = arr.length;
        initEditors();
    }

    private void initEditors() {
        editors = new PropertyEditor[getSize()];
        components = new Component[getSize()];
        for (int i = 0; i < getSize(); i++) {
            editors[i] = EditorRegistration.getIndexedEditor(parent, ipd, i);
            if (editors[i] != null && editors[i].supportsCustomEditor()) {
                components[i] = editors[i].getCustomEditor();
            } else {
                components[i] = new DefaultPropertyComponent(parent, ipd, i);
            }
            if (getElementAt(i).getWriteMethod() == null) {
                // if no write method make sure it is disabled
                components[i].setEnabled(false); 
            }
            if (components[i] instanceof JComponent) {
                // do not show any borders
                ((JComponent) components[i]).setBorder(null); 
            }
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public IndexedPropertyDescriptor getElementAt(int index) {
        return ipd;
    }

    @Override
    public Component getEditor(int row) {
        return components[row];
    }

    @Override
    public Class<?> getValueType(int row) {
        return ipd.getIndexedPropertyType();
    }

    @Override
    public Object getValue(int pos) {
        return ReflectionUtils.tryInvokeIndexedRead(parent, ipd, pos);
    }

    @Override
    public void setValue(int pos, Object value) {
        ReflectionUtils.tryInvokeIndexedWrite(parent, ipd, pos, value);
    }
    
    /** 
     * Create and add a new value to the list.
     * @return newly created value, null if unable to create one
     */
    Object addNewValue() {
        Object newObject = ReflectionUtils.tryInvokeNew(ipd.getIndexedPropertyType());
        if (newObject == null) {
            return null;
        }

        Object[] objs = (Object[]) ReflectionUtils.tryInvokeRead(parent, ipd);
        if (objs == null) {
            return null;
        }
        Object[] newArr = Arrays.copyOf(objs, objs.length+1);
        newArr[objs.length] = newObject;
        if (ReflectionUtils.tryInvokeWrite(parent, ipd, newArr)) {
            size = newArr.length;
            initEditors();
            fireContentsChanged(this, 0, size-1);
            return newObject;
        } else {
            return null;
        }
    }
    
    void removeValues(int[] rows) {
        if (rows.length == 0) {
            return;
        }
        Object[] objs = (Object[]) ReflectionUtils.tryInvokeRead(parent, ipd);
        if (objs == null) {
            return;
        }
        List listObjs = new ArrayList(Arrays.asList(objs));
        for (int i = rows.length-1; i>=0; i--) {
            listObjs.remove(rows[i]);
        }
        Object[] newArr = listObjs.toArray((Object[])Array.newInstance(ipd.getIndexedPropertyType(), 0));
        if (ReflectionUtils.tryInvokeWrite(parent, ipd, newArr)) {
            size = newArr.length;
            initEditors();
            fireContentsChanged(this, 0, size-1);
        }
    }
    
    //
    // PROPERTY CHANGE
    //

    @Override
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    @Override
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
