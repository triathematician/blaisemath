/**
 * ProxyGraphicEditor.java
 * Created Oct 14, 2014
 */
package com.googlecode.blaisemath.sketch;

import com.googlecode.blaisemath.graphics.core.Graphic;
import com.googlecode.blaisemath.graphics.core.PrimitiveGraphicSupport;
import com.googlecode.blaisemath.style.Styles;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Proxy for editing a graphic's primitive and style.
 * @author elisha
 */
public class ProxyGraphicEditor {

    /** The graphic being edited */
    private final Graphic graphic;
    /** Handles property listening */
    protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ProxyGraphicEditor(Graphic graphic) {
        this.graphic = graphic;
    }
    
    public Color getFill() {
        return graphic.getStyle().getColor(Styles.FILL, null);
    }
    
    public void setFill(Color fill) {
        graphic.getStyle().put(Styles.FILL, fill);
    }
    
    public Color getStroke() {
        return graphic.getStyle().getColor(Styles.STROKE, null);
    }
    
    public void setStroke(Color stroke) {
        graphic.getStyle().put(Styles.STROKE, stroke);
    }
    
    public float getStrokeWidth() {
        return graphic.getStyle().getFloat(Styles.STROKE_WIDTH, 1f);
    }
    
    public void setStrokeWidth(float width) {
        graphic.getStyle().put(Styles.STROKE_WIDTH, width);
    }
    
    public Object getPrimitive() {
        return graphic instanceof PrimitiveGraphicSupport
                ? ((PrimitiveGraphicSupport)graphic).getPrimitive()
                : null;
    }
    
    public void setPrimitive(Object prim) {
        if (graphic instanceof PrimitiveGraphicSupport) {
            ((PrimitiveGraphicSupport) graphic).setPrimitive(prim);
        } else {
            throw new UnsupportedOperationException();
        }
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
