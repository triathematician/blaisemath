/**
 * EditorRegistration.java
 * Created on Jul 1, 2009
 */
package com.googlecode.blaisemath.firestarter.editor;

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

import java.awt.Color;
import java.awt.Font;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;

/**
 * <p>
 *   <code>EditorRegistration</code> contains static code for finding and using registered
 *   property editors.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class EditorRegistration {

    /** Registers all of the editors specified within this backage. */
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

        // complex editors

        PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
        PropertyEditorManager.registerEditor(Font.class, FontEditor.class);

    }


    
    /**
     * Returns editor type for a given class, as registered by the property manager.
     *
     * @param cls the class type
     * @return a property editor for the provided class, or <code>null</code> if there is no available editor
     */
    public static PropertyEditor getRegisteredEditor(Class cls) {
        try {
            // look for an editor for the specific class
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
            // default to return the registered object editor
            result = PropertyEditorManager.findEditor(Object.class);
            if (result != null) {
                return result;
            }
        } catch (Exception ex) {
            System.out.println("Unable to find editor for property of type " + cls);
            ex.printStackTrace();
        }
        // if everything else fails, return a null value
        return null;
    }

    /**
     * Returns a new instance of an editor for the given property, initialized with
     * the introspected value of the property in the bean.
     * @param bean the underlying class
     * @param descriptor a descriptor for a bean property
     * @return an appropriate editor for the property (not initialized)
     */
    public static PropertyEditor getEditor(final Object bean, final PropertyDescriptor descriptor) {
        if (descriptor == null)
            throw new IllegalArgumentException("Null property descriptor!");
        
        // set to type specified by the propertyeditor, if possible
        PropertyEditor result = null;
        Class cls = descriptor.getPropertyEditorClass();
        if (cls != null) {
            try {
                result = (PropertyEditor) cls.newInstance();
            } catch (Exception ex) {}
        }
        
        // if no luck, set to editor registered for actual type
        if (result == null)
            try {
                result = getRegisteredEditor(descriptor.getReadMethod().invoke(bean).getClass());
            } catch (Exception ex) {}
        
        // if no luck, set to editor registered for descriptor type
        if (result == null && descriptor.getPropertyType() != null)
            result = getRegisteredEditor(descriptor.getPropertyType());
        
        // if no luck, use the default editor
        if (result == null)
            result = new PropertyEditorSupport();
        
        updateEditorValue(bean, descriptor, result);
        addChangeListening(bean, descriptor, result);
        return result;
    }
    
    /** Updates the value of an editor. */
    static void updateEditorValue(Object bean, PropertyDescriptor descriptor, PropertyEditor editor) {
        Object value = null;
        Method getter = descriptor.getReadMethod();
        if (getter != null) {
            Class[] paramTypes = getter.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            try {
                value = getter.invoke(bean);
            } catch (Exception e) {
                System.out.println("NoSuchMethodError for method invocation\nBean: " + bean.toString() + "\nGetter: " + getter.getName() + "\nGetter args: ");
                for (int i = 0; i < args.length; i++) System.out.println("\t" + "type: " + paramTypes[i] + " value: " + args[i]);
                e.printStackTrace();
            }
        }
        editor.setValue(value);
    }

    /** Sets up change listening to update the bean when the value changes. */
    static void addChangeListening(final Object bean, final PropertyDescriptor descriptor, final PropertyEditor result) {
        final Method setter = descriptor.getWriteMethod();
        // set up listening for changes to properties... update the underlying bean
        result.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    Object source = evt.getSource();
                    if (source instanceof MPropertyEditorSupport) {
                        setter.invoke(bean, ((MPropertyEditorSupport) source).getNewValue());
                    } else if (source instanceof PropertyEditor) {
                        setter.invoke(bean, ((PropertyEditor) source).getValue());
                    } else {
                        setter.invoke(bean, source);
                    }
                } catch (Exception ex) {
                    System.out.println("Unable to change underlying bean value: " + ex);
                }
            }
        });
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
        Object value = null;
        Method getter = descriptor.getIndexedReadMethod();
        if (getter != null) {
            Class[] paramTypes = getter.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            try {
                value = getter.invoke(bean, n);
            } catch (Exception e) {
                System.out.println("NoSuchMethodError for method invocation\nBean: " + bean.toString() + "\nGetter: " + getter.getName() + "\nGetter args: ");
                for (int i = 0; i < args.length; i++) System.out.println("\t" + "type: " + paramTypes[i] + " value: " + args[i]);
                e.printStackTrace();
            }
        }
        editor.setValue(value);
    }

    /** Sets up change listening to update the bean when the value changes (indexed properties). */
    static void addChangeListening(final Object bean, final IndexedPropertyDescriptor descriptor, final int n, final PropertyEditor result) {
        final Method setter = descriptor.getIndexedWriteMethod();
        // set up listening for changes to properties... update the underlying bean
        result.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    Object source = evt.getSource();
                    if (source instanceof MPropertyEditorSupport) {
                        setter.invoke(bean, n, ((MPropertyEditorSupport) source).getNewValue());
                    } else if (source instanceof PropertyEditor) {
                        setter.invoke(bean, n, ((PropertyEditor) source).getValue());
                    } else {
                        setter.invoke(bean, n, source);
                    }
                } catch (Exception ex) {
                    System.out.println("Unable to change underlying bean value: " + ex);
                }
            }
        });
    }
}
