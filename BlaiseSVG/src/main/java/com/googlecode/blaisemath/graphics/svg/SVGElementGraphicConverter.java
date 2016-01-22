/**
 * SVGElementGraphicConverter.java
 * Created Sep 27, 2014
 */

package com.googlecode.blaisemath.graphics.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveArrayGraphicSupport;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSets;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.SVGCircle;
import com.googlecode.blaisemath.svg.SVGElement;
import com.googlecode.blaisemath.svg.SVGElements;
import com.googlecode.blaisemath.svg.SVGEllipse;
import com.googlecode.blaisemath.svg.SVGGroup;
import com.googlecode.blaisemath.svg.SVGImage;
import com.googlecode.blaisemath.svg.SVGLine;
import com.googlecode.blaisemath.svg.SVGPath;
import com.googlecode.blaisemath.svg.SVGPolygon;
import com.googlecode.blaisemath.svg.SVGPolyline;
import com.googlecode.blaisemath.svg.SVGRectangle;
import com.googlecode.blaisemath.svg.SVGRoot;
import com.googlecode.blaisemath.svg.SVGText;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.AnchoredText;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

/** 
 * Adapter for converting SVG objects to/from Blaise {@link PrimitiveGraphicSupport} objects.
 * The conversion is imperfect in this implementation. Supported SVG types include:
 * <ul>
 *   <li>{@link SVGRectangle}, {@link SVGEllipse}, {@link SVGCircle}, {@link SVGPolygon}</li>
 *   <li>{@link SVGLine}, {@link SVGPolyline}</li>
 *   <li>{@link SVGPath}</li>
 *   <li>{@link SVGImage}</li>
 *   <li>{@link SVGText}</li>
 * </ul>
 * This adapter also converts {@link SVGGroup} to/from {@link GraphicComposite}.
 * 
 * @author elisha
 */
public class SVGElementGraphicConverter extends Converter<SVGElement, Graphic<Graphics2D>> {
    
    private static final SVGElementGraphicConverter INST = new SVGElementGraphicConverter();
    
    public static Converter<SVGElement, Graphic<Graphics2D>> getInstance() {
        return INST;
    }
    
