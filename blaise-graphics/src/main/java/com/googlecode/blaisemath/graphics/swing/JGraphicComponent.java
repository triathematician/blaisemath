package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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

import com.googlecode.blaisemath.geom.TransformedCoordinateSpace;
import com.googlecode.blaisemath.util.swing.CanvasPainter;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graphics.GraphicMouseEvent;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.graphics.GraphicUtils;
import com.googlecode.blaisemath.style.StyleContext;
import com.googlecode.blaisemath.util.SetSelectionModel;
import com.googlecode.blaisemath.graphics.swing.JGraphicRoot;
import org.checkerframework.checker.nullness.qual.Nullable;
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

/**
 * Swing component that collects and draws shapes on a screen.
 * The shapes and their styles are enclosed within a {@link JGraphicRoot} class,
 * which also sets up the requisite mouse handling and manages the drawing.
 *
 * @see JGraphicRoot
 *
 * @author Elisha Peterson
 */
public class JGraphicComponent extends javax.swing.JComponent implements TransformedCoordinateSpace {

    public static final String P_TRANSFORM = "transform";

    /** The visible shapes. */
    protected final JGraphicRoot root;
    /** Affine transform applied to graphics canvas before drawing (enables pan and zoom). */
    protected @Nullable AffineTransform transform = null;
    /** Store inverse transform */
    protected @Nullable AffineTransform inverseTransform = null;

    /** Underlay painters */
    protected final List<CanvasPainter> underlays = Lists.newArrayList();
    /** Overlay painters */
    protected final List<CanvasPainter> overlays = Lists.newArrayList();

    /** Used for selecting graphics */
    protected final JGraphicSelectionHandler selector = new JGraphicSelectionHandler(this);

    /** Whether antialias is enabled */
    protected boolean antialias = true;

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

    //region PROPERTIES

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

    //endregion

    //region GRAPHICS MUTATORS

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

    //endregion

    //region CANVAS TRANSFORM

    @Override
    public @Nullable AffineTransform getTransform() {
        return transform;
    }

    @Override
    public @Nullable AffineTransform getInverseTransform() {
        return inverseTransform;
    }

    /**
     * Set the transform used for drawing objects on the canvas.
     * @param at the transform (null for identity transform)
     * @throws IllegalArgumentException if the transform is non-null but not invertible
     */
    @Override
    public void setTransform(@Nullable AffineTransform at) {
        checkArgument(at == null || at.getDeterminant() != 0);
        AffineTransform old = transform;
        if (old != at) {
            transform = at;
            try {
                inverseTransform = at == null ? null : at.createInverse();
            } catch (NoninvertibleTransformException ex) {
                throw new IllegalStateException("Already checked that the argument is invertible...", ex);
            }
            firePropertyChange(P_TRANSFORM, old, at);
            repaint();
        }
    }

    /**
     * Reset transform to the default.
     */
    public void resetTransform() {
        setTransform(null);
    }

    //endregion

    //region ZOOM OPERATIONS

