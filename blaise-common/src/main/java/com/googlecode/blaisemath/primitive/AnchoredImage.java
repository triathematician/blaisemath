package com.googlecode.blaisemath.primitive;

/*
 * #%L
 * BlaiseGraphics
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

import com.googlecode.blaisemath.coordinate.Point2DBean;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An image anchored at a given location.
 * @author Elisha Peterson
 */
public final class AnchoredImage extends Point2DBean {

    private final Image originalImage;
    private final Image scaledImage;
    private final String ref;
    private final java.lang.Double width;
    private final java.lang.Double height;

    public AnchoredImage(double x, double y, Image image, @Nullable String ref) {
        this(x, y, null, null, image, ref);
    }

    public AnchoredImage(double x, double y, java.lang.@Nullable Double width, java.lang.@Nullable Double height, Image image, @Nullable String ref) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.ref = ref;
        this.originalImage = image;
        if (width != null && width > 0 && height != null && height > 0 && (image.getWidth(null) != width || image.getHeight(null) != height)) {
            this.scaledImage = image.getScaledInstance(width.intValue(), height.intValue(), Image.SCALE_DEFAULT);
        } else {
            this.scaledImage = image;
        }
    }

    @Override
    public String toString() {
        return "AnchoredImage{" + getX() + ',' + getY() + ',' + ref + '}';
    }

    //region PROPERTIES

    public String getReference() {
        return ref;
    }

    public double getWidth() {
        return width == null ? scaledImage.getWidth(null) : width;
    }

    public double getHeight() {
        return height == null ? scaledImage.getHeight(null) : height;
    }
    
    public Rectangle2D getBounds(ImageObserver io) {
        double iw = width == null ? scaledImage.getWidth(io) : width;
        double ih = height == null ? scaledImage.getHeight(io) : height;
        return new Rectangle2D.Double(x, y, iw, ih);
    }
    
    public Image getImage() {
        return scaledImage;
    }
    
    public Image getOriginalImage() {
        return originalImage;
    }
    
    //endregion
    
}
