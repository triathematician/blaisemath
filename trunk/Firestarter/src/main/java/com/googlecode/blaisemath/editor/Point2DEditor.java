/**
 * Point2DEditor.java
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

import java.awt.geom.Point2D;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   The <code>Point2DEditor</code> class represents a single point
 *   with event handling support.
 * </p>
 *
 * @author Elisha Peterson
 */
public class Point2DEditor extends MultiSpinnerSupport {

    public Point2DEditor() {
        super(2);
    }

    @Override
    public void initCustomizer() {
        super.initCustomizer();
        spinners[0].setToolTipText("x coordinate");
        spinners[1].setToolTipText("y coordinate");
    }

    //
    //
    // PROPERTYEDITOR METHODS
    //
    //
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return (value != null) ? ("new java.awt.geom.Point2D.Double(" + getAsText() + ")") : "null";
    }

    @Override
    public void setAsText(String... s) throws IllegalArgumentException {
        try {
            double x = Double.parseDouble(s[0]);
            double y = Double.parseDouble(s[1]);
            setValue(new Point2D.Double(x, y));
        } catch (Exception ex) {
            throw new IllegalArgumentException(s.toString());
        }
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), -Double.MAX_VALUE, Double.MAX_VALUE, .01));
            spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), -Double.MAX_VALUE, Double.MAX_VALUE, .01));
        }
    }


    //
    // ADDITIONAL METHODS
    //
    
    @Override
    public Object getValue(Object bean, int i) {
        if (bean == null)
            return 0.0;
        switch (i) {
            case 0:
                return ((Point2D.Double) bean).x;
            case 1:
                return ((Point2D.Double) bean).y;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    void setNewValue(Object... values) {
        setNewValue(new Point2D.Double((Double) values[0], (Double) values[1]));
    }
}
