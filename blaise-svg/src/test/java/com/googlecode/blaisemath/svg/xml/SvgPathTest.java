package com.googlecode.blaisemath.svg.xml;

/*
 * #%L
 * BlaiseGraphics
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

import java.io.IOException;

import static com.googlecode.blaisemath.svg.xml.SvgRootTest.*;
import static junit.framework.TestCase.assertEquals;

public class SvgPathTest {

    @Test
    public void testXml() throws IOException {
        assertEquals(SVG_HEADER + "<path d=\"M 0,0 L 1,1 L 2,2 Z\" " + SVG_NS_CLOSE,
                SvgIo.writeToCompactString(SvgPath.create("M 0,0 L 1,1 L 2,2 Z")));
    }

}
