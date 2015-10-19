/**
 * SketchCanvasModel.java
 * Created Oct 18, 2015
 */
package com.googlecode.blaisemath.sketch;

import com.google.common.base.Objects;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.CanvasPainter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * <p>
 *   Describes the background canvas for a sketch drawing.
 * </p>
 * @author elisha
 */
public final class SketchCanvasModel {

    public static final String P_BOUNDS = "bounds";
    
    private Dimension bounds = new Dimension(800, 600);
    private AttributeSet style = Styles.strokeWidth(new Color(255, 0, 0, 128), .5f);
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public SketchCanvasModel() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public Dimension getBounds() {
        return bounds;
    }
    
    public void setBounds(Dimension bounds) {
        if (!Objects.equal(this.bounds, bounds)) {
            Object old = this.bounds;
            this.bounds = bounds;
            pcs.firePropertyChange(P_BOUNDS, old, bounds);
        }
    }
    
    //</editor-fold>
    
    /**
     * Get underlay that paints the canvas on the given graphics.
     * @return painter
     */
    public CanvasPainter<Graphics2D> underlay() {
        return new CanvasPainter<Graphics2D>() {
            @Override
            public void paint(Component component, Graphics2D canvas) {
                JGraphicComponent gc = (JGraphicComponent) component;
                Shape r = new Rectangle(0, 0, bounds.width, bounds.height);
                AffineTransform at = gc.getTransform();
                if (at != null) {
                    r = at.createTransformedShape(r);
                }
                ShapeRenderer.getInstance().render(r, style, canvas);
            }
        };
    }
    

    //<editor-fold defaultstate="collapsed" desc="PROPERTY CHANGE LISTENING">
    //
    // PROPERTY CHANGE LISTENING
    //
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }
    
    public void addPropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.addPropertyChangeListener(string, pl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }
    
    public void removePropertyChangeListener(String string, PropertyChangeListener pl) {
        pcs.removePropertyChangeListener(string, pl);
    }

    //</editor-fold>

    
}
