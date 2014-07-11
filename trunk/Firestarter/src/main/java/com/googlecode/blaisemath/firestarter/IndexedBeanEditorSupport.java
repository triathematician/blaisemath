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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import com.googlecode.blaisemath.firestarter.editor.EditorRegistration;

/**
 * <p>
 *   <code>IndexedBeanEditorSupport</code> works much like <code>BeanEditorSupport</code>,
 *   but instead has the second column display editors <i>for each indexed property in an array</i>.
 * </p>
 *
 * @author Elisha Peterson
 */
final class IndexedBeanEditorSupport extends BeanEditorSupport {

    /** Descriptor */
    IndexedPropertyDescriptor ipd;
    /** Size of the indexed bean */
    int size;
    /** The type of class for indiviudal entries in the array. */
    Class type;

    /**
     * Constructs for specified bean.
     * @param bean the underlying object.
     */
    public IndexedBeanEditorSupport(Object bean, IndexedPropertyDescriptor ipd) {
        if (bean == null || ipd == null)
            throw new IllegalArgumentException("IndexedBeanEditorSupport cannot be constructed with null objects!");
        this.bean = bean;
        this.ipd = ipd;
        Object[] arr = new Object[]{};
        try {
            // the underlying object
            arr = (Object[]) ipd.getReadMethod().invoke(bean);
        } catch (Exception ex) {
            Logger.getLogger(IndexedBeanEditorSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        size = arr.length;
        type = ipd.getIndexedPropertyType();
        initEditors();
    }

    @Override
    public void initEditors() {
        editors = new PropertyEditor[getSize()];
        components = new Component[getSize()];
        for (int i = 0; i < getSize(); i++) {
            editors[i] = EditorRegistration.getIndexedEditor(bean, ipd, i);
            if (editors[i] != null && editors[i].supportsCustomEditor())
                components[i] = editors[i].getCustomEditor();
            else
                components[i] = new DefaultPropertyComponent(bean, ipd, i);
            if (getElementAt(i).getWriteMethod() == null)
                components[i].setEnabled(false); // if no write method make sure it is disabled
            if (components[i] instanceof JComponent)
                ((JComponent) components[i]).setBorder(null); // do not show any borders
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public PropertyDescriptor getElementAt(int index) {
        return ipd;
    }

    /** Returns value at given position. */
    @Override
    public Object getValue(int pos) {
        try {
            return ipd.getIndexedReadMethod().invoke(bean, pos);
        } catch (Exception ex) {
        }
        return null;
    }

    /** Sets property in given row. */
    @Override
    public void setValue(int pos, Object value) {
        try {
            ipd.getIndexedWriteMethod().invoke(bean, pos, value);
        } catch (Exception ex) {
        }
    }

    @Override
    public void refilter() {
        // no need for filtering in the indexed sheet
    }



    // *********************************************** //
    //                  INNER CLASSES                  //
    // *********************************************** //

    /** Action that adds an element to the bean. */
    AbstractAction addAction = new AbstractAction("+") {
        { putValue(SHORT_DESCRIPTION, "Add a new element to the end of the list."); }

        /** Attempts to add a new element to the list (only supported with no-arg constructors. */
        public void actionPerformed(ActionEvent e) {
            Object newObject = null;
            try {
                newObject = ipd.getIndexedPropertyType().getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                // no constructor, cannot create a new object
                setEnabled(false);
                return;
            }
            
            try {
                Object[] objs = (Object[]) ipd.getReadMethod().invoke(bean);
                Object[] arr = (Object[]) Array.newInstance(ipd.getIndexedPropertyType(), objs.length + 1);
                System.arraycopy(objs, 0, arr, 0, objs.length);
                arr[objs.length] = newObject;
                ipd.getWriteMethod().invoke(bean, (Object) arr);
                size = arr.length;
//                initEditors();
                fireContentsChanged(this, 0, size-1);
            } catch (Exception ex) {
                // cannot create a new object
                setEnabled(false);
            }
        }
    }; // ADD ACTION


    /** 
     * Action that removes an element to the bean. 
     * @param t table providing indices for removal
     */
    AbstractAction createRemoveAction(final JTable t) {
        return new AbstractAction("-") {
            { putValue(SHORT_DESCRIPTION, "Remove the selected element from the end of the list."); }
            /** Attempts to add a new element to the list (only supported with no-arg constructors. */
            public void actionPerformed(ActionEvent e) {
                try {
                    int[] sel = t.getSelectedRows();
                    if (sel.length == 0)
                        return;
                    Object[] objs = (Object[]) ipd.getReadMethod().invoke(bean);
                    List lObjs = new ArrayList();
                    for (Object o : objs)
                        lObjs.add(o);
                    for (int i = sel.length-1; i>=0; i--)
                        lObjs.remove(sel[i]);
                    Object[] arr = lObjs.toArray((Object[]) Array.newInstance(ipd.getIndexedPropertyType(), 0));
                    ipd.getWriteMethod().invoke(bean, (Object) arr);
                    size = arr.length;
                    fireContentsChanged(this, 0, size-1);
                } catch (Exception ex) {
//                    setEnabled(false);
                }
            }
        };
    }; // REMOVE ACTION

}
