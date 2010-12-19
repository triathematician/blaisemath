/*
 * GraphControllerMaster.java
 * Created Jul 29, 2010
 */

package graphexplorer.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *   Provides "master" control for application where multiple graphs are open
 *   simultaneously.
 * </p>
 * @author Elisha Peterson
 */
public class GraphControllerMaster {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Status update */
    public static final String $STATUS = "status";
    /** Output stream update */
    public static final String $OUTPUT = "output";
    /** The active graph */
    public static final String $ACTIVE = "active";

    //
    // PROPERTIES
    //

    /** Handles property changes */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Stores the loaded controllers, and their "active" reportStatus. */
    private ArrayList<GraphController> controllers = new ArrayList<GraphController>();
    /** Stores the active controller */
    private GraphController active = null;

//    /** Marks whether changes are currently propagating from this class */
//    boolean updating = false;

    //
    // CONSTRUCTOR
    //

    /** Default constructor */
    public GraphControllerMaster() {}

    //
    // UTILITY METHODS
    //

    @Override
    public String toString() {
        return "GraphControllerMaster";
    }

    //
    // STATUS/OUTPUT
    //

    /** Updates the application reportStatus bar. */
    public void reportStatus(String string) { pcs.firePropertyChange($STATUS, null, string); }
    /** Updates the application reportOutput. */
    public void reportOutput(String string) { pcs.firePropertyChange($OUTPUT, null, string); }

    //
    // CONTROLLER HANDLING
    //

    /** Adds and activates a new controller & deactivates those currently active. */
    public synchronized void setActiveController(GraphController c) {
        if (active != c) { // && !updating) {
            GraphController oldActive = active;
//            updating = true;
            if (c != null && !controllers.contains(c))
                controllers.add(c);
            active = c;
            pcs.firePropertyChange($ACTIVE, oldActive, active);
//            updating = false;
        }
    }

    /** @return currently active controller. */
    public synchronized GraphController getActiveController() {
        return active;
    }

    /** @return unmodifiable view of controllers */
    public synchronized List<GraphController> getControllers() {
        return Collections.unmodifiableList(controllers);
    }

    /** @return true if specified controller is in this master's list */
    public synchronized boolean containsController(GraphController gc) {
        return controllers.contains(gc);
    }

    /** Closes the active controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    public synchronized void closeController() {
        closeController(active);
    }

    /** Closes specified controller, removing it from the list of controllers;
     * if no controllers are active, activates the last one & notifies listeners */
    public synchronized void closeController(GraphController c) {
//        System.out.println("closing controller " + c);
//        System.out.println("there are " + controllers.size() + " total controllers.");
        if (controllers.contains(c)) {
            controllers.remove(c);
//                updating = true;
            if (active == c) {
                active = null;
                if (controllers.size() > 0)
                    active = controllers.get(0);
                pcs.firePropertyChange($ACTIVE, c, active);
            } else
                pcs.firePropertyChange($ACTIVE, c, null);
//                updating = false;
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
