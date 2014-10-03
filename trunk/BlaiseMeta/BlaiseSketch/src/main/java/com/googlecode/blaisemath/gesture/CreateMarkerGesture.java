/**
 * CreateMarkerGesture.java Created Oct 3, 2014
 */
package com.googlecode.blaisemath.gesture;

import com.googlecode.blaisemath.graphics.core.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.PointRenderer;
import com.googlecode.blaisemath.graphics.swing.TransformedCanvasPainter;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.StyleHints;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.OrientedPoint2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

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
 * Gesture for adding a point to the canvas.
 * 
 * @author Elisha
 */
public class CreateMarkerGesture extends SketchGesture<JGraphicComponent> {

    private static final Renderer<Point2D, Graphics2D> REND = PointRenderer.getInstance();
    private static final AttributeSet DRAW_STYLE = Styles.DEFAULT_POINT_STYLE;
    private Point2D movePoint = null;
    private Point2D locPoint = null;
    
    public CreateMarkerGesture() {
        super("Place point", "Click on the canvas to create a point.");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        movePoint = gc.toGraphicCoordinate(e.getPoint());
        gc.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        movePoint = null;
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        gc.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        JGraphicComponent gc = (JGraphicComponent) e.getSource();
        locPoint = gc.toGraphicCoordinate(e.getPoint());
        gc.repaint();
    }

    @Override
    public void finish(JGraphicComponent view) {
        if (locPoint != null) {
            PrimitiveGraphic<OrientedPoint2D, Graphics2D> gfc 
                    = JGraphics.marker(new OrientedPoint2D(locPoint), DRAW_STYLE.copy());
            view.addGraphic(gfc);
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
                REND.render(locPoint, DRAW_STYLE, (Graphics2D) gd);
            }
            if (movePoint != null) {
                AttributeSet hints = AttributeSet.with(StyleHints.HILITE_HINT, true);
                REND.render(movePoint, Styles.defaultColorModifier().apply(DRAW_STYLE, hints), gd);
            }
        }
    }
    
}
