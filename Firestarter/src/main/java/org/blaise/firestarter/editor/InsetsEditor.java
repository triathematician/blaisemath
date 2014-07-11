/**
 * InsetsEditor.java
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

import java.awt.Insets;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   The <code>InsetsEditor</code> edits insets, with 4 spinners.
 * </p>
 *
 * @author Elisha Peterson
 */
public class InsetsEditor extends MultiSpinnerSupport {

    public InsetsEditor() {
        super(4);
        setNewValue(0,0,0,0);
    }

    @Override
    public void initCustomizer() {
        super.initCustomizer();
        spinners[0].setToolTipText("top");
        spinners[1].setToolTipText("left");
        spinners[2].setToolTipText("bottom");
        spinners[3].setToolTipText("right");
    }

    //
    //
    // PROPERTYEDITOR METHODS
    //
    //
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return (value != null) ? ("new java.awt.Insets(" + getAsText() + ")") : "null";
    }

    @Override
    public void setAsText(String... s) throws IllegalArgumentException {
        try {
            int t = Integer.decode(s[0]);
            int l = Integer.decode(s[1]);
            int b = Integer.decode(s[2]);
            int r = Integer.decode(s[3]);
            setValue(new Insets(t, l, b, r));
        } catch (Exception ex) {
            throw new IllegalArgumentException(s.toString());
        }
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            spinners[0].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(0)), 0, Integer.MAX_VALUE, 1));
            spinners[1].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(1)), 0, Integer.MAX_VALUE, 1));
            // permit negative width/height rectangles
            spinners[2].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(2)), 0, Integer.MAX_VALUE, 1));
            spinners[3].setModel(new SpinnerNumberModel(Math.max(0, (Integer) getNewValue(3)), 0, Integer.MAX_VALUE, 1));
        }
    }


    //
    //
    // ADDITIONAL METHODS
    //
    //
    @Override
    public Object getValue(Object bean, int i) {
        switch (i) {
            case 0:
                return ((Insets) bean).top;
            case 1:
                return ((Insets) bean).left;
            case 2:
                return ((Insets) bean).bottom;
            case 3:
                return ((Insets) bean).right;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    void setNewValue(Object... values) {
        setNewValue(new Insets((Integer) values[0], (Integer) values[1], (Integer) values[2], (Integer) values[3]));
    }
}
