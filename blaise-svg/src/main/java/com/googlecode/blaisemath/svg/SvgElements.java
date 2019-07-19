package com.googlecode.blaisemath.svg;

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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.geom.*;
import com.googlecode.blaisemath.graphics.swing.StyledText;
import com.googlecode.blaisemath.graphics.swing.WrappedTextRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Renderer;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.Images;
import com.googlecode.blaisemath.coordinate.OrientedPoint2D;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

/**
 * Factory methods for converting to/from SVG Objects.
 *
 * @author Elisha Peterson
 */
public class SvgElements {

    private static final Logger LOG = Logger.getLogger(SvgElements.class.getName());
    
    // UTILITY CLASS - PREVENT INSTANTIATION
    private SvgElements() {
    }

    /**
     * Create new svg object from given id, shape, and style. Supports shapes
     * {@link Rectangle2D}, {@link RoundRectangle2D}, {@link Ellipse2D}, and
     * {@link Line2D}.
     * @param id object id
     * @param shape object's shape
     * @param style object's style
     * @return svg object
     */
    public static SvgElement create(String id, Shape shape, AttributeSet style) {
        SvgElement res;
        if (shape instanceof Rectangle2D || shape instanceof RoundRectangle2D) {
            res = SvgRectangle.shapeConverter().reverse().convert((RectangularShape) shape);
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D ell = (Ellipse2D) shape;
            if (ell.getWidth() == ell.getHeight()) {
                res = SvgCircle.shapeConverter().reverse().convert(ell);
            } else {
                res = SvgEllipse.shapeConverter().reverse().convert(ell);
            }
        } else if (shape instanceof Line2D) {
            res = SvgLine.shapeConverter().reverse().convert((Line2D) shape);
        } else if (shape instanceof Path2D) {
            res = SvgPath.shapeConverter().reverse().convert((Path2D) shape);
        } else if (shape instanceof Area) {
            res = SvgPath.create(shape.getPathIterator(null));
        } else {
            Logger.getLogger(SvgElements.class.getName()).log(Level.WARNING, "Shapes of type {0} are not yet supported.", shape.getClass());
            return null;
        }
        res.setId(id);
        res.setStyle(AttributeSet.create(style.getAttributeMap()));
        if (style.get(Styles.FILL) == null) {
            res.getStyle().put(Styles.FILL, null);
        }
        if (style.get(Styles.STROKE) == null) {
            res.getStyle().put(Styles.STROKE, null);
        }
        return res;
    }

    /**
     * Create new svg object from a provided point/style.
     * @param id object id
     * @param point the point
     * @param style object's style
     * @return svg object
     */
    public static SvgElement create(String id, Point2D point, AttributeSet style) {
        Marker m = (Marker) style.get(Styles.MARKER);
        if (m == null) {
            m = Markers.CIRCLE;
        }
        Float ort = style.getFloat(Styles.MARKER_ORIENT, point instanceof OrientedPoint2D ? (float) ((OrientedPoint2D)point).getAngle() : 0f);
        Float rad = style.getFloat(Styles.MARKER_RADIUS, 4f);
        Shape shape = m.create(point, ort, rad);
        return create(id, shape, style);
    }

