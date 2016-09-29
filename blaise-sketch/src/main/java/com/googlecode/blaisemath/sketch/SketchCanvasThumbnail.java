/**
 * SketchCanvasThumbnail.java
 * Created May 1, 2016
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * blaise-sketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

/**
 * Displays a thumbnail version of a {@link SketchCanvas}.
 * @author elisha
 */
public class SketchCanvasThumbnail extends JComponent {
    
    private final SketchCanvas canvas;

    public SketchCanvasThumbnail(SketchCanvas canvas) {
        this.canvas = canvas;
        setMinimumSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(150, 150));
        canvas.addPropertyChangeListener(evt -> repaint());
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        Rectangle2D canvasBounds = canvas.getCanvasModel().getBoundingBox();
        AffineTransform at = PanAndZoomHandler.scaleRectTransform(getBounds(), 
                new Rectangle2D.Double(canvasBounds.getMinX()-10, canvasBounds.getMinY()-10,
                canvasBounds.getWidth()+20, canvasBounds.getHeight()+20));
        g2.transform(at);
        canvas.getCanvasModel().paintCanvas(g2, null);
        canvas.getGraphicRoot().renderTo(g2);
        
        Rectangle2D visibleBounds = canvas.getVisibleBounds();
        AttributeSet style = AttributeSet.of(
                Styles.FILL, new Color(128, 128, 255),
                Styles.STROKE, new Color(0, 0, 255),
                Styles.STROKE_WIDTH, 2)
                .and(Styles.OPACITY, .5);
        ShapeRenderer.getInstance().render(visibleBounds, style, g2);
    }
    
}
