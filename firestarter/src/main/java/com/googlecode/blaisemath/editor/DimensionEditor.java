/**
 * DimensionEditor.java
 * Created on Jun 18, 2009
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

import java.awt.Dimension;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   Edits a {@link Dimension} using 2 spinners.
 * </p>
 *
 * @author Elisha Peterson
 */
public class DimensionEditor extends MultiSpinnerSupport<Integer> {

    public DimensionEditor() {
        super(new Dimension(), "width", "height");
    }
    
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return value != null ? "new java.awt.Dimension(" + getAsText() + ")" : "null";
    }

    @Override
    public void setAsText(String... s) {
        int[] arr = Numbers.decodeAsIntegers(s);
        setValue(new Dimension(arr[0], arr[1]));
    }

    @Override
    protected void initSpinnerModels() {
        spinners[0].setModel(new SpinnerNumberModel(Math.max(0, getNewValue(0)), 0, Integer.MAX_VALUE, 1));
        spinners[1].setModel(new SpinnerNumberModel(Math.max(0, getNewValue(1)), 0, Integer.MAX_VALUE, 1));
    }
    
    @Override
    public Integer getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((Dimension) bean).width;
            case 1:
                return ((Dimension) bean).height;
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override
    void setNewValueList(List<Integer> values) {
        setNewValue(new Dimension(values.get(0), values.get(1)));
    }
}
