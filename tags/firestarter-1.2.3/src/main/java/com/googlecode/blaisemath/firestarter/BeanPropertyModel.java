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

import com.google.common.base.Predicate;
import com.googlecode.blaisemath.util.FilteredListModel;
import com.googlecode.blaisemath.util.ReflectionUtils;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * <p>
 *   Uses bean information about an object, gathered by introspection, to provide
 *   editable attributes of that object.
 * </p>
 * <p>
 *   A filter may be supplied to limit the properties made available by the model.
 * </p>
 *
 * @author Elisha Peterson
 */
public final class BeanPropertyModel extends PropertyModelSupport {

    /** Object of this class. */
    private final Object bean;
    /** Filtered items */
    private final FilteredListModel<PropertyDescriptor> filteredProperties;

    /**
     * Constructs for specified bean.
     * @param bean the underlying object.
     */
    public BeanPropertyModel(Object bean) {
        this.bean = bean;

        filteredProperties = new FilteredListModel<PropertyDescriptor>();
        filteredProperties.setFilter(BeanPropertyFilter.STANDARD);
        filteredProperties.addListDataListener(new ListDataListener(){
            @Override
            public void intervalAdded(ListDataEvent e) {
                fireIntervalAdded(BeanPropertyModel.this, e.getIndex0(), e.getIndex1());
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                fireIntervalRemoved(BeanPropertyModel.this, e.getIndex0(), e.getIndex1());
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                fireContentsChanged(BeanPropertyModel.this, e.getIndex0(), e.getIndex1());
            }
        });
        
        BeanInfo info = ReflectionUtils.getBeanInfo(bean.getClass());
        filteredProperties.setUnfilteredItems(Arrays.asList(info.getPropertyDescriptors()));
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /** 
     * Get the bean object represented by this class.
     * @return the underlying object.
     */
    public Object getBean() {
        return bean;
    }

    public Predicate<PropertyDescriptor> getFilter() {
        return filteredProperties.getFilter();
    }

    public void setFilter(Predicate<PropertyDescriptor> filter) {
        filteredProperties.setFilter(filter);
    }
    
    //</editor-fold>

    @Override
    public int getSize() {
        return filteredProperties.getSize();
    }

    @Override
    public String getElementAt(int row) {
        return getPropertyDescriptor(row).getDisplayName();
    }
    
    public PropertyDescriptor getPropertyDescriptor(int row) {
        return filteredProperties.getElementAt(row);
    }

    @Override
    public Class<?> getPropertyType(int row) {
        PropertyDescriptor pd = getPropertyDescriptor(row);
        return pd instanceof IndexedPropertyDescriptor
                ? Object[].class : pd.getPropertyType();
    }

    @Override
    public boolean isWritable(int row) {
        PropertyDescriptor pd = getPropertyDescriptor(row);
        return pd.getWriteMethod() != null;
    }

    @Override
    public Object getPropertyValue(int row) {
        PropertyDescriptor pd = getPropertyDescriptor(row);
        return ReflectionUtils.tryInvokeRead(bean, pd);
    }

    @Override
    public void setPropertyValue(int row, Object value) {
        PropertyDescriptor pd = getPropertyDescriptor(row);
        Object cur = ReflectionUtils.tryInvokeRead(bean, pd);
        if (ReflectionUtils.tryInvokeWrite(bean, pd, value)) {
            pcs.firePropertyChange(getElementAt(row), cur, value);
        }
    }

}
