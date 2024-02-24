package com.googlecode.blaisemath.graphics.swing.render;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.graphics.Renderer;
import com.googlecode.blaisemath.primitive.AnchoredText;
import com.googlecode.blaisemath.style.Styles;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Renders text in one or more lines along a given arc.
 *
 * @author Elisha Peterson
 */
public class MultiArcStringRenderer implements Renderer<AnchoredText, Graphics2D> {

    private final double arcMinR;
    private final double arcMaxR;
    private final double arcStart;
    private final double arcExtent;

    public MultiArcStringRenderer(double arcMinR, double arcMaxR, double arcStart, double arcExtent) {
        this.arcMinR = arcMinR;
        this.arcMaxR = arcMaxR;
        this.arcStart = arcStart;
        this.arcExtent = arcExtent;
    }

    @Override
    public void render(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        canvas.setFont(Styles.fontOf(style));
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        FontMetrics fm = canvas.getFontMetrics();
        double avgR = .5 * (arcMinR + arcMaxR);
        WrappedTextRenderer rend = new WrappedTextRenderer();
        List<String> lines = rend.computeLineBreaks(primitive.getText(), canvas.getFont(),
                arcExtent * arcMinR * Math.PI / 180, arcMaxR - arcMinR);
        double lineR = avgR - .5 * fm.getHeight() + .5 * lines.size() * fm.getHeight() - fm.getDescent();
        for (String line : lines) {
            double newExtent = Math.min(arcExtent, fm.getStringBounds(line, canvas).getWidth() / lineR * 180 / Math.PI);
            double mid = arcStart + .5 * arcExtent;
            Arc2D.Double arc = new Arc2D.Double(
                    primitive.getX() - lineR, primitive.getY() - lineR, 2 * lineR, 2 * lineR,
                    mid + .5 * newExtent, -newExtent, Arc2D.OPEN);
            new TextPathRenderer().pathText(line)
                    .textStyle(style)
                    .render(arc, style, canvas);
            lineR -= fm.getHeight();
        }
    }

    @Override
    public boolean contains(Point2D point, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        // not supported yet
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D rect, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        // not supported yet
        return false;
    }

    @Override
    public Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style, @Nullable Graphics2D canvas) {
        // not supported yet
        return null;
    }

}
