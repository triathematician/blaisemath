/**
 * SketchIO.java
 * Created Oct 17, 2015
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
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


import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.svg.SVGElementGraphicConverter;
import com.googlecode.blaisemath.svg.SVGElement;
import com.googlecode.blaisemath.svg.SVGRoot;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Handles I/O actions for sketch.
 * 
 * @author elisha
 */
public class SketchIO {

    // utility class cannot be instantiated
    private SketchIO() {
    }
    
    /**
     * Converts given composite graphics to SVG-compatible object.
     * @param comp graphic to convert
     * @return SVG representation
     */
    public static SVGRoot toSVG(GraphicComposite<Graphics2D> comp) {
        Converter<Graphic<Graphics2D>, SVGElement> conv = SVGElementGraphicConverter.getInstance().reverse();
        SVGRoot r = new SVGRoot();
        for (Graphic<Graphics2D> g : comp.getGraphics()) {
            r.addElement(conv.convert(g));
        }
        return r;
    }
    
    /**
     * Create composite graphics from SVG representation.
     * @param svg the SVG representation
     * @return set of top-level graphics
     */
    public static Iterable<Graphic<Graphics2D>> fromSVG(SVGRoot svg) {
        Converter<SVGElement, Graphic<Graphics2D>> conv = SVGElementGraphicConverter.getInstance();
        List<Graphic<Graphics2D>> res = Lists.newArrayList();
        for (SVGElement el : svg.getElements()) {
            res.add(conv.convert(el));
        }
        return res;
    }
    
}
