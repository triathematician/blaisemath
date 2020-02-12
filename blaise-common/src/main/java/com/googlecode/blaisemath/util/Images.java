package com.googlecode.blaisemath.util;

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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Image encoding utilities, to convert images to/from Base64 strings.
 * 
 * @see javax.imageio.ImageIO
 * @see java.util.Base64
 * 
 * @author Elisha Peterson
 */
public class Images {
    
    public static final String DATA_URI_PREFIX = "data:";
    private static final String BASE_64_TOKEN = ";base64";
    
    public static final String BMP = "bmp";
    public static final String GIF = "gif";
    public static final String JPEG = "jpeg";
    public static final String JPG = "jpg";
    public static final String PNG = "png";
    
    private static final List<String> IMAGE_FORMATS = Arrays.asList(JPG, JPEG, BMP, GIF, PNG);
    
    private Images() {
    }
    
    /**
     * Encode the target image as standard base-64, with the target format.
     * The result conforms to RFC-4648, and does not include line breaks.
     * 
     * @param image the image to encode
     * @param format the image format (gif, png, bmp, jpg, jpeg)
     * @return base-64 string
     * @throws java.io.IOException if there's an error writing image content
     */
    public static String encodeStandardBase64(BufferedImage image, String format) throws IOException {
        checkArgument(IMAGE_FORMATS.contains(format.toLowerCase()), "Unsupported format: "+format);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format.toLowerCase(), Base64.getEncoder().wrap(os));
        return os.toString();
    }
    
    /**
     * Create an image from a base-64 string.
     * @param base64 input string
     * @return image
     * @throws java.io.IOException if there's a problem reading from the string
     */
    public static BufferedImage decodeStandardBase64(String base64) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(base64);
        InputStream is = new ByteArrayInputStream(bytes);
        return ImageIO.read(is);
    }

    /**
     * Encode the target image as standard base-64, with the target format,
     * and return the result as a data URI.
     * The result conforms to RFC-4648, and does not include line breaks.
     * 
     * @param image the image to encode
     * @param format the image format (gif, png, bmp, jpg, jpeg)
     * @return base-64 string inside an image data URI
     * @throws java.io.IOException if there's a problem encoding
     * 
     * @see Base64
     */
    public static String encodeDataUriBase64(BufferedImage image, String format) throws IOException {
        return DATA_URI_PREFIX + mimeType(format) + BASE_64_TOKEN + ','
                + encodeStandardBase64(image, format);
    }

    /**
     * Create an image from a standard base-64 data URI.
     * @param base64 base-64 string inside an image data URI
     * @return image
     * @throws java.io.IOException if there's a problem reading from the string
     */
    public static BufferedImage decodeDataUriBase64(String base64) throws IOException {
        checkArgument(base64.startsWith(DATA_URI_PREFIX), "Invalid data URI: "+base64);
        int pos = base64.indexOf(',');
        String content = base64.substring(pos+1);
        return decodeStandardBase64(content);
    }
    
    private static String mimeType(String format) {
        String fixedFormat = JPG.equals(format.toLowerCase()) ? JPEG : format.toLowerCase();
        return "image/"+fixedFormat;
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
