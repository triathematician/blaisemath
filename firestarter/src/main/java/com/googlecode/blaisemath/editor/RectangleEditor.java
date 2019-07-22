/**
 * RectangleEditor.java
 * Created on Jul 2, 2009
 */
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

import java.awt.Rectangle;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   Edits a rectangle: x, y, width, height.
 * </p>
 *
 * @author Elisha Peterson
 */
public class RectangleEditor extends MultiSpinnerSupport<Integer> {

    public RectangleEditor() {
        super(new Rectangle(), "x0", "y0", "width", "height");
    }
    
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return value != null ? "new java.awt.Rectangle(" + getAsText() + ")" : "null";
    }

    @Override
    public void setAsText(String... s) {
        int[] arr = Numbers.decodeAsIntegers(s);
        setValue(new Rectangle(arr[0], arr[1], arr[2], arr[3]));
    }

    @Override
    protected void initSpinnerModels() {
        spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        spinners[2].setModel(new SpinnerNumberModel((Number) getNewValue(2), 0, Integer.MAX_VALUE, 1));
        spinners[3].setModel(new SpinnerNumberModel((Number) getNewValue(3), 0, Integer.MAX_VALUE, 1));
    }
    
    @Override
    public Integer getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((Rectangle) bean).x;
            case 1:
                return ((Rectangle) bean).y;
            case 2:
                return ((Rectangle) bean).width;
            case 3:
                return ((Rectangle) bean).height;
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    void setNewValueList(List<Integer> values) {
        setNewValue(new Rectangle(values.get(0), values.get(1), values.get(2), values.get(3)));
    }
}
