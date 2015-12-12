/**
 * NumberEditor.java
 * Created on Jun 30, 2009
 */
package com.googlecode.blaisemath.editor;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   Components for editing numbers, using spinners.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class NumberEditor extends MPropertyEditorSupport {
    
    private static final int MAX_SPINNER_WIDTH = 100;

    protected final JSpinner spinner;

    public NumberEditor() {
        spinner = new JSpinner();
        spinner.addChangeListener(new ChangeListener() {
            @Override
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
    
    public static final class ByteEditor extends NumberEditor {

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
        public void setAsText(String text) {
            setValue((text == null) ? null : Byte.decode(text));
        }

        @Override
        protected void initEditorValue() {
            byte ival = ((Number) newValue).byteValue();
            // cast to Number required for proper spinner setup
            spinner.setModel(new SpinnerNumberModel((Number) ival, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
        }
    }

    public static final class ShortEditor extends NumberEditor {

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
        public void setAsText(String text) {
            setValue((text == null) ? null : Short.decode(text));
        }

        @Override
        protected void initEditorValue() {
            short val = ((Number)newValue).shortValue();
            // cast to Number required for proper spinner setup
            spinner.setModel(new SpinnerNumberModel((Number) val, Short.MIN_VALUE, Short.MAX_VALUE, 1));
        }
    }

    public static final class IntegerEditor extends NumberEditor {

        public IntegerEditor() {
            newValue = 0;
            initEditorValue();
        }

        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Integer.decode(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel(((Number) newValue).intValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
        }
    }

    public static final class LongEditor extends NumberEditor {

        public LongEditor() {
            newValue = 0L;
            initEditorValue();
        }

        @Override
        public String getJavaInitializationString() {
            Object value = getValue();
            return (value != null) ? value + "L" : "null";
        }

        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Long.decode(text));
        }

        @Override
        protected void initEditorValue() {
            long val = ((Number)newValue).longValue();
            // cast to Number required for proper spinner setup
            spinner.setModel(new SpinnerNumberModel((Number) val, Long.MIN_VALUE, Long.MAX_VALUE, 1));
            Dimension pref = spinner.getPreferredSize();
            spinner.setPreferredSize(new Dimension(
                    Math.min(MAX_SPINNER_WIDTH, pref.width), pref.height));
        }
    }

    public static final class FloatEditor extends NumberEditor {

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
        public void setAsText(String text) {
            setValue((text == null) ? null : Float.valueOf(text));
        }

        @Override
        protected void initEditorValue() {
            Float ival = ((Number) newValue).floatValue();
            // cast to Number required for proper spinner setup
            spinner.setModel(new SpinnerNumberModel((Number) ival, -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f));
            spinner.setPreferredSize(new Dimension(MAX_SPINNER_WIDTH, spinner.getPreferredSize().height));
        }
    }

    public static final class DoubleEditor extends NumberEditor {

        public DoubleEditor() {
            newValue = 0.0;
            initEditorValue();
        }

        @Override
        public void setAsText(String text) {
            setValue((text == null) ? null : Double.valueOf(text));
        }

        @Override
        protected void initEditorValue() {
            spinner.setModel(new SpinnerNumberModel(((Number) newValue).doubleValue(), -Double.MAX_VALUE, Double.MAX_VALUE, 0.01));
            spinner.setPreferredSize(new Dimension(MAX_SPINNER_WIDTH, spinner.getPreferredSize().height));
        }
    }
}
