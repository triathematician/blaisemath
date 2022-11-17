package com.googlecode.blaisemath.svg.batik;

/*-
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2022 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.svg.SvgCoder;
import com.googlecode.blaisemath.graphics.svg.SvgGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.CachedImageHandlerBase64Encoder;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.io.StringWriter;

public class BatikSvgCoder extends SvgCoder {

    private static final DOMImplementation DOM_IMPL = GenericDOMImplementation.getDOMImplementation();
    private static final String SVG_NS = "http://www.w3.org/2000/svg";
    private static final CachedImageHandlerBase64Encoder IMAGE_HANDLER = new CachedImageHandlerBase64Encoder();
    private static final Document DOCUMENT = DOM_IMPL.createDocument(SVG_NS, "svg", null);
    private static final SVGGeneratorContext GENERATOR_CONTEXT = SVGGeneratorContext.createDefault(DOCUMENT);

    @Override
    public SvgGraphic graphicFrom(JGraphicComponent comp) {
        try {
            SVGGraphics2D svgGraphics = new SVGGraphics2D(GENERATOR_CONTEXT, false);
            comp.paint(svgGraphics);
            StringWriter sw = new StringWriter();
            svgGraphics.stream(sw, true);
        } catch (SVGGraphics2DIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SvgGraphic decode(String str) {
        return null;
    }

    @Override
    public String encode(SvgGraphic obj) {
        return null;
    }
}
