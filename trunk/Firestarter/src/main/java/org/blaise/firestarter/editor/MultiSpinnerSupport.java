/**
 * MultiSpinnerSupport.java
 * Created on Jul 2, 2009
 */
package org.blaise.firestarter.editor;

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

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   <code>MultiSpinnerSupport</code> serves as a base class for editors that work
 *   with multiple coordinates.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class MultiSpinnerSupport extends MPanelEditorSupport {

    JSpinner[] spinners;

    public MultiSpinnerSupport(final int n) {
        spinners = new JSpinner[n];
    }

    public void initCustomizer() {
        final int n = spinners.length;
        
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, n));

        for (int i = 0; i < n; i++) {
            spinners[i] = new JSpinner();
            spinners[i].setBorder(null);
            panel.add(spinners[i]);
        }

        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Object[] values = new Object[n];
                for (int i = 0; i < n; i++) {
                    values[i] = spinners[i].getValue();
                }
                setNewValue(values);
            }
        };

        for (int i = 0; i < n; i++) {
            spinners[i].addChangeListener(cl);
        }

        panel.revalidate();
        panel.repaint();
    }

    @Override
    public String getAsText() {
        String result = getValue(0).toString();
        for (int i = 1; i < spinners.length; i++) {
            result += "," + getValue(i);
        }
        return result;
    }

    /** Use a comma-delimited technique for setting as text. */
    @Override
    public void setAsText(String s) throws IllegalArgumentException {
        String[] splits = s.split(",");
        if (splits.length != spinners.length) {
            throw new IllegalArgumentException();
        }
        setAsText(splits);
    }

    public abstract void setAsText(String... strings) throws IllegalArgumentException;

    public Object getValue(int i) {
        return getValue(getValue(), i);
    }
    
    public Object getNewValue(int i) {
        return getValue(newValue, i);
    }

    /**
     * @param bean the bean under consideration
     * @param i the position of the desired property
     * @return property at the given position of the bean. */
    protected abstract Object getValue(Object bean, int i);

    abstract void setNewValue(Object... values);
}
