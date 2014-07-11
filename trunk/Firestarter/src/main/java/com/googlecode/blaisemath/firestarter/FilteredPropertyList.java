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

import com.google.common.base.Predicate;
import java.beans.PropertyDescriptor;
import java.util.Vector;
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

    PropertyDescriptor[] items;
    Vector<PropertyDescriptor> filterItems;
    
    /** Stores the present filter value. */
    protected Predicate<PropertyDescriptor> filter = BeanFilterRule.STANDARD;

    /** Construct with nothing. */
    public FilteredPropertyList(){
    }

    /**
     * Constructs with specified backing 
     * @param items the items to be filtered. */
    public FilteredPropertyList(PropertyDescriptor[] items, Predicate<PropertyDescriptor> filter) {
        this.filter = filter;
        this.items = items;
        refilter();
    }

    /** @return current filter value. */
    public Predicate<PropertyDescriptor> getFilter() {
        return filter;
    }

    /** @param filter the new filter value. */
    public void setFilter(Predicate<PropertyDescriptor> filter) {
        if (this.filter != filter) {
            this.filter = filter;
            refilter();
        }
    }

    public int getSize() {
        return filterItems.size();
    }

    public PropertyDescriptor getElementAt(int index) {
        return index < filterItems.size() ? filterItems.get(index) : null;
    }

    /** Refilters the list of properties based on the current criteria. */
    public void refilter() {
        filterItems = new Vector<PropertyDescriptor>();
        // add preferred items first
        for (int i = 0; i < items.length; i++)
            if (items[i].isPreferred() && filter.apply(items[i]))
                filterItems.add(items[i]);
        // now add standard items
        for (int i = 0; i < items.length; i++)
            if (!items[i].isPreferred() && !items[i].isExpert() && filter.apply(items[i]))
                filterItems.add(items[i]);
        // now add expert items
        for (int i = 0; i < items.length; i++)
            if (items[i].isExpert() && !items[i].isPreferred() && filter.apply(items[i]))
                filterItems.add(items[i]);
        fireContentsChanged(this, 0, getSize()+1);
    }

    /** Sorts the list of properties by their names. */
    public void sort() {
        // TODO - write this code
        System.out.println("Need to sort items here.");
        refilter();
    }
}
