/**
 * CreateTextGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture.graphics;

import com.googlecode.blaisemath.gesture.GestureOrchestrator;
import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.MultilineTextRenderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.swing.BSwingUtilities;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2015 - 2016 Elisha Peterson
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

/**
 * Gesture for adding text to the canvas.
 * 
 * @author Elisha
 */
public class CreateTextGesture extends CreateGraphicGesture {

    private static final Logger LOG = Logger.getLogger(CreateTextGesture.class.getName());
    
    private String text;
    
    public CreateTextGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Place text", "Click where you want to place text.");
    }

    @Override
    public boolean activate() {
        if (active) {
            LOG.log(Level.WARNING, "Attempted to activate gesture twice.");
        }
        JTextArea textArea = new JTextArea();
        BSwingUtilities.requestFocusWhenShown(textArea);
        if (OK_OPTION == JOptionPane.showConfirmDialog(null, new JScrollPane(textArea),
                "Enter text", OK_CANCEL_OPTION)) {
            text = textArea.getText();
            active = !Strings.isNullOrEmpty(text);
        }
        return active;
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        return locPoint == null ? null
                : new PrimitiveGraphic<>(
                        new AnchoredText(locPoint, text), 
                        Styles.DEFAULT_TEXT_STYLE.copy(), 
                        MultilineTextRenderer.getInstance());
    }
    
}
