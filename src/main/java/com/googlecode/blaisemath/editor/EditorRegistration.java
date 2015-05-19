/**
 * EditorRegistration.java
 * Created on Jul 1, 2009
 */
package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import static com.googlecode.blaisemath.util.Preconditions.checkNotNull;
import com.googlecode.blaisemath.util.ReflectionUtils;
import java.awt.Color;
import java.awt.Font;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;

/**
 * <p>
 *   Static code for registering and accessing registered property editors.
 * </p>
 *
 * @author Elisha Peterson
 */
public class EditorRegistration {

    // utility class
    private EditorRegistration() {
    }

    /** Registers all of the editors specified within this package. */
    public static void registerEditors() {
        // basic editors

        PropertyEditorManager.registerEditor(boolean.class, BooleanEditor.class);
        PropertyEditorManager.registerEditor(Boolean.class, BooleanEditor.class);
        
        PropertyEditorManager.registerEditor(String.class, StringEditor.class);
        
        PropertyEditorManager.registerEditor(Enum.class, EnumEditor.class);
        
        // number editors

        PropertyEditorManager.registerEditor(Byte.class, NumberEditor.ByteEditor.class);
        PropertyEditorManager.registerEditor(byte.class, NumberEditor.ByteEditor.class);
        
        PropertyEditorManager.registerEditor(Short.class, NumberEditor.ShortEditor.class);
        PropertyEditorManager.registerEditor(short.class, NumberEditor.ShortEditor.class);
        
        PropertyEditorManager.registerEditor(Integer.class, NumberEditor.IntegerEditor.class);
        PropertyEditorManager.registerEditor(int.class, NumberEditor.IntegerEditor.class);
        
        PropertyEditorManager.registerEditor(Long.class, NumberEditor.LongEditor.class);
        PropertyEditorManager.registerEditor(long.class, NumberEditor.LongEditor.class);
        
        PropertyEditorManager.registerEditor(Float.class, NumberEditor.FloatEditor.class);
        PropertyEditorManager.registerEditor(float.class, NumberEditor.FloatEditor.class);
        
        PropertyEditorManager.registerEditor(Double.class, NumberEditor.DoubleEditor.class);
        PropertyEditorManager.registerEditor(double.class, NumberEditor.DoubleEditor.class);

        // array editors

        PropertyEditorManager.registerEditor(new String[]{}.getClass(), IndexedPropertyEditor.class);

        // point editors

        PropertyEditorManager.registerEditor(java.awt.Point.class, PointEditor.class);
        PropertyEditorManager.registerEditor(java.awt.Dimension.class, DimensionEditor.class);
        PropertyEditorManager.registerEditor(java.awt.Rectangle.class, RectangleEditor.class);
        PropertyEditorManager.registerEditor(java.awt.Insets.class, InsetsEditor.class);

        PropertyEditorManager.registerEditor(java.awt.geom.Point2D.Double.class, Point2DEditor.class);
        PropertyEditorManager.registerEditor(java.awt.geom.Line2D.Double.class, Line2DEditor.class);
        PropertyEditorManager.registerEditor(java.awt.geom.Ellipse2D.Double.class, RectangularShapeEditor.class);
        PropertyEditorManager.registerEditor(java.awt.geom.Rectangle2D.Double.class, RectangularShapeEditor.class);
        PropertyEditorManager.registerEditor(java.awt.geom.RectangularShape.class, RectangularShapeEditor.class);

        // complex editors

        PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
        PropertyEditorManager.registerEditor(Font.class, FontEditor.class);
    }
    
    /**
     * Returns editor type for a given class, as registered by the property manager.
     * @param cls the class type
     * @return a property editor for the provided class, or {@code null} if there is no available editor
     */
    public static PropertyEditor getRegisteredEditor(Class<?> cls) {
        return getRegisteredEditor(null, cls);
    }
    
    /**
     * Returns editor type for a given object/class, as registered by the property manager.
     * @param obj the object type
     * @param cls the class type
     * @return a property editor for the provided class, or {@code null} if there is no available editor
     */
    public static <T> PropertyEditor getRegisteredEditor(T obj, Class<?> cls) {
        checkNotNull(cls);
        PropertyEditor result = PropertyEditorManager.findEditor(cls);
        if (result != null) {
            return result;
        }
        // look for an enum editor for enum classes
        if (cls != null && cls.isEnum()) {
            result = PropertyEditorManager.findEditor(Enum.class);
            if (result != null) {
                return result;
            }
        }
        // look for the object instance type
        if (obj != null) {
            assert cls.isInstance(obj);
            result = PropertyEditorManager.findEditor(obj.getClass());
            if (result != null) {
                return result;
            }
        }
        return PropertyEditorManager.findEditor(Object.class);
    }

