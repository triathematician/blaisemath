/**
 * CreateTextGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture;

import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.graphics.swing.TransformedCanvasPainter;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Graphics;
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
public class CreateTextGesture extends SketchGesture<JGraphicComponent> {
    
    protected static final AttributeSet DRAW_STYLE = Styles.DEFAULT_TEXT_STYLE;
    private static final Renderer<AnchoredText, Graphics2D> REND = TextRenderer.getInstance();
    
    public CreateTextGesture() {
        super("Place text", "Click where you want to place text.");
    }

    @Override
    public void finish(JGraphicComponent view) {
        if (locPoint != null) {
            String s = JOptionPane.showInputDialog("Enter text:");
            if (!Strings.isNullOrEmpty(s)) {
                PrimitiveGraphic<AnchoredText, Graphics2D> gfc = JGraphics.text(new AnchoredText(locPoint, s), DRAW_STYLE);
                gfc.setSelectionEnabled(true);
                gfc.setDragEnabled(true);
                view.addGraphic(gfc);
            }
        }
    }

    @Override
    public void paint(Graphics g, JGraphicComponent view) {
        new Painter().paint(view, (Graphics2D) g);
    }
    
    private final class Painter extends TransformedCanvasPainter {
        @Override
        public void paintTransformed(JGraphicComponent jgc, Graphics2D gd) {
            if (locPoint != null) {
                REND.render(new AnchoredText(locPoint, "text here"), DRAW_STYLE, (Graphics2D) gd);
            }
        }
    }
    
}
