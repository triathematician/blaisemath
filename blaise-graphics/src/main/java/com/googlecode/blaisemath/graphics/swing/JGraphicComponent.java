/**
 * JGraphicComponent.java
 * Created on Jul 30, 2009
 */

package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graphics.core.GMouseEvent;
import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.GraphicUtils;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.SetSelectionModel;
import com.googlecode.blaisemath.util.TransformedCoordinateSpace;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.annotation.Nullable;

/**
 * <p>
 *   Swing component that collects and draws shapes on a screen.
 *   The shapes and their styles are enclosed within a {@link JGraphicRoot} class,
 *   which also sets up the requisite mouse handling and manages the drawing.
 * </p>
 * <p>
 *   This class is not designed for serialization.
 * </p>
 *
 * @see JGraphicRoot
 *
 * @author Elisha Peterson
 */
public class JGraphicComponent extends javax.swing.JComponent implements TransformedCoordinateSpace {

    /** The visible shapes. */
    protected final transient JGraphicRoot root;
    /** Affine transform applied to graphics canvas before drawing (enables pan and zoom). */
    @Nullable
    protected AffineTransform transform = null;
    /** Store inverse transform */
    @Nullable
    protected AffineTransform inverseTransform = null;
    
    /** Underlay painters */
    protected final transient List<CanvasPainter> underlays = Lists.newArrayList();
    /** Overlay painters */
    protected final transient List<CanvasPainter> overlays = Lists.newArrayList();
    
    /** Used for selecting graphics */
    protected final transient JGraphicSelectionHandler selector = new JGraphicSelectionHandler(this);

    /** Whether antialias is enabled */
    protected boolean antialias = true;

    // CONSTRUCTOR

