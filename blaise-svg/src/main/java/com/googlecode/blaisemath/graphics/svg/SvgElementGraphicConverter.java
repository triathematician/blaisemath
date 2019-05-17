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
import com.googlecode.blaisemath.graphics.AnchoredIcon;
import com.googlecode.blaisemath.graphics.AnchoredImage;
import com.googlecode.blaisemath.graphics.AnchoredText;
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
import com.googlecode.blaisemath.style.*;
import com.googlecode.blaisemath.svg.*;
import com.googlecode.blaisemath.svg.SvgElement;
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

/** 
 * Adapter for converting SVG objects to/from Blaise {@link PrimitiveGraphicSupport} objects.
 * The conversion is imperfect in this implementation. Supported SVG types include:
 * <ul>
 *   <li>{@link SvgRectangle}, {@link SvgEllipse}, {@link SvgCircle}, {@link SvgPolygon}</li>
 *   <li>{@link SvgLine}, {@link SvgPolyline}</li>
 *   <li>{@link SvgPath}</li>
 *   <li>{@link SvgImage}</li>
 *   <li>{@link SvgText}</li>
 *   <li>{@link SvgGroup}</li>
 * </ul>
 * This adapter also converts {@link SvgGroup} to/from {@link GraphicComposite}.
 * 
 * @author Elisha Peterson
 */
public class SvgElementGraphicConverter extends Converter<SvgElement, Graphic<Graphics2D>> {
    
    private static final Logger LOG = Logger.getLogger(SvgElementGraphicConverter.class.getName());
    private static final SvgElementGraphicConverter INST = new SvgElementGraphicConverter();

    /**
     * Get global instance of the converter.
     * @return instance
     */
    public static Converter<SvgElement, Graphic<Graphics2D>> getInstance() {
        return INST;
    }

    /**
     * Convert a graphic component to an SVG object, including a view box.
     * @param compt component to convert
     * @return result
     */
    public static SvgRoot componentToSvg(JGraphicComponent compt) {
        SvgRoot root = new SvgRoot();
        root.setWidth(compt.getWidth());
        root.setHeight(compt.getHeight());
        root.setViewBoxAsRectangle(PanAndZoomHandler.getLocalBounds(compt));
        root.getStyle().put("background", Colors.encode(compt.getBackground()));
        root.getStyle().put(Styles.FONT_SIZE, Styles.DEFAULT_TEXT_STYLE.get(Styles.FONT_SIZE));
        SvgGroup group = (SvgGroup) SvgElementGraphicConverter.getInstance().reverse()
                .convert(compt.getGraphicRoot());
        group.getElements().forEach(root::addElement);
        return root;
    }
    
