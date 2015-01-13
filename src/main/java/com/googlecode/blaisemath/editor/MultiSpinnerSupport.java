/**
 * MultiSpinnerSupport.java
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

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   Base class for editors that use multiple coordinates.
 * </p>
 *
 * @param <N> numeric type for the spinners
 * 
 * @author Elisha Peterson
 */
public abstract class MultiSpinnerSupport<N extends Number> extends MPanelEditorSupport {

    protected final JSpinner[] spinners;
    private final String[] tips;
    
    public MultiSpinnerSupport(Object iVal, String... tips) {
        newValue = iVal;
        spinners = new JSpinner[tips.length];
        this.tips = tips;
    }

    @Override
    public void initCustomizer() {
        final int n = spinners.length;
        
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, n));

        for (int i = 0; i < n; i++) {
            spinners[i] = new JSpinner();
            spinners[i].setToolTipText(tips[i]);
            spinners[i].setBorder(null);
            panel.add(spinners[i]);
        }

        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                List<N> vals = new ArrayList<N>();
                for (int i = 0; i < n; i++) {
                    vals.add((N) spinners[i].getValue());
                }
                setNewValueList(vals);
            }
        };

        for (int i = 0; i < n; i++) {
            spinners[i].addChangeListener(cl);
        }

        panel.revalidate();
        panel.repaint();
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            initSpinnerModels();
        }
    }
    
    /** Used by subclass to initialize its spinner models */
    protected abstract void initSpinnerModels();
    
    @Override
    public String getAsText() {
        String result = getValue(0).toString();
        for (int i = 1; i < spinners.length; i++) {
            result += "," + getValue(i);
        }
        return result;
    }

    /** 
     * Use a comma-delimited technique for setting as text.
     * @param s 
     */
    @Override
    public void setAsText(String s) {
        String[] splits = s.split(",");
        if (splits.length != spinners.length) {
            throw new IllegalArgumentException();
        }
        setAsText(splits);
    }

    public abstract void setAsText(String... strings);

    public N getValue(int i) {
        return getValue(getValue(), i);
    }
    
    public N getNewValue(int i) {
        return getValue(newValue, i);
    }

    /**
     * Retrieve the indexed value for the given object.
     * @param bean the bean under consideration
     * @param i the position of the desired property
     * @return property at the given position of the bean. */
    protected abstract N getValue(Object bean, int i);

    /**
     * Set the object with a list of values.
     * @param values the values
     */
    abstract void setNewValueList(List<N> values);
}