    /**
     * Convert an {@link SVGElement} to a {@link Graphic}. The resulting object will
     * be a {@link GraphicComposite} if the argument is an instance of {@link SVGGroup}
     * or {@link SVGRoot}, and otherwise a {@link PrimitiveGraphicSupport}. In the case of
     * a group, recursive calls are made to convert all elements in the group.
     * 
     * @param sh the element to convert
     * @return the corresponding graphic
     */
    @Override
    public Graphic<Graphics2D> doForward(SVGElement sh) {
        Graphic<Graphics2D> prim = null;
        AttributeSet style = sh.getStyle() == null ? Styles.DEFAULT_SHAPE_STYLE.copy()
                : sh.getStyle();
        if (sh instanceof SVGRectangle) {
            RectangularShape rsh = SVGRectangle.shapeConverter().convert((SVGRectangle) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGEllipse) {
            Shape rsh = SVGEllipse.shapeConverter().convert((SVGEllipse) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGCircle) {
            Shape rsh = SVGCircle.shapeConverter().convert((SVGCircle) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGPolygon) {
            Shape rsh = SVGPolygon.shapeConverter().convert((SVGPolygon) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGLine) {
            Line2D line = SVGLine.shapeConverter().convert((SVGLine) sh);
            prim = JGraphics.path(line, style);
        } else if (sh instanceof SVGPolyline) {
            Shape rsh = SVGPolyline.shapeConverter().convert((SVGPolyline) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGPath) {
            Shape rsh = SVGPath.shapeConverter().convert((SVGPath) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SVGImage) {
            AnchoredImage img = SVGImage.imageConverter().convert((SVGImage) sh);
            prim = JGraphics.image(img);
            prim.setMouseEnabled(false);
        } else if (sh instanceof SVGText) {
            AnchoredText text = SVGText.textConverter().convert((SVGText) sh);
            prim = JGraphics.text(text, style);
            prim.setMouseEnabled(false);
        } else if (sh instanceof SVGGroup || sh instanceof SVGRoot) {
            prim = new GraphicComposite<Graphics2D>();
            ((GraphicComposite)prim).setStyle(style);
            for (SVGElement el : ((SVGGroup)sh).getElements()) {
                ((GraphicComposite<Graphics2D>)prim).addGraphic(doForward(el));
            }
        } else {
            throw new IllegalStateException("Unexpected SVG element: "+sh);
        }
        if (sh.getOtherAttributes() != null || sh.getId() != null) {
//            if (prim.getStyle() instanceof ImmutableAttributeSet) {
//                Logger.getLogger(SVGElementGraphicConverter.class.getName()).log(Level.WARNING, 
//                        "Attempt to set id of graphic w/ immutable style: {0}", prim.getStyle());
//            } else 
                if (prim.getStyle() == null) {
                Logger.getLogger(SVGElementGraphicConverter.class.getName()).log(Level.WARNING, 
                        "Attempt to set id of graphic w/ null style: {0}", prim.getStyle());
            } else {
                Map<QName, Object> attr = sh.getOtherAttributes();
                if (attr != null) {
                    for (Entry<QName, Object> en : attr.entrySet()) {
                        Object val = AttributeSets.valueFromString((String) en.getValue());
                        prim.getStyle().put(en.getKey().toString(), val);
                    }
                }
                if (sh.getId() != null) {
                    prim.getStyle().put(Styles.ID, sh.getId());
                }
            }
        }
        prim.setDefaultTooltip(sh.getId());
        return prim;
    }

    @Override
    public SVGElement doBackward(Graphic<Graphics2D> v) {
        SVGElement res = null;
        if (v instanceof PrimitiveGraphicSupport) {
            AttributeSet sty = v.getStyle();
            Object p = ((PrimitiveGraphicSupport)v).getPrimitive();
            res = createElement(p, sty);
        } else if (v instanceof GraphicComposite) {
            GraphicComposite<Graphics2D> gc = (GraphicComposite<Graphics2D>) v;
            SVGGroup grp = new SVGGroup();
            for (Graphic<Graphics2D> g : gc.getGraphics()) {
                try {
                    grp.addElement(doBackward(g));
                } catch (IllegalArgumentException x) {
                    Logger.getLogger(SVGElementGraphicConverter.class.getName())
                            .log(Level.WARNING, "Graphic not added to result", x);
                }
            }
            res = grp;
        } else if (v instanceof PrimitiveArrayGraphicSupport) {
            PrimitiveArrayGraphicSupport pags = (PrimitiveArrayGraphicSupport) v;
            SVGGroup grp = new SVGGroup();
            grp.setStyle(pags.getStyle());
            for (Object o : pags.getPrimitive()) {
                grp.addElement(createElement(o, pags.getStyle()));
            }
            res = grp;
        } else {
            throw new IllegalArgumentException("Graphic conversion not supported for "+v.getClass());
        }
        String id = v.getStyle().getString(Styles.ID, null);
        if (id != null) {
            res.setId(id);
        }
        return res;
    }

    private static SVGElement createElement(Object p, AttributeSet sty) {
        if (p instanceof Shape) {
            return SVGElements.create(null, (Shape) p, sty);
        } else if (p instanceof AnchoredText) {
            return SVGElements.create(null, (AnchoredText) p, sty);
        } else if (p instanceof AnchoredImage) {
            return SVGElements.create(null, (AnchoredImage) p, sty);
        } else if (p instanceof Point2D) {
            return SVGElements.create(null, (Point2D) p, sty);
        } else {
            throw new IllegalArgumentException("Graphic conversion not supported for primitive "+p);
        }
    }
}
