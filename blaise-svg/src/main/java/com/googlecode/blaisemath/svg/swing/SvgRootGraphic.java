package com.googlecode.blaisemath.svg.swing;

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
import com.googlecode.blaisemath.svg.internal.SvgUtils;
import com.googlecode.blaisemath.svg.reader.SvgGroupReader;
import com.googlecode.blaisemath.svg.reader.SvgReadException;
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
 * Uses an {@link SvgRoot} as a primitive to be rendered on a {@link JGraphicComponent}. The root element is used to set
 * the viewbox (coordinates for the source graphic) fits within the viewport (coordinates on the target canvas).
 * Otherwise, the elements will be rendered without transformations or clipping.
 *
 * @author Elisha Peterson
 */
public class SvgRootGraphic extends SvgGraphic {

    private static final Logger LOG = Logger.getLogger(SvgRootGraphic.class.getName());

    /** Source SVG element to be drawn */
    private SvgRoot element;
    /** The graphic object that will be rendered */
    private Graphic<Graphics2D> primitiveElement;

    /** Whether viewport should be rendered */
    private boolean renderViewport = false;
    /** Whether background should be rendered (will be limited to the viewport) */
    private boolean renderBackground = true;
    /** Whether viewbox should be rendered */
    private boolean renderViewBox = false;

    /**
     * Create a new instance for the provided SVG.
     * @param element SVG to draw
     * @return graphic
     */
    public static SvgRootGraphic create(SvgRoot element) {
        checkNotNull(element);
        SvgRootGraphic res = new SvgRootGraphic();
        res.setElement(element);
        return res;
    }

    /**
     * Create a new instance for the provided SVG.
     * @param svg svg as a string
     * @return graphic
     */
    public static SvgRootGraphic create(String svg) throws IOException{
        return create(SvgIo.read(svg));
    }

    //region PROPERTIES

    public SvgRoot getElement() {
        return element;
    }

    public void setElement(SvgRoot el) {
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

    public boolean isRenderViewport() {
        return renderViewport;
    }

    public void setRenderViewport(boolean renderViewport) {
        this.renderViewport = renderViewport;
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

    /** Update the graphic, and the viewport/viewBox parameters if the root element is {@link SvgRoot}. */
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
            viewport = new Rectangle2D.Double(0, 0, element.getWidth(), element.getHeight());
            viewBox = element.getViewBoxAsRectangle();
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
        return norm == null ? null : tx.createTransformedShape(norm).getBounds2D();
    }

    @Override
    public boolean contains(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return viewBox.contains(tp) && super.contains(tp, canvas);
    }

    @Override
    public boolean intersects(Rectangle2D box, Graphics2D canvas) {
        Rectangle2D clippedBox = transform(box).createIntersection(viewBox);
        return clippedBox.getWidth() >= 0 && clippedBox.getHeight() >= 0 && super.intersects(clippedBox, canvas);
    }

    @Override
    public Graphic<Graphics2D> graphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !viewBox.contains(tp) ? null : super.graphicAt(tp, canvas);
    }

    @Override
    public Graphic<Graphics2D> selectableGraphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !viewBox.contains(tp) ? null : super.selectableGraphicAt(tp, canvas);
    }

    @Override
    public Set<Graphic<Graphics2D>> selectableGraphicsIn(Rectangle2D box, Graphics2D canvas) {
        Rectangle2D clippedBox = transform(box).createIntersection(viewBox);
        return clippedBox.getWidth() <= 0 || clippedBox.getHeight() <= 0 ? Collections.emptySet()
                : super.selectableGraphicsIn(clippedBox, canvas);
    }

    @Override
    public Graphic<Graphics2D> mouseGraphicAt(Point2D point, Graphics2D canvas) {
        Point2D tp = transform(point);
        return !viewBox.contains(tp)  ? null : super.mouseGraphicAt(tp, canvas);
    }

    @Override
    public String getTooltip(Point2D p, Graphics2D canvas) {
        Point2D tp = transform(p);
        return !viewBox.contains(tp)  ? null : super.getTooltip(tp, canvas);
    }

    @Override
    public void initContextMenu(JPopupMenu menu, Graphic<Graphics2D> src, Point2D point, Object focus, Set<Graphic<Graphics2D>> selection, Graphics2D canvas) {
        Point2D tp = transform(point);
        if (viewBox.contains(tp)) {
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

    //endregion

    @Override
    public void renderTo(Graphics2D canvas) {
        AffineTransform tx = transform();
        maybeRenderViewport(canvas);
        if (element == null) {
            return;
        }
        maybeRenderBackground(canvas);

        AffineTransform original = canvas.getTransform();
        Shape oldClip = canvas.getClip();
        canvas.transform(tx);
        if (oldClip != null) {
            canvas.setClip(viewBox.createIntersection(transform(oldClip.getBounds2D())));
        }
        maybeRenderViewBox(canvas);
        super.renderTo(canvas);

        canvas.setTransform(original);
        canvas.setClip(oldClip);
    }

    private void maybeRenderViewport(Graphics2D canvas) {
        if (renderViewport) {
            canvas.setStroke(new BasicStroke(1f));
            canvas.setColor(Color.red);
            canvas.draw(viewport);
        }
    }

    private void maybeRenderBackground(Graphics2D canvas) {
        if (renderBackground) {
            SvgUtils.backgroundColor(element).ifPresent(bg -> {
                canvas.setColor(bg);
                canvas.fill(viewport);
            });
        }
    }

    private void maybeRenderViewBox(Graphics2D canvas) {
        if (renderViewBox) {
            canvas.setStroke(new BasicStroke(1f));
            canvas.setColor(Color.blue);
            canvas.draw(viewBox);
        }
    }

}
