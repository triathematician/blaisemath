package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.graphics.swing.render.ImageRenderer;
import com.googlecode.blaisemath.primitive.AnchoredImage;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgImage;

import java.awt.geom.Rectangle2D;

/**
 * Write SVG image representation.
 * @author Elisha Peterson
 */
public class SvgImageRenderer extends SvgRenderer<AnchoredImage> {

    @Override
    public void render(AnchoredImage i, AttributeSet style, SvgTreeBuilder canvas) {
        if (i == null || i.getWidth() == 0 || i.getHeight() == 0) {
            throw new SvgRenderException("Missing or 0 width/height image cannot be converted.");
        }

        Rectangle2D r = new ImageRenderer().boundingBox(i, style, null);
        SvgImage res = SvgImage.create((float) r.getX(), (float) r.getY(), (float) r.getWidth(), (float) r.getHeight(), i.getReference());

        res.id = StyleWriter.id(style);
        res.style = StyleWriter.toString(style);

        canvas.add(res);
    }

}
