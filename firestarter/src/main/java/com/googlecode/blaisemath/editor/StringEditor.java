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

import java.awt.Component;
import java.util.Arrays;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Editor for strings.
 *
 * @author Elisha Peterson
 */
public class StringEditor extends MPropertyEditorSupport {
    
    private static final Character[] SPECIAL_CHARS 
            = { '\b', '\t', '\n', '\f', '\r', '\"', '\\' };
    private static final String[] SPECIAL_CHARS_REPLACE
            = { "\\b", "\\t", "\\n", "\\f", "\\r", "\\\"", "\\\\" };

    private JTextField field;

    private boolean updating = false;

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        if (field == null) {
            field = new JTextField();
            initEditorValue();
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setNewAsText(field.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setNewAsText(field.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    setNewAsText(field.getText());
                }
            });
        }

        return field;
    }

    @Override
    protected void initEditorValue() {
        if (field != null && !updating) {
            field.setText(getAsText());
        }
    }

    @Override
    public String getJavaInitializationString() {
        Object value = getValue();
        if (value == null) {
            return "null";
        }

        String str = value.toString();
        int length = str.length();
        StringBuilder sb = new StringBuilder(length + 2);
        sb.append('"');
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            int iSpecial = Arrays.asList(SPECIAL_CHARS).indexOf(ch);
            if (iSpecial != -1) {
                sb.append(SPECIAL_CHARS_REPLACE[iSpecial]);
            } else if ((ch < ' ') || (ch > '~')) {
                sb.append("\\u");
                String hex = Integer.toHexString(ch);
                for (int len = hex.length(); len < 4; len++) {
                    sb.append('0');
                }
                sb.append(hex);
            } else {
                sb.append(ch);
            }
        }
        sb.append('"');
        return sb.toString();
    }

    @Override
    public void setAsText(String text) {
        setValue(text);
    }

    private void setNewAsText(String text) {
        updating = true;
        setNewValue(text);
        updating = false;
    }
}
