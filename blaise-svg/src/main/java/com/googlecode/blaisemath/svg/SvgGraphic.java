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

import com.googlecode.blaisemath.geom.AffineTransformBuilder;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.graphics.GraphicUtils;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.reader.SvgGroupReader;
import com.googlecode.blaisemath.svg.reader.SvgReadException;
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgRoot;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Uses an {@link SvgElement} as a primitive to be rendered on a {@link JGraphicComponent}.
 * Allows setting bounding boxes, so that the SVG element can be scaled or moved on the canvas.
 * 
 * @author Elisha Peterson
 */
public class SvgGraphic extends GraphicComposite<Graphics2D> {

    private static final Logger LOG = Logger.getLogger(SvgGraphic.class.getName());
    private static final boolean RENDER_BOUNDS = false;
    private static final boolean RENDER_VIEW_BOX = false;
    
    /** Source SVG element to be drawn */
    private SvgElement element;
    /** Describes where on the canvas the element will be drawn */
    private Rectangle2D graphicBounds;
    /** The graphic object that will be rendered */
    private Graphic<Graphics2D> primitiveElement;
    
    /**
     * Create a new instance for the provided SVG.
     * @param element SVG to draw
     * @return graphic
     */
    public static SvgGraphic create(SvgElement element) {
        checkNotNull(element);
        SvgGraphic res = new SvgGraphic();
        res.setElement(element);
        return res;
    }
    
    /**
     * Create a new instance for the provided SVG.
     * @param svg svg as a string
     * @return graphic
     */
    public static SvgGraphic create(String svg) {
        try {
            return create(SvgRoot.load(svg));
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Invalid SVG", ex);
            return new SvgGraphic();
        }
    }
    
    private void updateGraphics() {
        Graphic<Graphics2D> nue = null;
        try {
            nue = SvgGroupReader.readGraphic(element);
            if (primitiveElement == null) {
                addGraphic(nue);
            } else {
                replaceGraphics(Collections.singleton(primitiveElement), Collections.singleton(nue));
            }
            primitiveElement = nue;

            if (element instanceof SvgRoot) {
                double wid = ((SvgRoot) element).getWidth();
                double ht = ((SvgRoot) element).getHeight();
                this.graphicBounds = new Rectangle2D.Double(0, 0, wid, ht);
            }
        } catch (SvgReadException e) {
            LOG.log(Level.SEVERE, "Unable to read SVG", e);
            primitiveElement = null;
            clearGraphics();
        }
    }
    
    //region PROPERTIES

    public SvgElement getElement() {
        return element;
    }
    
    public void setElement(SvgElement el) {
        Object old = this.element;
        if (old != el) {
            this.element = el;
            updateGraphics();
        }
    }

    public Rectangle2D getGraphicBounds() {
        return graphicBounds;
    }

    public void setGraphicBounds(Rectangle2D graphicBounds) {
        this.graphicBounds = graphicBounds;
        fireGraphicChanged();
    }
    
    //endregion
    
    /** Generate transform used to scale/translate the SVG. Transforms the view box to within the graphic bounds. */
    private @Nullable AffineTransform transform() {
        if (graphicBounds == null || !(element instanceof SvgRoot)) {
            return null;
        }
        Rectangle2D viewBox = ((SvgRoot) element).getViewBoxAsRectangle();
        return viewBox == null ? null : AffineTransformBuilder.transformingTo(graphicBounds, viewBox);
    }
    
    /** Inverse transform. Transforms the graphic bounds to the view box. */
    private @Nullable AffineTransform inverseTransform() {
        if (graphicBounds == null || !(element instanceof SvgRoot)) {
            return null;
        }
        AffineTransform tx = transform();
        try {
            return tx == null || tx.getDeterminant() == 0 ? null : tx.createInverse();
        } catch (NoninvertibleTransformException ex) {
            LOG.log(Level.SEVERE, "Unexpected", ex);
            return null;
        }
    }

    @Override
    public Rectangle2D boundingBox(Graphics2D canvas) {
        AffineTransform tx = transform();
        Rectangle2D norm = GraphicUtils.boundingBox(entries, canvas);
        return tx == null || norm == null ? norm : tx.createTransformedShape(norm).getBounds2D();
    }

    @Override
    public boolean contains(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return inViewBox(tp) && super.contains(tp, canvas);
    }

    @Override
    public boolean intersects(Rectangle2D box, Graphics2D canvas) {
        Rectangle2D vb = viewBox();
        Rectangle2D tbox = vb == null ? transform(box) : transform(box).createIntersection(vb);
        return tbox.getWidth() >= 0 && tbox.getHeight() >= 0 && super.intersects(tbox, canvas);
    }

    @Override
    public Graphic<Graphics2D> graphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !inViewBox(tp) ? null : super.graphicAt(tp, canvas);
    }

    @Override
    public Graphic<Graphics2D> selectableGraphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !inViewBox(tp) ? null : super.selectableGraphicAt(tp, canvas);
    }

    @Override
    public Set<Graphic<Graphics2D>> selectableGraphicsIn(Rectangle2D box, Graphics2D canvas) {
        Rectangle2D vb = viewBox();
        Rectangle2D tp = vb == null ? transform(box) : transform(box).createIntersection(vb);
        return tp.getWidth() <= 0 || tp.getHeight() <= 0 ? Collections.emptySet()
                : super.selectableGraphicsIn(tp, canvas);
    }

    @Override
    public Graphic<Graphics2D> mouseGraphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !inViewBox(tp) ? null : super.mouseGraphicAt(tp, canvas);
    }

    @Override
    public String getTooltip(Point2D p, Graphics2D canvas) {
        Point2D tp = transform(p);
        return !inViewBox(tp) ? null : super.getTooltip(tp, canvas);
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<Graphics2D> src, Point2D point, Object focus, Set<Graphic<Graphics2D>> selection, Graphics2D canvas) {
        Point2D tp = transform(point);
        if (inViewBox(tp)) {
            super.initContextMenu(menu, src, tp, focus, selection, canvas);
        }
    }

    @Override
    public void renderTo(Graphics2D canvas) {
        AffineTransform tx = transform();
        if (RENDER_BOUNDS && graphicBounds != null) {
            canvas.setColor(Color.red);
            canvas.draw(graphicBounds);
        }
        if (tx == null) {
            super.renderTo(canvas);
        } else {
            AffineTransform original = canvas.getTransform();
            Shape oldClip = canvas.getClip();
            canvas.transform(tx);
            Rectangle2D viewBox = viewBox();
            if (oldClip != null) {
                canvas.setClip(viewBox.createIntersection(transform(oldClip.getBounds2D())));
            }
            if (RENDER_VIEW_BOX) {
                canvas.setColor(Color.blue);
                canvas.draw(viewBox);
            }
            super.renderTo(canvas);
            canvas.setTransform(original);
            canvas.setClip(oldClip);
        }
    }
    
    private Point2D transform(Point2D pt) {
        AffineTransform tx = inverseTransform();
        return tx == null ? pt : tx.transform(pt, null);
    }
    
    private Rectangle2D transform(Rectangle2D box) {
        AffineTransform tx = inverseTransform();
        return tx == null ? box : tx.createTransformedShape(box).getBounds2D();
    }
    
    private Rectangle2D viewBox() {
        return element instanceof SvgRoot ? ((SvgRoot) element).getViewBoxAsRectangle() : null;
    }

    /** Test if point is in view box, where point is in svg coords */
    private boolean inViewBox(Point2D pt) {
        Rectangle2D box = viewBox();
        return box == null || box.contains(pt);
    }

}
