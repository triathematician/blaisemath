/**
 * DefaultTimelineModel.java
 * Created Jul 20, 2012
 */
package timeline;

import java.beans.PropertyChangeListener;

/**
 * <p>
 * </p>
 * @author elisha
 */
public class DefaultTimelineModel implements TimelineModel {

    //
    // ATTRIBUTES
    //
    
    long min, max;
    long vmin, vmax;
    boolean selection = false;
    long smin, smax;
    Long pos = null;
    
    //
    // CONSTRUCTORS
    //
    
    /** Initialize without arguments */
    public DefaultTimelineModel() {
        // default values
        min = System.currentTimeMillis()-60*60*1000;
        max = min+60*60*1000;
        vmin = smin = min;
        vmax = smax = max;
        pos = (min+max)/2;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public long getMinimum() { return min; }
    public long getMaximum() { return max; }
    public long getExtent() { return max-min; }
    public void setExtent(long extent) { setMaximum(min+extent); }
    public long getViewMinimum() { return vmin; }
    public long getViewMaximum() { return vmax; }
    public long getViewExtent() { return vmax-vmin; }
    public void setViewExtent(long extent) { setViewMaximum(vmin+extent); }
    public boolean isSelectionEnabled() { return selection; }
    public long getSelectionMinimum() { return smin; }
    public long getSelectionMaximum() { return smax; }
    public long getSelectionExtent() { return smax-smin; }
    public void setSelectionExtent(long extent) { setSelectionMaximum(smin+extent); }
    public Long getPosition() { return pos; }

    public void setMinimum(long min) {}
    public void setMaximum(long max) {}
    public void setViewMinimum(long min) {}
    public void setViewMaximum(long max) {}
    public void setSelectionEnabled(boolean val) {}
    public void setSelectionMinimum(long min) {}
    public void setSelectionMaximum(long max) {}
    public void setPosition(Long pos) {}

    //</editor-fold>
    
    //
    // EVENT HANDLING
    //
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
