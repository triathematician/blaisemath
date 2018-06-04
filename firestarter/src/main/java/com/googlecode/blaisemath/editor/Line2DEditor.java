/**
 * RectangleEditor.java
 * Created on Jul 2, 2009
 */
package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import java.awt.geom.Line2D;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   Edits a rectangle: x, y, width, height.
 * </p>
 *
 * @author Elisha Peterson
 */
public class Line2DEditor extends MultiSpinnerSupport<Double> {

    public Line2DEditor() {
        super(new Line2D.Double(), "x1", "y1", "x2", "y2");
    }
    
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return value != null ? "new java.awt.geom.Line2D.Double(" + getAsText() + ")" : "null";
    }

    @Override
    public void setAsText(String... s) {
        double[] arr = Numbers.decodeAsDoubles(s);
        setValue(new Line2D.Double(arr[0], arr[1], arr[2], arr[3]));
    }

    @Override
    protected void initSpinnerModels() {
        spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
        spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
        // permit negative width/height rectangles
        spinners[2].setModel(new SpinnerNumberModel((Number) getNewValue(2), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
        spinners[3].setModel(new SpinnerNumberModel((Number) getNewValue(3), -Double.MAX_VALUE, Double.MAX_VALUE, .1));
    }
    
    @Override
    public Double getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((Line2D) bean).getX1();
            case 1:
                return ((Line2D) bean).getY1();
            case 2:
                return ((Line2D) bean).getX2();
            case 3:
                return ((Line2D) bean).getY2();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    void setNewValueList(List<Double> values) {
        setNewValue(new Line2D.Double(values.get(0), values.get(1), values.get(2), values.get(3)));
    }
}
