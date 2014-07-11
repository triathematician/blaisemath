/**
 * MPanelEditorSupport.java
 * Created on Jul 2, 2009
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * <p>
 *   <code>MPanelEditorSupport</code> is a generic super class for custom editors that use a panel
 *   for layout and display.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class MPanelEditorSupport extends MPropertyEditorSupport {
    JPanel panel;

    public MPanelEditorSupport() {
    }

    /** Initializes the customized component... this is not called initially in
     * case subclasses do not need the custom panel.
     */
    protected abstract void initCustomizer();

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        if (panel == null) {
            initCustomizer();
            initEditorValue();
        }
        panel.addPropertyChangeListener("enabled", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                for (int i = 0; i < panel.getComponentCount(); i++) {
                    panel.getComponent(i).setEnabled(panel.isEnabled());
                }
            }
        });
        return panel;
    }
}