    /**
     * Returns a new instance of an editor for the given property, initialized with
     * the introspected value of the property in the bean.
     * @param bean the underlying class
     * @param descriptor a descriptor for a bean property
     * @return an appropriate editor for the property (not initialized)
     */
    public static PropertyEditor getEditor(final Object bean, final PropertyDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("Null property descriptor!");
        }
        
        // set to type specified by the propertyeditor, if possible
        PropertyEditor result = null;
        Class<?> cls = descriptor.getPropertyEditorClass();
        if (cls != null) {
            result = (PropertyEditor) ReflectionUtils.tryInvokeNew(cls);
        }
        
        // if no luck, set to editor registered for actual type
        if (result == null) {
            Object sampleRead = ReflectionUtils.tryInvokeRead(bean, descriptor);
            if (sampleRead != null) {
                result = getRegisteredEditor(sampleRead.getClass());
            }
        }
        
        // if no luck, set to editor registered for descriptor type
        if (result == null && descriptor.getPropertyType() != null) {
            result = getRegisteredEditor(descriptor.getPropertyType());
        }
        
        // if no luck, use the default editor
        if (result == null) {
            result = new PropertyEditorSupport();
        }
        
        updateEditorValue(bean, descriptor, result);
        addChangeListening(bean, descriptor, result);
        return result;
    }
    
    /** Updates the value of an editor. */
    static void updateEditorValue(Object bean, PropertyDescriptor descriptor, PropertyEditor editor) {
        Object value = ReflectionUtils.tryInvokeRead(bean, descriptor);
        editor.setValue(value);
    }

    /** Sets up change listening to update the bean when the value changes. */
    static void addChangeListening(final Object bean, final PropertyDescriptor descriptor, final PropertyEditor result) {
        if (descriptor.getWriteMethod() != null) {
            result.addPropertyChangeListener(new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object source = evt.getSource();
                    Object newValue = source instanceof MPropertyEditorSupport
                            ? ((MPropertyEditorSupport) source).getNewValue()
                            : source instanceof PropertyEditor ? ((PropertyEditor) source).getValue()
                            : source;
                    ReflectionUtils.tryInvokeWrite(bean, descriptor, newValue);
                }
            });
        }
    }

    /**
     * Returns a new instance of an editor for the given indexed property, initialized with
     * the introspected value of the property in the bean.
     *
     * @param bean the underlying class
     * @param descriptor a descriptor for a bean property
     * @param n the index to use for values
     *
     * @return an appropriate editor for the property (not initialized)
     */
    public static PropertyEditor getIndexedEditor(Object bean, IndexedPropertyDescriptor descriptor, int n) {
        PropertyEditor result = getRegisteredEditor(descriptor.getIndexedPropertyType());
        if (result == null) {
            result = new PropertyEditorSupport();
        }
        updateEditorValue(bean, descriptor, n, result);
        addChangeListening(bean, descriptor, n, result);
        return result;
    }
    
    /** Updates the value of an editor for an indexed property. */
    static void updateEditorValue(Object bean, IndexedPropertyDescriptor descriptor, int n, PropertyEditor editor) {
        Object value = ReflectionUtils.tryInvokeIndexedRead(bean, descriptor, n);
        editor.setValue(value);
    }

    /** Sets up change listening to update the bean when the value changes (indexed properties). */
    static void addChangeListening(final Object bean, final IndexedPropertyDescriptor descriptor, final int n, final PropertyEditor result) {
        if (descriptor.getIndexedWriteMethod() != null) {
            result.addPropertyChangeListener(new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object source = evt.getSource();
                    Object newValue = source instanceof MPropertyEditorSupport
                            ? ((MPropertyEditorSupport) source).getNewValue()
                            : source instanceof PropertyEditor ? ((PropertyEditor) source).getValue()
                            : source;
                    ReflectionUtils.tryInvokeIndexedWrite(bean, descriptor, n, newValue);
                }
            });
        }
    }
}
