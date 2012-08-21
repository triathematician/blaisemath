/*
 * GraphControllerListener.java
 * Created Dec 20, 2010
 */

package graphexplorer.controller;

import graphexplorer.controller.GraphController;
import java.beans.PropertyChangeListener;

/**
 * Methods that allow a view to interact with an underlying graph controller.
 * 
 * @author elisha
 */
public interface GraphControllerListener extends PropertyChangeListener {

    /** Sets the controller that contains the data backed by this model. */
    public void setController(GraphController gc);

}
