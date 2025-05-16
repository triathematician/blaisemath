package com.googlecode.blaisemath.svg.reader;

/*-
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

import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.svg.xml.SvgImage;
import org.junit.Test;

import java.net.MalformedURLException;

import static com.googlecode.blaisemath.svg.reader.SvgReaderTest.assertSvgReadException;
import static org.junit.Assert.*;

public class SvgImageReaderTest {

    @Test
    public void createPrimitive() {
        assertSvgReadException(() -> new SvgImageReader().createPrimitive(null));
        AnchoredImage image = new SvgImageReader().createPrimitive(SvgImage.create(1, 2, "file:image.png"));
        assertEquals(1, image.x, .01);
        assertEquals(2, image.y, .01);
        assertEquals("file:image.png", image.getReference());
    }

//    @Test
//    public void testLoadImage() throws MalformedURLException {
//        System.out.println("loadImage");
//        SvgImage i = new SvgImage();
//        assertTrue(i.getImage() == null);
//
//        i.href = "cherries.png";
//        assertTrue(i.getImage() == null);
//
//        i.href = "file:cherries.png";
//        assertTrue(i.getImage() == null);
//
//        i.href = "file:src/test/resources/com/googlecode/blaisemath/svg/resources/cherries.png";
//        assertTrue(i.getImage() != null);
//        assertEquals(20.0, i.getWidth());
//        assertEquals(20.0, i.getHeight());
//    }

}
