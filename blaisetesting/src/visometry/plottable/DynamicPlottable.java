/*
 * AbstractDynamicPlottable.java
 * Created on Sep 14, 2007, 7:55:27 AM
 */
package visometry.plottable;

import javax.swing.event.ChangeListener;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;

/**
 * <p>
 *  <code>DynamicPlottable</code>s adds standard change broadcasting code to a <code>Plottable</code>.
 *  Interested listeners may register and be notified when the plottable changes.
 * <p>
 * @author Elisha Peterson
 */
public abstract class DynamicPlottable<C> extends Plottable<C>
        implements ChangeBroadcaster {   

    /** Used to keep track of change listeners. */
    protected DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster(this);
    
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }
    
    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() { changer.fireStateChanged(); }

    /** Notifies listeners that the plottable has changed in some way. Also notifies the parent group. */
    @Override
    protected void firePlottableChanged() {
        needsComputation = true;
        needsRepaint = true;
        fireStateChanged();
        if (parent != null)
            parent.plottableChanged(this);
    }
}
