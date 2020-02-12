package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseSVG
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.GraphicUtils;
import com.googlecode.blaisemath.graphics.svg.SvgGraphic;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.svg.reader.SvgGroupReader;
import com.googlecode.blaisemath.svg.reader.SvgReadException;
import com.googlecode.blaisemath.svg.xml.SvgElement;
import com.googlecode.blaisemath.svg.xml.SvgIo;
import com.googlecode.blaisemath.svg.xml.SvgRoot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Uses an {@link SvgElement} as a primitive to be rendered on a {@link JGraphicComponent}. If the primitive is an {@link SvgRoot}
 * with a non-null view box, then that view box will be resized to fit within the {@code graphicBounds} specified in this object.
 * Otherwise, if not an {@code SvgRoot} or if the view box or graphic bounds are not available, the elements are rendered using
 * without transformations.
 * 
 * @author Elisha Peterson
 */
public class SvgElementGraphic extends SvgGraphic {

    private static final Logger LOG = Logger.getLogger(SvgElementGraphic.class.getName());
    
    /** Source SVG element to be drawn */
    private SvgElement element;
    /** The graphic object that will be rendered */
    private Graphic<Graphics2D> primitiveElement;

    /** Whether background should be rendered */
    private boolean renderBackground = true;
    /** Whether bounding box should be rendered */
    private boolean renderCanvasBounds = false;
    /** Whether view box should be rendered */
    private boolean renderViewBox = false;
    
    /**
     * Create a new instance for the provided SVG.
     * @param element SVG to draw
     * @return graphic
     */
    public static SvgElementGraphic create(SvgElement element) {
        checkNotNull(element);
        SvgElementGraphic res = new SvgElementGraphic();
        res.setElement(element);
        return res;
    }
    
    /**
     * Create a new instance for the provided SVG.
     * @param svg svg as a string
     * @return graphic
     */
    public static SvgElementGraphic create(String svg) {
        try {
            return create(SvgIo.read(svg));
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Invalid SVG", ex);
            return new SvgElementGraphic();
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

    public boolean isRenderBackground() {
        return renderBackground;
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
        fireGraphicChanged();
    }

    public boolean isRenderCanvasBounds() {
        return renderCanvasBounds;
    }

    public void setRenderCanvasBounds(boolean renderCanvasBounds) {
        this.renderCanvasBounds = renderCanvasBounds;
        fireGraphicChanged();
    }

    public boolean isRenderViewBox() {
        return renderViewBox;
    }

    public void setRenderViewBox(boolean renderViewBox) {
        this.renderViewBox = renderViewBox;
        fireGraphicChanged();
    }

    //endregion

    private void updateGraphics() {
        Graphic<Graphics2D> nue;
        try {
            nue = SvgGroupReader.readGraphic(element);
            if (primitiveElement == null) {
                addGraphic(nue);
            } else {
                replaceGraphics(Collections.singleton(primitiveElement), Collections.singleton(nue));
            }
            primitiveElement = nue;

            if (element instanceof SvgRoot) {
                Integer wid = ((SvgRoot) element).getWidth();
                Integer ht = ((SvgRoot) element).getHeight();
                this.size = wid == null || ht == null ? null : new Dimension(wid, ht);
                this.canvasBounds = wid == null || ht == null ? null : new Rectangle2D.Double(0, 0, wid, ht);
                this.viewBox = ((SvgRoot) element).getViewBoxAsRectangle();
            }
        } catch (SvgReadException e) {
            LOG.log(Level.SEVERE, "Unable to read SVG", e);
            primitiveElement = null;
            clearGraphics();
        }
    }

    //region GRAPHICS API LOCATIONS

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
        Rectangle2D tbox = viewBox == null ? transform(box) : transform(box).createIntersection(viewBox);
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
        Rectangle2D tp = viewBox == null ? transform(box) : transform(box).createIntersection(viewBox);
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

    private Point2D transform(Point2D pt) {
        AffineTransform tx = inverseTransform();
        return tx == null ? pt : tx.transform(pt, null);
    }

    private Rectangle2D transform(Rectangle2D box) {
        AffineTransform tx = inverseTransform();
        return tx == null ? box : tx.createTransformedShape(box).getBounds2D();
    }

    /** Test if point is in view box, where point is in svg coords */
    private boolean inViewBox(Point2D pt) {
        return viewBox == null || viewBox.contains(pt);
    }

    //endregion

    @Override
    public void renderTo(Graphics2D canvas) {
        AffineTransform tx = transform();
        maybeRenderCanvasBounds(canvas);
        if (element == null) {
            return;
        }
        maybeRenderBackground(canvas);
        if (tx == null) {
            super.renderTo(canvas);
        } else {
            AffineTransform original = canvas.getTransform();
            Shape oldClip = canvas.getClip();
            canvas.transform(tx);
            if (oldClip != null) {
                canvas.setClip(viewBox.createIntersection(transform(oldClip.getBounds2D())));
            }
            maybeRenderViewBox(canvas, viewBox);
            super.renderTo(canvas);
            canvas.setTransform(original);
            canvas.setClip(oldClip);
        }
    }

    private void maybeRenderBackground(Graphics2D canvas) {
        if (renderBackground) {
            SvgUtils.backgroundColor(element).ifPresent(bg -> {
                if (canvasBounds != null) {
                    canvas.setColor(bg);
                    canvas.fill(canvasBounds);
                }
            });
        }
    }

    private void maybeRenderCanvasBounds(Graphics2D canvas) {
        if (renderCanvasBounds && canvasBounds != null) {
            canvas.setStroke(new BasicStroke(1f));
            canvas.setColor(Color.red);
            canvas.draw(canvasBounds);
        }
    }

    private void maybeRenderViewBox(Graphics2D canvas, Rectangle2D viewBox) {
        if (renderViewBox) {
            canvas.setStroke(new BasicStroke(1f));
            canvas.setColor(Color.blue);
            canvas.draw(viewBox);
        }
    }

}
