/*
 * GraphExplorerInterface.java
 * Created Jul 2010
 */

package graphexplorer;

import graphexplorer.controller.GraphController;
import java.awt.Component;
import visometry.PlotComponent;

/**
 * Provides methods that actions classes can use, regardless of the GUI.
 * @author Elisha Peterson
 */
public interface GraphExplorerInterface {

    /** Returns active graph component displayed (or null if none is active) */
    PlotComponent activePlotComponent();
    /** Returns component-parent for dialogs */
    Component dialogComponent();
    /** @return active controller */
    GraphController activeController();
    
    /** Adds a message to the output window. */
    void output(String output);

}
