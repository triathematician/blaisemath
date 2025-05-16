package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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
import com.googlecode.blaisemath.graphics.GraphicComposite;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * A graphic designed to contain SVG content, to be rendered on a canvas. When drawn the source content will draw within
 * the target location on the canvas (defined by the {@code canvasBounds} property). To do this mapping, source content
 * should set either the {@code viewBox} field, to specify the boundaries to be fit explicitly, or else the
 *
 * @author Elisha Peterson
 */
@Beta
public abstract class SvgGraphic extends GraphicComposite<Graphics2D> {

    private static final Logger LOG = Logger.getLogger(SvgGraphic.class.getName());

    public static final String CANVAS_BOUNDS = "canvasBounds";
    public static final String VIEW_BOX = "viewBox";
    public static final String SIZE = "size";

    /** View box, representing the dimensions of the source SVG content. */
    protected @NonNull Rectangle2D viewBox = new Rectangle2D.Double(0, 0, 100, 100);
    /** Viewport, representing the target rectangle on the canvas for content. */
    protected @NonNull Rectangle2D viewport = new Rectangle2D.Double(0, 0, 100, 100);
    
    //region PROPERTIES

    public @NonNull Dimension getSize() {
        return new Dimension((int) viewport.getWidth(), (int) viewport.getHeight());
    }

    public @NonNull Rectangle2D getViewBox() {
        return viewBox;
    }

    public void setViewBox(@NonNull Rectangle2D viewBox) {
        if (this.viewBox != requireNonNull(viewBox)) {
            Object old = this.viewBox;
            this.viewBox = viewBox;
            fireGraphicChanged();
            pcs.firePropertyChange(VIEW_BOX, old, viewBox);
        }
    }

    public @NonNull Rectangle2D getViewport() {
        return viewport;
    }

    public void setViewport(@NonNull Rectangle2D bounds) {
        if (this.viewport != requireNonNull(bounds)) {
            Object old = this.viewport;
            Object oldSize = getSize();
            this.viewport = bounds;
            fireGraphicChanged();
            pcs.firePropertyChange(CANVAS_BOUNDS, old, bounds);
            pcs.firePropertyChange(SIZE, oldSize, getSize());
        }
    }
    
    //endregion

    /** Generate transform used to scale/translate the SVG. Transforms the viewbox to the viewport. */
    protected AffineTransform transform() {
        return AffineTransformBuilder.transformingTo(viewport, viewBox);
    }

    /** Inverse transform. Transforms the graphic bounds to the view box. */
    protected @Nullable AffineTransform inverseTransform() {
        try {
            return transform().createInverse();
        } catch (NoninvertibleTransformException ex) {
            LOG.log(Level.SEVERE, "Target viewbox has 0 width or height!", ex);
            return null;
        }
    }

}