    /**
     * Set transform to include all components in the graphic tree. Does nothing
     * if there are no graphics. Animates zoom operation.
     */
    public void zoomToAll() {
        zoomToAll(new Insets(0, 0, 0, 0), true);
    }

    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Animates zoom operation.
     *
     * @param outsets additional space to leave around the graphics
     */
    public void zoomToAll(Insets outsets) {
        zoomToAll(outsets, true);
    }

    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The insets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     *
     * @param outsets additional space to leave around the graphics
     * @param animate if true, zoom operation will animate
     */
    public void zoomToAll(Insets outsets, boolean animate) {
        Rectangle2D bounds = getGraphicRoot().boundingBox(canvas());
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, outsets);
        } else if (bounds != null) {
            instantZoomWithOutsets(bounds, outsets);
        }
    }

    /**
     * Zooms in in to the graphics canvas (animated).
     */
    public void zoomIn() {
        PanAndZoomHandler.zoomIn(this, true);
    }

    /**
     * Zooms in in to the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    public void zoomIn(boolean animate) {
        PanAndZoomHandler.zoomIn(this, animate);
    }

    /**
     * Zooms out of the graphics canvas (animated).
     */
    public void zoomOut() {
        PanAndZoomHandler.zoomOut(this, true);
    }

    /**
     * Zooms out of the graphics canvas.
     * @param animate if true, zoom operation will animate
     */
    public void zoomOut(boolean animate) {
        PanAndZoomHandler.zoomOut(this, animate);
    }

    /**
     * Set transform to include all selected components. Does nothing if nothing
     * is selected. Zoom is animated.
     */
    public void zoomToSelected() {
        zoomToSelected(new Insets(0, 0, 0, 0), true);
    }

    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     */
    public void zoomToSelected(Insets locCoordOutsets) {
        zoomToSelected(locCoordOutsets, true);
    }

    /**
     * Set transform to include all components in the graphic tree inside display
     * area plus insets. The outsets are expressed in local coordinates, not window
     * coordinates. Positive insets result in extra space around the graphics.
     * Zoom is animated.
     *
     * @param locCoordOutsets additional space to leave around the graphics (in local coordinate space)
     * @param animate if true, zoom operation will animate
     */
    public void zoomToSelected(Insets locCoordOutsets, boolean animate) {
        Rectangle2D bounds = GraphicUtils.boundingBox(getSelectionModel().getSelection(), canvas());
        if (bounds != null && animate) {
            animatedZoomWithOutsets(bounds, locCoordOutsets);
        } else if (bounds != null) {
            instantZoomWithOutsets(bounds, locCoordOutsets);
        }
    }

    /**
     * Utility method to animate the zoom operation to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private void animatedZoomWithOutsets(Rectangle2D bounds, Insets locCoordOutsets) {
        double minX = bounds.getMinX() - locCoordOutsets.left;
        double maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right);
        double minY = bounds.getMinY() - locCoordOutsets.top;
        double maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom);
        PanAndZoomHandler.zoomCoordBoxAnimated(this,
                new Point2D.Double(minX, minY),
                new Point2D.Double(maxX, maxY));
    }

    /**
     * Utility method to instantly change the zoom to the target local bounds.
     * @param bounds local bounds
     * @param locCoordOutsets outsets beyond the local bounds
     */
    private void instantZoomWithOutsets(Rectangle2D bounds, Insets locCoordOutsets) {
        double minX = bounds.getMinX() - locCoordOutsets.left;
        double maxX = Math.max(minX, bounds.getMaxX() + locCoordOutsets.right);
        double minY = bounds.getMinY() - locCoordOutsets.top;
        double maxY = Math.max(minY, bounds.getMaxY() + locCoordOutsets.bottom);
        PanAndZoomHandler.setDesiredLocalBounds(this,
                new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY));
    }

    //endregion

    //region GRAPHICS QUERIES

    /**
     * Return the tooltip associated with the mouse event's point.
     * This will look for the topmost {@link Graphic} beneath the mouse and return that.
     * @param event the event with the point for the tooltip
     * @return tooltip for the point
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        String ct = root.getTooltip(toGraphicCoordinate(event.getPoint()), null);
        return ct != null ? ct
                : "".equals(super.getToolTipText()) ? null
                : super.getToolTipText();
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
    public GraphicMouseEvent toGraphicCoordinateSpace(MouseEvent winEvent) {
        Point2D loc = toGraphicCoordinate(winEvent.getPoint());
        return new GraphicMouseEvent(winEvent, loc, null);
    }

    /**
     * Return the graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic<Graphics2D> graphicAt(Point winLoc) {
        return root.graphicAt(toGraphicCoordinate(winLoc), canvas());
    }

    /**
     * Return the functional graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic functionalGraphicAt(Point winLoc) {
        return root.mouseGraphicAt(toGraphicCoordinate(winLoc), canvas());
    }

    /**
     * Return the selectable graphic at the given window location
     * @param winLoc window location
     * @return graphic
     */
    public Graphic selectableGraphicAt(Point winLoc) {
        return root.selectableGraphicAt(toGraphicCoordinate(winLoc), canvas());
    }

    //endregion

    //region PAINT

    /**
     * Get instance of canvas to use for style location checking.
     * @return canvas
     */
    Graphics2D canvas() {
        // TODO
        return null;
    }

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
        underlays.forEach(p -> p.paint(this, canvas));
    }

    /**
     * Hook to render overlay elements. Called after everything else is drawn.
     * @param canvas the canvas to render to
     */
    protected void renderOverlay(Graphics2D canvas) {
        overlays.forEach(p -> p.paint(this, canvas));
    }

    //endregion

}
