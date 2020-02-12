package com.googlecode.blaisemath.graphics.swing.render;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Renders text at an angle, by rotating the canvas around the text's anchor point.
 *
 * @author Elisha
 */
public class SlopedTextRenderer implements Renderer<AnchoredText, Graphics2D> {

    private double theta;

    public SlopedTextRenderer() {
        this(0);
    }

    public SlopedTextRenderer(double theta) {
        this.theta = theta;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    @Override
    public void render(AnchoredText primitive, AttributeSet style, Graphics2D canvas) {
        AffineTransform orig = canvas.getTransform();
        canvas.rotate(theta, primitive.getX(), primitive.getY());
        TextRenderer.getInstance().render(primitive, style, canvas);
        canvas.setTransform(orig);
    }

    public @Nullable Shape shape(AnchoredText primitive, AttributeSet style, Graphics2D gr) {
        Rectangle2D base = TextRenderer.getInstance().boundingBox(primitive, style, gr);
        return transform(primitive).createTransformedShape(base);
    }

    @Override
    public @Nullable Rectangle2D boundingBox(AnchoredText primitive, AttributeSet style, @Nullable Graphics2D gr) {
        Shape shape = shape(primitive, style, gr);
        return shape == null ? null : shape.getBounds2D();
    }

    @Override
    public boolean contains(Point2D point, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D gr) {
        Shape shape = shape(primitive, style, gr);
        return shape != null && shape.contains(point);
    }

    @Override
    public boolean intersects(Rectangle2D rect, AnchoredText primitive, AttributeSet style, @Nullable Graphics2D gr) {
        Shape shape = shape(primitive, style, gr);
        return shape != null && shape.intersects(rect);
    }

    private AffineTransform transform(Point2D pt) {
        AffineTransform at = new AffineTransform();
        at.translate(pt.getX(), pt.getY());
        at.rotate(theta);
        at.translate(-pt.getX(), -pt.getY());
        return at;
    }
}
