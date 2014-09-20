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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import javax.swing.ListModel;

/**
 * Provides several editors for an object, called the "bean". These editors are
 * referred to by their "row".
 * @author Elisha
 */
public interface BeanEditorModel extends ListModel {

    /**
     * Get the editor component at the given row
     * @param row row of editor
     * @return editor component at specified position.
     */
    Component getEditor(int row);

    /**
     * Returns the Java type info for the property at the given row.
     * @param row the row
     * @return property type
     */
    Class getValueType(int row);

    /**
     * Returns value at given position.
     * @param row the row
     * @return value at the row, null if there is none
     */
    Object getValue(int row);

    /**
     * Sets property in given row.
     * @param row
     * @param value
     */
    void setValue(int row, Object value);

    //
    // PROPERTY CHANGE
    //

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void addPropertyChangeListener(PropertyChangeListener listener);
    
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
    
}
