package com.googlecode.blaisemath.util;

/*-
 * #%L
 * blaise-common
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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Image encoding utilities, to convert images to/from Base64 strings.
 * 
 * @see javax.imageio.ImageIO
 * @see java.util.Base64
 * 
 * @author Elisha Peterson
 */
public class ImageUtils {
    
    private static final Logger LOG = Logger.getLogger(ImageUtils.class.getName());
    
    public static final String PNG = "png";
    public static final String JPG = "jpg";
    public static final String BMP = "bmp";
    public static final String GIF = "gif";

    private ImageUtils() {
    }

    /**
     * Convert image to base64 string representation.
     * @param img image to convert
     * @return encoding as base64 string
     */
    public static String encodeImage(BufferedImage img) {
        return encodeImage(img, PNG);
    }

    /**
     * Convert image to base64 string representation.
     * @param img image to convert
     * @param format image format (as returned by {@link ImageIO#getWriterFormatNames()})
     * @return encoding as base64 string
     */
    public static String encodeImage(BufferedImage img, String format) {
        String encodedImage = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, format, baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error encoding image!", ex);
        }
        return encodedImage;
    }
    
    /**
     * Convert string to image representation
     * @param imageString string to convert
     * @return encoding as image
     */
    public static BufferedImage stringToImage(String imageString)    {
        byte[] imgByteArray = Base64.getDecoder().decode(imageString);
        ByteArrayInputStream bais = new ByteArrayInputStream(imgByteArray);
        try {
            return ImageIO.read(bais);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error decoding image!", ex);
        }
        return null;
    }

    /**
     * Renders component on a {@link BufferedImage}.
     * @param view the component
     * @return rendered image
     */
    public static BufferedImage renderImage(Component view) {
        BufferedImage image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D canvas = image.createGraphics();
        canvas.setClip(new Rectangle2D.Double(0, 0, view.getWidth(), view.getHeight()));
        view.paint(canvas);
        canvas.dispose();
        return image;
    }
    
}
