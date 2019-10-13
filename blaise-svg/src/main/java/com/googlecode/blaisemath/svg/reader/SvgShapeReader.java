package com.googlecode.blaisemath.svg.reader;

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.SvgElement;

import java.awt.*;

/**
 * Partial implementation of SVG to graphic reader that creates a styled shape graphic object.
 * @param <S> SVG element type
 * @param <P> shape primitive type
 * @author Elisha Peterson
 */
abstract class SvgShapeReader<S extends SvgElement, P extends Shape> extends SvgReader<S, P> {

    @Override
    protected Graphic<Graphics2D> createGraphic(P shape, AttributeSet style) {
        if (shape == null) {
            throw new SvgReadException("Null shape");
        }
        return JGraphics.shape(shape, style);
    }

}
