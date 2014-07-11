/**
 * NumberEditor.java
 * Created on Jun 30, 2009
 */
package com.googlecode.blaisemath.firestarter.editor;

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

import java.awt.Component;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   <code>NumberEditor</code> contains property editors for the standard number types.
 *   The default editor component is a spinner.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class NumberEditor extends MPropertyEditorSupport {

    JSpinner spinner;

    public NumberEditor() {
        spinner = new JSpinner();
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setNewValue(spinner.getValue());
            }
        });
    }

    @Override
    public boolean supportsCustomEditor() {
        return spinner != null;
    }

    @Override
    public Component getCustomEditor() {
        return spinner;
    }

    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        return (value != null) ? value.toString() : "null";
    }

    //
    //  INSTANCES
    //
    //
    public static class ByteEditor extends NumberEditor {

        public ByteEditor() {
            newValue = (byte) 0;
            initEditorValue();
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? "((byte)" + value + ")" : "null";
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Byte.decode(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number)newValue, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
        }
    }

    public static class ShortEditor extends NumberEditor {

        public ShortEditor() {
            newValue = (short) 0;
            initEditorValue();
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? "((short)" + value + ")" : "null";
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Short.decode(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number)newValue, Short.MIN_VALUE, Short.MAX_VALUE, 1));
        }
    }

    public static class IntegerEditor extends NumberEditor {

        public IntegerEditor() {
            newValue = 0;
            initEditorValue();
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Integer.decode(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number) newValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        }
    }

    public static class LongEditor extends NumberEditor {

        public LongEditor() {
            newValue = 0l;
            initEditorValue();
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? value + "L" : "null";
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Long.decode(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number) newValue, Long.MIN_VALUE, Long.MAX_VALUE, 1));
        }
    }

    public static class FloatEditor extends NumberEditor {

        public FloatEditor() {
            newValue = 0f;
            initEditorValue();
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? value + "F" : "null";
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Float.valueOf(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number) newValue, -Float.MAX_VALUE, Float.MAX_VALUE, 0.1));
        }
    }

    public static class DoubleEditor extends NumberEditor {

        public DoubleEditor() {
            newValue = 0.0;
            initEditorValue();
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue((text == null) ? null : Double.valueOf(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel((Number) newValue, -Double.MAX_VALUE, Double.MAX_VALUE, 0.01));
        }
    }
}
