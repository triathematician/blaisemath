/**
 * RectangleEditor.java
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

import java.awt.Rectangle;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 *   The <code>PointEditor</code> edits a rectangle: x, y, width, height.
 * </p>
 *
 * @author Elisha Peterson
 */
public class RectangleEditor extends MultiSpinnerSupport {

    public RectangleEditor() {
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

    //
    //
    // PROPERTYEDITOR METHODS
    //
    //
    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return (value != null) ? ("new java.awt.Rectangle(" + getAsText() + ")") : "null";
    }

    @Override
    public void setAsText(String... s) throws IllegalArgumentException {
        try {
            int x = Integer.decode(s[0]);
            int y = Integer.decode(s[1]);
            int w = Integer.decode(s[2]);
            int h = Integer.decode(s[3]);
            setValue(new Rectangle(x, y, w, h));
        } catch (Exception ex) {
            throw new IllegalArgumentException(s.toString());
        }
    }

    @Override
    protected void initEditorValue() {
        if (panel != null) {
            spinners[0].setModel(new SpinnerNumberModel((Number) getNewValue(0), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
            spinners[1].setModel(new SpinnerNumberModel((Number) getNewValue(1), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
            // permit negative width/height rectangles
            spinners[2].setModel(new SpinnerNumberModel((Number) getNewValue(2), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
            spinners[3].setModel(new SpinnerNumberModel((Number) getNewValue(3), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
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
                return ((Rectangle) bean).x;
            case 1:
                return ((Rectangle) bean).y;
            case 2:
                return ((Rectangle) bean).width;
            case 3:
                return ((Rectangle) bean).height;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    void setNewValue(Object... values) {
        setNewValue(new Rectangle((Integer) values[0], (Integer) values[1], (Integer) values[2], (Integer) values[3]));
    }
}
