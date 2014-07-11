/**
 * StringEditor.java
 * Created on Jul 1, 2009
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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * <p>
 *   <code>StringEditor</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class StringEditor extends MPropertyEditorSupport {

    JTextField field;

    public StringEditor() { }

    @Override
    public boolean supportsCustomEditor() { return true; }

    @Override
    public Component getCustomEditor() {
        if (field == null) {
            field = new JTextField();
            initEditorValue();
            field.getDocument().addDocumentListener(new DocumentListener(){
                public void insertUpdate(DocumentEvent e) { setNewAsText(field.getText()); }
                public void removeUpdate(DocumentEvent e) { setNewAsText(field.getText()); }
                public void changedUpdate(DocumentEvent e) { setNewAsText(field.getText()); }
            });
        }

        return field;
    }

    private boolean updating = false;

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
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if ((ch < ' ') || (ch > '~')) {
                        sb.append("\\u");
                        String hex = Integer.toHexString((int) ch);
                        for (int len = hex.length(); len < 4; len++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        sb.append('"');
        return sb.toString();
    }

    @Override
    public void setAsText(String text) {
        setValue(text);
    }

    private synchronized void setNewAsText(String text) {
        updating = true;
        setNewValue(text);
        updating = false;
    }
}
