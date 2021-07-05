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
package com.googlecode.blaisemath.svg.xml;

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


import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

import static com.googlecode.blaisemath.svg.xml.SvgRootTest.*;

public class SvgImageTest extends TestCase {

    @Test
    public void testXml() throws IOException {
        assertEquals(SVG_HEADER + "<image x=\"1.0\" y=\"2.0\" width=\"0.0\" height=\"0.0\" xlink:href=\"test.png\" " + SVG_NS_CLOSE,
                SvgIo.writeToCompactString(SvgImage.create(1, 2, "test.png")));
    }
    
}