    /**
     * Convert an {@link SvgElement} to a {@link Graphic}. The resulting object will
     * be a {@link GraphicComposite} if the argument is an instance of {@link SvgGroup}
     * or {@link SvgRoot}, and otherwise a {@link PrimitiveGraphicSupport}. In the case of
     * a group, recursive calls are made to convert all elements in the group.
     * 
     * @param sh the element to convert
     * @return the corresponding graphic
     */
    @Override
    public Graphic<Graphics2D> doForward(SvgElement sh) {
        Graphic<Graphics2D> prim = null;
        AttributeSet style = aggregateStyle(sh);
        if (sh instanceof SvgRectangle) {
            RectangularShape rsh = SvgRectangle.shapeConverter().convert((SvgRectangle) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgEllipse) {
            Shape rsh = SvgEllipse.shapeConverter().convert((SvgEllipse) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgCircle) {
            Shape rsh = SvgCircle.shapeConverter().convert((SvgCircle) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgPolygon) {
            Shape rsh = SvgPolygon.shapeConverter().convert((SvgPolygon) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgLine) {
            Line2D line = SvgLine.shapeConverter().convert((SvgLine) sh);
            prim = JGraphics.path(line, style);
        } else if (sh instanceof SvgPolyline) {
            Shape rsh = SvgPolyline.shapeConverter().convert((SvgPolyline) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgPath) {
            Shape rsh = SvgPath.shapeConverter().convert((SvgPath) sh);
            prim = JGraphics.shape(rsh, style);
        } else if (sh instanceof SvgImage) {
            AnchoredImage img = SvgImage.imageConverter().convert((SvgImage) sh);
            prim = JGraphics.image(img);
            prim.setMouseDisabled(true);
        } else if (sh instanceof SvgText) {
            AnchoredText text = SvgText.textConverter().convert((SvgText) sh);
            prim = JGraphics.text(text, style);
            prim.setMouseDisabled(true);
        } else if (sh instanceof SvgGroup || sh instanceof SvgRoot) {
            prim = new GraphicComposite<>();
            ((GraphicComposite)prim).setStyle(style);
            for (SvgElement el : ((SvgGroup)sh).getElements()) {
                ((GraphicComposite<Graphics2D>)prim).addGraphic(doForward(el));
            }
        } else {
            throw new IllegalStateException("Unexpected SVG element: "+sh);
        }
        prim.setDefaultTooltip(sh.getId());
        return prim;
    }

    private AttributeSet aggregateStyle(SvgElement element) {
        AttributeSet shapeStyle = element.getStyle();
        AttributeSet res = shapeStyle == null 
                ? new AttributeSet()
                : shapeStyle.copy();
        
        Map<String, Object> attr = element.getOtherAttributes();
        if (attr != null) {
            for (Entry<String, Object> en : attr.entrySet()) {
                Object val = new AttributeSetCoder().decode((String) en.getValue());
                res.put(en.getKey(), val);
            }
        }
        if (element.getId() != null) {
            res.put(Styles.ID, element.getId());
        }
        AttributeSetAdapter.updateColorFields(res);
        return res;
    }

    @Override
    public SvgElement doBackward(Graphic<Graphics2D> v) {
        return graphicToSvg(v);
    }
    
    //region UTILITIES
    
    /** Converts a graphic element to an SVG element */
    private static SvgElement graphicToSvg(Graphic<Graphics2D> v) {
        SvgElement res = null;
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
    private static SvgElement compositeToSvg(GraphicComposite<Graphics2D> gc) {
        SvgGroup grp = new SvgGroup();
        if (gc.getStyle() != null) {
            grp.setStyle(AttributeSet.create(gc.getStyle().getAttributeMap()));
        }
        for (Graphic<Graphics2D> g : gc.getGraphics()) {
            try {
                SvgElement el = graphicToSvg(g);
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
    private static SvgElement primitiveArrayToSvg(PrimitiveArrayGraphicSupport pags) {
        SvgGroup grp = new SvgGroup();
        grp.setStyle(pags.renderStyle().flatCopy());
        for (Object o : pags.getPrimitive()) {
            grp.addElement(primitiveStyleToSvg(o, pags.renderStyle().flatCopy(), pags.getRenderer()));
        }
        return grp;
    }

    /** Creates an SVG element from given primitive/style */
    private static SvgElement primitiveStyleToSvg(Object primitive, AttributeSet sty, Renderer rend) {
        if (primitive instanceof Shape) {
            return SvgElements.create(null, (Shape) primitive, sty);
        } else if (primitive instanceof AnchoredText) {
            return SvgElements.create(null, (AnchoredText) primitive, sty, rend);
        } else if (primitive instanceof AnchoredImage) {
            return SvgElements.create(null, (AnchoredImage) primitive, sty);
        } else if (primitive instanceof AnchoredIcon) {
            return SvgElements.create(null, (AnchoredIcon) primitive, sty);
        } else if (primitive instanceof Point2D) {
            return SvgElements.create(null, (Point2D) primitive, sty);
        } else {
            throw new IllegalArgumentException("Graphic conversion not supported for primitive "+primitive);
        }
    }

    /** Converts a labeled shape to svg */
    private static SvgElement labeledShapeToSvg(LabeledShapeGraphic<Graphics2D> gfc) {
        SvgElement shape = primitiveStyleToSvg(gfc.getPrimitive(), gfc.renderStyle().flatCopy(), gfc.getRenderer());
        SvgElement text = labelToSvg(gfc);
        return text == null ? shape : SvgGroup.create(shape, text);
    }

    /** Generates element for object label */
    private static SvgElement labelToSvg(LabeledShapeGraphic<Graphics2D> gfc) {
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
            return SvgElements.createWrappedText(label, style, LabeledShapeGraphic.wrappedLabelBounds(gfc.getPrimitive()));
        } else if (textRend instanceof TextRenderer) {
            return primitiveStyleToSvg(new AnchoredText(label), style.flatCopy(), textRend);
        } else {
            LOG.log(Level.WARNING, "Unsupported text renderer: {0}", textRend);
            return null;
        }
    }
    
    //endregion
    
}
