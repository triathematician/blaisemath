/**
 * GraphicComponent.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 *   GraphicComponent is a swing component that collects and draws shapes on a screen.
 *   The shapes and their styles are enclosed within the <code>GraphicCache</code>
 *   class, and the <code>SimplePlotRenderer</code> performs the drawing.
 * </p>
 * <p>
 *   Listens for changes to the shape cache, forcing a redraw whenever some of the shapes change.
 * </p>
 *
 * @see GraphicCache
 * @see SimplePlotRenderer
 *
 * @author Elisha Peterson
 */
public class GraphicComponent extends javax.swing.JComponent
        implements ChangeListener {

    // PROPERTIES

    /** Stores the visible shapes. */
    protected final GraphicCache cache;

    // CONSTRUCTOR

    /** Construction of a generic graphics view component. */
    public GraphicComponent() {
        this(new GraphicCache());
    }

    /** Construct with a specific cache */
    public GraphicComponent(GraphicCache cache) {
        if (cache == null) throw new IllegalArgumentException("GraphicCache cannot be null");
        
        this.cache = cache;
        cache.c = this;
        cache.addChangeListener(this);

        setDoubleBuffered(true);
        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(300, 200));
        setToolTipText("Canvas");

        addMouseListener(cache);
        addMouseMotionListener(cache);
    }

    //
    // PROPERTIES
    //

    /** @return shapes cache */
    public GraphicCache getCache() { return cache; }
    /** @return current renderer used to draw shapes in the component */
    public GraphicRendererProvider getRenderer() { return cache.rProvider; }
    /** Sets renderer used to draw shapes in the component */
    public void setRenderer(GraphicRendererProvider r) { cache.rProvider = r; repaint(); }

    //
    // PAINT METHODS
    //

    Graphics2D canvas;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderTo((Graphics2D) g);
    }

    /** 
     * Renders all shapes in cache to specified graphics object.
     * @param canvas graphics canvas to render to
     */
    public void renderTo(Graphics2D canvas) {
        canvas.setColor(getBackground());
        canvas.fillRect(0, 0, getWidth(), getHeight());

        cache.draw(canvas, cache.getRendererProvider());
    }

    //
    // TOOLTIPS
    //

    @Override
    public String getToolTipText(MouseEvent event) {
        String ct = cache.getTooltip(event.getPoint(), cache.getRendererProvider());
        return ct == null ? super.getToolTipText() : ct;
    }

    //
    // EVENT HANDLING
    //

    public void stateChanged(ChangeEvent e) {
//        if (e.getSource() == cache)
        repaint();
    }

}
