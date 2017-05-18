/*
 * Copyright 2014 petereb1.
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
/**
 * SVGRootTest.java
 * Created on Sep 26, 2014
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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
import java.io.StringWriter;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 * @author petereb1
 */
public class SVGRootTest {
    
    @Test
    public void testSerialize() throws JAXBException {
        SVGRoot r = new SVGRoot();
        r.addElement(new SVGRectangle());
        JAXBContext jc = JAXBContext.newInstance(SVGRoot.class);
        StringWriter sw = new StringWriter();
        jc.createMarshaller().marshal(r, sw);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<svg height=\"100\" width=\"100\" style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                + "<rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/>"
                + "</svg>",
                sw.toString());
    }
    
    @Test
    public void testRead() throws IOException {
        URL rsc = SVGRootTest.class.getResource("resources/test.svg");
        SVGRoot root = SVGRoot.load(rsc.openStream());
    }

}
