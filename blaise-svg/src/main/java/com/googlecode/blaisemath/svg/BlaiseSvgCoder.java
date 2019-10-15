package com.googlecode.blaisemath.svg;

import com.googlecode.blaisemath.graphics.svg.SvgCoder;
import com.googlecode.blaisemath.graphics.svg.SvgGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.render.SvgRenderer;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

public class BlaiseSvgCoder extends SvgCoder {
    @Override
    public SvgGraphic graphicFrom(JGraphicComponent comp) {
        SvgRoot root = SvgRenderer.componentToSvg(comp);
//        String svg = SvgIo.writeToString(root);
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
