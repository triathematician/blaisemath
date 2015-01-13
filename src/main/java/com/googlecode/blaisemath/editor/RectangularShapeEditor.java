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

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   Edits a rectangle: x, y, width, height.
 * </p>
 *
 * @author Elisha Peterson
 */
public class RectangularShapeEditor extends MultiSpinnerSupport<Double> {

    public RectangularShapeEditor() {
        super(new Rectangle2D.Double(), "x0", "y0", "width", "height");
    }

    @Override
    public void setAsText(String... s) {
        double[] arr = Numbers.decodeAsDoubles(s);
        Object val = getValue();
        if (val instanceof RectangularShape) {
            ((RectangularShape)val).setFrame(arr[0], arr[1], arr[2], arr[3]);
        } else {
            setValue(new Rectangle2D.Double(arr[0], arr[1], arr[2], arr[3]));
        }
    }

    @Override
    protected void initSpinnerModels() {
        spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
        spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
        spinners[2].setModel(new SpinnerNumberModel((Number) getNewValue(2), 0.0, Double.MAX_VALUE, .1));
        spinners[3].setModel(new SpinnerNumberModel((Number) getNewValue(3), 0.0, Double.MAX_VALUE, .1));
    }

    @Override
    public Double getValue(Object bean, int i) {
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
    void setNewValueList(List<Double> values) {
        if (newValue instanceof RectangularShape) {
            ((RectangularShape)newValue).setFrame(values.get(0), values.get(1), values.get(2), values.get(3));
        } else {
            newValue = new Rectangle2D.Double(values.get(0), values.get(1), values.get(2), values.get(3));
        }
        firePropertyChange();
    }
}