    /**
     * Create new svg text object from given id, anchored text, and style.
     * @param id object id
     * @param text object text
     * @param style object's style
     * @param rend the text renderer
     * @return svg text object
     */
    public static SvgElement create(String id, AnchoredText text, AttributeSet style, @Nullable Renderer rend) {
        if (rend instanceof WrappedTextRenderer) {
            SvgElement res = createWrappedText(text.getText(), style, ((WrappedTextRenderer) rend).getTextBounds());
            res.setId(id);
            return res;
        }
        SvgText res = SvgText.textConverter().reverse().convert(text);
        res.setId(id);
        Object ta = style.get(Styles.TEXT_ANCHOR);
        AttributeSet copy = AttributeSet.create(style.getAttributeMap());
        if (ta instanceof Anchor) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor((Anchor) ta));
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline((Anchor) ta));
        } else if (ta instanceof String && Styles.isAnchorName(ta)) {
            copy.put(Styles.TEXT_ANCHOR, Styles.toTextAnchor((String) ta));
            copy.put(Styles.ALIGN_BASELINE, Styles.toAlignBaseline((String) ta));
        }
        res.setStyle(copy);
        return res;
    }

    /** 
     * Generates group of text elements formed by wrapping text 
     * @param text text
     * @param style text style
     * @param bounds text bounds
     * @return group element with several lines (or text element if a single line)
     */
    public static SvgElement createWrappedText(String text, AttributeSet style, RectangularShape bounds) {
        Graphics2D testCanvas = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
        WrappedTextRenderer rend = new WrappedTextRenderer();
        rend.setMinWidthFactor(0);
        rend.setMaxReduceFontSize(0);
        Iterable<StyledText> lines = rend.computeLines(text, style, bounds, WrappedTextRenderer.defaultInsets(), testCanvas);
        
        SvgGroup grp = new SvgGroup();
        for (StyledText st : lines) {
            grp.addElement(create(null, st.getText(), st.getStyle(), null));
        }
        
        SvgElement res = grp.getElements().size() == 1 ? grp.getElements().get(0) : grp;
        res.setStyle(AttributeSet.create(style.getAttributeMap()));
        return res;
    }

    /**
     * Create new svg image object from given id, anchored image, and style.
     * @param id object id
     * @param img image object
     * @param style object's style
     * @return svg image object
     */
    public static SvgImage create(String id, AnchoredImage img, AttributeSet style) {
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        Point2D offset = anchor.offsetForRectangle(img.getWidth(), img.getHeight());
        AnchoredImage adjustedImage = new AnchoredImage(
                img.getX() + offset.getX(), 
                img.getY() - img.getHeight()+ offset.getY(), 
                (double) img.getWidth(), (double) img.getHeight(),
                img.getOriginalImage(), img.getReference());
        SvgImage res = SvgImage.imageConverter().reverse().convert(adjustedImage);
        res.setId(id);
        res.setStyle(AttributeSet.create(style.getAttributeMap()));
        return res;
    }

    /**
     * Create new svg image object from given id, anchored icon, and style.
     * @param id object id
     * @param icon icon object
     * @param style object's style
     * @return svg image object
     */
    public static SvgImage create(String id, AnchoredIcon icon, AttributeSet style) {
        Anchor anchor = Styles.anchorOf(style, Anchor.NORTHWEST);
        Point2D offset = anchor.offsetForRectangle(icon.getIconWidth(), icon.getIconHeight());
        SvgImage res = new SvgImage(
                icon.getX() + offset.getX(), 
                icon.getY() - icon.getIconHeight() + offset.getY(), 
                (double) icon.getIconWidth(), (double) icon.getIconHeight(), 
                encodeAsUri(icon.getIcon()));
        res.setId(id);
        AttributeSet sty = AttributeSet.create(style.getAttributeMap());
        sty.remove(Styles.TEXT_ANCHOR);
        sty.remove(Styles.ALIGN_BASELINE);
        res.setStyle(sty);
        return res;        
    }
    
    private static String encodeAsUri(Icon icon) {
        try {
            BufferedImage bi = new BufferedImage(2*icon.getIconWidth(), 2*icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setTransform(AffineTransform.getScaleInstance(2, 2));
            icon.paintIcon(null, g2, 0, 0);
            return Images.encodeDataUriBase64(bi, "png");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Encoding error", ex);
            return "";
        }
    }

    /**
     * Return true if element is a path type (i.e. no fill expected)
     * @param el an element
     * @return true if its a path
     */
    public static boolean isPath(SvgElement el) {
        return el instanceof SvgLine || el instanceof SvgPolyline;
    }
    
    /** 
     * Return an iterator for an element. Will iterate over this element, and if
     * a group, all nested children of this element. Groups are added before their
     * children.
     * @param element the element to start with
     * @return iteration over this element, and all of its child elements
     */
    public static Iterable<SvgElement> shapeIterator(SvgElement element) {
        List<SvgElement> res = Lists.newArrayList();
        res.add(element);
        if (element instanceof SvgGroup) {
            for (SvgElement el : ((SvgGroup)element).getElements()) {
                Iterables.addAll(res, shapeIterator(el));
            }
        }
        return res;
    }
    
}
