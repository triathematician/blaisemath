/**
 * FilteredPropertyList.java
 * Created on Jul 3, 2009
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
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractListModel;

/**
 * <p>
 *   {@code FilteredPropertyList} maintains an array of {@link PropertyDescriptor}s, as well
 *   as a vector representing a filtered version of this array.
 * </p>
 *
 * @author Elisha Peterson
 */
abstract class FilteredPropertyList extends AbstractListModel {

    protected PropertyDescriptor[] unfilteredProperties = new PropertyDescriptor[0];
    private final List<PropertyDescriptor> filterItems = new ArrayList<PropertyDescriptor>();
    
    /** Stores the present filter value. */
    protected Predicate<PropertyDescriptor> filter = BeanFilterRule.STANDARD;

    /**
     * Set the properties
     * @param props properties
     */
    protected void setProperties(PropertyDescriptor[] props) {
        this.unfilteredProperties = Arrays.copyOf(props, props.length);
        refilter();
    }
    
    /** 
     * Get current filter.
     * @return current filter value. 
     */
    Predicate<PropertyDescriptor> getFilter() {
        return filter;
    }

    /** 
     * Set current filter
     * @param filter the new filter value. 
     */
    public void setFilter(Predicate<PropertyDescriptor> filter) {
        if (this.filter != filter) {
            this.filter = filter;
            refilter();
        }
    }

    @Override
    public int getSize() {
        return filterItems.size();
    }

    @Override
    public PropertyDescriptor getElementAt(int index) {
        return index < filterItems.size() ? filterItems.get(index) : null;
    }

    /** Refilters the list of properties based on the current criteria. */
    protected final void refilter() {
        Set<PropertyDescriptor> unsorted = filter(unfilteredProperties, filter);
        filterItems.clear();
        filterItems.addAll(filter(unsorted, BeanFilterRule.PREFERRED));
        filterItems.addAll(filter(unsorted, BeanFilterRule.STANDARD));
        filterItems.addAll(filter(unsorted, BeanFilterRule.EXPERT));
        fireContentsChanged(this, 0, getSize()+1);
    }
    
    private static <T> Set<T> filter(T[] src, Predicate<? super T> filter) {
        Set<T> res = new LinkedHashSet<T>();
        for (T t : src) {
            if (filter.apply(t) && !res.contains(t)) {
                res.add(t);
            }
        }
        return res;
    }
    
    private static <T> Set<T> filter(Iterable<T> src, Predicate<? super T> filter) {
        Set<T> res = new LinkedHashSet<T>();
        for (T t : src) {
            if (filter.apply(t) && !res.contains(t)) {
                res.add(t);
            }
        }
        return res;
    }
}
