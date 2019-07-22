package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * Edits a single Point2D with event handling support.
 *
 * @author Elisha Peterson
 */
public final class Point2DEditor extends MultiSpinnerSupport<Double> {

    public Point2DEditor() {
        super(new Point2D.Double(), "x", "y");
    }
    
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return value != null ? "new java.awt.geom.Point2D.Double(" + 
                getAsText() + ")" : "null";
    }

    @Override
    public void setAsText(String... s) {
        double[] arr = Numbers.decodeAsDoubles(s);
        setValue(new Point2D.Double(arr[0], arr[1]));
    }

    @Override
    protected void initSpinnerModels() {
        spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), -Double.MAX_VALUE, Double.MAX_VALUE, .01));
        spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), -Double.MAX_VALUE, Double.MAX_VALUE, .01));
    }
    
    @Override
    public Double getValue(Object bean, int i) {
        if (bean == null) {
            return 0.0;
        }
        switch (i) {
            case 0:
                return ((Point2D) bean).getX();
            case 1:
                return ((Point2D) bean).getY();
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    void setNewValueList(List<Double> values) {
        setNewValue(new Point2D.Double(values.get(0), values.get(1)));
    }
}
