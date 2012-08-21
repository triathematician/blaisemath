/**
 * TimelineModel.java
 * Created Jul 20, 2012
 */
package timeline;

import java.beans.PropertyChangeListener;

/**
 * <p>
 *  Model of a timeline, consisting of an overall region, an "in view" region,
 *  a "selected" region, and a "position".
 * </p>
 * @author elisha
 */
public interface TimelineModel {

    //
    // OUTER BOUNDARIES
    //
    
    public long getMinimum();
    public void setMinimum(long min);
    public long getMaximum();
    public void setMaximum(long max);
    public long getExtent();
    public void setExtent(long extent);
    
    //
    // VIEW BOUNDARIES
    //
    
    public long getViewMinimum();
    public void setViewMinimum(long min);
    public long getViewMaximum();
    public void setViewMaximum(long max);
    public long getViewExtent();
    public void setViewExtent(long extent);
    
    //
    // SELECTION BOUNDARIES
    //
    
    public boolean isSelectionEnabled();
    public void setSelectionEnabled(boolean val);
    public long getSelectionMinimum();
    public void setSelectionMinimum(long min);
    public long getSelectionMaximum();
    public void setSelectionMaximum(long max);
    public long getSelectionExtent();
    public void setSelectionExtent(long extent);
    
    //
    // POSITION
    //
    
    public Long getPosition();
    public void setPosition(Long pos);
    
    //
    // EVENT HANDLING
    //
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void addPropertyChangeListener(String prop, PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(String prop, PropertyChangeListener l);
    
}
