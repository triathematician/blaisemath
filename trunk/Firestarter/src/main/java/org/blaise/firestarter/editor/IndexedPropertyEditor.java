/**
 * IndexedPropertyEditor.java
 * Created on Jul 3, 2009
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

import java.beans.PropertyEditor;
import java.lang.reflect.Array;

/**
 * <p>
 *   <code>IndexedPropertyEditor</code> allows for string-based editing of indexed
 *   properties. This requires that the underlying data type supports the
 *   <code>getAsText()</code> and <code>setAsText()</code> methods, <i>without using commas</i>.
 * </p>
 *
 * @author Elisha Peterson
 */
public class IndexedPropertyEditor extends MPropertyEditorSupport {

    /** The editor that handles individual components of the array. */
    PropertyEditor baseEditor;
    /** For use by the class setting and getting text. */
    String[] splits;

    public IndexedPropertyEditor() {
        setValue(new Object[0]);
    }

    /** @return component type of the underlying array. */
    public Class getComponentType() {
        Object[] array = (Object[]) getValue();
        return array.getClass().getComponentType();
    }

    @Override
    protected void initEditorValue() {
        Object[] array = (Object[]) getValue();
        baseEditor = EditorRegistration.getRegisteredEditor(getComponentType());
        if (array.length > 0) {
            baseEditor.setValue(array[0]);
        }
    }

    @Override
    public String getAsText() {
        Object[] array = (Object[]) getValue();
        String result = "";
        for (int i = 0; i < array.length; i++) {
            baseEditor.setValue(array[i]);
            result += (i == 0 ? "" : ", ") + baseEditor.getAsText();
        }
        return result;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        splits = text.split(",");
        if (splits.length != splits.length) {
            throw new IllegalArgumentException();
        }
        setAsText(splits);
    }

    private void setAsText(String... splits) {
        Object[] result = (Object[]) Array.newInstance(getComponentType(), splits.length);
        for (int i = 0; i < result.length; i++) {
            baseEditor.setAsText(splits[i]);
            result[i] = baseEditor.getValue();
        }
        setValue(result);
    }
}
