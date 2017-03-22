/**
 * ImageRenderer.java
 * Created on Sep 12, 2014
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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

import com.googlecode.blaisemath.style.Anchor;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.AnchoredImage;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Renderer for drawing images on a canvas.
 * 
 * @author petereb1
 */
public class ImageRenderer implements Renderer<AnchoredImage, Graphics2D> {

    private static final ImageRenderer INST = new ImageRenderer();
    
    public static Renderer<AnchoredImage, Graphics2D> getInstance() {
        return INST;
    }

    @Override
    public void render(AnchoredImage primitive, AttributeSet style, Graphics2D canvas) {
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        Point2D offset = anchor.getRectOffset(primitive.getWidth(), primitive.getHeight());
        canvas.drawImage(primitive.getImage(), 
                (int) (primitive.getX() + offset.getX()), 
                (int) (primitive.getY() - primitive.getHeight() + offset.getY()), 
                null);
    }

    @Override
    public Rectangle2D boundingBox(AnchoredImage primitive, AttributeSet style) {
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        return anchor.anchoredRectangle(primitive.getX(),
                primitive.getY() - primitive.getHeight(), 
                primitive.getWidth(), primitive.getHeight());
    }

    @Override
    public boolean contains(AnchoredImage primitive, AttributeSet style, Point2D point) {
        return boundingBox(primitive, style).contains(point);
    }

    @Override
    public boolean intersects(AnchoredImage primitive, AttributeSet style, Rectangle2D rect) {
        return boundingBox(primitive, style).intersects(rect);
    }

}
