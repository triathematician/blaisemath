package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
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

import com.googlecode.blaisemath.geom.AffineTransformBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Encapsulates an image with a target (clipped) render region. May be treated as a shape, using the
 * clip boundaries.
 * @author Elisha Peterson
 */
public class ClippedImage implements Shape {
    
    private Shape shape = new Rectangle();
    private Image image;
    //base64 encoded data from the image (optional)
    private String base64 = null;

    public ClippedImage() {
    }

    public ClippedImage(URL resource, Shape shape) throws IOException {
        this.image = ImageIO.read(resource);
        this.shape = shape;
    }

    public ClippedImage(Image image, Shape shape, @Nullable String base64) {
        this.image = image;
        this.shape = shape;
        this.base64 = base64;
    }
    
    //region PROPERTIES

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Nullable
    public String getBase64() {
        return base64;
    }

    public void setBase64(@Nullable String base64) {
        this.base64 = base64;
    }
    
    //endregion

    //region DELEGATES
    
    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return shape.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return shape.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return shape.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return shape.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return shape.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return shape.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at, flatness);
    }
    
    //endregion

    /**
     * Compute transform that can be used to render image inside shape.
     * @return transform
     */
    public AffineTransform imageTransform() {
        Rectangle2D src = new Rectangle2D.Double(0, 0, image.getWidth(null), image.getHeight(null));
        Rectangle2D tgt = shape.getBounds2D();
        return AffineTransformBuilder.transformingTo(tgt, src);
    }

}
