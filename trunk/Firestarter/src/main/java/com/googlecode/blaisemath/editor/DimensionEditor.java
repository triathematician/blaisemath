/**
 * DimensionEditor.java
 * Created on Jun 18, 2009
 */
package com.googlecode.blaisemath.editor;

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

import java.awt.Dimension;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   The <code>DimensionEditor</code> handles editing of a dimension, using
 *   2 spinners.
 * </p>
 *
 * @author Elisha Peterson
 */
public class DimensionEditor extends MultiSpinnerSupport {

    public DimensionEditor() {
        super(2);
        setNewValue(0,0);
    }

    @Override
    public void initCustomizer() {
        super.initCustomizer();
        spinners[0].setToolTipText("width");
        spinners[1].setToolTipText("height");
    }

    //
    //
    // PROPERTYEDITOR METHODS
    //
    //
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return (value != null) ? ("new java.awt.Dimension(" + getAsText() + ")") : "null";
    }

    @Override
    public void setAsText(String... s) throws IllegalArgumentException {
        try {
            int x = Integer.decode(s[0]);
            int y = Integer.decode(s[1]);
            setValue(new Dimension(x, y));
        } catch (Exception ex) {
            throw new IllegalArgumentException(s.toString());
        }
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            spinners[0].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(0)), 0, Integer.MAX_VALUE, 1));
            spinners[1].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(1)), 0, Integer.MAX_VALUE, 1));
        }
    }

    //
    //
    // ADDITIONAL METHODS
    //
    //
    public Object getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((Dimension) bean).width;
            case 1:
                return ((Dimension) bean).height;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    void setNewValue(Object... values) {
        setNewValue(new Dimension((Integer) values[0], (Integer) values[1]));
    }
}
