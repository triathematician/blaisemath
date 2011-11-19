/**
 * GraphicComponent.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.graphics;

import org.bm.blaise.style.StyleProvider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

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

    /** Stores the visible shapes. */
    protected final GraphicRoot root = new GraphicRoot();
    /** Whether antialias is enabled */
    protected boolean antialias = true;

    // CONSTRUCTOR

    /** 
     * Construction of a generic graphics view component. 
     */
    public GraphicComponent() {
        root.initComponent(this);

        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(300, 200));
        setToolTipText("Canvas");
    }

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
    public StyleProvider getStyleProvider() { 
        return root.getStyleProvider(); 
    }
    
    /** 
     * Sets the default render factory used to draw shapes
     * @param factory render factory
     * @throws IllegalArgumentException if the factory is null
     */
    public void setStyleProvider(StyleProvider factory) { 
        root.setStyleProvider(factory);
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

    //
    // PAINT METHODS
    //

    /**
     * Paints the graphics to the specified canvas.
     * @param g graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderTo((Graphics2D) g);
    }

    /** 
     * Renders all shapes in root to specified graphics object.
     * @param canvas graphics canvas to render to
     */
    public void renderTo(Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
        canvas.setColor(getBackground());
        canvas.fillRect(0, 0, getWidth(), getHeight());
        renderUnderlay(canvas);
        root.draw(canvas);
        renderOverlay(canvas);
    }

    /**
     * Hook to render underlay elements. Called after the background is drawn, 
     * but before anything else.
     * @param canvas the canvas to render to
     */
    protected void renderUnderlay(Graphics2D canvas) {}
    
    /** 
     * Hook to render overlay elements. Called after everything else is drawn. 
     * @param canvas the canvas to render to
     */
    protected void renderOverlay(Graphics2D canvas) {}

    /**
     * Return the tooltip associated with the mouse event's point.
     * This will look for the topmost {@link Graphic} beneath the mouse and return that.
     * @param event the event with the point for the tooltip
     * @return tooltip for the point
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        String ct = root.getTooltip(event.getPoint());
        return ct == null ? super.getToolTipText() : ct;
    }

}
