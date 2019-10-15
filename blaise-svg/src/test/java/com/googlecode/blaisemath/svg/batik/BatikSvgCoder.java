package com.googlecode.blaisemath.svg.batik;

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
