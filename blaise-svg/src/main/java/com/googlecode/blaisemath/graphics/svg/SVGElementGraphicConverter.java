package com.googlecode.blaisemath.graphics.svg;

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

import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicComposite;
import com.googlecode.blaisemath.graphics.core.PrimitiveArrayGraphicSupport;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.JGraphics;
import com.googlecode.blaisemath.graphics.swing.LabeledShapeGraphic;
import com.googlecode.blaisemath.graphics.swing.PanAndZoomHandler;
import com.googlecode.blaisemath.graphics.swing.TextRenderer;
import com.googlecode.blaisemath.graphics.swing.WrappedTextRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSets;
import com.googlecode.blaisemath.style.ObjectStyler;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.style.xml.AttributeSetAdapter;
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
import com.googlecode.blaisemath.util.AnchoredIcon;
import com.googlecode.blaisemath.util.AnchoredImage;
import com.googlecode.blaisemath.util.AnchoredText;
import com.googlecode.blaisemath.util.Colors;
import java.awt.Color;
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
 *   <li>{@link SVGGroup}</li>
 * </ul>
 * This adapter also converts {@link SVGGroup} to/from {@link GraphicComposite}.
 * 
 * @author elisha
 */
public class SVGElementGraphicConverter extends Converter<SVGElement, Graphic<Graphics2D>> {
    
    private static final Logger LOG = Logger.getLogger(SVGElementGraphicConverter.class.getName());
    private static final SVGElementGraphicConverter INST = new SVGElementGraphicConverter();

    /**
     * Get global instance of the converter.
     * @return instance
     */
    public static Converter<SVGElement, Graphic<Graphics2D>> getInstance() {
        return INST;
    }

    /**
     * Convert a graphic component to an SVG object, including a view box.
     * @param compt component to convert
     * @return result
     */
    public static SVGRoot componentToSvg(JGraphicComponent compt) {
        SVGRoot root = new SVGRoot();
        root.setWidth(compt.getWidth());
        root.setHeight(compt.getHeight());
        root.setViewBoxAsRectangle(PanAndZoomHandler.getLocalBounds(compt));
        root.getStyle().put("background", Colors.stringConverter().convert(compt.getBackground()));
        root.getStyle().put(Styles.FONT_SIZE, Styles.DEFAULT_TEXT_STYLE.get(Styles.FONT_SIZE));
        SVGGroup group = (SVGGroup) SVGElementGraphicConverter.getInstance().reverse()
                .convert(compt.getGraphicRoot());
        group.getElements().forEach(root::addElement);
        return root;
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
        AttributeSet style = aggregateStyle(sh);
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
        prim.setDefaultTooltip(sh.getId());
        return prim;
    }

    private AttributeSet aggregateStyle(SVGElement element) {
        AttributeSet shapeStyle = element.getStyle();
        AttributeSet res = shapeStyle == null 
                ? new AttributeSet()
                : shapeStyle.copy();
        
        Map<QName, Object> attr = element.getOtherAttributes();
        if (attr != null) {
            for (Entry<QName, Object> en : attr.entrySet()) {
                Object val = AttributeSets.valueFromString((String) en.getValue());
                res.put(en.getKey().toString(), val);
            }
        }
        if (element.getId() != null) {
            res.put(Styles.ID, element.getId());
        }
        AttributeSetAdapter.updateColorFields(res);
        return res;
    }

