package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.PrimitiveGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgImage;
import com.googlecode.blaisemath.util.Images;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts an SVG image to its Blaise/Swing equivalent. Caches results obtained in the last 10 minutes to speed up image decoding
 * if its done several times in a short period of time.
 * @author Elisha Peterson
 */
public final class SvgImageReader extends SvgReader<SvgImage, AnchoredImage> {

    private static final Logger LOG = Logger.getLogger(SvgImageReader.class.getName());

    private static LoadingCache<String, BufferedImage> IMAGE_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public BufferedImage load(String imageRef) throws IOException {
                    return imageRef.startsWith(Images.DATA_URI_PREFIX)
                            ? Images.decodeDataUriBase64(imageRef)
                            : ImageIO.read(new URL(imageRef));
                }
            });

    @Override
    protected AnchoredImage createPrimitive(SvgImage r) {
        if (r == null) {
            throw new SvgReadException("Null SVG element");
        }
        return new AnchoredImage(r.x, r.y, loadImage(r), r.href);
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(AnchoredImage prim, AttributeSet style) {
        PrimitiveGraphic<AnchoredImage, Graphics2D> res = JGraphics.image(prim);
        res.setStyle(style);
        res.setMouseDisabled(true);
        return res;
    }

    /**
     * Load image referenced by location or encoding in given SVG image object.
     * @param r image object
     * @return loaded image, if possible
     */
    private static @Nullable Image loadImage(SvgImage r) {
        BufferedImage img;
        try {
            img = IMAGE_CACHE.get(r.href);
        } catch (ExecutionException ex) {
            LOG.log(Level.FINE, "Failed to load image from " + r.href, ex);
            if (ex.getCause() instanceof IIOException) {
                LOG.log(Level.WARNING, "Failed to load image from " + r.href + ": " + ex.getCause().getCause());
            }
            return null;
        }
        if (r.width == 0 || r.height == 0 || r.width == img.getWidth() && r.height == img.getHeight()) {
            return img;
        } else {
            int iw = r.width == 0f ? img.getWidth() : (int) r.width;
            int ih = r.height == 0f ? img.getHeight() : (int) r.height;
            return img.getScaledInstance(iw, ih, Image.SCALE_SMOOTH);
        }
    }

}
