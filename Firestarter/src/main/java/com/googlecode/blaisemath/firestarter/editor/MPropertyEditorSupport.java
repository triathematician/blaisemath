/**
 * MPropertyEditorSupport.java
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

import java.beans.PropertyEditorSupport;

/**
 * <p>
 *   <code>MPropertyEditorSupport</code> extends the basic property editor to
 *   improve handling of events. Maintains a temporary <code>newValue</code> object that
 *   tracks changes by the editor. These changes will pass events to any interested
 *   listeners if adjusted through the <code>setNewValue</code> method rather than directly.
 *   An interested bean can then check the "newValue" to see what has changed.
 * </p>
 * <p>
 *   The <code>cancelEditAction()</code> and <code>stopEditAction()</code> methods will
 *   either reset the editor to the initial state, or make a permanent change to the
 *   initial value. In both cases, an event may be fired to indicate that the external
 *   value has changed.
 * </p>
 * <p>
 *   Registered listeners hear when the new value is changed somehow. If interested
 *   in updating constantly, they should look to the <code>getNewValue()</code> method.
 *   If only interested in the final value, they should update only when the <code>getValue()</code>
 *   method returns a different value.
 * </p>
 * <p>
 *   External changes, e.g. to the underlying bean, should be handled by invoking the
 *   <code>setValue</code> method here. This calls the <code>initEditorValue</code>
 *   method to set up the editor based on some external value.
 * </p>
 *
 * @author Elisha Peterson
 * @see java.beans.PropertyEditor
 */
public abstract class MPropertyEditorSupport extends PropertyEditorSupport {

    /** 
     * Maintains the updated value of the object.
     */
    Object newValue;

    /**
     * Contains revised/new value of the property. This may be called by
     * external classes when they hear that the value has changed.
     *
     * @return initial value of the editor.
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * Internally adjusts the new value of the editor. This is intended to be
     * called within event handling methods from customizer components.
     *
     * @param newValue the new value for the object.
     */
    protected void setNewValue(Object newValue) {
        this.newValue = newValue;
        firePropertyChange();
    }

    /**
     * <p>
     *  Sets both the {@code newValue} property and the {@code value} property to the
     *  specified value.
     * </p>
     * <p>
     *  This is typically called when the editor is initially created and its value
     *  is changed to reflect an underlying bean property. It should not be called
     *  within any editor class itself. Instead, the editor class should update the
     *  {@code newValue} field with any changes made.
     * </p>
     *
     * @param value the value of the property being edited
     */
    @Override
    public void setValue(Object value) {
        if (getNewValue() != value)
            setNewValue(value);
        if (getValue() != value)
            super.setValue(value);
        initEditorValue();
    }
    
    private boolean updating = false;

    @Override
    public void firePropertyChange() {
        if (updating) return;
        updating = true;
        super.firePropertyChange();
        updating = false;
    }

    /**
     * This is called whenever an external class changes the value of this editor.
     * It is intended to be overridden by subclasses and to propagate changes to
     * any custom component.
     */
    protected abstract void initEditorValue();

    /** 
     * Called to indicate that the editor is finished editing.
     */
    protected void cancelEditAction() {
        if (newValue != getValue()) {
            newValue = getValue();
            initEditorValue();
            firePropertyChange();
        }
    }

    /** 
     * Called to indicate that the editor is finished editing.
     */
    protected void stopEditAction() {
        setValue(newValue);
    }
}