    @Override
    public SVGElement doBackward(Graphic<Graphics2D> v) {
        return graphicToSvg(v);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PRIVATE UTILITIES">
    
    /** Converts a graphic element to an SVG element */
    private static SVGElement graphicToSvg(Graphic<Graphics2D> v) {
        SVGElement res = null;
        if (v instanceof LabeledShapeGraphic) {
            res = labeledShapeToSvg((LabeledShapeGraphic<Graphics2D>) v);
        } else if (v instanceof PrimitiveGraphicSupport) {
            PrimitiveGraphicSupport pgs = (PrimitiveGraphicSupport) v;
            res = primitiveStyleToSvg(pgs.getPrimitive(), v.getStyle(), pgs.getRenderer());
        } else if (v instanceof GraphicComposite) {
            res = compositeToSvg((GraphicComposite<Graphics2D>) v);
        } else if (v instanceof PrimitiveArrayGraphicSupport) {
            res = primitiveArrayToSvg((PrimitiveArrayGraphicSupport) v);
        } else {
            throw new IllegalArgumentException("Graphic conversion not supported for "+v.getClass());
        }
        String id = v.renderStyle().getString(Styles.ID, null);
        if (id != null && res != null) {
            res.setId(id);
        }
        if (res != null && res.getStyle() != null && res.getStyle().getAttributes().isEmpty()) {
            res.setStyle(null);
        }
        if (res != null && res.getStyle() != null) {
            for (String c : res.getStyle().getAttributes()) {
                Object col = res.getStyle().get(c);
                if (col instanceof Color) {
                    res.getStyle().put(c, Colors.alpha((Color) col, 255));
                }
            }
        }
        return res;
    }

    /** Converts a blaise composite to an SVG group */
    private static SVGElement compositeToSvg(GraphicComposite<Graphics2D> gc) {
        SVGGroup grp = new SVGGroup();
        if (gc.getStyle() != null) {
            grp.setStyle(AttributeSet.create(gc.getStyle().getAttributeMap()));
        }
        for (Graphic<Graphics2D> g : gc.getGraphics()) {
            try {
                SVGElement el = graphicToSvg(g);
                if (el != null) {
                    grp.addElement(el);
                } else {
                    LOG.log(Level.WARNING, "Null graphic for {0}", g);
                }
            } catch (IllegalArgumentException x) {
                LOG.log(Level.WARNING, "Graphic not added to result", x);
            }
        }
        return grp;
    }

    /** Converts a blaise array graphic to SVG group */
    private static SVGElement primitiveArrayToSvg(PrimitiveArrayGraphicSupport pags) {
        SVGGroup grp = new SVGGroup();
        grp.setStyle(pags.renderStyle().flatCopy());
        for (Object o : pags.getPrimitive()) {
            grp.addElement(primitiveStyleToSvg(o, pags.renderStyle().flatCopy(), pags.getRenderer()));
        }
        return grp;
    }

    /** Creates an SVG element from given primitive/style */
    private static SVGElement primitiveStyleToSvg(Object primitive, AttributeSet sty, Renderer rend) {
        if (primitive instanceof Shape) {
            return SVGElements.create(null, (Shape) primitive, sty);
        } else if (primitive instanceof AnchoredText) {
            return SVGElements.create(null, (AnchoredText) primitive, sty, rend);
        } else if (primitive instanceof AnchoredImage) {
            return SVGElements.create(null, (AnchoredImage) primitive, sty);
        } else if (primitive instanceof AnchoredIcon) {
            return SVGElements.create(null, (AnchoredIcon) primitive, sty);
        } else if (primitive instanceof Point2D) {
            return SVGElements.create(null, (Point2D) primitive, sty);
        } else {
            throw new IllegalArgumentException("Graphic conversion not supported for primitive "+primitive);
        }
    }

    /** Converts a labeled shape to svg */
    private static SVGElement labeledShapeToSvg(LabeledShapeGraphic<Graphics2D> gfc) {
        SVGElement shape = primitiveStyleToSvg(gfc.getPrimitive(), gfc.renderStyle().flatCopy(), gfc.getRenderer());
        SVGElement text = labelToSvg(gfc);
        return text == null ? shape : SVGGroup.create(shape, text);
    }

    /** Generates element for object label */
    private static SVGElement labelToSvg(LabeledShapeGraphic<Graphics2D> gfc) {
        ObjectStyler<Graphics2D> styler = gfc.getObjectStyler();
        if (styler == null) {
            return null;
        }
        String label = styler.label(gfc.getSourceObject());
        AttributeSet style = styler.labelStyle(gfc.getSourceObject());
        if (Strings.isNullOrEmpty(label) || style == null) {
            return null;
        }
        Renderer<AnchoredText, Graphics2D> textRend = gfc.getTextRenderer();
        if (textRend instanceof WrappedTextRenderer) {
            return SVGElements.createWrappedText(label, style, LabeledShapeGraphic.wrappedLabelBounds(gfc.getPrimitive()));
        } else if (textRend instanceof TextRenderer) {
            return primitiveStyleToSvg(new AnchoredText(label), style.flatCopy(), textRend);
        } else {
            LOG.log(Level.WARNING, "Unsupported text renderer: {0}", textRend);
            return null;
        }
    }
    
    //</editor-fold>
    
}
