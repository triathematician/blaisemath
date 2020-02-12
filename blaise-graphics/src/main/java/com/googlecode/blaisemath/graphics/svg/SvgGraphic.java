package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.geom.AffineTransformBuilder;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicComposite;
import com.googlecode.blaisemath.style.AttributeSet;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A graphic designed to contain SVG content, to be rendered on a canvas. When drawn the source content will draw within
 * the target location on the canvas (defined by the {@code canvasBounds} property). To do this mapping, source content
 * should set eithr the {@code viewBox} field, to specify the boundaries to be fit explicitly, or else the
 *
 * @author Elisha Peterson
 */
@Beta
public abstract class SvgGraphic extends GraphicComposite<Graphics2D> {

    private static final Logger LOG = Logger.getLogger(SvgGraphic.class.getName());

    public static final String CANVAS_BOUNDS = "canvasBounds";
    public static final String VIEW_BOX = "viewBox";
    public static final String SIZE = "size";

    /** Width/height of the content. */
    protected @Nullable Dimension size = new Dimension(100, 100);
    /** View box of the content to be drawn (in SVG coordinates) */
    protected @Nullable Rectangle2D viewBox;
    /** Boundary of view box in target canvas. */
    protected @Nullable Rectangle2D canvasBounds;
    
    //region PROPERTIES

    public @Nullable Dimension getSize() {
        return size;
    }

    public void setSize(@Nullable Dimension size) {
        if (this.size != size) {
            Object old = this.size;
            this.size = size;
            fireGraphicChanged();
            pcs.firePropertyChange(SIZE, old, style);
        }
    }
    
    public int getWidth() {
        return size == null ? 0 : size.width;
    }

    public int getHeight() {
        return size == null ? 0 : size.height;
    }

    public @Nullable Rectangle2D getViewBox() {
        return viewBox;
    }

    public void setViewBox(@Nullable Rectangle2D viewBox) {
        if (this.viewBox != viewBox) {
            Object old = this.viewBox;
            this.viewBox = viewBox;
            fireGraphicChanged();
            pcs.firePropertyChange(VIEW_BOX, old, viewBox);
        }
    }

    public @Nullable Rectangle2D getCanvasBounds() {
        return canvasBounds;
    }

    public void setCanvasBounds(@Nullable Rectangle2D bounds) {
        if (this.canvasBounds != bounds) {
            Object old = this.canvasBounds;
            this.canvasBounds = bounds;
            fireGraphicChanged();
            pcs.firePropertyChange(CANVAS_BOUNDS, old, bounds);
        }
    }
    
    //endregion

    /** Generate transform used to scale/translate the SVG. Transforms the view box to within the graphic bounds. */
    protected @Nullable AffineTransform transform() {
        return canvasBounds == null || viewBox == null ? null
                : AffineTransformBuilder.transformingTo(canvasBounds, viewBox);
    }

    /** Inverse transform. Transforms the graphic bounds to the view box. */
    protected @Nullable AffineTransform inverseTransform() {
        AffineTransform tx = transform();
        try {
            return tx == null ? null : tx.createInverse();
        } catch (NoninvertibleTransformException ex) {
            LOG.log(Level.SEVERE, "Unexpected", ex);
            return null;
        }
    }

}
