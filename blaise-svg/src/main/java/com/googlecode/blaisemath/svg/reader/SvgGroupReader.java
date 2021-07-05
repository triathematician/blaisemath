package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
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

import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.*;

import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads group of SVG content as Blaise graphics content. Supported SVG types include:
 * <ul>
 * <li>{@link SvgRect}, {@link SvgEllipse}, {@link SvgCircle}, {@link SvgPolygon}</li>
 * <li>{@link SvgLine}, {@link SvgPolyline}</li>
 * <li>{@link SvgPath}</li>
 * <li>{@link SvgImage}</li>
 * <li>{@link SvgText}</li>
 * <li>{@link SvgGroup}</li>
 * </ul>
 * @author Elisha Peterson
 */
public final class SvgGroupReader extends SvgReader<SvgGroup, List<SvgElement>> {

    private static final Logger LOG = Logger.getLogger(SvgGroupReader.class.getName());

    @Override
    protected List<SvgElement> createPrimitive(SvgGroup el) {
        if (el == null) {
            throw new SvgReadException("Null SVG element");
        }
        return el.elements;
    }

    @Override
    protected Graphic<Graphics2D> createGraphic(List<SvgElement> prims, AttributeSet style) {
        GraphicComposite<Graphics2D> res = new GraphicComposite<>();
        res.setStyle(style);
        for (SvgElement p : prims) {
            try {
                Graphic<Graphics2D> g = readGraphic(p);
                g.setDefaultTooltip(p.id);
                res.addGraphic(g);
            } catch (SvgReadException e) {
                LOG.log(Level.SEVERE, "Graphic read error", e);
            }
        }
        return res;
    }

    public static Graphic<Graphics2D> readGraphic(SvgElement el) throws SvgReadException {
        for (SvgReader r : SvgReader.readers()) {
            if (r.canRead(el)) {
                return r.read(el);
            }
        }
        throw new SvgReadException("Unsupported: "+el);
    }

}
