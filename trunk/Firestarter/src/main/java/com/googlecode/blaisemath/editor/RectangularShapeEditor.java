/**
 * RectangleEditor.java
 * Created on Jul 2, 2009
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

import java.awt.geom.RectangularShape;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   Edits a rectangle: x, y, width, height.
 * </p>
 *
 * @author Elisha Peterson
 */
public class RectangularShapeEditor extends MultiSpinnerSupport {

    public RectangularShapeEditor() {
        super(4);
        setNewValue(0,0,0,0);
    }

    @Override
    public void initCustomizer() {
        super.initCustomizer();
        spinners[0].setToolTipText("x coordinate");
        spinners[1].setToolTipText("y coordinate");
        spinners[2].setToolTipText("width");
        spinners[3].setToolTipText("height");
    }

    @Override
    public void setAsText(String... s) {
        try {
            double x = Double.parseDouble(s[0]);
            double y = Double.parseDouble(s[1]);
            double w = Double.parseDouble(s[2]);
            double h = Double.parseDouble(s[3]);
            setNewValue(x, y, w, h);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
            spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
            spinners[2].setModel(new SpinnerNumberModel((Number) getNewValue(2), 0.0, Double.MAX_VALUE, .1));
            spinners[3].setModel(new SpinnerNumberModel((Number) getNewValue(3), 0.0, Double.MAX_VALUE, .1));
        }
    }

    @Override
    public Object getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((RectangularShape) bean).getX();
            case 1:
                return ((RectangularShape) bean).getY();
            case 2:
                return ((RectangularShape) bean).getWidth();
            case 3:
                return ((RectangularShape) bean).getHeight();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    void setNewValue(Object... values) {
        ((RectangularShape) newValue).setFrame((Double) values[0], (Double) values[1], (Double) values[2], (Double) values[3]);
        firePropertyChange();
    }
}
