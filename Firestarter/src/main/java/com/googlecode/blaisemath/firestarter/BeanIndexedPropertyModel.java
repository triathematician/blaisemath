/**
 * BeanIndexedPropertyListModel.java
 * Created Sep 23, 2014
 */

package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.googlecode.blaisemath.util.ReflectionUtils;
import java.beans.IndexedPropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *   Supports editing of individual items in an indexed property of a bean object.
 *   Provides methods for adding a new item to the property, and for removing an
 *   arbitrary set of items.
 * </p>
 * @author elisha
 */
public final class BeanIndexedPropertyModel extends PropertyModelSupport {

    /** The parent object */
    private final Object parent;
    /** Descriptor */
    private final IndexedPropertyDescriptor ipd;
    /** Size of the array property */
    private int size;

    /**
     * Constructs for specified bean and property
     * @param parent the object owning the indexed property
     * @param ipd the indexed property for this model instance
     */
    public BeanIndexedPropertyModel(Object parent, IndexedPropertyDescriptor ipd) {
        this.parent = parent;
        this.ipd = ipd;
        Object[] arr = getPropertyValueArray();
        size = arr == null ? 0 : arr.length;
    }

    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public String getElementAt(int row) {
        return ""+row;
    }

    @Override
    public Class<?> getPropertyType(int row) {
        return ipd.getIndexedPropertyType();
    }

    @Override
    public Object getPropertyValue(int pos) {
        return ReflectionUtils.tryInvokeIndexedRead(parent, ipd, pos);
    }
    
    public Object[] getPropertyValueArray() {
        return (Object[]) ReflectionUtils.tryInvokeRead(parent, ipd);
    }

    @Override
    public boolean isWritable(int row) {
        return ipd.getIndexedWriteMethod() != null;
    }

    @Override
    public void setPropertyValue(int pos, Object value) {
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

        Object[] objs = getPropertyValueArray();
        if (objs == null) {
            return null;
        }
        Object[] newArr = Arrays.copyOf(objs, objs.length+1);
        newArr[objs.length] = newObject;
        if (ReflectionUtils.tryInvokeWrite(parent, ipd, newArr)) {
            size = newArr.length;
            fireIntervalAdded(this, objs.length, objs.length);
            return newObject;
        } else {
            return null;
        }
    }
    
    /**
     * Removes items at the specified indices from the indexed property
     * @param rows rows to remove
     */
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
            fireContentsChanged(this, 0, newArr.length-1);
        }
    }

}
