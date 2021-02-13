/**
 * SVGImage.java
 * Created Sep 26, 2014
 */

package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.Images;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *   SVG-compatible image, with size parameters and a reference URL.
 * </p>
 * @author elisha
 */
@XmlRootElement(name="image")
public final class SVGImage extends SVGElement {
    
    private static final Logger LOG = Logger.getLogger(SVGImage.class.getName());
    private static final ImageConverter CONVERTER_INST = new ImageConverter();
    
    private double x;
    private double y;
    private Double width;
    private Double height;
    private String imageRef = null;
    
    private Image image;

    public SVGImage() {
        this(0, 0, null, null, "");
    }
    
    public SVGImage(double x, double y, String ref) {
        this(x, y, null, null, ref);
    }

    public SVGImage(double x, double y, Double width, Double height, String ref) {
        super("image");
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imageRef = ref;
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    @XmlAttribute
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @XmlAttribute
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @XmlAttribute
    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    @XmlAttribute
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
    
    @XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        if (!Objects.equals(this.imageRef, imageRef)) {
            this.imageRef = imageRef;
            image = null;
        }
    }
    
    /**
     * Load (if necessary) and return the image corresponding to the image reference.
     * @return loaded image, or null if it couldn't be loaded
     */
    @Nullable
    public Image getImage() {
        if (image == null && !Strings.isNullOrEmpty(imageRef)) {
            loadImage();
        }
        return image;
    }
    
    private void loadImage() {
        BufferedImage img;
        try {
            img = imageRef.startsWith(Images.DATA_URI_PREFIX) 
                    ? Images.decodeDataUriBase64(imageRef)
                    : ImageIO.read(new URL(imageRef));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Failed to load image from "+imageRef, ex);
            return;
        }
        if (width == null || height == null) {
            image = img;
            width = (double) img.getWidth();
            height = (double) img.getHeight();
        } else if (width == img.getWidth() && height == img.getHeight()) {
            image = img;
        } else {
            int iw = width == null ? img.getWidth() : width.intValue();
            int ih = height == null ? img.getHeight() : height.intValue();
            image = img.getScaledInstance(iw, ih, Image.SCALE_SMOOTH);
        }
    }
    
    //</editor-fold>

    public static Converter<SVGImage, AnchoredImage> imageConverter() {
        return CONVERTER_INST;
    }
    
    private static final class ImageConverter extends Converter<SVGImage, AnchoredImage> {
        @Override
        protected SVGImage doBackward(AnchoredImage r) {
            return new SVGImage(r.getX(), r.getY(), r.getWidth(), r.getHeight(), r.getReference());
        }

        @Override
        protected AnchoredImage doForward(SVGImage r) {
            Image bi = r.getImage();
            if (bi == null) {
                return null;
            } else if (r.width == null || r.height == null) {
                return new AnchoredImage(r.x, r.y, (double) bi.getWidth(null), (double) bi.getHeight(null), bi, r.imageRef);
            } else {
                return new AnchoredImage(r.x, r.y, r.width, r.height, r.getImage(), r.imageRef);
            }
        }
    }
}
