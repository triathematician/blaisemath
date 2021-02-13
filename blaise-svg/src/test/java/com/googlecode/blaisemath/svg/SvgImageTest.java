/*
 * Copyright 2015 elisha.
 *
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
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
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


import java.net.MalformedURLException;

import com.google.common.base.Converter;
import com.googlecode.blaisemath.graphics.AnchoredImage;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class SVGImageTest extends TestCase {
    
    @Test
    public void testLoadImage() throws MalformedURLException {
        System.out.println("loadImage");
        SVGImage i = new SVGImage();
        assertTrue(i.getImage() == null);
        
        i.setImageRef("cherries.png");
        assertTrue(i.getImage() == null);

        i.setImageRef("file:cherries.png");
        assertTrue(i.getImage() == null);
        
        i.setImageRef("file:src/test/resources/com/googlecode/blaisemath/util/resources/cherries.png");
        assertTrue(i.getImage() != null);
        assertEquals(20.0, i.getWidth());
        assertEquals(20.0, i.getHeight());
    }

    @Test
    public void testConvertToSvg() {
        System.out.println("convertToSvg");
        Converter<SVGImage, AnchoredImage> conv = SVGImage.imageConverter();
        assertEquals(null, conv.reverse().convert(null));
    }

    @Test
    public void testConvertFromSvg() {
        System.out.println("convertToSvg");
        Converter<SVGImage, AnchoredImage> conv = SVGImage.imageConverter();
        assertEquals(null, conv.convert(null));
    }
    
}
