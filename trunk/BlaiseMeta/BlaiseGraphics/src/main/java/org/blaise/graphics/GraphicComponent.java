/**
 * GraphicComponent.java
 * Created on Jul 30, 2009
 */

package org.blaise.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import static com.google.common.base.Preconditions.*;
import com.google.common.collect.Lists;
import org.blaise.util.CanvasPainter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import org.blaise.style.context.StyleContext;
import org.blaise.util.SetSelectionModel;

/**
 * <p>
 *   Swing component that collects and draws shapes on a screen.
 *   The shapes and their styles are enclosed within a {@link GraphicRoot} class,
 *   which also sets up the requisite mouse handling and manages the drawing.
 * </p>
 *
 * @see GraphicRoot
 *
 * @author Elisha Peterson
 */
public class GraphicComponent extends javax.swing.JComponent {

    /** The visible shapes. */
    protected final GraphicRoot root;
    /** Affine transform applied to graphics canvas before drawing (enables pan & zoom). */
    protected AffineTransform transform = null;
    /** Store inverse transform */
    protected AffineTransform inverseTransform = null;
    
    /** Underlay painters */
    protected final List<CanvasPainter> underlays = Lists.newArrayList();
    /** Overlay painters */
    protected final List<CanvasPainter> overlays = Lists.newArrayList();
    
    /** Used for selecting graphics */
    protected final GraphicSelectionHandler selector = new GraphicSelectionHandler(this);

    /** Whether antialias is enabled */
    protected boolean antialias = true;

    // CONSTRUCTOR

    /**
     * Construction of a generic graphics view component.
     */
    public GraphicComponent() {
        root = new GraphicRoot(this);
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
        String ct = root.getTooltip(inverseTransform == null ? event.getPoint() : inverseTransform.transform(event.getPoint(),null));
        return ct != null ? ct : "".equals(super.getToolTipText()) ? null : super.getToolTipText();
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Return graphic root managing the shapes to be rendered
     * @return shapes root
     */
    public GraphicRoot getGraphicRoot() {
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

    public boolean isSelectionEnabled() {
        return selector.isSelectionEnabled();
    }

    public void setSelectionEnabled(boolean b) {
        selector.setSelectionEnabled(b);
    }

    public SetSelectionModel<Graphic> getSelectionModel() {
        return selector.getSelectionModel();
    }
    
    public AffineTransform getTransform() {
        return transform;
    }
    
    public AffineTransform getInverseTransform() {
        return inverseTransform;
    }
    
    public void setTransform(AffineTransform at) {
        checkArgument(at != null && at.getDeterminant() != 0);
        transform = at;
        try {
            inverseTransform = at.createInverse();
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalStateException("Already checked that the argument is invertible...", ex);
        }
        repaint();
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
    public final void addGraphics(Iterable<? extends Graphic> add) {
        root.addGraphics(add);
    }

    /**
     * Add a single graphic to the component
     * @param gfc graphic to add
     */
    public final void addGraphic(Graphic gfc) {
        root.addGraphic(gfc);
    }

    /**
     * Remove graphics from the component
     * @param remove graphics to remove
     */
    public final void removeGraphics(Iterable<? extends Graphic> remove) {
        root.removeGraphics(remove);
    }

    /**
     * Remove a single graphic from the component
     * @param gfc graphic to remove
     */
    public void removeGraphic(Graphic gfc) {
        root.removeGraphic(gfc);
    }

    /**
     * Remove all graphics from the component.
     */
    public void clearGraphics() {
        root.clearGraphics();
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
        if (transform != null) {
            canvas.transform(transform);
        }
        root.draw(canvas);
        if (transform != null) {
            canvas.transform(inverseTransform);
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
