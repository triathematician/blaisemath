/**
 * EventChannelModel.java
 * Created Jul 20, 2012
 */
package timeline;

import java.beans.PropertyChangeListener;

/**
 * <p>
 * </p>
 * @author elisha
 */
public interface EventChannelModel {
    
    public String getName();
    public void setName(String t);
    
    public void addEventSet(EventSet e);
    public void removeEventSet(EventSet e);
    
    //
    // EVENT HANDLING
    //
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void addPropertyChangeListener(String prop, PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(String prop, PropertyChangeListener l);
}
