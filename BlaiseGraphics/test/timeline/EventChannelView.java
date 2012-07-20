/**
 * EventChannelView.java
 * Created Jul 20, 2012
 */
package timeline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bm.blaise.graphics.GraphicComponent;

/**
 * <p>
 *  Component that displays events within a channel.
 * </p>
 * @author elisha
 */
public class EventChannelView extends GraphicComponent implements PropertyChangeListener {

    //
    // ATTRIBUTES
    //
    
    private EventChannelModel model;
    private long viewMinimum;
    private long viewMaximum;
    
    //
    // CONSTRUCTORS
    //
    
    /** Initialize without arguments */
    public EventChannelView() {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == model)
            repaint();
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public EventChannelModel getModel() {
        return model;
    }

    public synchronized void setModel(EventChannelModel model) {
        if (this.model != model) {
            if (this.model != null)
                this.model.removePropertyChangeListener(this);
            this.model = model;
            if (this.model != null)
                this.model.addPropertyChangeListener(this);
            rebuild();
        }
    }
    
    public synchronized void setViewBounds(long min, long max) {
        if (viewMinimum != min || viewMaximum != max) {
            viewMinimum = min;
            viewMaximum = max;
            rebuild();
        }
    }
    
    private void rebuild() {
        // TODO - create the view elements to display
    }
    
    //</editor-fold>
    
    
    //
    // METHODS
    //
}
