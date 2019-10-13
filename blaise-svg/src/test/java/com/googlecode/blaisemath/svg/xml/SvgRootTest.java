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
        assertEquals(SVG_HEADER + "<svg height=\"100.0\" width=\"100.0\" style=\"font-family:sans-serif\" " + SVG_NS_CLOSE, SvgIo.writeToCompactString(SvgRoot.create()));
        assertEquals(SVG_HEADER + "<svg height=\"100.0\" width=\"100.0\" style=\"font-family:sans-serif\" " + SVG_NS_OPEN + "<line x1=\"1.0\" y1=\"2.0\" x2=\"3.0\" y2=\"4.0\"/></svg>",
                SvgIo.writeToCompactString(SvgRoot.create(SvgLine.create(1, 2, 3, 4))));
    }
    
}
