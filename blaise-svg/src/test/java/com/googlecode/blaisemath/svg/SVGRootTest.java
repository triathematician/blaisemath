package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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

import java.io.IOException;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * @author petereb1
 */
public class SVGRootTest {
    
    @Test
    public void testWrite() throws IOException {
        SVGRoot r = new SVGRoot();
        r.addElement(new SVGRectangle());
        String text = SVGRoot.saveToString(r);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<svg height=\"100.0\" width=\"100.0\" style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "    <rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/>\n"
                + "</svg>\n",
                text);
    }
    
    @Test
    public void testLoad() throws IOException {
        SVGRoot.load(SVGRootTest.class.getResource("resources/test.svg").openStream());
        SVGRoot.load(SVGRootTest.class.getResource("resources/test2.svg").openStream());
        SVGRoot.load(SVGRootTest.class.getResource("resources/test3.svg").openStream());
        
        SVGRoot.load("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<svg height=\"100\" width=\"100\" style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                + "<rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/>"
                + "</svg>");
        
        SVGRoot.load("<svg><rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/></svg>");
        
        SVGRoot r2 = SVGRoot.load("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image xlink:href=\"file:src/test/resources/com/googlecode/blaisemath/util/resources/cherries.png\"/></svg>");
        assertTrue(r2.getElements().get(0) instanceof SVGImage);
    }

}
