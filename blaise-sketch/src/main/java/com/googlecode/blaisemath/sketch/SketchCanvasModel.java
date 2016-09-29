/**
 * SketchCanvasModel.java
 * Created Oct 18, 2015
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseSketch
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.google.common.base.Objects;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.graphics.swing.PathRenderer;
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
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * <p>
 *   Describes the background canvas for a sketch drawing.
 * </p>
 * @author elisha
 */
public final class SketchCanvasModel {

    public static final String P_DIMENSIONS = "dimensions";
    public static final String P_STYLE = "style";
    
    private Dimension dim = new Dimension(800, 600);
    private final AttributeSet style = Styles.fillStroke(new Color(250, 250, 250), new Color(255, 0, 0, 128), .5f);
    
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public SketchCanvasModel() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    /**
     * Get the dimensions of the canvas.
     * @return dimensions
     */
    public Dimension getDimensions() {
        return dim;
    }
    
    /**
     * Set the dimensions of the canvas.
     * @param dim dimensions
     */
    public void setDimensions(Dimension dim) {
        if (!Objects.equal(this.dim, dim)) {
            Object old = this.dim;
            this.dim = dim;
            pcs.firePropertyChange(P_DIMENSIONS, old, dim);
        }
    }

    /**
     * Get the bounding box around the canvas.
     * @return bounding box
     */
    public Rectangle2D getBoundingBox() {
        return new Rectangle(0, 0, dim.width, dim.height);
    }
    
    /**
     * Get the paint style of the canvas.
     * @return paint style
     */
    public AttributeSet getStyle() {
        return style;
    }
    
    public Color getBackground() {
        return style.getColor(Styles.FILL, new Color(250, 250, 250));
    }
    
    public void setBackground(Color c) {
        Object old = getBackground();
        if (old != c) {
            style.put(Styles.FILL, c);
            pcs.firePropertyChange(P_STYLE, null, style);
        }
    }
    
    //</editor-fold>
    
    /**
     * Get underlay that paints the canvas on the given graphics.
     * @return painter
     */
    public CanvasPainter<Graphics2D> underlay() {
        return (component, canvas) -> {
            JGraphicComponent gc = (JGraphicComponent) component;
            AffineTransform at = gc.getTransform();
            paintCanvas(canvas, at);
        };
    }
    
    /**
     * Paints the canvas on the given graphics, using the specified transform.
     * @param canvas what to paint on
     * @param at the transform for the canvas
     */
    public void paintCanvas(Graphics2D canvas, AffineTransform at) {
        Shape r = getBoundingBox();
        if (at != null) {
            r = at.createTransformedShape(r);
        }
        Area area = new Area(canvas.getClip());
        area.subtract(new Area(r));
        ShapeRenderer.getInstance().render(area, Styles.fillStroke(getBackground(), null), canvas);
        PathRenderer.getInstance().render(r, style, canvas);
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
