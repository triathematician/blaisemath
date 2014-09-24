/**
 * BeanEditorModel.java
 * Created Sep 18, 2014
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

import java.beans.PropertyChangeListener;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Provides several editors for an object, called the "bean". These editors are
 * referred to by their "row". Methods are also provided for modifying the properties,
 * and listeners are available to send notifications when the properties change.
 * As a {@link ListModel}, this class provides names for properties being displayed,
 * and generates {@link ListDataEvent}s when that list changes.
 * 
 * @author Elisha
 */
public interface PropertyModel extends ListModel<String> {
    
    /**
     * Returns the Java type info for the property at the given row.
     * @param row the row
     * @return property type
     */
    Class<?> getPropertyType(int row);
    
    /**
     * Whether write is enabled for the given row
     * @param row the row
     * @return true if row is writable
     */
    boolean isWritable(int row);
    
    /**
     * Returns value at given position.
     * @param row the row
     * @return value at the row, null if there is none
     */
    Object getPropertyValue(int row);

    /**
     * Sets property in given row.
     * @param row
     * @param value
     */
    void setPropertyValue(int row, Object value);


    //
    // PROPERTY CHANGE
    //

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void addPropertyChangeListener(PropertyChangeListener listener);
    
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    
    /** Blank instance of a property model */
    public static class Empty implements PropertyModel {
        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public String getElementAt(int index) {
            throw new UnsupportedOperationException("Not supported.");
        }
        
        @Override
        public Class<?> getPropertyType(int row) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isWritable(int row) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Object getPropertyValue(int row) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setPropertyValue(int row, Object value) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            // ignore
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // ignore
        }

        @Override
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            // ignore
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // ignore
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            // ignore
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            // ignore
        }
    }
    
}
