package com.googlecode.blaisemath.primitive;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** 
 * Marker defined by an image and clip path.
 * @author Elisha Peterson
 */
public class ImageMarker implements Marker {

    private final String name;
    private final Shape clip;
    private final Image image;

    public ImageMarker(@Nullable String name, Shape shape, Image image) {
        this.name = name;
        this.clip = shape;
        this.image = image;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public Shape getClip() {
        return clip;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public Shape create(Point2D point, double orientation, float r) {
        Rectangle2D tgt = new Rectangle2D.Double(point.getX() - r, point.getY() - r, 2*r, 2*r);
        Rectangle2D src = clip.getBounds2D();
        AffineTransform at = AffineTransformBuilder.transformingTo(tgt, src);
        return new ClippedImage(image, at.createTransformedShape(clip), null);
    }

}