    /**
     * Construction of a generic graphics view component.
     */
    public JGraphicComponent() {
        root = new JGraphicRoot(this);
        selector.setSelectionEnabled(false);
        addMouseListener(selector);
        addMouseMotionListener(selector);
        overlays.add(selector);

        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(300, 200));
        // this line enables tooltips
        setToolTipText("");
    }

    /**
     * Return the tooltip associated with the mouse event's point.
     * This will look for the topmost {@link Graphic} beneath the mouse and return that.
     * @param event the event with the point for the tooltip
     * @return tooltip for the point
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        String ct = root.getTooltip(toGraphicCoordinate(event.getPoint()));
        return ct != null ? ct 
                : "".equals(super.getToolTipText()) ? null 
                : super.getToolTipText();
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return graphic root managing the shapes to be rendered
     * @return shapes root
     */
    public JGraphicRoot getGraphicRoot() {
        return root;
    }

    /**
     * Return the default render factory used to draw shapes
     * @return current style provider used to draw shapes in the component
     */
    public StyleContext getStyleContext() {
        return root.getStyleContext();
    }

    /**
     * Sets the default render factory used to draw shapes
     * @param factory render factory
     * @throws IllegalArgumentException if the factory is null
     */
    public void setStyleContext(StyleContext factory) {
        root.setStyleContext(factory);
    }


    /**
     * If the mouse control allowing for selection of graphic objects is currently active
     * @return true if enabled, false if not
     */
    public boolean isSelectionEnabled() {
        return selector.isSelectionEnabled();
    }

    /**
     * Enable/disable the mouse control allowing for selection of graphic objects.
     * @param b true to enable, false to disable
     */
    public void setSelectionEnabled(boolean b) {
        selector.setSelectionEnabled(b);
    }

    public SetSelectionModel<Graphic<Graphics2D>> getSelectionModel() {
        return selector.getSelectionModel();
    }

    /**
     * Return true if antialias is enabled
     * @return antialias setting
     */
    public boolean isAntialiasOn() {
        return antialias;
    }

    /**
     * Sets antialias status
     * @param aa antialias status
     */
    public void setAntialiasOn(boolean aa) {
        antialias = aa;
        repaint();
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="DELEGATES">
    //
    // DELEGATES
    //
    
    

    /**
     * Add graphics to the component
     * @param add graphics to add
     */
    public final void addGraphics(Iterable<? extends Graphic<Graphics2D>> add) {
        root.addGraphics(add);
    }

    /**
     * Add a single graphic to the component
     * @param gfc graphic to add
     */
    public final void addGraphic(Graphic<Graphics2D> gfc) {
        root.addGraphic(gfc);
    }

    /**
     * Remove graphics from the component
     * @param remove graphics to remove
     */
    public final void removeGraphics(Iterable<? extends Graphic<Graphics2D>> remove) {
        root.removeGraphics(remove);
    }

    /**
     * Remove a single graphic from the component
     * @param gfc graphic to remove
     */
    public void removeGraphic(Graphic<Graphics2D> gfc) {
        root.removeGraphic(gfc);
    }

    /**
     * Remove all graphics from the component.
     */
    public void clearGraphics() {
        root.clearGraphics();
    }

    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="CANVAS TRANSFORM">
    
    @Nullable
    @Override
    public AffineTransform getTransform() {
        return transform;
    }
    
    @Nullable
    @Override
    public AffineTransform getInverseTransform() {
        return inverseTransform;
    }
    
    /**
     * Set thee transform used for drawing objects on the canvas.
     * @param at the transform
     * @throws IllegalArgumentException if the transform is non-null but not invertible
     */
    @Override
    public void setTransform(@Nullable AffineTransform at) {
        if (at == null) {
            transform = null;
            inverseTransform = null;
        } else {
            checkArgument(at.getDeterminant() != 0);
            transform = at;
            try {
                inverseTransform = at.createInverse();
            } catch (NoninvertibleTransformException ex) {
                throw new IllegalStateException("Already checked that the argument is invertible...", ex);
            }
        }
        repaint();
    }
    
    /**
     * Reset transform to the default.
     */
    public void resetTransform() {
        setTransform(null);
    }
    
    /**
     * Set transform to include all components in the graphic tree. Does nothing
     * if there are no graphics.
     */
    public void zoomToAll() {
        zoomToAll(new Insets(0, 0, 0, 0));
    }

    /**
     * Set transform to include all components in the graphic tree inside display
     * area minus insets.
     * @param insets insets for zoom
     */
    public void zoomToAll(Insets insets) {
        Rectangle2D bounds = getGraphicRoot().boundingBox();
        if (bounds != null) {
            double minX = bounds.getMinX() - insets.left;
            double maxX = Math.max(minX, bounds.getMaxX() - insets.right);
            double minY = bounds.getMinY() - insets.top;
            double maxY = Math.max(minY, bounds.getMaxY() - insets.bottom);
            PanAndZoomHandler.zoomCoordBoxAnimated(this, 
                    new Point2D.Double(minX, minY),
                    new Point2D.Double(maxX, maxY));
        }
    }
    
    /**
     * Zooms in in to the graphics canvas.
     */
    public void zoomIn() {
        PanAndZoomHandler.zoomIn(this);
    }
    
    /**
     * Zooms out of the graphics canvas.
     */
    public void zoomOut() {
        PanAndZoomHandler.zoomOut(this);
    }
    
    /**
     * Set transform to include all selected components. Does nothing if nothing
     * is selected.
     */
    public void zoomToSelected() {
        Rectangle2D bounds = GraphicUtils.boundingBox(getSelectionModel().getSelection());
        if (bounds != null) {
            PanAndZoomHandler.zoomCoordBoxAnimated(this, new Point2D.Double(
                    bounds.getMinX(), bounds.getMinY()),
                    new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()));
        }
    }
    
    /**
     * Convert window point location to graphic root location
     * @param winLoc window location
     * @return graphic coordinate system location
     */
    @Override
    public Point2D toGraphicCoordinate(Point2D winLoc) {
        return inverseTransform == null ? winLoc : inverseTransform.transform(winLoc, null);
    }
    
    /**
     * Convert mouse event to local coordinate space
     * @param winEvent event in windows coordinate space
     * @return event w/ location in local coordinate space
     */
    public GMouseEvent toGraphicCoordinateSpace(MouseEvent winEvent) {
        Point2D loc = toGraphicCoordinate(winEvent.getPoint());
        return new GMouseEvent(winEvent, loc, null);
    }
    
    /**
     * Return the graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic graphicAt(Point winLoc) {
        return root.graphicAt(toGraphicCoordinate(winLoc));
    }
    
    /**
     * Return the functional graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic functionalGraphicAt(Point winLoc) {
        return root.mouseGraphicAt(toGraphicCoordinate(winLoc));
    }
    
    /**
     * Return the selectable graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic selectableGraphicAt(Point winLoc) {
        return root.selectableGraphicAt(toGraphicCoordinate(winLoc));
    }
    
    //</editor-fold>
    

    //
    // PAINT METHODS
    //

    /**
     * Return modifiable list of overlay painters
     * @return list
     */
    public List<CanvasPainter> getOverlays() {
        return overlays;
    }

    /**
     * Return modifiable list of underlay painters
     * @return list
     */
    public List<CanvasPainter> getUnderlays() {
        return underlays;
    }

    /**
     * Paints the graphics to the specified canvas.
     * @param g graphics object
     */
    @Override
    protected void paintChildren(Graphics g) {
        renderTo((Graphics2D) g);
        super.paintChildren(g);
    }

    /**
     * Renders all shapes in root to specified graphics object.
     * @param canvas graphics canvas to render to
     */
    public void renderTo(Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
        if (isOpaque()) {
            canvas.setColor(getBackground());
            canvas.fillRect(0, 0, getWidth(), getHeight());
        }
        renderUnderlay(canvas);
        if (transform == null) {
            root.renderTo(canvas);
        } else {
            AffineTransform priorTransform = canvas.getTransform();
            canvas.transform(transform);
            root.renderTo(canvas);
            canvas.setTransform(priorTransform);
        }
        renderOverlay(canvas);
    }

    /**
     * Hook to render underlay elements. Called after the background is drawn,
     * but before anything else.
     * @param canvas the canvas to render to
     */
    protected void renderUnderlay(Graphics2D canvas) {
        for (CanvasPainter p : underlays) {
            p.paint(this, canvas);
        }
    }

    /**
     * Hook to render overlay elements. Called after everything else is drawn.
     * @param canvas the canvas to render to
     */
    protected void renderOverlay(Graphics2D canvas) {
        for (CanvasPainter p : overlays) {
            p.paint(this, canvas);
        }
    }

}
