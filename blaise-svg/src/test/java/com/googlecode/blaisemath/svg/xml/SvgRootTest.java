package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseSVG
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

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

public class SvgRootTest extends TestCase {

    static final String SVG_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    static final String SVG_NS_OPEN = "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">";
    static final String SVG_NS_CLOSE = "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"/>";

    @Test
    public void testXml() throws IOException {
        assertEquals(SVG_HEADER + "<svg style=\"font-family:sans-serif\" " + SVG_NS_CLOSE, SvgIo.writeToCompactString(SvgRoot.create()));
        assertEquals(SVG_HEADER + "<svg style=\"font-family:sans-serif\" " + SVG_NS_OPEN + "<line x1=\"1.0\" y1=\"2.0\" x2=\"3.0\" y2=\"4.0\"/></svg>",
                SvgIo.writeToCompactString(SvgRoot.create(SvgLine.create(1, 2, 3, 4))));
    }

    @Test
    public void testWrite() throws IOException {
        SvgRoot r = new SvgRoot();
        r.elements.add(new SvgRect());
        String text = SvgIo.writeToString(r);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                        + "<svg style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                        + "    <rect x=\"0.0\" y=\"0.0\" width=\"0.0\" height=\"0.0\"/>\n"
                        + "</svg>\n",
                text);
    }

    @Test
    public void testLoad() throws IOException {
        SvgIo.read(SvgRootTest.class.getResource("resources/test.svg").openStream());
        SvgIo.read(SvgRootTest.class.getResource("resources/test2.svg").openStream());
        SvgIo.read(SvgRootTest.class.getResource("resources/test3.svg").openStream());

        SvgIo.read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<svg height=\"100\" width=\"100\" style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                + "<rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/>"
                + "</svg>");

        SvgIo.read("<svg><rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/></svg>");

        SvgRoot r2 = SvgIo.read("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image xlink:href=\"file:src/test/resources/com/googlecode/blaisemath/svg/resources/cherries.png\"/></svg>");
        assertTrue(r2.elements.get(0) instanceof SvgImage);
    }
    
}
