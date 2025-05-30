package com.googlecode.blaisemath.util;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;

public class ImagesTest {
    
    URL testImage = Images.class.getResource("resources/cherries.png");
    
    @Test
    public void testEncodeStandardBase64() throws Exception {
        System.out.println("encodeStandardBase64");
        BufferedImage bi = ImageIO.read(testImage);
        System.out.println(Images.encodeStandardBase64(bi, Images.PNG));
        System.out.println(Images.encodeStandardBase64(bi, Images.GIF));
    }
    
    @Test
    public void testDecodeStandardBase64() throws Exception {
        System.out.println("decodeStandardBase64");
        BufferedImage bi = ImageIO.read(testImage);
        String b64 = Images.encodeStandardBase64(bi, Images.PNG);
        BufferedImage res = Images.decodeStandardBase64(b64);
        assertEquals(b64, Images.encodeStandardBase64(res, Images.PNG));
    }
    
    @Test
    public void testEncodeDataUriBase64() throws Exception {
        System.out.println("encodeDataUriBase64");
        BufferedImage bi = ImageIO.read(testImage);
        System.out.println(Images.encodeDataUriBase64(bi, Images.PNG));
        System.out.println(Images.encodeDataUriBase64(bi, Images.GIF));
    }
    
    @Test
    public void testDecodeDataUriBase64() throws Exception {
        System.out.println("encodeDataUriBase64");
        BufferedImage bi = ImageIO.read(testImage);
        String b64 = Images.encodeDataUriBase64(bi, Images.PNG);
        BufferedImage res = Images.decodeDataUriBase64(b64);
        assertEquals(b64, Images.encodeDataUriBase64(res, Images.PNG));
    }
    
}
