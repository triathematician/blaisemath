package com.googlecode.blaisemath.svg.render;

/*-
 * #%L
 * blaise-svg
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.blaisemath.graphics.swing.render.IconRenderer;
import com.googlecode.blaisemath.primitive.AnchoredIcon;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.xml.SvgImage;
import com.googlecode.blaisemath.util.Images;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Write SVG icon representation. Icons are rendered as images.
 * @author Elisha Peterson
 */
public class SvgIconRenderer extends SvgRenderer<AnchoredIcon> {

    private static final Logger LOG = Logger.getLogger(SvgIconRenderer.class.getName());

    private static LoadingCache<Icon, String> ICON_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public String load(Icon icon) {
                    return loadIconEncodingForCache(icon);
                }
            });

    @Override
    public void render(AnchoredIcon i, AttributeSet style, SvgTreeBuilder canvas) {
        if (i == null || i.getIconWidth() == 0 || i.getIconHeight() == 0) {
            throw new SvgRenderException("Missing or 0 width/height image cannot be converted.");
        }

        Rectangle2D r = new IconRenderer().boundingBox(i, style, null);
        SvgImage res = SvgImage.create((float) r.getX(), (float) r.getY(), (float) r.getWidth(), (float) r.getHeight(), ICON_CACHE.getUnchecked(i.getIcon()));

        res.id = StyleWriter.id(style);
        AttributeSet sty = style.copy();
        sty.remove(Styles.TEXT_ANCHOR);
        sty.remove(Styles.ALIGN_BASELINE);
        res.style = StyleWriter.toString(style);

        canvas.add(res);
    }

    private static String loadIconEncodingForCache(Icon icon) {
        try {
            BufferedImage bi = new BufferedImage(2*icon.getIconWidth(), 2*icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setTransform(AffineTransform.getScaleInstance(2, 2));
            icon.paintIcon(null, g2, 0, 0);
            return Images.encodeDataUriBase64(bi, "png");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Encoding error", ex);
            return "";
        }
    }

}
