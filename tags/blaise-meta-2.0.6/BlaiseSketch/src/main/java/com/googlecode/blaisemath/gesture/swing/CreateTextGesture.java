/**
 * CreateTextGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture.swing;

import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Graphics2D;
import javax.swing.JOptionPane;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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
public class CreateTextGesture extends JGraphicCreatorGesture {
    
    private String text;
    
    public CreateTextGesture(GestureOrchestrator orchestrator) {
        super(orchestrator, "Place text", "Click where you want to place text.");
    }

    @Override
    public void initiate() {
        text = JOptionPane.showInputDialog("Enter text:");
        if (Strings.isNullOrEmpty(text)) {
            finish();
        }
    }

    @Override
    protected Graphic<Graphics2D> createGraphic() {
        return locPoint == null ? null
                : JGraphics.text(new AnchoredText(locPoint, text), Styles.DEFAULT_TEXT_STYLE.copy());
    }
    
}
