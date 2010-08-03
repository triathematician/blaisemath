/*
 * GraphControllerMaster.java
 * Created Jul 29, 2010
 */

package graphexplorer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Provides "master" control for scenario where multiple graphs are open
 * simultaneously.
 * @author Elisha Peterson
 */
public class GraphControllerMaster {

    /** Handles property changes */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Stores the loaded controllers, and their "active" status. */
    private ArrayList<GraphController> controllers = new ArrayList<GraphController>();
    /** Stores the active controller */
    private GraphController active = null;

    /** Marks whether changes are currently propagating from this class */
    boolean updating = false;

    @Override public String toString() { return "GraphControllerMaster"; }

    //
    // STATUS/OUTPUT
    //

    /** Prints the specified string to output */
    public void output(String string) {
        pcs.firePropertyChange("output", null, string);
    }

    //
    // CONTROLLER HANDLING
    //

    /** Adds and activates a new controller & deactivates those currently active. */
    void setActiveController(GraphController c) {
        if (!updating && c != null) {
            controllers.add(c);
            active = c;
            updating = true;
            pcs.firePropertyChange("active", null, c);
            updating = false;
        }
    }

    /** @return currently active controller. */
    GraphController getActiveController() { 
        return active;
    }

    /** Closes the active controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    void closeController() {
        closeController(active);
    }

    /** Closes specified controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    void closeController(GraphController c) {
        if (controllers.contains(c)) {
            controllers.remove(c);
            if (active == c) {
                if (controllers.size() > 0)
                    active = controllers.get(controllers.size()-1);
                else
                    active = null;
                pcs.firePropertyChange("active", c, active);
            }
        }
    }

    //
    // PropertyChangeSupport methods
    //

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
