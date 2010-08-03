/*
 * GraphControllerMaster.java
 * Created Jul 29, 2010
 */

package graphexplorer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        GraphController oldActive = active;
        if (!updating) {
            updating = true;
            if (c != null && !controllers.contains(c))
                controllers.add(c);
            active = c;
            pcs.firePropertyChange("active", oldActive, active);
            updating = false;
        }
    }

    /** @return currently active controller. */
    GraphController getActiveController() { 
        return active;
    }

    /** @return unmodifiable view of controllers */
    List<GraphController> getControllers() {
        return Collections.unmodifiableList(controllers);
    }

    /** @return true if specified controller is in this master's list */
    boolean containsController(GraphController gc) {
        return controllers.contains(gc);
    }

    /** Closes the active controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    void closeController() {
        closeController(active);
    }

    /** Closes specified controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    void closeController(GraphController c) {
//        System.out.println("closing controller " + c);
//        System.out.println("there are " + controllers.size() + " total controllers.");
        if (controllers.contains(c)) {
            controllers.remove(c);
            updating = true;
            if (active == c) {
                active = null;
                if (controllers.size() > 0)
                    active = controllers.get(0);
                pcs.firePropertyChange("active", c, active);
            } else
                pcs.firePropertyChange("active", c, null);
            updating = false;
        }
//        System.out.println("... now there are " + controllers.size() + " total controllers.");
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
