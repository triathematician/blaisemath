package org.apache.batik.svggen;

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

import com.googlecode.blaisemath.svg.*;
import com.googlecode.blaisemath.svg.io.SvgIo;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

public class SvgIoTest {
    
    @Test
    public void testWrite() throws IOException {
        SvgRoot r = new SvgRoot();
        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?>\n"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100.0\" height=\"100.0\" style=\"font-family:sans-serif\"/>",
                SvgIo.writeToString(r));

        r.addElement(new SvgRectangle());
        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?>\n"
                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100.0\" height=\"100.0\" style=\"font-family:sans-serif\">\n"
                        + "  <rect x=\"0.0\" y=\"0.0\" width=\"0.0\" height=\"0.0\" rx=\"0.0\" ry=\"0.0\"/>\n"
                        + "</svg>",
                SvgIo.writeToString(r));

        SvgGroup g = new SvgGroup();
        g.addElement(new SvgLine());
        r.addElement(g);
        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8'?>\n"
                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"100.0\" height=\"100.0\" style=\"font-family:sans-serif\">\n"
                        + "  <g>\n"
                        + "    <rect x=\"0.0\" y=\"0.0\" width=\"0.0\" height=\"0.0\" rx=\"0.0\" ry=\"0.0\"/>\n"
                        + "  </g>\n"
                        + "</svg>",
                SvgIo.writeToString(r));
    }
    
    @Test
    public void testRead() throws IOException {
        SvgIo.read(SvgIo.class.getResource("resources/test.svg").openStream());
        SvgIo.read(SvgIo.class.getResource("resources/test2.svg").openStream());
        SvgIo.read(SvgIo.class.getResource("resources/test3.svg").openStream());
        batikRead("resources/test.svg");
        batikRead("resources/test2.svg");
        batikRead("resources/test3.svg");

        SvgRoot r1 = SvgIo.read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<svg height=\"100\" width=\"100\" style=\"font-family:sans-serif\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                + "<rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/>"
                + "</svg>");
        Assert.assertTrue(r1.getElements().get(0) instanceof SvgRectangle);

        SvgIo.read("<svg><rect height=\"0.0\" rx=\"0.0\" ry=\"0.0\" width=\"0.0\" x=\"0.0\" y=\"0.0\"/></svg>");
        
        SvgRoot r2 = SvgIo.read("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                "<image xlink:href=\"file:src/test/resources/com/googlecode/blaisemath/util/resources/cherries.png\"/>" +
                "</svg>");
        Assert.assertTrue(r2.getElements().get(0) instanceof SvgImage);
    }

    private static void batikRead(String resource) {
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            URL rsc = SvgIo.class.getResource("resources/test.svg");
            SVGDocument doc = f.createSVGDocument(rsc.toURI().toString(), rsc.openStream());
            PrintWriter writer = new PrintWriter(System.out);
            XmlWriter.writeXml(doc, writer, false);
            writer.flush();
        } catch (IOException | URISyntaxException ex) {
            // ...
        }
    }

}
